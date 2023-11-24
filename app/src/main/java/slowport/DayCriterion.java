import java.time.DayOfWeek;
import java.util.List;
public class DayCriterion extends Criterion {
    DayOfWeek day;

    public DayCriterion(DayOfWeek day) {
        this.day = day;
    }
    public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
}
