package baeldung.kmeans.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TagsWrapper {
	private Tags tags;
	
	
	public TagsWrapper() {
	}

	public TagsWrapper(Tags tags) {
		super();
		this.tags = tags;
	}

	public Tags getTags() {
		return tags;
	}

	public void setTags(Tags tags) {
		this.tags = tags;
	}
	
	public Set<String> all() {
		Set<String> tagNames = new HashSet<>();
		
		List<Tag> tags = this.tags.getTag();
		tags.stream().forEach(tag -> tagNames.add(tag.getName()));
		
		return tagNames;
	}


	@Override
	public String toString() {
		return "TagsWrapper [tags=" + tags + "]";
	}
	
	
	

}
