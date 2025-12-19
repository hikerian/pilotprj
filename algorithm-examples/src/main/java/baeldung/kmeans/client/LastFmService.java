package baeldung.kmeans.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * https://ws.audioscrobbler.com/2.0/?method=artist.gettoptags&artist=cher&api_key=5a8babc9e8fec5c8dfc6059959511d1f&format=json
 * https://www.last.fm/api/account/create
 * API account created
 * Here are the details of your new API account.
 * Application name	AI Example
 * API key	5a8babc9e8fec5c8dfc6059959511d1f
 * Shared secret	e8e7d799cc06446f6a0bc2a82e897702
 * Registered to	dongilcho
 */
public interface LastFmService {
	
	@GET("/2.0/?method=chart.gettopartists&api_key=5a8babc9e8fec5c8dfc6059959511d1f&format=json&limit=50")
	Call<ArtistsWrapper> topArtists(@Query("page") int page);
	
	@GET("/2.0/?method=artist.gettoptags&api_key=5a8babc9e8fec5c8dfc6059959511d1f&format=json&limit=20&autocorrect=1")
	Call<TopTagsWrapper> topTagsFor(@Query("artist") String artist);
	
	@GET("/2.0/?method=chart.gettoptags&api_key=5a8babc9e8fec5c8dfc6059959511d1f&format=json&limit=100")
	Call<TagsWrapper> topTags();

}
