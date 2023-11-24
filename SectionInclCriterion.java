import java.util.List;

public class SectionInclCriterion extends SectionCriterion {
    public SectionInclCriterion(String section) {
        super(section);
    }
        public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
}
