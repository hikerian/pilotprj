package baeldung.kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * https://www.baeldung.com/java-k-means-clustering-algorithm
 * Now that we have a few necessary abstractions in place, it’s time to write our K-Means implementation. Here’s a quick look at our method signature
 */
public class KMeans {
	private static final Random random = new Random();
	
	/**
	 * Let’s break down this method signature:
	 * ● The dataset is a set of feature vectors. Since each feature vector is a DataFrame, then the dataset type is List<DataFrame>
	 * ● The k parameter determines the number of clusters, which we should provide in advance
	 * ● distance encapsulates the way we’re going to calculate the difference between two features
	 * ● K-Means terminates when the assignment stops changing for a few consecutive iterations.
	 *   In addition to this termination condition, we can place an upper bound for the number of iterations, too.
	 *   The maxIterations argument determines that upper bound
	 * ● When K-Means terminates, each centroid should have a few assigned features, hence we’re using a Map<Centroid, List<DataFrame>> as the return type.
	 *   Basically, each map entry corresponds to a cluster
	 * 
	 * In each iteration, after assigning all dataframes to their nearest centroid, first, we should compare the current assignments with the last iteration.
	 * If the assignments were identical, then the algorithm terminates. Otherwise, before jumping to the next iteration, we should relocate the centroids:
	 * 
	 * @param dataset
	 * @param k
	 * @param distance
	 * @param maxIterations
	 * @return
	 */
	public static Map<Centroid, List<baeldung.kmeans.DataFrame>> fit(List<baeldung.kmeans.DataFrame> dataset, int k, Distance distance, int maxIterations) {
		
		List<Centroid> centroids = randomCentroids(dataset, k);
		Map<Centroid, List<baeldung.kmeans.DataFrame>> clusters = new HashMap<>();
		Map<Centroid, List<baeldung.kmeans.DataFrame>> lastState = new HashMap<>();
		
		// iterate for a pre-defined number of items
		for(int i = 0; i < maxIterations; i++) {
			boolean isLastIteration = i == maxIterations - 1;
			
			// in each iteration we should find the nearest centroid for each dataframe
			for(baeldung.kmeans.DataFrame dataframe : dataset) {
				Centroid centroid = nearestCentroid(dataframe, centroids, distance);
				assignToCluster(clusters, dataframe, centroid);
			}
			
			// if the assignments do not change, then the algorithm terminates
			boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
			lastState = clusters;
			if(shouldTerminate) {
				break;
			}
			
			// at the end of each iteration we should relocate the centroids
			centroids = relocateCentroids(clusters);
			clusters = new HashMap<>();
		}
		
		return lastState;
	}
	
	/**
	 * The first step is to generate k randomly placed centroids.
	 * Although each centroid can contain totally random coordinates,
	 * it’s a good practice to generate random coordinates between the minimum and maximum possible values for each attribute.
	 * Generating random centroids without considering the range of possible values would cause the algorithm to converge more slowly.
	 * First, we should compute the minimum and maximum value for each attribute, and then, generate the random values between each pair of them:
	 * @param dataset
	 * @param k
	 * @return
	 */
	private static List<Centroid> randomCentroids(List<baeldung.kmeans.DataFrame> dataset, int k) {
		List<Centroid> centroids = new ArrayList<>();
		Map<String, Double> maxs = new HashMap<>();
		Map<String, Double> mins = new HashMap<>();
		
		for(baeldung.kmeans.DataFrame dataframe : dataset) {
			dataframe.getFeatures().forEach((key, value) -> {
				// compares the value with the current max and choose the bigger value between them
				maxs.compute(key, (k1, max) -> max == null || value > max ? value : max);
				
				// compares the value with the current min and choose the smaller value between them
				mins.compute(key, (k1, min) -> min == null || value < min ? value : min);
			});
		}
		
		Set<String> attributes = dataset.stream()
				.flatMap(e -> e.getFeatures().keySet().stream())
				.collect(Collectors.toSet());

		for(int i = 0; i < k; i++) {
			Map<String, Double> coordinates = new HashMap<>();
			for(String attribute : attributes) {
				double max = maxs.get(attribute);
				double min = mins.get(attribute);
				coordinates.put(attribute, random.nextDouble() * (max - min) + min);
			}
			
			centroids.add(new Centroid(coordinates));
		}
		
		return centroids;
	}
	
	/**
	 * First off, given a DataFrame, we should find the centroid nearest to it:
	 * @param dataframe
	 * @param centroids
	 * @param distance
	 * @return
	 */
	private static Centroid nearestCentroid(DataFrame dataframe, List<Centroid> centroids, Distance distance) {
		double minimumDistance = Double.MAX_VALUE;
		Centroid nearest = null;
		
		for(Centroid centroid : centroids) {
			double currentDistance = distance.calculate(dataframe.getFeatures(), centroid.getCoodinates());
			
			if(currentDistance < minimumDistance) {
				minimumDistance = currentDistance;
				nearest = centroid;
			}
		}
		
		return nearest;
	}
	
	/**
	 * Each dataframe belongs to its nearest centroid cluster:
	 * @param clusters
	 * @param dataframe
	 * @param centroid
	 */
	private static void assignToCluster(Map<Centroid, List<DataFrame>> clusters, DataFrame dataframe, Centroid centroid) {
		clusters.compute(centroid, (key, list) -> {
			if(list == null) {
				list = new ArrayList<>();
			}
			
			list.add(dataframe);
			return list;
		});
	}
	
	/**
	 * If, after one iteration, a centroid does not contain any assignments, then we won’t relocate it.
	 * Otherwise, we should relocate the centroid coordinate for each attribute to the average location of all assigned dataframes:
	 * @param centroid
	 * @param dataset
	 * @return
	 */
	private static Centroid average(Centroid centroid, List<DataFrame> dataset) {
		if(dataset == null || dataset.isEmpty()) {
			return centroid;
		}
		
		Map<String, Double> average = centroid.getCoodinates();
		dataset.stream().flatMap(e -> e.getFeatures().keySet().stream())
		.forEach(k -> average.put(k, 0.0));
		
		for(DataFrame dataframe : dataset) {
			dataframe.getFeatures().forEach((k, v) -> average.compute(k, (k1, currentValue) -> v + currentValue));
		}
		
		average.forEach((k, v) -> average.put(k, v / dataset.size()));
		
		return new Centroid(average);
	}
	
	/**
	 * Since we can relocate a single centroid, now it’s possible to implement the relocateCentroids method:
	 * @param clusters
	 * @return
	 */
	private static List<Centroid> relocateCentroids(Map<Centroid, List<DataFrame>> clusters) {
		return clusters.entrySet().stream().map(e -> average(e.getKey(), e.getValue())).collect(Collectors.toList());
	}



}
