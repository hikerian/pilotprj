package utilityprj.selfhealing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealingEngine {
    // 각 알고리즘별 가중치 설정
    private static final double W_LCS = 0.3;         // XPath(구조)
    private static final double W_LEVENSHTEIN = 0.4; // ID, Text(정확도)
    private static final double W_COSINE = 0.3;      // Class/Attributes(스타일 맥락)

    public WebElement findBestMatch(SavedElement saved, List<WebElement> candidates) {
        WebElement bestElement = null;
        double maxTotalScore = 0;

        for (WebElement candidate : candidates) {
            // 1. 후보 요소의 정보 추출
            String currentXPath = getXPath(candidate);
            String currentId = candidate.getAttribute("id");
            String currentText = candidate.getText();
            String currentClasses = candidate.getAttribute("class");

            // 2. 각 알고리즘별 점수 계산
            // XPath는 구조가 중요하므로 LCS 적용
            double lcsScore = LCSImplementation.calculateSimilarity(saved.getXpath(), currentXPath);
            
            // ID와 Text는 정밀 매칭이 중요하므로 Levenshtein 적용
            double levIdScore = LevenshteinExample.calculateSimilarity(saved.getId(), currentId != null ? currentId : "");
            double levTextScore = LevenshteinExample.calculateSimilarity(saved.getText(), currentText);
            double avgLevScore = (levIdScore + levTextScore) / 2;

            // Class와 태그 정보는 특징 뭉치로 코사인 유사도 적용
            double cosineScore = CosineSimilarity.calculateCosineSimilarity(saved.getAttribute("class"), currentClasses);

            // 3. 가중치 합산 최종 점수 (Final Score)
            double totalScore = (lcsScore * W_LCS) + (avgLevScore * W_LEVENSHTEIN) + (cosineScore * W_COSINE);

            // 4. 최적의 후보 갱신
            if (totalScore > maxTotalScore) {
                maxTotalScore = totalScore;
                bestElement = candidate;
            }
        }

        // 임계값(Threshold) 검증 (예: 0.65 이상일 때만 복구 승인)
        if (maxTotalScore >= 0.65) {
            System.out.printf("🎯 치유 성공! (최종 유사도: %.2f)\n", maxTotalScore);
            return bestElement;
        }

        return null; // 치유 실패
    }

    private String getXPath(WebElement element) {
        // JavascriptExecutor를 이용한 실시간 XPath 추출 로직 (생략)
        return "/html/body/div/button"; 
    }

    public static class SavedElement {
    	private String id;
    	private String xpath;
    	private String text;
    	private Map<String, String> attr = new HashMap<>();
    	
		public SavedElement(String id, String xpath, String text) {
			super();
			this.id = id;
			this.xpath = xpath;
			this.text = text;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getXpath() {
			return xpath;
		}
		public void setXpath(String xpath) {
			this.xpath = xpath;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		
		public void setAttribute(String name, String value) {
			this.attr.put(name, value);
		}
		
		public String getAttribute(String name) {
			return this.attr.get(name);
		}
    }
    
    public static class WebElement extends SavedElement {

		public WebElement(String id, String xpath, String text) {
			super(id, xpath, text);
		}
    	
    }

}
