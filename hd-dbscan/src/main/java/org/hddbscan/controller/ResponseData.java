package org.hddbscan.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ResponseData implements Serializable {
	private static final long serialVersionUID = 4756828950474846704L;

	private boolean success;
	private String errorCode;
	private final Map<String, Object> payload;
	
	
	public ResponseData() {
		this.payload = new HashMap<>();
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}
	
	public void addPayload(String name, Object value) {
		this.payload.put(name, value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(errorCode, payload, success);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseData other = (ResponseData) obj;
		return Objects.equals(errorCode, other.errorCode) && Objects.equals(payload, other.payload)
				&& success == other.success;
	}

	@Override
	public String toString() {
		return "ResponseData [success=" + success + ", errorCode=" + errorCode + ", payload=" + payload + "]";
	}


}
