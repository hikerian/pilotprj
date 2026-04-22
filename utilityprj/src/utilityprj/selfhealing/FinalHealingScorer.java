package utilityprj.selfhealing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FinalHealingScorer {
    // 가중치 설정 (프로젝트 성격에 따라 조정 가능)
    private static final double W_XPATH = 0.25;  // LCS
    private static final double W_STRING = 0.35; // Levenshtein (ID, Text)
    private static final double W_CLASSES = 0.20; // Jaccard
    private static final double W_CONTEXT = 0.20; // Cosine

    public double getFinalScore(SavedElement saved, SavedElement candidate) {
        
        // 1. LCS - 구조 비교 (XPath)
        double xpathScore = LCSImplementation.calculateSimilarity(saved.getXpath(), candidate.getXpath());

        // 2. Levenshtein - 정밀 문자열 비교 (ID & Text 평균)
        double idScore = LevenshteinExample.calculateSimilarity(saved.getId(), candidate.getId());
        double textScore = LevenshteinExample.calculateSimilarity(saved.getText(), candidate.getText());
        double stringScore = (idScore + textScore) / 2.0;

        // 3. Jaccard - 클래스 집합 비교
        double jaccardScore = JaccardSimilarity.calculateJaccardSimilarity(saved.getClassSet(), candidate.getClassSet());

        // 4. Cosine - 전체 맥락 비교 (모든 속성 결합 문자열)
        double cosineScore = CosineSimilarity.calculateCosineSimilarity(saved.getAttrBundle(), candidate.getAttrBundle());

        // 최종 가중치 합산
        double totalScore = (xpathScore * W_XPATH) + 
                            (stringScore * W_STRING) + 
                            (jaccardScore * W_CLASSES) + 
                            (cosineScore * W_CONTEXT);

        return totalScore;
    }

    public void processHealing(List<WebElement> candidates, SavedElement saved) {
        WebElement bestMatch = null;
        double maxScore = 0;

        for (WebElement candidate : candidates) {
            double score = getFinalScore(saved, candidate);

            if (score > maxScore) {
                maxScore = score;
                bestMatch = candidate;
            }
        }

        if (maxScore > 0.7) {
            System.out.println("✅ 최적의 요소 발견! 최종 점수: " + maxScore);
            // 여기서 해당 요소로 테스트 계속 진행
        } else {
            System.out.println("❌ 신뢰할 수 있는 후보가 없습니다.");
        }
        
        System.out.println("최적의 후보는 " + bestMatch + " score: " + maxScore);
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
		
		public Set<String> getClassSet() {
			return new HashSet<String>();
		}
		
		public String getAttrBundle() {
			return "";
		}
    }
    
    public static class WebElement extends SavedElement {

		public WebElement(String id, String xpath, String text) {
			super(id, xpath, text);
		}
    	
    }
    
    public static class CandidateElement extends SavedElement {

		public CandidateElement(String id, String xpath, String text) {
			super(id, xpath, text);
		}
    	
    }

}

