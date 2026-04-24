package org.hddbscan.controller;

import java.util.Objects;


public class ModelInfo {
	private long modelId;
	private String createDate;
	private String modelDesc;
	
	
	public ModelInfo(long modelId, String createDate, String modelDesc) {
		super();
		this.modelId = modelId;
		this.createDate = createDate;
		this.modelDesc = modelDesc;
	}

	public long getModelId() {
		return modelId;
	}

	public void setModelId(long modelId) {
		this.modelId = modelId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createDate, modelDesc, modelId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelInfo other = (ModelInfo) obj;
		return Objects.equals(createDate, other.createDate) && Objects.equals(modelDesc, other.modelDesc)
				&& modelId == other.modelId;
	}


}
