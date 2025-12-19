package baeldung.kmeans;

import java.util.List;
import java.util.Map;


public class Errors {

	
	/**
	 * ● Number of Clusters
	 * One of the fundamental properties of K-Means is the fact that we should define the number of clusters in advance.
	 * So far, we used a static value for k, but determining this value can be a challenging problem.
	 * There are two common ways to calculate the number of clusters:
	 * 1. Domain Knowledge
	 * 2. Mathematical Heuristics
	 * If we’re lucky enough that we know so much about the domain, then we might be able to simply guess the right number.
	 * Otherwise, we can apply a few heuristics like Elbow Method or Silhouette Method to get a sense on the number of clusters.
	 * Before going any further, we should know that these heuristics, although useful, are just heuristics and may not provide clear-cut answers.
	 * 
	 * ● Elbow Method
	 * To use the elbow method,
	 * we should first calculate the difference between each cluster centroid and all its members.
	 * As we group more unrelated members in a cluster, the distance between the centroid and its members goes up, hence the cluster quality decreases.
	 * One way to perform this distance calculation is to use the Sum of Squared Errors.
	 * Sum of squared errors or SSE is equal to the sum of squared differences between a centroid and all its members:
	 * @param clustered
	 * @param distance
	 * @return
	 */
	public static double sse(Map<Centroid, List<DataFrame>> clustered, Distance distance) {
		double sum = 0;
		for(Map.Entry<Centroid, List<DataFrame>> entry : clustered.entrySet()) {
			Centroid centroid = entry.getKey();
			for(DataFrame dataframe : entry.getValue()) {
				double d = distance.calculate(centroid.getCoodinates(), dataframe.getFeatures());
				sum += Math.pow(d, 2);
			}
		}
		
		return sum;
	}

}
