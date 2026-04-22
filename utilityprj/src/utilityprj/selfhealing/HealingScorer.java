package utilityprj.selfhealing;

public class HealingScorer {
	// 각 속성별 가중치 설정 (합계 1.0)
    private static final double WEIGHT_ID = 0.5;    // ID는 매우 강력한 식별자
    private static final double WEIGHT_XPATH = 0.3; // 구조적 위치
    private static final double WEIGHT_TEXT = 0.2;  // 표시 텍스트

    public static double calculateFinalScore(SavedElement saved, CandidateElement candidate) {
        // 1. ID 유사도 (Levenshtein 사용)
        double idSim = LevenshteinExample.calculateSimilarity(saved.getId(), candidate.getId());
        
        // 2. XPath 유사도 (LCS 사용 - 구조적 순서가 중요하므로)
        double xpathSim = LCSImplementation.calculateSimilarity(saved.getXpath(), candidate.getXpath());
        
        // 3. Text 유사도 (Levenshtein 사용)
        double textSim = LevenshteinExample.calculateSimilarity(saved.getText(), candidate.getText());

        // 4. 가중치 합산
        double finalScore = (idSim * WEIGHT_ID) + (xpathSim * WEIGHT_XPATH) + (textSim * WEIGHT_TEXT);

        return finalScore;
    }

    public static void main(String[] args) {
        // 데이터 예시
        SavedElement saved = new SavedElement("login_btn", "/html/body/div/button", "Login");
        CandidateElement candidate = new CandidateElement("log_btn_01", "/html/body/section/button", "Login Now");

        double score = calculateFinalScore(saved, candidate);
        System.out.printf("종합 유사도 점수: %.2f\n", score);

        if (score > 0.7) { // 0.7 이상이면 동일 요소로 판단
            System.out.println("✅ 이 요소를 타겟으로 확정합니다.");
        }
    }
    
    public static class SavedElement {
    	private String id;
    	private String xpath;
    	private String text;
    	
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
    }
    
    public static class CandidateElement extends SavedElement {

		public CandidateElement(String id, String xpath, String text) {
			super(id, xpath, text);
		}
    	
    }
}
