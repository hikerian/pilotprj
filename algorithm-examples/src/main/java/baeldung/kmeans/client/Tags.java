package baeldung.kmeans.client;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Tags {
	private List<Tag> tag;
	@JsonProperty("@attr")
	private Map<String, String> attr;
	
	
	public Tags() {
	}
	
	public Tags(List<Tag> tag, Map<String, String> attr) {
		this.tag = tag;
		this.attr = attr;
	}

	public List<Tag> getTag() {
		return tag;
	}

	public void setTag(List<Tag> tag) {
		this.tag = tag;
	}
	
	public void add(Tag tag) {
		this.tag.add(tag);
	}

	public Map<String, String> getAttr() {
		return attr;
	}


	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}

	@Override
	public String toString() {
		return "Tags [tag=" + tag + ", attr=" + attr + "]";
	}

	
	

}
