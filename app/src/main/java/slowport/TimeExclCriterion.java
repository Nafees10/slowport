package slowport;

import java.time.LocalTime;
import java.util.*;

public abstract class TimeExclCriterion extends TimeCriterion{
	public TimeExclCriterion(LocalTime start, LocalTime end) {
		super(start, end);
	}

	public boolean validate(Session session) {
		return true; // TODO
	}
}
