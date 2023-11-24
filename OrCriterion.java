import java.util.List;
public class OrCriterion extends Criterion {
    private List<Criterion> criteria;

    public OrCriterion(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    public boolean validdate(Session session) {
        for (Criterion criterion : criteria) {
            if (criterion.validdate(session)) {
                return true;
            }
        }
        return false;
    }
    public boolean validdate(List<Session> sessions) {
        for (Criterion criterion : criteria) {
            if (criterion.validdate(sessions)) {
                return true;
            }
        }
        return false;
    }

    
}
