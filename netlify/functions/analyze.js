// Plagiarism detection algorithms ported from Java Spring Boot backend

const STOPWORDS = new Set([
  "the", "a", "an", "and", "or", "but", "is", "are", "was", "were",
  "be", "been", "being", "have", "has", "had", "do", "does", "did",
  "can", "could", "would", "should", "may", "might", "must", "shall",
  "with", "from", "to", "of", "in", "on", "at", "by", "for", "as",
  "it", "this", "that", "these", "those", "i", "you", "he", "she",
  "we", "they", "what", "which", "who", "when", "where", "why", "how",
  "all", "each", "every", "both", "either", "neither", "such", "no",
  "not", "only", "own", "same", "so", "than", "too", "very"
]);

const MIN_SEGMENT_LENGTH = 50;

// --- Text Preprocessing ---

function normalize(text) {
  return text.toLowerCase().replace(/[^a-z0-9\s]/g, " ").replace(/\s+/g, " ").trim();
}

function extractSentences(text) {
  return text.split(/[.!?]+/).map(s => s.trim()).filter(s => s.length > 0);
}

function extractStyleFeatures(text) {
  const words = text.split(/\s+/).filter(w => w.length > 0);
  const sentences = text.split(/[.!?]+/).filter(s => s.trim().length > 0);

  const avgWordLength = words.length > 0
    ? words.reduce((sum, w) => sum + w.length, 0) / words.length
    : 0;

  const avgSentenceLength = words.length / Math.max(1, sentences.length);

  const uniqueWords = new Set(words);
  const typeTokenRatio = uniqueWords.size / Math.max(1, words.length);

  const punctuationCount = (text.match(/[^a-z0-9\s]/gi) || []).length;
  const punctuationDensity = punctuationCount / Math.max(1, text.length);

  return { avgWordLength, avgSentenceLength, typeTokenRatio, punctuationDensity };
}

// --- Similarity Algorithms ---

function getTermFrequency(text) {
  const words = text.toLowerCase().split(/[^a-z0-9]+/);
  const freq = {};
  for (const word of words) {
    if (word.length > 2) {
      freq[word] = (freq[word] || 0) + 1;
    }
  }
  return freq;
}

function calculateCosineSimilarity(text1, text2) {
  if (!text1 || !text2) return 0;

  const vector1 = getTermFrequency(text1);
  const vector2 = getTermFrequency(text2);

  const allTerms = new Set([...Object.keys(vector1), ...Object.keys(vector2)]);

  let dotProduct = 0;
  let magnitude1 = 0;
  let magnitude2 = 0;

  for (const term of allTerms) {
    const freq1 = vector1[term] || 0;
    const freq2 = vector2[term] || 0;
    dotProduct += freq1 * freq2;
    magnitude1 += freq1 * freq1;
    magnitude2 += freq2 * freq2;
  }

  magnitude1 = Math.sqrt(magnitude1);
  magnitude2 = Math.sqrt(magnitude2);

  if (magnitude1 === 0 || magnitude2 === 0) return 0;
  return dotProduct / (magnitude1 * magnitude2);
}

function calculateLevenshteinSimilarity(str1, str2) {
  const maxLen = Math.max(str1.length, str2.length);
  if (maxLen === 0) return 1;

  // For very long texts, sample to avoid memory/time issues in serverless
  const limit = 5000;
  const s1 = str1.length > limit ? str1.substring(0, limit) : str1;
  const s2 = str2.length > limit ? str2.substring(0, limit) : str2;
  const effectiveMax = Math.max(s1.length, s2.length);

  const distance = levenshteinDistance(s1, s2);
  return 1.0 - (distance / effectiveMax);
}

function levenshteinDistance(str1, str2) {
  const m = str1.length;
  const n = str2.length;

  // Use two-row optimization to save memory
  let prev = new Array(n + 1);
  let curr = new Array(n + 1);

  for (let j = 0; j <= n; j++) prev[j] = j;

  for (let i = 1; i <= m; i++) {
    curr[0] = i;
    for (let j = 1; j <= n; j++) {
      const cost = str1[i - 1] === str2[j - 1] ? 0 : 1;
      curr[j] = Math.min(
        prev[j] + 1,       // deletion
        curr[j - 1] + 1,   // insertion
        prev[j - 1] + cost  // substitution
      );
    }
    [prev, curr] = [curr, prev];
  }

  return prev[n];
}

function getNgrams(text, n) {
  const cleaned = text.toLowerCase().replace(/[^a-z0-9\s]/g, "");
  const ngrams = new Set();
  for (let i = 0; i <= cleaned.length - n; i++) {
    ngrams.add(cleaned.substring(i, i + n));
  }
  return ngrams;
}

