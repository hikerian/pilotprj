package baeldung.kmeans.client;

import java.util.List;


public class Artist {
	private String name;
	private long playcount;
	private long listeners;
	private String mbid;
	private String url;
	private int streamable;
	private List<Image> image;
	
	
	public Artist() {
	}

	public Artist(String name, long playcount, long listeners, String mbid, String url, int streamable,
			List<Image> image) {
		super();
		this.name = name;
		this.playcount = playcount;
		this.listeners = listeners;
		this.mbid = mbid;
		this.url = url;
		this.streamable = streamable;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getPlaycount() {
		return playcount;
	}

	public void setPlaycount(long playcount) {
		this.playcount = playcount;
	}

	public long getListeners() {
		return listeners;
	}

	public void setListeners(long listeners) {
		this.listeners = listeners;
	}

	public String getMbid() {
		return mbid;
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStreamable() {
		return streamable;
	}

	public void setStreamable(int streamable) {
		this.streamable = streamable;
	}

	public List<Image> getImage() {
		return image;
	}

	public void setImage(List<Image> image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Artist [name=" + name + ", playcount=" + playcount + ", listeners=" + listeners + ", mbid=" + mbid
				+ ", url=" + url + ", streamable=" + streamable + ", image=" + image + "]";
	}



}
