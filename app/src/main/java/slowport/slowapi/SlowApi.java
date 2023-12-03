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
     
    }

    // Method to get timetable for a specific version
    public static String getTimetable(String version) {
       
    }

    // Method to get makeup timetable
    public static String getMakeupTimetable() {
        
    }
}
