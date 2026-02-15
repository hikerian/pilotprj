package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DBSCAN {
	private final Logger log = LoggerFactory.getLogger(DBSCAN.class);
	
//	/**
//	 * maximum distance of values to be considered as cluster
//	 */
//	private double eps = 1d;
//	
//	/**
//	 * minimum number of members to consider cluster
//	 */
//	private int minPts = 2;
	
	
//	/**
//	 * 
//	 * @param eps epsilon maximum distance of values to be considered as cluster.
//	 * @param minPts minimum number of members to consider cluster.
//	 */
//	public DBSCAN(double eps, int minPts) {
//		this.eps = eps;
//		this.minPts = minPts;
//	}
	
	public DBSCAN() {
	}
	
	/**
	 * 
	 * @param inputValues
	 * @param colIdx
	 * @param eps maximum distance of values to be considered as cluster.
	 * @param minPts minimum number of members to consider cluster.
	 * @return
	 */
	public List<DBSCANCluster> fit(DataSet inputValues, int colIdx, double eps, int minPts) {
		List<DBSCANCluster> resultList = new ArrayList<>();
		Set<DataRow> visited = new HashSet<>();
		
		for(DataRow p : inputValues) {
			if(visited.contains(p) == false) {
				visited.add(p);
				List<DataRow> neighbours = this.getNeighbours(p, inputValues, colIdx, eps);
				
				if(neighbours.size() >= minPts) {
					int idx = 0;
					while(neighbours.size() > idx) {
						DataRow r = neighbours.get(idx);
						if(visited.contains(r) == false) {
							visited.add(r);
							List<DataRow> individualNeighbours = this.getNeighbours(r, inputValues, colIdx, eps);
							if(individualNeighbours.size() >= minPts) {
								neighbours = this.mergeRightToLeft(neighbours, individualNeighbours);
							}
						}
						
						idx++;
					}
					resultList.add(new DBSCANCluster(neighbours));
				}
			}
		}
		
		
		return resultList;
	}
	
	private List<DataRow> getNeighbours(DataRow p, DataSet inputValues, int colIdx, double eps) {
		List<DataRow> neighbours = new ArrayList<>();
		for(DataRow candidate : inputValues) {
			if(this.distance(p.getData(colIdx), candidate.getData(colIdx)) <= eps) {
				neighbours.add(candidate);
			}
		}
		return neighbours;
	}
	
	private <V> List<V> mergeRightToLeft(List<V> neighbours1, List<V> neighbours2) {
		for(V p : neighbours2) {
			if(neighbours1.contains(p) == false) {
				neighbours1.add(p);
			}
		}
		return neighbours1;
	}
	
	protected Double distance(Number val1, Number val2) {
		return Math.abs(val1.doubleValue() - val2.doubleValue());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<List<Number>> fit(List<Number> inputValues, double eps, int minPts) {
		List<List<Number>> resultList = new ArrayList<>();
		Set<Number> visited = new HashSet<>();
		
		for(Number p : inputValues) {
			if(visited.contains(p) == false) {
				visited.add(p);
				List<Number> neighbours = this.getNeighbours(p, inputValues, eps);
				
				if(neighbours.size() >= minPts) {
					int idx = 0;
					while(neighbours.size() > idx) {
						Number r = neighbours.get(idx);
						if(visited.contains(r) == false) {
							visited.add(r);
							List<Number> individualNeighbours = this.getNeighbours(r, inputValues, eps);
							if(individualNeighbours.size() >= minPts) {
								neighbours = this.mergeRightToLeft(neighbours, individualNeighbours);
							}
						}
						
						idx++;
					}
					resultList.add(neighbours);
				}
			}
		}
		
		
		return resultList;
	}
	
	private List<Number> getNeighbours(Number p, List<Number> inputValues, double eps) {
		List<Number> neighbours = new ArrayList<>();
		for(Number candidate : inputValues) {
			if(this.distance(p, candidate) <= eps) {
				neighbours.add(candidate);
			}
		}
		return neighbours;
	}
	

	
	
	public static void main(String[] args) {
		DBSCAN.testCase1();
		DBSCAN.testCase2();
	}
	
	private static void testCase2() {
        Random random = new Random(4522);
        
        DataSet dataSet = new DataSet();
        
        for(int i = 0; i < 1000; i++) {
        	DataRow row = new DataRow();
        	dataSet.addRow(row);
        	
        	row.setData(random.nextInt(1000), random.nextInt(1000));
        }
        
        double eps = 2d;
        int minPts = 5;
        
//        DBSCAN clusterer = new DBSCAN(eps, minPts);
        DBSCAN clusterer = new DBSCAN();
        List<DBSCANCluster> result = clusterer.fit(dataSet, 0, eps, minPts);
        
        System.out.println("Number of clusters: " + result.size());
        System.out.println("Clusters: " + result);
	}
	
	private static void testCase1() {
        Random random = new Random(4522);
        List<Number> numbers = new ArrayList<Number>();
        
        for(int i = 0; i < 1000; i++) {
        	numbers.add(random.nextInt(1000));
        }
        
        double eps = 2d;
        int minPts = 5;
        
//        DBSCAN clusterer = new DBSCAN(eps, minPts);
        DBSCAN clusterer = new DBSCAN();
        List<List<Number>> result = clusterer.fit(numbers, eps, minPts);
        
        System.out.println("Number of clusters: " + result.size());
        System.out.println("Clusters: " + result);		
	}




}
