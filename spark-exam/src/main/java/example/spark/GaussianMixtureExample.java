package example.spark;

import java.io.IOException;

import org.apache.spark.ml.clustering.GaussianMixture;
import org.apache.spark.ml.clustering.GaussianMixtureModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


/**
 * https://github.com/apache/spark/blob/master/examples/src/main/java/org/apache/spark/examples/ml/JavaGaussianMixtureExample.java
 */
public class GaussianMixtureExample {
	
	public static void main(String[] args) throws IOException {
		// Creates a SparkSession
		SparkSession spark = SparkSession
				.builder()
				.appName("GaussianMixtureExample")
				.master("local[*]")
				.getOrCreate();
		
		// Loads data
		Dataset<Row> dataset = spark.read().format("libsvm").load("E:/work/workspaces/pilotprj/.git/pilotprj/spark-exam/src/main/resources/data/mllib/sample_kmeans_data.txt");
		
		// Trains a GaussianMixture model
//		GaussianMixture gmm = new GaussianMixture().setK(2);
		GaussianMixture gmm = new GaussianMixture();
		GaussianMixtureModel model = gmm.fit(dataset);
		
		final String modelPath = "E:/work/workspaces/pilotprj/.git/pilotprj/spark-exam/src/main/resources/data/mllib/model/gmm";
		
		model.write().overwrite().saveToLocal(modelPath);
		
		model = GaussianMixtureModel.read().loadFromLocal(modelPath);
		
		// Output the parameters of the mixture model
		for(int i = 0; i < model.getK(); i++) {
			System.out.printf("Gaussian %d:\nweight=%f\nmu=%s\nsigma=\n%s\n\n"
					, i, model.weights()[i], model.gaussians()[i].mean(), model.gaussians()[i].cov());
		}
		
		spark.stop();
	}

}
