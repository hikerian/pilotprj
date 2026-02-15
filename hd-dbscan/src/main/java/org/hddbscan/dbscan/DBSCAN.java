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
	
	/**
	 * maximum distance of values to be considered as cluster
	 */
	private double eps = 1d;
	
	/**
	 * minimum number of members to consider cluster
	 */
	private int minPts = 2;
	
	
	/**
	 * 
	 * @param eps epsilon maximum distance of values to be considered as cluster.
	 * @param minPts minimum number of members to consider cluster.
	 */
	public DBSCAN(double eps, int minPts) {
		this.eps = eps;
		this.minPts = minPts;
	}
	
	public List<List<Number>> fit(List<Number> inputValues) {
		List<List<Number>> resultList = new ArrayList<>();
		Set<Number> visited = new HashSet<>();
		
		for(Number p : inputValues) {
			if(visited.contains(p) == false) {
				visited.add(p);
				List<Number> neighbours = this.getNeighbours(p, inputValues);
				
				if(neighbours.size() >= this.minPts) {
					int idx = 0;
					while(neighbours.size() > idx) {
						Number r = neighbours.get(idx);
						if(visited.contains(r) == false) {
							visited.add(r);
							List<Number> individualNeighbours = this.getNeighbours(r, inputValues);
							if(individualNeighbours.size() >= this.minPts) {
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
	
	private List<Number> getNeighbours(Number p, List<Number> inputValues) {
		List<Number> neighbours = new ArrayList<>();
		for(Number candidate : inputValues) {
			if(this.distance(p, candidate) <= this.eps) {
				neighbours.add(candidate);
			}
		}
		return neighbours;
	}
	
	private List<Number> mergeRightToLeft(List<Number> neighbours1, List<Number> neighbours2) {
		for(Number p : neighbours2) {
			if(neighbours1.contains(p) == false) {
				neighbours1.add(p);
			}
		}
		return neighbours1;
	}
	
	protected Double distance(Number val1, Number val2) {
		return Math.abs(val1.doubleValue() - val2.doubleValue());
	}
	
	
	public static void main(String[] args) {
        Random random = new Random(4522);
        List<Number> numbers = new ArrayList<Number>();
        
        for(int i = 0; i < 1000; i++) {
        	numbers.add(random.nextInt(1000));
        }
        
        double eps = 2d;
        int minPts = 5;
        
        DBSCAN clusterer = new DBSCAN(eps, minPts);
        List<List<Number>> result = clusterer.fit(numbers);
        
        System.out.println("Number of clusters: " + result.size());
        System.out.println("Clusters: " + result);
	}




}
