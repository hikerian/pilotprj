package example.spark;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.classification.LogisticRegressionTrainingSummary;
import org.apache.spark.ml.linalg.SparseVector;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


/**
 * Multinomial Logistic Regression
 */
public class JavaMulticlassLogisticRegressionWithElasticNetExample {

	
	public static void main(String[] args) {
		SparkSession spark = SparkSession.builder()
				.appName("JavaMulticlassLogisticRegressionWithElasticNetExample")
				.config("spark.master", "local")
				.getOrCreate();
		
		// Load training data
		Dataset<Row> training = spark.read().format("libsvm")
				.load("E:/work/workspaces/pilotprj/.git/pilotprj/spark-exam/src/main/resources/data/mllib/sample_multiclass_classification_data.txt");
		
		LogisticRegression lr = new LogisticRegression()
				.setMaxIter(100)
				.setRegParam(0.3) // 정규화 매개변수를 설정합니다.
				.setFamily("multinomial")
				.setElasticNetParam(0.8); // ElasticNet 믹싱 파라미터를 설정합니다.
		
		// Fir the model
		LogisticRegressionModel lrModel = lr.fit(training);
		
		// Print the coefficients and intercept for multinomial logistic regression
		System.out.println("Coefficients: \n" + lrModel.coefficientMatrix() + " \nIntercept: " + lrModel.interceptVector());
		LogisticRegressionTrainingSummary trainingSummary = lrModel.summary();
		
		// Obtain the loss per iteration.
		double[] objectiveHistory = trainingSummary.objectiveHistory();
		for(double lossPerIteration : objectiveHistory) {
			System.out.println(lossPerIteration);
		}
		
		// for multiclass, we can inspect metrics on a per-label basis
		System.out.println("False positive rate by label:");
		int i = 0;
		double[] fprLabel = trainingSummary.falsePositiveRateByLabel();
		for(double fpr : fprLabel) {
			System.out.println("label: " + i + ": " + fpr);
			i++;
		}
		
		System.out.println("True positive rate by label:");
		i = 0;
		double[] tprLabel = trainingSummary.truePositiveRateByLabel();
		for(double tpr : tprLabel) {
			System.out.println("label: " + i + ": " + tpr);
			i++;
		}
		
		System.out.println("Precision by label:");
		i = 0;
		double[] precLabel = trainingSummary.precisionByLabel();
		for(double prec : precLabel) {
			System.out.println("label: " + i + ": " + prec);
			i++;
		}
		
		System.out.println("Recall by label:");
		i = 0;
		double[] recLabel = trainingSummary.recallByLabel();
		for(double rec : recLabel) {
			System.out.println("label: " + i + ": "+ rec);
			i++;
		}
		
		System.out.println("F-measure by label:");
		i = 0;
		double[] fLabel = trainingSummary.fMeasureByLabel();
		for(double f : fLabel) {
			System.out.println("label: " + i + ": " + f);
			i++;
		}
		
		double accuracy = trainingSummary.accuracy();
		double falsePositiveRate = trainingSummary.weightedFalsePositiveRate();
		double truePositiveRate = trainingSummary.weightedTruePositiveRate();
		double fMeasure = trainingSummary.weightedFMeasure();
		double precision = trainingSummary.weightedPrecision();
		double recall = trainingSummary.weightedRecall();
		
		System.out.println("Accuracy: " + accuracy);
		System.out.println("FalsePositiveRate: " + falsePositiveRate);
		System.out.println("TruePositiveRate: " + truePositiveRate);
		System.out.println("F-Measure: " + fMeasure);
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		
		spark.stop();
		
		predict: {
			Vector v = new SparseVector(4, new int[] {0, 1, 2, 3}, new double[] {-0.222222, 0.5, -0.762712, -0.833333});
			double predicted = lrModel.predict(v);
			System.out.println(v + ": Predicted: " + predicted); // assume 1
			
			
			v = new SparseVector(4, new int[] {0, 1, 2, 3}, new double[] {-0.333333, -0.666667, -0.0847458, -0.25});
			predicted = lrModel.predict(v);
			System.out.println(v + ": Predicted: " + predicted); // assume 2
			
			v = new SparseVector(4, new int[] {0, 1, 2, 3}, new double[] {-0.222222, -0.583333, 0.355932, 0.583333});
			predicted = lrModel.predict(v);
			System.out.println(v + ": Predicted: " + predicted); // assume 0

		
			v = new SparseVector(4, new int[] {0, 1, 2, 3}, new double[] {-0.2222, 0.6, -0.8612, -0.833});
			predicted = lrModel.predict(v);
			System.out.println(v + ": Predicted: " + predicted); // new value assume 1
		}
		
		
	}




}
