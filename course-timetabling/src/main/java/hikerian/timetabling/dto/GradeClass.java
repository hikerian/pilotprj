package hikerian.timetabling.dto;


public class GradeClass {
	private int grade;
	private String classNm;
	
	
	public GradeClass() {
	}
	
	public GradeClass(int grade, String classNm) {
		this.grade = grade;
		this.classNm = classNm;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getClassNm() {
		return classNm;
	}

	public void setClassNm(String classNm) {
		this.classNm = classNm;
	}
	

}
