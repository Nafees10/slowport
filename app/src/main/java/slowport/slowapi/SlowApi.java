import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
package slowport.slowapi;

public class SlowApi {
    private static String apiEndpoint;

    // Setter for apiEndpoint
    public static void setApiEndpoint(String endpoint) {
        apiEndpoint = endpoint;
    }

    // Getter for apiEndpoint
    public static String getApiEndpoint() {
        return apiEndpoint;
    }

    // Method to get versions
    public static List<String> getVersions() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint + "/versions"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return List.of(response.body().split("\n"));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Method to get timetable for a specific version
    public static String getTimetable(String version) {
        String endpoint = apiEndpoint + "/timetable/" + version;
        return sendHttpGetRequest(endpoint);
    }


    // Method to get makeup timetable
    public static String getMakeupTimetable() {
        String endpoint = apiEndpoint + "/makeup-timetable";
        return sendHttpGetRequest(endpoint);
    }
    //Method to fetch the data body present at that specific endpoint.
    private static String sendHttpGetRequest(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "Request failed for " + endpoint;
        }
    }
}
