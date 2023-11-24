package slowport;

import java.time.*;

public class DayCriterion extends Criterion {
	DayOfWeek day;

	public DayCriterion(DayOfWeek day) {
		this.day = day;
	}

	public boolean validate(Session session){
		return true; /// TODO
	}
}
