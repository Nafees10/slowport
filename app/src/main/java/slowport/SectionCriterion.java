import java.util.List;

public class SectionCriterion extends Criterion {

    private String section;

    public SectionCriterion(String section) {
        this.section = section;
    }
        public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
}
