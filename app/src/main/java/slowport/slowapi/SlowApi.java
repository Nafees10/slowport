package slowport.slowapi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SlowApi {
	private final static String apiEndpoint = "http://localhost:8080/slowport/";

	public static List<String> getVersions() {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiEndpoint + "versions.txt"))
				.build();
		try {
			HttpResponse<String> response = client.send(request,
					HttpResponse.BodyHandlers.ofString());
			return List.of(response.body().split("\n"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getTimetable(String version) {
		String endpoint = apiEndpoint + version + ".txt";
		return sendHttpGetRequest(endpoint);
	}

	public static String getMakeupTimetable(String version) {
		String endpoint = apiEndpoint + version + "-makeup.txt";
		return sendHttpGetRequest(endpoint);
	}

	private static String sendHttpGetRequest(String endpoint) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(endpoint))
			.build();
		try {
			HttpResponse<String> response = client.send(request,
					HttpResponse.BodyHandlers.ofString());
			int statusCode = response.statusCode();
			if (statusCode >= 200 && statusCode < 300)
				return response.body();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
