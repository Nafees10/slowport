import java.util.List;
public class AndCriterian extends Criterion {
    private List<Criterion> criteria;

    public AndCriterian(List<Criterion> criteria) {
        this.criteria = criteria;
    }
    
    public boolean validdate(Session session) {
        for (Criterion criterion : criteria) {
            if (!criterion.validdate(session)) {
                return false;
            }
        }
        return true;
    }

    public boolean validdate(List<Session> sessions) {
        for (Criterion criterion : criteria) {
            if (!criterion.validdate(sessions)) {
                return false;
            }
        }
        return true;
    }

}
