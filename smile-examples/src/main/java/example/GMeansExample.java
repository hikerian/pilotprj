package example;

import java.util.Arrays;

import smile.clustering.CentroidClustering;
import smile.clustering.GMeans;


public class GMeansExample {

	public static void main(String[] args) {
		// Sample data: a 2D array of data points
        // In a real application, you would load your data from a file or database
        double[][] data = {
            {1.0, 2.0}, {1.5, 1.8}, {5.0, 8.0}, {8.0, 8.0},
            {1.5, 0.8}, {8.0, 9.0}, {4.0, 5.0}, {2.0, 2.0},
            {9.0, 8.0}, {1.0, 0.8}, {4.0, 8.0}, {4.0, 2.0}
        };

        // Optional: set a maximum number of clusters to consider (e.g., 100)
        int kmax = 100; 
        // Optional: set a maximum number of iterations for the underlying k-means
        int maxIter = 100;

        // Perform G-Means clustering
        // The algorithm automatically determines 'k'
        CentroidClustering<double[], double[]> result = GMeans.fit(data, kmax, maxIter);

        // Get the results
        int numClusters = result.size(0);
        int[] clusterLabels = result.group();
        double[][] centroids = result.centers();

        System.out.println("G-Means determined optimal number of clusters: " + numClusters);
        System.out.println("Cluster centroids:");
        for (double[] centroid : centroids) {
            System.out.println(Arrays.toString(centroid));
        }

        System.out.println("Data point assignments:");
        for (int i = 0; i < data.length; i++) {
            System.out.printf("Point %s assigned to cluster %d%n", Arrays.toString(data[i]), clusterLabels[i]);
        }

	}

}
