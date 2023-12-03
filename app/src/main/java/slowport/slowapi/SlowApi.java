package slowport.slowapi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SlowApi {
	private final static String apiEndpoint = "https://nafees.digital/slowport/";

	public static List<String> getVersions() {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(apiEndpoint + "versions"))
				.build();
		try {
			HttpResponse<String> response = client.send(request,
					HttpResponse.BodyHandlers.ofString());
			return List.of(response.body().split("\n"));
		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	public static String getTimetable(String version) {
		String endpoint = apiEndpoint + version;
		return sendHttpGetRequest(endpoint);
	}

	public static String getMakeupTimetable(String version) {
		String endpoint = apiEndpoint + version + "-makeup";
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
			return response.body();
		} catch (Exception e) {
			e.printStackTrace();
			return "Request failed for " + endpoint;
		}
	}
}
