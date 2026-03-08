package org.hddbscan.preprocessing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RawCluster<T> {
	private String id;
	private List<T> elementList = new ArrayList<>();
	
	
	public RawCluster(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void add(T element) {
		this.elementList.add(element);
	}
	
	public void addAll(List<T> elementList) {
		this.elementList.addAll(elementList);
	}
	
	public int elementCount() {
		return this.elementList.size();
	}
	
	public T get(int idx) {
		return this.elementList.get(idx);
	}
	
	public List<T> getList() {
		return this.elementList;
	}
	
	public void print(Appendable out, String delimiter) throws IOException {
		out.append("RawCluster:(").append(this.id).append(")\n")
			.append(this.elementList.toString());
	}

}
