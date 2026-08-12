package spring.ai.ollama.dto;

import java.util.Objects;


public class ReviewClassification {
	public enum Sentiment {
		POSITIVE, NEUTRAL, NEGATIVE
	}
	
	private String review;
	private Sentiment classification;
	
	
	public ReviewClassification() {
	}


	public String getReview() {
		return review;
	}


	public void setReview(String review) {
		this.review = review;
	}


	public Sentiment getClassification() {
		return classification;
	}


	public void setClassification(Sentiment classification) {
		this.classification = classification;
	}


	@Override
	public String toString() {
		return "ReviewClassification [review=" + review + ", classification=" + classification + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(classification, review);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReviewClassification other = (ReviewClassification) obj;
		return classification == other.classification && Objects.equals(review, other.review);
	}
	

}
