package org.hddbscan.entity;

import java.util.Objects;


public class UiPage {
	private long id;
	private String nm;


	public UiPage() {
	}

	public UiPage(long id, String nm) {
		super();
		this.id = id;
		this.nm = nm;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNm() {
		return nm;
	}

	public void setNm(String nm) {
		this.nm = nm;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nm);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UiPage other = (UiPage) obj;
		return id == other.id && Objects.equals(nm, other.nm);
	}

	@Override
	public String toString() {
		return "UiPage [id=" + id + ", nm=" + nm + "]";
	}


}
