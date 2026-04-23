package org.hddbscan.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="cluster_model")
public class ClusterModel {
	@Id
	@Column(name="model_id", columnDefinition="BIGINT UNSIGNED", nullable=false)
	private long modelId;
	
	@Column(name="model_desc", columnDefinition="VARCHAR(1000)", nullable=false)
	private String modelDesc;
	
	@Column(name="model_json", columnDefinition="MEDIUMTEXT", nullable=false)
	private String modelJson;
	
	
	public ClusterModel() {
		
	}

	public ClusterModel(long modelId, String modelDesc, String modelJson) {
		super();
		this.modelId = modelId;
		this.modelDesc = modelDesc;
		this.modelJson = modelJson;
	}

	public long getModelId() {
		return modelId;
	}

	public void setModelId(long modelId) {
		this.modelId = modelId;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getModelJson() {
		return modelJson;
	}

	public void setModelJson(String modelJson) {
		this.modelJson = modelJson;
	}

	@Override
	public int hashCode() {
		return Objects.hash(modelDesc, modelId, modelJson);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterModel other = (ClusterModel) obj;
		return Objects.equals(modelDesc, other.modelDesc) && modelId == other.modelId
				&& Objects.equals(modelJson, other.modelJson);
	}

	@Override
	public String toString() {
		return "ClusterModel [modelId=" + modelId + ", modelDesc=" + modelDesc + ", modelJson=" + modelJson + "]";
	}



}
