package baeldung.kmeans.client;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Artists {
	private List<Artist> artist;
	@JsonProperty("@attr")
	private Map<String, String> attr;
	
	
	public Artists() {
	}
	
	public Artists(List<Artist> artist, Map<String, String> attr) {
		this.artist = artist;
		this.attr = attr;
	}

	public List<Artist> getArtist() {
		return artist;
	}

	public void setArtist(List<Artist> artist) {
		this.artist = artist;
	}

	public void addArtist(Artist artist) {
		this.artist.add(artist);
	}
	
	public Map<String, String> getAttr() {
		return attr;
	}


	public void setAttr(Map<String, String> attr) {
		this.attr = attr;
	}

	@Override
	public String toString() {
		return "Artists [artist=" + artist + ", attr=" + attr + "]";
	}



}