function jaccardSimilarity(set1, set2) {
  if (set1.size === 0 && set2.size === 0) return 1;
  if (set1.size === 0 || set2.size === 0) return 0;

  let intersectionSize = 0;
  for (const item of set1) {
    if (set2.has(item)) intersectionSize++;
  }

  const unionSize = set1.size + set2.size - intersectionSize;
  return intersectionSize / unionSize;
}

function calculateNgramSimilarity(text1, text2) {
  if (!text1 || !text2) return 0;

  const bigrams1 = getNgrams(text1, 2);
  const bigrams2 = getNgrams(text2, 2);
  const trigrams1 = getNgrams(text1, 3);
  const trigrams2 = getNgrams(text2, 3);

  const bigramSim = jaccardSimilarity(bigrams1, bigrams2);
  const trigramSim = jaccardSimilarity(trigrams1, trigrams2);

  return bigramSim * 0.4 + trigramSim * 0.6;
}

function calculateSemanticSimilarity(sourceText, targetText) {
  if (!sourceText || !targetText) return 0;

  const features1 = extractStyleFeatures(sourceText);
  const features2 = extractStyleFeatures(targetText);

  let totalDifference = 0;
  let maxDifference = 0;

  for (const key of Object.keys(features1)) {
    if (key in features2) {
      totalDifference += Math.abs(features1[key] - features2[key]);
      maxDifference += 1.0;
    }
  }

  if (maxDifference === 0) return 0;
  return Math.max(0, 1.0 - (totalDifference / maxDifference));
}

// --- Suspicious Segment Detection ---

function detectSuspiciousSegments(sourceText, targetText) {
  const segments = [];
  const sourceSentences = extractSentences(sourceText);
  const targetSentences = extractSentences(targetText);

  for (let i = 0; i < sourceSentences.length; i++) {
    const sourceSentence = sourceSentences[i];
    if (sourceSentence.length < MIN_SEGMENT_LENGTH) continue;

    for (let j = 0; j < targetSentences.length; j++) {
      const targetSentence = targetSentences[j];
      const similarity = calculateCosineSimilarity(sourceSentence, targetSentence);

      if (similarity >= 0.7) {
        segments.push({
          text: sourceSentence.substring(0, Math.min(100, sourceSentence.length)),
          similarity: similarity,
          source: `Segment ${j} in target document`
        });
      }
    }
  }

  return segments;
}

// --- Risk Level ---

function getRiskLevel(score) {
  if (score >= 0.80) return "CRITICAL";
  if (score >= 0.60) return "HIGH";
  if (score >= 0.40) return "MEDIUM";
  return "LOW";
}

// --- Word Count ---

function countWords(text) {
  return text.trim().split(/\s+/).filter(w => w.length > 0).length;
}

// --- Main Handler ---

exports.handler = async (event) => {
  if (event.httpMethod === "OPTIONS") {
    return { statusCode: 204, headers: { "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Headers": "Content-Type", "Access-Control-Allow-Methods": "POST, OPTIONS" }, body: "" };
  }

  if (event.httpMethod !== "POST") {
    return { statusCode: 405, body: JSON.stringify({ error: "Method not allowed" }) };
  }

  try {
    const startTime = Date.now();
    const { sourceText, targetText } = JSON.parse(event.body);

    if (!sourceText || !targetText) {
      return {
        statusCode: 400,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ error: "Both sourceText and targetText are required" })
      };
    }

    // Normalize
    const sourceNormalized = normalize(sourceText);
    const targetNormalized = normalize(targetText);

    // Calculate similarities
    const cosineSimilarity = calculateCosineSimilarity(sourceNormalized, targetNormalized);
    const levenshteinSimilarity = calculateLevenshteinSimilarity(sourceNormalized, targetNormalized);
    const ngramSimilarity = calculateNgramSimilarity(sourceNormalized, targetNormalized);
    const semanticSimilarity = calculateSemanticSimilarity(sourceText, targetText);

    // Weighted final score
    const finalScore = (cosineSimilarity * 0.35) + (levenshteinSimilarity * 0.25) + (ngramSimilarity * 0.25) + (semanticSimilarity * 0.15);

    // Detect suspicious segments
    const suspiciousSegments = detectSuspiciousSegments(sourceNormalized, targetNormalized);

    const analysisTimeMs = Date.now() - startTime;

    const result = {
      finalScore,
      riskLevel: getRiskLevel(finalScore),
      cosineSimilarity,
      levenshteinSimilarity,
      ngramSimilarity,
      semanticSimilarity,
      suspiciousSegments,
      sourceWordCount: countWords(sourceText),
      targetWordCount: countWords(targetText),
      analysisTimeMs
    };

    return {
      statusCode: 200,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(result)
    };
  } catch (err) {
    return {
      statusCode: 500,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ error: "Analysis failed: " + err.message })
    };
  }
};
