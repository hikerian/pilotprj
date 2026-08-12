package spring.ai.ollama.dto;

import java.util.List;
import java.util.Objects;


public class Hotel {
	// 도시 이름
	private String city;
	
	// 호텔 이름 목록
	private List<String> names;
	
	
	public Hotel() {
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	@Override
	public String toString() {
		return "Hotel [city=" + city + ", names=" + names + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(city, names);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hotel other = (Hotel) obj;
		return Objects.equals(city, other.city) && Objects.equals(names, other.names);
	}


}
