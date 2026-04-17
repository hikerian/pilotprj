package spring.ai.demo.dto;

public class ReviewClassification {

    public enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE
    }

    private String review;
    private Sentiment sentiment;

    public ReviewClassification() {
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((review == null) ? 0 : review.hashCode());
        result = prime * result + ((sentiment == null) ? 0 : sentiment.hashCode());
        return result;
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
        if (review == null) {
            if (other.review != null)
                return false;
        } else if (!review.equals(other.review))
            return false;
        if (sentiment != other.sentiment)
            return false;
        return true;
    }

}
