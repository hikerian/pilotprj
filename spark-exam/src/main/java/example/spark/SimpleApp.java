package example.spark;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;


/**
 * @see https://spark.apache.org/docs/latest/quick-start.html
 */
public class SimpleApp {

	public static void main(String[] args) {
		String logFile = "C:/Temp/lockinglog1747383869427.log";
		SparkSession spark = SparkSession.builder()
				.appName("Simple Application")
				.config("spark.master", "local")
				.getOrCreate();

		Dataset<String> logData = spark.read().textFile(logFile).cache();
		
		long numAs = logData.filter((FilterFunction<String>)s -> s.contains("a")).count();
		long numBs = logData.filter((FilterFunction<String>)s -> s.contains("b")).count();
		
		System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
		
		spark.stop();
	}

}
