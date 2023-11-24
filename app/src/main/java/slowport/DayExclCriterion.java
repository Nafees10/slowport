package slowport;

import java.util.List;
import java.time.DayOfWeek;

public class DayExclCriterion extends DayCriterion {
	public DayExclCriterion(DayOfWeek day) {
		super(day);
	}

	public boolean validate(Session session) {
		return true; /// TODO
	}
}
