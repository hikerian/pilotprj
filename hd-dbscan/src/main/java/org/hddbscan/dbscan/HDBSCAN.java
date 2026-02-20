package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
		
		try {
			model.print(System.out, ",");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.log.info((System.currentTimeMillis() - startTime) + "ms spent!");

		return model;

	}
	
//	private List<DBSCANCluster> fit(List<DataRow> inputValues, int colIdx, int minPts) {
//		List<DBSCANCluster> resultList = new ArrayList<>();
//		Set<DataRow> visited = new HashSet<>();
//		
//		for(DataRow p : inputValues) {
//			if(visited.contains(p) == false) {
//				visited.add(p);
//				List<DataRow> neighbours = this.getNeighbours(p, inputValues, colIdx);
//				
//				if(neighbours.size() >= minPts) {
//					int idx = 0;
//					while(neighbours.size() > idx) {
//						DataRow r = neighbours.get(idx);
//						if(visited.contains(r) == false) {
//							visited.add(r);
//							List<DataRow> individualNeighbours = this.getNeighbours(r, inputValues, colIdx);
//							if(individualNeighbours.size() >= minPts) {
//								neighbours = this.mergeRightToLeft(neighbours, individualNeighbours);
//							}
//						}
//						
//						idx++;
//					}
//					resultList.add(new DBSCANCluster(neighbours));
//				}
//			}
//		}
//		
//		return resultList;
//	}
	private List<DBSCANCluster> fit(List<DataRow> inputValues, int colIdx, int minPts) {
		List<DBSCANCluster> resultList = new ArrayList<>();
		Set<DataRow> visited = new HashSet<>();
		
		for(DataRow p : inputValues) {
			if(visited.contains(p) == false) {
				visited.add(p);
				Neighbours neighbours = this.getNeighbours(p, inputValues, colIdx);
				
				if(neighbours.size() >= minPts) {
					int idx = 0;
					while(neighbours.size() > idx) {
						DataRow r = neighbours.get(idx);
						if(visited.contains(r) == false) {
							visited.add(r);
							Neighbours individualNeighbours = this.getNeighbours(r, inputValues, colIdx);
							if(individualNeighbours.size() >= minPts) {
								neighbours.addAll(individualNeighbours);
							}
						}
						
						idx++;
					}
					resultList.add(new DBSCANCluster(neighbours.getDatas()));
				}
			}
		}
		
		return resultList;
	}
	
//	private List<DataRow> getNeighbours(DataRow p, List<DataRow> inputValues, int colIdx) {
//		Distance distance = this.metadata.getEps(colIdx);
//		
//		List<DataRow> neighbours = new ArrayList<>();
//		for(DataRow candidate : inputValues) {
//			if(distance.isNeighbours(p.getData(colIdx), candidate.getData(colIdx))) {
//				neighbours.add(candidate);
//			}
//		}
//		
//		return neighbours;
//	}
	private Neighbours getNeighbours(DataRow p, List<DataRow> inputValues, int colIdx) {
		Distance distance = this.metadata.getEps(colIdx);
		
		Neighbours neighbours = new Neighbours();
		for(DataRow candidate : inputValues) {
			if(distance.isNeighbours(p.getData(colIdx), candidate.getData(colIdx))) {
				neighbours.add(candidate);
			}
		}
		
		return neighbours;
	}
	
//	private <V> List<V> mergeRightToLeft(List<V> neighbours1, List<V> neighbours2) {
//		for(V p : neighbours2) {
//			if(neighbours1.contains(p) == false) {
//				neighbours1.add(p);
//			}
//		}
//		
//		return neighbours1;
//	}
	
	/**
	 * 이웃에 중복을 제거하여 포함할 때 List의 <code>for(V p : neighbours2) if(neighbours1.contains(p) == false) neighbours1.add(p);</code>
	 * 방식을 사용하면 호출횟수가 많고 성능이 어마어마하게 느려져서 이웃에 index를 추가하여 성능을 개선하기 위한 클래스.
	 * 어마어마하게 빠라짐.
	 */
	private static class Neighbours {
		private static final Object PRESENT = new Object();
		private final HashMap<String, Object> index = new HashMap<>();
		private final List<DataRow> datas = new ArrayList<>();
		
		
		Neighbours() {
		}
		
		public void add(DataRow data) {
			String id = data.getId();
			if(this.index.put(id, Neighbours.PRESENT) != PRESENT) {
				this.datas.add(data);
			}
		}
		
		public int size() {
			return this.datas.size();
		}
		
		public DataRow get(int idx) {
			return this.datas.get(idx);
		}
		
		public void addAll(Neighbours other) {
			other.datas.stream().forEach((data) -> add(data));
		}
		
		public List<DataRow> getDatas() {
			return this.datas;
		}

	}


}
