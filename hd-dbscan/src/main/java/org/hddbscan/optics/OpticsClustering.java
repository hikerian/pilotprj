package org.hddbscan.optics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class OpticsClustering {
	private final double maxRadius; // Epsilon max
	private final int minPts;
	
	
	public OpticsClustering(double maxRadius, int minPts) {
		this.maxRadius = maxRadius;
		this.minPts = minPts;
	}
	
	public List<DataPoint> computeOrder(List<DataPoint> dataset) {
		List<DataPoint> orderedList = new ArrayList<>();
		
		for(DataPoint point : dataset) {
			if(point.isProcessed()) continue;
			
			List<DataPoint> neighbors = this.getNeighbors(point, dataset);
			point.setProcessed(true);
			orderedList.add(point);
			
			double coreDist = this.calculateCoreDistance(point, neighbors);
			if(coreDist != Double.MAX_VALUE) {
				point.setCoreDistance(coreDist);
				
				// Priority Queue ordered by lowest reachability distance
				PriorityQueue<DataPoint> seeds = new PriorityQueue<>(
						Comparator.comparingDouble(p -> p.getReachabilityDistance()));
				
				this.updateSeeds(neighbors, point, seeds);
				
				while(!seeds.isEmpty()) {
					DataPoint currentSeed = seeds.poll();
					if(currentSeed.isProcessed()) continue;
					
					List<DataPoint> seedNeighbors = this.getNeighbors(currentSeed, dataset);
					currentSeed.setProcessed(true);
					orderedList.add(currentSeed);
					
					double seedCoreDist = this.calculateCoreDistance(currentSeed, seedNeighbors);
					if(seedCoreDist != Double.MAX_VALUE) {
						currentSeed.setCoreDistance(seedCoreDist);
						this.updateSeeds(seedNeighbors, currentSeed, seeds);
					}
				}
			}
		}
		return orderedList;
	}
	
	private List<DataPoint> getNeighbors(DataPoint center, List<DataPoint> dataset) {
        List<DataPoint> neighbors = new ArrayList<>();
        for (DataPoint point : dataset) {
            if (center.distanceTo(point) <= this.maxRadius) {
                neighbors.add(point);
            }
        }
        return neighbors;
	}
	
	private double calculateCoreDistance(DataPoint point, List<DataPoint> neighbors) {
        if (neighbors.size() < this.minPts) return Double.MAX_VALUE;
        
        // Sort distances to find the distance to the minPts-th neighbor
        List<Double> distances = new ArrayList<>();
        for (DataPoint neighbor : neighbors) {
            distances.add(point.distanceTo(neighbor));
        }
        distances.sort(Double::compareTo);
        return distances.get(this.minPts - 1);
	}
	
    private void updateSeeds(List<DataPoint> neighbors, DataPoint center, PriorityQueue<DataPoint> seeds) {
        for (DataPoint neighbor : neighbors) {
            if (neighbor.isProcessed()) continue;

            // Reachability distance = max(coreDistance of center, distance from center to neighbor)
            double newReachability = Math.max(center.getCoreDistance(), center.distanceTo(neighbor));

            if (neighbor.getReachabilityDistance() == Double.MAX_VALUE) {
                neighbor.setReachabilityDistance(newReachability);
                seeds.add(neighbor);
            } else if (newReachability < neighbor.getReachabilityDistance()) {
                seeds.remove(neighbor); // Re-index priority queue
                neighbor.setReachabilityDistance(newReachability);
                seeds.add(neighbor);
            }
        }
    }
    
    public static void main(String[] args) {
        List<DataPoint> points = new ArrayList<>();
        points.add(new DataPoint(new double[]{1.0, 1.0}));
        points.add(new DataPoint(new double[]{1.2, 1.1}));
        points.add(new DataPoint(new double[]{1.0, 1.3}));
        points.add(new DataPoint(new double[]{10.0, 10.0})); // Outlier / Noise
        points.add(new DataPoint(new double[]{5.0, 5.0}));
        points.add(new DataPoint(new double[]{5.1, 4.9}));

        OpticsClustering optics = new OpticsClustering(3.0, 2);
        List<DataPoint> resultOrder = optics.computeOrder(points);

        System.out.println("Processing Execution Sequence:");
        for (DataPoint p : resultOrder) {
            System.out.printf("Point: [%.1f, %.1f] -> Core Dist: %.2f, Reachability Dist: %.2f\n",
                    p.getCoordinates()[0], p.getCoordinates()[1], 
                    p.getCoreDistance() == Double.MAX_VALUE ? -1.0 : p.getCoreDistance(),
                    p.getReachabilityDistance() == Double.MAX_VALUE ? -1.0 : p.getReachabilityDistance());
        }
    }
    

    
    
    
}
