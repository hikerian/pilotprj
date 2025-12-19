package baeldung.kmeans.client;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class TopTags {
	private List<TopTag> tag;
	@JsonProperty("@attr")
	private Map<String, String> attr;
	
	
	public TopTags() {
	}


	public TopTags(List<TopTag> tag, Map<String, String> attr) {
		super();
		this.tag = tag;
		this.attr = attr;
	}


	public List<TopTag> getTag() {
		return tag;
	}


	public void setTag(List<TopTag> tag) {
		this.tag = tag;
	}
	
	public void add(TopTag tag) {
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
		return "TopTags [tag=" + tag + ", attr=" + attr + "]";
	}



	
}
