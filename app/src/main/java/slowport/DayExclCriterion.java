import java.util.List;
import java.time.DayOfWeek;
public class DayExclCriterion extends DayCriterion {
    public DayExclCriterion(DayOfWeek day) {
        super(day);
    }   
        public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
}
