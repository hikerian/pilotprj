package org.hddbscan.dbscan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hddbscan.dbscan.feature.Distance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HDBSCAN {
	private final Logger log = LoggerFactory.getLogger(HDBSCAN.class);
	
	private DBSCANMetadata metadata;
	
	
	public HDBSCAN() {
		this.metadata = new DBSCANMetadata();
	}
	
	public void setMetadata(DBSCANMetadata metadata) {
		this.metadata = metadata;
	}
	
	public DBSCANMetadata getMetadata() {
		return this.metadata;
	}
	
	public DBSCANModel fit(final DataSet inputValues) {
		long startTime = System.currentTimeMillis();
		
		int colCnt = inputValues.getColumnCount();
		int minPts = this.metadata.getMinPts();

		this.log.info("ColCnt: {}, MinPts: {}", colCnt, minPts);

		List<DBSCANCluster> clusterList = new ArrayList<>();
		clusterList.add(new DBSCANCluster(inputValues.getAllRows()));

		for (int i = 0; i < colCnt; i++) {
			List<DBSCANCluster> mergeList = new ArrayList<>();

			for (DBSCANCluster cluster : clusterList) {
				List<DBSCANCluster> newClusterList = this.fit(cluster.getDataList(), i, minPts);

				mergeList.addAll(newClusterList);
			}

			clusterList = mergeList;
		}
		
		// create Model
		DBSCANModel model = new DBSCANModel();
		model.setMetadata(this.metadata);
		model.setLabels(inputValues.getLabels());
		
		clusterList.forEach((cluster)-> model.addGroup(cluster));
		
		
		this.log.info((System.currentTimeMillis() - startTime) + "ms spent!");

		return model;

	}
	
	private List<DBSCANCluster> fit(List<DataRow> inputValues, int colIdx, int minPts) {
		List<DBSCANCluster> resultList = new ArrayList<>();
		Set<DataRow> visited = new HashSet<>();
		
		for(DataRow p : inputValues) {
			if(visited.contains(p) == false) {
				visited.add(p);
				List<DataRow> neighbours = this.getNeighbours(p, inputValues, colIdx);
				
				if(neighbours.size() >= minPts) {
					int idx = 0;
					while(neighbours.size() > idx) {
						DataRow r = neighbours.get(idx);
						if(visited.contains(r) == false) {
							visited.add(r);
							List<DataRow> individualNeighbours = this.getNeighbours(r, inputValues, colIdx);
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
	
	private List<DataRow> getNeighbours(DataRow p, List<DataRow> inputValues, int colIdx) {
		Distance distance = this.metadata.getEps(colIdx);
		
		List<DataRow> neighbours = new ArrayList<>();
		for(DataRow candidate : inputValues) {
			if(distance.isNeighbours(p.getData(colIdx), candidate.getData(colIdx))) {
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


}
