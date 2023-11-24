import java.util.List;

public class CourseInCriterion extends CourseCriterion {
    public CourseInCriterion(String course) {
        super(course);
    }
    
    public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
}
