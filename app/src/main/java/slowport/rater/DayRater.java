package slowport.rater;

import java.time.*;
import java.util.*;
import slowport.common.*;

public class DayRater extends Rater{
	public int rate(Timetable timetable){
		Map<DayOfWeek, Boolean> dayStatus = new HashMap<>();
		for (Session session : timetable.getSessions())
			dayStatus.put(session.getDay(), true);
		int days = dayStatus.size();
		return days * 1000 / 7;
	}
}
