package slowport;

import java.util.*;
import java.time.*;

public class TimeInclCriterion extends TimeCriterion{
	public TimeInclCriterion(LocalTime start, LocalTime end) {
		super(start, end);
	}

	public boolean validate(Session session) {
		return true; // TODO
	}
}
