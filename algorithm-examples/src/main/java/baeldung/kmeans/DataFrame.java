package baeldung.kmeans;

import java.util.Map;
import java.util.Objects;


public class DataFrame {
	private final String description;
	private final Map<String, Double> features;
	
	
	public DataFrame(String description, Map<String, Double> features) {
		this.description = description;
		this.features = features;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void put(String name, Double value) {
		this.features.put(name, value);
	}
	
	public Map<String, Double> getFeatures() {
		return this.features;
	}

	@Override
	public String toString() {
		return "DataFrame [description=" + this.description + ", features=" + this.features + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, features);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataFrame other = (DataFrame) obj;
		return Objects.equals(description, other.description) && Objects.equals(features, other.features);
	}


}
