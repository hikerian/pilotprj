package utilityprj.selfhealing;


/**
 * 웹 UI Self-healing에서 Levenshtein Distance(편집 거리)는 요소의 ID, Class, Text 등 문자열 속성이 일부 변경되었을 때(예: btn_login_01 → btn_login_02) 그 차이를 정량화하는 데 매우 유용합니다.
 * Java를 이용한 동적 계획법(Dynamic Programming) 방식의 효율적인 구현 예제입니다.
 * 1. Levenshtein Distance 구현 코드
 * 2. Self-healing 적용 시 활용 팁
 * LCS vs Levenshtein:
 * LCS는 XPath처럼 전체적인 순서와 구조가 중요한 데이터에 유리합니다.
 * Levenshtein은 오타나 짧은 문자열의 변형 정도를 측정하는 데 더 정밀합니다.
 * 복합 평가: 실제 치유 엔진에서는 LCS 점수와 Levenshtein 점수를 가중치로 합산하여 최종 후보를 결정합니다.
 * 대소문자 처리: 웹 UI 요소 비교 시에는 toLowerCase()를 적용하여 대소문자 차이로 인한 거리 증가를 방지하는 것이 일반적입니다.
 * 이제 LCS와 Levenshtein 두 알고리즘을 모두 확보하셨습니다. 이 두 점수를 결합하여 최종 후보를 선정하는 가중치 로직 예시가 필요하신가요?
 */
public class LevenshteinExample {
	public static void main(String[] args) {
        // 예: 웹 요소의 ID가 변경된 상황
        String oldAttribute = "submit_button_login";
        String newAttribute = "submit_btn_log";

        int distance = getDistance(oldAttribute, newAttribute);
        double similarity = calculateSimilarity(oldAttribute, newAttribute);

        System.out.println("원본 속성: " + oldAttribute);
        System.out.println("현재 속성: " + newAttribute);
        System.out.println("편집 거리 (Distance): " + distance);
        System.out.printf("유사도 점수 (Similarity): %.2f%%\n", similarity * 100);
    }

    /**
     * 두 문자열 사이의 Levenshtein 거리를 계산합니다.
     */
    public static int getDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        // 거리 저장을 위한 2차원 배열 (공간 최적화 가능하나 가독성을 위해 2차원 사용)
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j; // s1이 공백일 때 전체 삽입 비용
                } else if (j == 0) {
                    dp[i][j] = i; // s2가 공백일 때 전체 삭제 비용
                } else {
                    // 문자가 같으면 비용 0, 다르면 교체 비용 1
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    
                    dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1,    // 삭제
                                 dp[i][j - 1] + 1),   // 삽입
                        dp[i - 1][j - 1] + cost       // 교체
                    );
                }
            }
        }
        
        System.out.println("================= DP =================");
        for(int i = 0; i < m; i++) {
        	System.out.println();
        	for(int j = 0; j < n; j++) {
        		System.out.print(dp[i][j]);
        		System.out.print('\t');
        	}
        }
        System.out.println("======================================");
        
        return dp[m][n];
    }

    /**
     * 거리를 기반으로 0.0 ~ 1.0 사이의 유사도 점수를 계산합니다.
     */
    public static double calculateSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) getDistance(s1, s2) / maxLen);
    }
}
