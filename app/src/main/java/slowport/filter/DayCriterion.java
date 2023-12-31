package slowport.filter;

import java.time.*;
import slowport.common.*;

public class DayCriterion extends Criterion {
	DayOfWeek day;

	public DayCriterion(DayOfWeek day) {
		this.day = day;
	}

	public boolean validate(Session session){
		if (session == null)
			return false;
		return session.getDay().equals(day);
	}
}
