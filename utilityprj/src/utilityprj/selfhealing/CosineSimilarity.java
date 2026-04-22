package utilityprj.selfhealing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 웹 UI Self-healing에서 Cosine Similarity(코사인 유사도)는 요소의 속성들을 수치 벡터(Vector)로 변환하여 두 요소 사이의 '방향적 유사성'을 측정할 때 사용합니다.
 * 이 방식은 속성의 길이가 길거나 특정 속성이 누락되어도 전체적인 '특징 분포'가 비슷하면 높은 점수를 주는 장점이 있습니다.
 * 1. Cosine Similarity Java 구현 코드
 * 문자열의 빈도수를 기반으로 벡터를 생성하여 비교하는 방식입니다.
 * 2. Self-healing에서의 활용 포인트
 * 특징 벡터화: 클래스명(btn, primary), 태그명, 부모 요소의 이름 등을 하나의 문자열 뭉치로 만들어 벡터로 변환합니다.
 * 구조적 맥락 파악: 단순히 문자열이 얼마나 바뀌었나(Levenshtein)를 넘어, 해당 요소가 가진 '의미적 속성'들이 얼마나 유지되었는지 판단하는 데 효과적입니다.
 * 하이브리드 적용:
 * LCS: XPath(구조) 비교
 * Levenshtein: ID/Text(정확도) 비교
 * Cosine Similarity: Class/Attributes(전체 스타일 및 성격) 비교
 * 이제 LCS, Levenshtein, Cosine Similarity 세 가지 알고리즘이 준비되었습니다. 이 알고리즘들을 사용하여 실제 웹 페이지의 후보군 중 최적의 요소를 뽑는 통합 검색 로직을 구성해 볼까요?
 */
public class CosineSimilarity {
    public static void main(String[] args) {
        // 예: 기존 요소의 속성 집합 vs 새로운 후보 요소의 속성 집합
        String attr1 = "button login blue-large submit";
        String attr2 = "btn login blue-small submit-action";

        double score = calculateCosineSimilarity(attr1, attr2);
        System.out.println("속성 1: " + attr1);
        System.out.println("속성 2: " + attr2);
        System.out.printf("코사인 유사도 점수: %.4f\n", score);
    }

    public static double calculateCosineSimilarity(String s1, String s2) {
        // 1. 단어 빈도수 맵(Vector) 생성
        Map<String, Integer> v1 = getTermFrequencyMap(s1.split(" "));
        Map<String, Integer> v2 = getTermFrequencyMap(s2.split(" "));

        // 2. 모든 고유 단어 집합 추출
        Set<String> allWords = new HashSet<>();
        allWords.addAll(v1.keySet());
        allWords.addAll(v2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        // 3. 코사인 유사도 공식 적용: (A · B) / (|A| * |B|)
        for (String word : allWords) {
            int freq1 = v1.getOrDefault(word, 0);
            int freq2 = v2.getOrDefault(word, 0);

            dotProduct += freq1 * freq2;
            norm1 += Math.pow(freq1, 2);
            norm2 += Math.pow(freq2, 2);
        }

        if (norm1 == 0 || norm2 == 0) return 0.0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private static Map<String, Integer> getTermFrequencyMap(String[] words) {
        Map<String, Integer> map = new HashMap<>();
        for (String word : words) {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }
        return map;
    }
}
