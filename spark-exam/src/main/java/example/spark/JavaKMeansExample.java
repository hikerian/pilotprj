package example.spark;

import java.io.IOException;

import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * https://github.com/apache/spark/blob/master/examples/src/main/java/org/apache/spark/examples/ml/JavaKMeansExample.java
 */
public class JavaKMeansExample {
	private static final Logger log = LoggerFactory.getLogger(JavaKMeansExample.class);

	public static void main(String[] args) throws IOException {
		// Create a SparkSession.
		SparkSession spark = SparkSession.builder()
				.appName("JavaKMeansExample")
//				.config("spark.master", "local")
				.master("local[*]")
				.getOrCreate();
		
		
		// Loads data.
		log.info("Load Model");
		Dataset<Row> dataset = spark.read()
				.format("libsvm") // <label> <index1>:<value1> <index2>:<value2> ... <indexN>:<valueN>
				.load("E:/work/workspaces/pilotprj/.git/pilotprj/spark-exam/src/main/resources/data/mllib/sample_kmeans_data.txt");
		
		// Trains a k-means model.
		KMeans kmeans = new KMeans().setK(2).setSeed(1L);
		
		log.info("Fit");
		KMeansModel model = kmeans.fit(dataset);
		
		final String modelPath = "E:/work/workspaces/pilotprj/.git/pilotprj/spark-exam/src/main/resources/data/mllib/model/kmeans";
		
		model.write().overwrite().saveToLocal(modelPath);
		log.info("======= Saved!!!");
		
		model = KMeansModel.read().loadFromLocal(modelPath);
		log.info("======= Loaded!!!");
		
		// Make predictions
		log.info("Make predictions");
		Dataset<Row> predictions = model.transform(dataset);
		
		log.info("Predictions {}", predictions);
		
		log.info("Evaluate clustering by computing Silhouette score");
		ClusteringEvaluator evaluator = new ClusteringEvaluator();
		
		double silhouette = evaluator.evaluate(predictions);
		log.info("Silhouette with squared euclidean distance = " + silhouette);
		
		// Shows the result.
		log.info("Shows the result.");
		Vector[] centers = model.clusterCenters();
		
		log.info("Cluster centers: ");
		for(Vector center : centers) {
			log.info("" + center);
		}

		spark.stop();
	}


}
