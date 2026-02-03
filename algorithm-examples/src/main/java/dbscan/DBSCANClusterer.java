package dbscan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Implementation of density-based clustering algorithm DBSCAN.
 * 
 * Original Publication: 
 * Ester, Martin; Kriegel, Hans-Peter; Sander, Jörg; Xu, Xiaowei (1996). 
 * Simoudis, Evangelos; Han, Jiawei; Fayyad, Usama M., eds. 
 * A density-based algorithm for discovering clusters in large spatial 
 * databases with noise. Proceedings of the Second International Conference 
 * on Knowledge Discovery and Data Mining (KDD-96). AAAI Press. pp. 226-231
 * 
 * Usage:
 * - Identify type of input values.
 * - Implement metric for input value type using DistanceMetric interface.
 * - Instantiate using {@link #DBSCANClusterer(Collection, int, double, DistanceMetric)}.
 * - Invoke {@link #performClustering()}.
 * 
 * See tests and metrics for example implementation and use.
 * 
 * @author <a href="mailto:cf@christopherfrantz.org">Christopher Frantz</a>
 * @version 0.1
 * @see https://github.com/chrfrantz/DBSCAN/blob/master/src/main/java/org/christopherfrantz/dbscan/DBSCANClusterer.java
 *
 * @param <V> Input value element type
 */
public class DBSCANClusterer<V> {
	
	/**
	 * maximum distance of values th be considered as cluster
	 */
	private double epsilon = 1f;
	
	/**
	 * minimum number of members to consider cluster
	 */
	private int minimumNumberOfClusterMembers = 2;
	
	/**
	 * distance metric applied for clustering
	 */
	private DistanceMetric<V> metric = null;
	
	/**
	 * internal list of input values to be clustered
	 */
	private ArrayList<V> inputValues = null;
	
	/**
	 * index maintaining visited points
	 */
	private HashSet<V> visitedPoints = new HashSet<>();
	
	
	
	public DBSCANClusterer(final Collection<V> inputValues, int minNumElements, double maxDistance, DistanceMetric<V> metric) throws DBSCANClusteringException {
		this.setInputValues(inputValues);
		this.setMinimalNumberOfMembersForCluster(minNumElements);
		this.setMaximalDistanceOfClusterMembers(maxDistance);
		this.setDistanceMetric(metric);
		
	}
	
	public void setInputValues(final Collection<V> collection) throws DBSCANClusteringException {
		if(collection == null) {
			throw new DBSCANClusteringException("DBSCAN: List of input values is null.");
		}
		this.inputValues = new ArrayList<V>(collection);
	}
	
	public void setMinimalNumberOfMembersForCluster(final int minimalNumberOfMembers) {
		this.minimumNumberOfClusterMembers = minimalNumberOfMembers;
	}
	
	public void setMaximalDistanceOfClusterMembers(final double maximalDistance) {
		this.epsilon = maximalDistance;
	}
	
	public void setDistanceMetric(final DistanceMetric<V> metric) throws DBSCANClusteringException {
		if(metric == null) {
			throw new DBSCANClusteringException("DBSCAN: Distance metric has not been specified(null).");
		}
		this.metric = metric;
	}
	
    /**
     * Applies the clustering and returns a collection of clusters (i.e., a list
     * of lists of the respective cluster members).
     * 
     * @return Collection of clusters identified as part of the clustering process
     * @throws DBSCANClusteringException 
     */
	public List<List<V>> performClustering() throws DBSCANClusteringException {
        if (this.inputValues == null) {
            throw new DBSCANClusteringException("DBSCAN: List of input values is null.");
        }

        if (this.inputValues.isEmpty()) {
            throw new DBSCANClusteringException("DBSCAN: List of input values is empty.");
        }

        if (this.inputValues.size() < 2) {
            throw new DBSCANClusteringException("DBSCAN: Less than two input values cannot be clustered. Number of input values: " + inputValues.size());
        }

        if (this.epsilon < 0) {
            throw new DBSCANClusteringException("DBSCAN: Maximum distance of input values cannot be negative. Current value: " + epsilon);
        }

        if (this.minimumNumberOfClusterMembers < 2) {
            throw new DBSCANClusteringException("DBSCAN: Clusters with less than 2 members don't make sense. Current value: " + minimumNumberOfClusterMembers);
        }
        
        List<List<V>> resultList = new ArrayList<>();
        
        this.visitedPoints.clear();
        List<V> neighbours = null;
        
        for(V p : this.inputValues) {
        	if(this.visitedPoints.contains(p) == false) {
        		this.visitedPoints.add(p);
                neighbours = this.getNeighbours(p);
        		
        		if(neighbours.size() >= this.minimumNumberOfClusterMembers) {
        			
        			int ind = 0;
        			while(neighbours.size() > ind) {
        				V r = neighbours.get(ind);
        				if(this.visitedPoints.contains(r) == false) {
        					this.visitedPoints.add(r);
        					List<V> individualNeighbours = this.getNeighbours(r);
        					if(individualNeighbours.size() >= this.minimumNumberOfClusterMembers) {
        						neighbours = this.mergeRightToLeftCollection(neighbours, individualNeighbours);
        					}
        				}        				
        				
        				ind++;
        			}
            		resultList.add(neighbours);
        		}
        	}
        }
        
        return resultList;
	}
	
    /**
     * Determines the neighbours of a given input value.
     * 
     * @param inputValue Input value for which neighbours are to be determined
     * @return List of neighbours for a given input value
     * @throws DBSCANClusteringException 
     */
	private List<V> getNeighbours(final V inputValue) throws DBSCANClusteringException {
		List<V> neighbours = new ArrayList<>();
		for(V candidate : this.inputValues) {
			if(this.metric.calculateDistance(inputValue, candidate) <= this.epsilon) {
				neighbours.add(candidate);
			}
		}
		return neighbours;
	}
	
    /**
     * Merges the elements of the right collection to the left one and returns
     * the combination.
     * 
     * @param neighbours1 left collection
     * @param neighbours2 right collection
     * @return Modified left collection
     */
	private List<V> mergeRightToLeftCollection(final List<V> neighbours1, final List<V> neighbours2) {
		for(V tempPt : neighbours2) {
			if(neighbours1.contains(tempPt) == false) {
				neighbours1.add(tempPt);
			}
		}
		return neighbours1;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	





}
