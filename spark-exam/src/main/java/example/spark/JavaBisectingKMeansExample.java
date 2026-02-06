package example.spark;

import java.io.IOException;

import org.apache.spark.ml.clustering.BisectingKMeans;
import org.apache.spark.ml.clustering.BisectingKMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


/**
 * https://github.com/apache/spark/blob/master/examples/src/main/java/org/apache/spark/examples/ml/JavaBisectingKMeansExample.java
 * https://spark.apache.org/docs/latest/configuration.html
 * https://spark.apache.org/docs/latest/submitting-applications.html#master-urls
 */
public class JavaBisectingKMeansExample {
	

	public static void main(String[] args) throws IOException {
		SparkSession spark = SparkSession.builder()
				.appName("JavaBisectingKMeansExample")
//				.config("spark.master", "local")
				.master("local[*]")
//				.master("local")
				.getOrCreate();
		
		// Loads data.
		Dataset<Row> dataset = spark.read().format("libsvm")
				.load("E:/work/workspaces/pilotprj/.git/pilotprj/spark-exam/src/main/resources/data/mllib/sample_kmeans_data.txt");
		
		// Trains a bisecting k-means model.
		BisectingKMeans bkm = new BisectingKMeans().setK(2).setSeed(1L);
		BisectingKMeansModel model = bkm.fit(dataset);
		
		final String modelPath = "E:/work/workspaces/pilotprj/.git/pilotprj/spark-exam/src/main/resources/data/mllib/model/b-kmeans";
		
		model.write().overwrite().saveToLocal(modelPath);
		model = BisectingKMeansModel.read().loadFromLocal(modelPath);
		
		// Make predictions
		Dataset<Row> predictions = model.transform(dataset);
		
		// Evaluate clustering by computing Silhouettte score
		ClusteringEvaluator evaluator = new ClusteringEvaluator();
		
		double silhouette = evaluator.evaluate(predictions);
		System.out.println("Silhouette with squared euclidean distance = " + silhouette);
		
		// Shows the result.
		System.out.println("Cluster Centers: ");
		Vector[] centers = model.clusterCenters();
		for(Vector center : centers) {
			System.out.println(center);
		}
		
		spark.stop();

	}

}
