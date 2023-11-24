package slowport;

import java.time.DayOfWeek;
import java.util.List;

public abstract class DayCriterion extends Criterion {
	DayOfWeek day;

	public DayCriterion(DayOfWeek day) {
		this.day = day;
	}
}
