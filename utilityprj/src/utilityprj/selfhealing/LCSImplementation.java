package utilityprj.selfhealing;


/**
 * 웹 UI Self-healing에서 LCS(최장 공통 부분 수열) 알고리즘은 주로 두 요소의 XPath 경로나 DOM 트리 구조의 유사성을 비교하여, 구조가 얼마나 유지되었는지를 측정하는 데 사용됩니다.
 * Java를 이용한 동적 계획법(Dynamic Programming) 방식의 구현 예제입니다.
 * 
 * 1. LCS 길이 및 문자열 추출 구현
 * 이 코드는 두 문자열 사이의 LCS 길이를 구하고, 실제 공통 부분 수열이 무엇인지 출력합니다.
 * 
 * 2. Self-healing 적용 시 포인트
 * 유사도 계산: 단순히 길이를 구하는 데 그치지 않고, LCS 길이 / Max(String1 길이, String2 길이) 공식을 통해 0.0~1.0 사이의 유사도 점수를 산출합니다.
 * 태그 경로 분석: UI 자동화에서는 문자열 단위뿐만 아니라 div > p > span 같은 태그 리스트(List) 단위로 LCS를 적용하여 구조적 일치도를 정밀하게 측정하기도 합니다.
 * 이 코드를 통해 계산된 유사도 점수를 다른 속성(ID, Class 등)의 점수와 합산하여 최종 후보를 결정하게 됩니다. 이 로직을 실제 Selenium 프로젝트에 적용하는 방법이 궁금하신가요?
 */
public class LCSImplementation {
	public static void main(String[] args) {
        // 예: 이전 XPath와 현재 후보 요소의 XPath 비교
        String str1 = "/html/body/div[1]/div[2]/button";
        String str2 = "/html/body/section/div[2]/button";

        findLCS(str1, str2);
    }
	
	public static double calculateSimilarity(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        // 1. DP 테이블 채우기 (LCS 길이 계산)
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
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

        // 2. LCS 문자열 역추적 (Backtracking)
        StringBuilder lcsString = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                lcsString.append(s1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        double similarity = (double) dp[m][n] / Math.max(m, n);

        System.out.println("문자열 1: " + s1);
        System.out.println("문자열 2: " + s2);
        System.out.println("LCS 길이: " + dp[m][n]);
        System.out.println("LCS 문자열: " + lcsString.reverse().toString());
        System.out.printf("유사도 점수: %.2f\n", similarity);
        
        return similarity;
	}

    public static void findLCS(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        // 1. DP 테이블 채우기 (LCS 길이 계산)
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
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

        // 2. LCS 문자열 역추적 (Backtracking)
        StringBuilder lcsString = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                lcsString.append(s1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        double similarity = (double) dp[m][n] / Math.max(m, n);

        System.out.println("문자열 1: " + s1);
        System.out.println("문자열 2: " + s2);
        System.out.println("LCS 길이: " + dp[m][n]);
        System.out.println("LCS 문자열: " + lcsString.reverse().toString());
        System.out.printf("유사도 점수: %.2f\n", similarity);
    }

}
