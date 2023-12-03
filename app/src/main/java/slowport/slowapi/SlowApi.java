import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
package slowapi;

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
       
    }

    // Method to get makeup timetable
    public static String getMakeupTimetable() {
        
    }
}
