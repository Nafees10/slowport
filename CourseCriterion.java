import java.util.List;

public class CourseCriterion {
    private String course;

    public CourseCriterion(String course) {
        this.course = course;
    }
    public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
    
}
