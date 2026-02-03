package dbscan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DBSCANTest {
	
    public static void testClustering() throws Exception {
        
        Random random = new Random(4522);
        
        List<Number> numbers = new ArrayList<Number>();
        
        for(int i = 0; i < 1000; i++) {
        	numbers.add(random.nextInt(1000));
        }
        
        int minCluster = 5;
        double maxDistance = 2;
        
        DBSCANClusterer<Number> clusterer = new DBSCANClusterer<Number>(numbers, minCluster, maxDistance, new DistanceMetricNumbers());
        List<List<Number>> result = clusterer.performClustering();
        
        System.out.println("Number of clusters: " + result.size());
        System.out.println("Clusters: " + result);
    }

	public static void main(String[] args) throws Exception {
		DBSCANTest.testClustering();

	}

}
