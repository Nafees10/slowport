import java.util.List;

public class SectionExclCriterion extends SectionCriterion {
    public SectionExclCriterion(String section) {
        super(section);
    }
        public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
}
