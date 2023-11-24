import java.time.LocalTime;
import java.util.List;

public class TimeCriterion  extends Criterion{
   private LocalTime start;
   private LocalTime end;

   public TimeCriterion(LocalTime start, LocalTime end) {
       this.start = start;
       this.end = end;
   }
    public boolean validdate(Session session) {
        return true;
    }
    
    public boolean validdate(List<Session> sessions) {
        return true;
    }
   
}
