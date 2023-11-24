package slowport;

import java.util.List;
import java.time.DayOfWeek;

public class DayInclCriterion extends DayCriterion {
	public DayInclCriterion(DayOfWeek day){
		super(day);
	}

	public boolean validate(Session session) {
		return true; /// TODO
	}
}
