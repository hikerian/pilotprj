package utilityprj.selfhealing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 웹 UI Self-healing에서 Jaccard Similarity(자카드 유사도)는 두 요소가 가진 속성 집합(Set)이 얼마나 겹치는지를 측정하는 데 매우 효과적입니다.
 * 특히 여러 개의 클래스명(Class Names)이나 다양한 HTML 속성(Attributes)들을 비교할 때 주로 사용됩니다.
 * 1. Jaccard Similarity Java 구현 코드
 * 자카드 유사도는 (교집합의 크기) / (합집합의 크기)로 계산됩니다.
 * 2. Self-healing에서의 주요 활용처
 * 클래스 리스트 비교: 웹 요소는 class="btn primary large"와 같이 여러 클래스를 가질 수 있습니다. 순서에 상관없이 핵심 클래스들이 얼마나 유지되었는지 확인할 때 최적입니다.
 * 속성 키(Key) 비교: 요소가 가진 속성들의 종류(id, name, data-v-id, placeholder 등) 자체가 비슷한지 판단할 때 사용합니다.
 * Cosine Similarity와의 차이:
 * Cosine은 단어의 출현 빈도(횟수)까지 계산에 포함하여 맥락을 파악합니다.
 * Jaccard는 빈도와 상관없이 존재 여부(집합)만을 따지므로, 중복된 클래스명이 의미 없는 HTML 비교에서 더 깔끔한 결과를 낼 수 있습니다.
 * 3. 알고리즘 조합 전략 업데이트
 * 이제 4가지 알고리즘이 모두 준비되었습니다:
 * LCS: XPath 구조 비교 (순서 중요)
 * Levenshtein: ID, Text 정밀 비교 (오타/변형)
 * Cosine: 전체 속성 뭉치의 의미적 유사성 (맥락)
 * Jaccard: 클래스 및 속성 집합의 포함 관계 (구성 요소)
 * 이 4가지 점수를 합산할 때 자카드 유사도를 클래스명 비교에 전담시키고, 기존 가중치 모델에 추가하면 훨씬 견고한 자가 치유가 가능합니다. 자카드가 포함된 최종 가중치 합산 코드까지 확인해 보시겠어요?
 */
public class JaccardSimilarity {
	public static void main(String[] args) {
        // 예: 기존 요소의 클래스 집합 vs 후보 요소의 클래스 집합
        Set<String> savedClasses = new HashSet<>(Arrays.asList("btn", "btn-primary", "login-form", "active"));
        Set<String> candidateClasses = new HashSet<>(Arrays.asList("btn", "btn-primary", "submit-form", "active"));

        double score = calculateJaccardSimilarity(savedClasses, candidateClasses);
        
        System.out.println("기존 클래스: " + savedClasses);
        System.out.println("후보 클래스: " + candidateClasses);
        System.out.printf("자카드 유사도 점수: %.2f (%.0f%% 일치)\n", score, score * 100);
    }

    /**
     * 두 Set 사이의 자카드 유사도를 계산합니다.
     */
    public static double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) return 1.0;
        
        // 1. 교집합(Intersection) 구하기
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        // 2. 합집합(Union) 구하기
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        // 3. (교집합 크기) / (합집합 크기)
        return (double) intersection.size() / union.size();
    }

}
