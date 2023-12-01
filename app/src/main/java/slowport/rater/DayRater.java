package slowport.rater;

import java.time.*;
import java.util.*;
import slowport.common.*;

public class DayRater extends Rater{
	public int rate(List<Session> sessions){
		Map<DayOfWeek, Boolean> dayStatus = new HashMap<>();
		for (Session session : sessions)
			dayStatus.put(session.getDay(), true);
		int days = dayStatus.size();
		if (days > 7) // HOW??
			return 0;
		days = 7 - days;
		return days * (1000 / 7);
	}
}
