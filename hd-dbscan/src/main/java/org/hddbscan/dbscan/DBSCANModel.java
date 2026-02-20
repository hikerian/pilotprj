package org.hddbscan.dbscan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hddbscan.dbscan.feature.ComputableFeature;
import org.hddbscan.dbscan.feature.Distance;


public class DBSCANModel {
	
	public static class DBSCANGroup {
		private String id;
		private List<DBSCANRange> rangeList = new ArrayList<>();
		private List<DataRow> dataList;
		
		
		public DBSCANGroup(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}
		
		void addRange(DBSCANRange range) {
			this.rangeList.add(range);
		}
		
		void setDataRowList(List<DataRow> dataList) {
			this.dataList = dataList;
		}
		
		public boolean hasDataRowId(String id) {
			return this.dataList.stream().anyMatch((row)-> {
				return id.equals(row.getId());
			});
		}
		
		public String getDataRowIds() {
			return String.join(","
					, this.dataList.stream().map((dataRow)-> '"' + dataRow.getId() + '"').collect(Collectors.toList()));
		}
		
		public boolean isAcceptable(DataRow data) {
			for(int i = 0; i < this.rangeList.size(); i++) {
				ComputableFeature value = data.getData(i);
				DBSCANRange range = this.rangeList.get(i);
				
				if(value.lessThan(range.getMin())
						|| value.greaterThan(range.getMax())) {
					return false;
				}
			}
			
			return true;
		}

		@Override
		public String toString() {
			return "DBSCANGroup [id=" + this.id + ", rangeList=" + this.rangeList + "\n, dataList=" + this.dataList + "]\n\n";
		}
		
		public void print(Appendable out, String delimiter) throws IOException {
			out.append(this.id).append(delimiter)
			   .append(String.join(delimiter, this.rangeList.stream().map((value)-> "{min:" + value.getMin() + ",max:" + value.getMax() + "}").toList()))
			   .append("\n");
			
			for(DataRow dataRow : this.dataList) {
				out.append(this.id).append(delimiter);
				dataRow.print(out, delimiter);
			}
			out.append('\n');
		}
		
		public void printRange(Appendable out, String delimiter) throws IOException {
			out.append(
				String.join(delimiter, this.rangeList.stream().map(
						(value)-> "{min:" + value.getMin() + ",max:" + value.getMax() + "}"
						).toList())
			);
		}
		
	}

	
	private List<String> labels = new ArrayList<>();
	private int minPts;
	private Distance[] epsList;
	
	private List<DBSCANGroup> groups = new ArrayList<>();
	
	
	public DBSCANModel() {
	}
	
	public List<DBSCANGroup> predict(DataRow data) {
		List<DBSCANGroup> acceptGroups = this.groups.stream().filter((group) -> group.isAcceptable(data)).collect(Collectors.toList());
		
		return acceptGroups;
	}
	
	public void setMetadata(DBSCANMetadata metadata) {
		this.minPts = metadata.getMinPts();
		this.epsList = new Distance[metadata.getEpsCount()];
		for(int i = 0; i < this.epsList.length; i++) {
			this.epsList[i] = metadata.getEps(i);
		}
	}
	
	public int getMinPts() {
		return this.minPts;
	}
	
	public void setLabels(List<String> labelList) {
		this.labels.addAll(labelList);
	}
	
	public void addGroup(List<DBSCANCluster> groupCluster) {		
		for(DBSCANCluster cluster : groupCluster) {
			this.addGroup(cluster);
		}
	}
	
	public void addGroup(DBSCANCluster groupCluster) {
		final int colSize = this.labels.size();
		
		DBSCANGroup group = new DBSCANGroup("group-" + (this.groups.size() + 1));
		this.groups.add(group);

		for(int i = 0; i < colSize; i++) {
			DBSCANRange range = groupCluster.getRange(i);
			group.addRange(range);
		}
		group.setDataRowList(new ArrayList<>(groupCluster.getDataList()));
	}
	
	public int getGroupCount() {
		return this.groups.size();
	}
	
	public List<DBSCANGroup> getGroups() {
		return new ArrayList<DBSCANGroup>(this.groups);
	}

	@Override
	public String toString() {
		return "DBSCANModel [labels=" + labels + ", groups=\n" + groups + "]";
	}
	
	public void print(Appendable out, String delimiter) throws IOException {
		out.append("DBSCANModel:\n")
			.append("minPts:").append(String.valueOf(this.minPts)).append(", epsList:").append(
					String.join(delimiter, Arrays.stream(this.epsList).map((eps)->String.valueOf(eps)).toArray((size)->new String[size]))
					).append('\n')
			.append("groupId").append(delimiter).append("id").append(delimiter)
			.append(String.join(delimiter, this.labels)).append('\n');
		for(DBSCANGroup group : this.groups) {
			group.print(out, delimiter);
		}
		
	}



}
