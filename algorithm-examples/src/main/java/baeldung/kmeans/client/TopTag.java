package baeldung.kmeans.client;


public class TopTag {
	private String name;
	private String url;
	private int count;
	
	
	public TopTag() {
	}

	public TopTag(String name, String url, int count) {
		super();
		this.name = name;
		this.url = url;
		this.count = count;
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
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "TopTag [name=" + name + ", url=" + url + ", count=" + count + "]";
	}

	



}
