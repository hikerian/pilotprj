package baeldung.kmeans.client;

import java.util.ArrayList;
import java.util.List;


public class ArtistsWrapper {
	private Artists artists;

	
	public ArtistsWrapper() {
	}

	public ArtistsWrapper(Artists artists) {
		super();
		this.artists = artists;
	}

	public Artists getArtists() {
		return artists;
	}

	public void setArtists(Artists artists) {
		this.artists = artists;
	}
	
	public List<String> all() {
		List<String> names = new ArrayList<>();
		
		List<Artist> artists = this.artists.getArtist();
		artists.forEach(artist -> names.add(artist.getName()));
		
		return names;
	}

	@Override
	public String toString() {
		return "TopArtists [artists=" + artists + "]";
	}
	

}
