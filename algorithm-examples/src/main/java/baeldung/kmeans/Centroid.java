package baeldung.kmeans;

import java.util.Map;
import java.util.Objects;


/**
 * Centroids are in the same space as normal features, so we can represent them similar to features:
 */
public class Centroid {
	private final Map<String, Double> coordinates;
	
	
	public Centroid(Map<String, Double> coordinates) {
		this.coordinates = coordinates;
	}
	
	public Map<String, Double> getCoodinates() {
		return this.coordinates;
	}

	@Override
	public String toString() {
		return "Centroid [coordinates=" + this.coordinates + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.coordinates);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Centroid other = (Centroid) obj;
		return Objects.equals(coordinates, other.coordinates);
	}
	
	

}
