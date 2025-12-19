package baeldung.kmeans.client;


public class Tag {
	private String name;
	private String url;
	private long reach;
	private long taggings;
	private int streamable;
	private Wiki wiki;
	
	
	public Tag() {
	}

	public Tag(String name, String url, long reach, long taggings, int streamable, Wiki wiki) {
		super();
		this.name = name;
		this.url = url;
		this.reach = reach;
		this.taggings = taggings;
		this.streamable = streamable;
		this.wiki = wiki;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getReach() {
		return reach;
	}

	public void setReach(long reach) {
		this.reach = reach;
	}

	public long getTaggings() {
		return taggings;
	}

	public void setTaggings(long taggings) {
		this.taggings = taggings;
	}

	public int getStreamable() {
		return streamable;
	}

	public void setStreamable(int streamable) {
		this.streamable = streamable;
	}

	public Wiki getWiki() {
		return wiki;
	}

	public void setWiki(Wiki wiki) {
		this.wiki = wiki;
	}

	@Override
	public String toString() {
		return "Tag [name=" + name + ", url=" + url + ", reach=" + reach + ", taggings=" + taggings + ", streamable="
				+ streamable + ", wiki=" + wiki + "]";
	}


}
