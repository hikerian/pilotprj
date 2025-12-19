package baeldung.kmeans.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import baeldung.kmeans.Centroid;
import baeldung.kmeans.Distance;
import baeldung.kmeans.Errors;
import baeldung.kmeans.EuclideanDistance;
import baeldung.kmeans.KMeans;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * https://www.last.fm
 * dongilcho/m4fi8vcx)!
 * 
 * API account created
 * Here are the details of your new API account.
 * Application name	AI Example
 * API key	5a8babc9e8fec5c8dfc6059959511d1f
 * Shared secret	e8e7d799cc06446f6a0bc2a82e897702
 * Registered to	dongilcho
 * 
 * API URL: https://ws.audioscrobbler.com
 */
public class LastFmServiceClient {

	
	public static void main(String[] args) {
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://ws.audioscrobbler.com")
				.addConverterFactory(JacksonConverterFactory.create())
				.client(httpClient.build())
				.build();
		
		LastFmService service = retrofit.create(LastFmService.class);

		List<baeldung.kmeans.DataFrame> dataset = new ArrayList<>();
		
		try {
			List<String> artists = new ArrayList<>();
			for(int i = 1; i <= 2; i++) {
				artists.addAll(service.topArtists(i).execute().body().all());
			}
			
			Set<String> tags = service.topTags().execute().body().all();
			
			for(String artist : artists) {
				Map<String, Double> artistTags = service.topTagsFor(artist).execute().body().all();
				
				// Only keep popular tags.
				artistTags.entrySet().removeIf(e -> !tags.contains(e.getKey()));
				
				dataset.add(new baeldung.kmeans.DataFrame(artist, artistTags));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Distance distance = new EuclideanDistance();
		Map<Centroid, List<baeldung.kmeans.DataFrame>> clusters = KMeans.fit(dataset, 7, distance, 1000);
		// Printing the cluster configuration
		clusters.forEach((key, value) -> {
			System.out.println("--------------- CLUSTER ---------------");
			
			// Sorting the coordinates to see the most significant tags first.
			System.out.println(key);
			String members = String.join(", ", value.stream().map(baeldung.kmeans.DataFrame::getDescription).collect(Collectors.toSet()));
			
			System.out.println(members);
			
			System.out.println();
			System.out.println();
		});
		
		/*
		 * Visualization
		 * A few moments ago, our algorithm visualized the cluster of artists in a terminal-friendly way.
		 * If we convert our cluster configuration to JSON and feed it to D3.js, then with a few lines of JavaScript, we’ll have a nice human-friendly Radial Tidy-Tree:
		 * https://observablehq.com/@d3/radial-tidy-tree?collection=@d3/d3-hierarchy
		 * We have to convert our Map<Centroid, List<DataFrame>> to a JSON with a similar schema like this d3.js example.
		 * https://raw.githubusercontent.com/d3/d3-hierarchy/v1.1.8/test/data/flare.json
		 */
		
		// Then, we can run the K-Means algorithm for different values of k and calculate the SSE for each of them:
		Map<Integer, Double> sumOfSquaredErrors = new HashMap<>();
		for(int k = 2; k <= 20; k++) {
			Map<Centroid, List<baeldung.kmeans.DataFrame>> cluster = KMeans.fit(dataset, k, distance, 1000);
			double sse = Errors.sse(cluster, distance);
			
			sumOfSquaredErrors.put(k, sse);
		}
		
		System.out.println(sumOfSquaredErrors);
		
	}

}
