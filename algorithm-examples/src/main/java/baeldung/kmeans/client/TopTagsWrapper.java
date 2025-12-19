package baeldung.kmeans.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TopTagsWrapper {
	private TopTags toptags;
	
	
	public TopTagsWrapper() {
	}
	
	public TopTagsWrapper(TopTags toptags) {
		this.toptags = toptags;
	}

	public TopTags getToptags() {
		return toptags;
	}

	public void setToptags(TopTags toptags) {
		this.toptags = toptags;
	}
	
	public Map<String, Double> all() {
		Map<String, Double> tagScore = new HashMap<>();
		
		List<TopTag> topTags = this.toptags.getTag();
		topTags.forEach(topTag -> tagScore.put(topTag.getName(), Integer.valueOf(topTag.getCount()).doubleValue()));
		
		return tagScore;
	}

	@Override
	public String toString() {
		return "TopTagsWrapper [toptags=" + toptags + "]";
	}
	
	

}
