import java.util.List;

public class CourseExclCriterion extends CourseCriterion {
    public CourseExclCriterion(String course) {
        super(course);
    }
        public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
}
