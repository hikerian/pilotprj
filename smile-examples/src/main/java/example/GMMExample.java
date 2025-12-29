package example;

import smile.stat.distribution.GaussianMixture;
import smile.stat.distribution.Mixture;

/**
 * Gaussian Mixture Models (GMM)
 */
public class GMMExample {

	public static void main(String[] args) {
		double[] data = {1.0, 1.2, 1.1, 5.0, 5.1, 5.3, 9.0, 9.1, 9.3};
        int k = 3; // Number of Gaussian components

        // Fit the GMM model
        GaussianMixture gmm = GaussianMixture.fit(k, data);

        // Print learned parameters (means, variances, weights)
        System.out.println("Learned GMM Components:");
        Mixture.Component[] components = gmm.components;
        for (int i = 0; i < components.length; i++) {
            System.out.printf("Component %d: Mean=%.2f, Variance=%.2f, Standard Deviation=%.2f%n"
            		, i
                    , components[i].distribution().mean()
                    , components[i].distribution().variance()
                    , components[i].distribution().sd());
        }

        // Predict cluster assignment (soft clustering) for a new point
        double newPoint = 1.05;
        double[] responsibilities = gmm.posteriori(newPoint);
        System.out.printf("Responsibilities for %.2f: %s%n", newPoint, java.util.Arrays.toString(responsibilities));
	}

}
