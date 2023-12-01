package slowport.rater;

import java.util.*;
import java.time.*;
import slowport.common.*;

public class ConsistencyRater extends Rater{
	private static final int MAGIC_NUMBER = 6 * 60;
	public int rate(Timetable timetable){
		if (timetable.getSessions().size() == 0)
			return 0;
		long earliestStart = -1, earliestEnd = -1,
				 latestStart = -1, latestEnd = -1;
		for (DayOfWeek day : DayOfWeek.values()){
			List<Session> sessions = timetable.getSessions(day);
			if (sessions == null || sessions.size() == 0)
				continue;
			Session first = sessions.get(0),
							last = sessions.get(sessions.size() - 1);
			long startTime = first.getTime().toSecondOfDay(),
					 endTime = last.getTime().toSecondOfDay() +
						 last.getDuration().toSeconds();
			if (earliestStart == -1 || latestEnd == -1){
				latestStart = earliestStart = startTime;
				latestEnd = earliestEnd = endTime;
				continue;
			}
			latestStart = Math.max(latestStart, startTime);
			earliestStart = Math.min(earliestStart, startTime);
			latestEnd = Math.max(latestEnd, endTime);
			earliestEnd = Math.min(earliestEnd, endTime);
		}
		long diff = (latestStart - earliestStart) + (latestEnd - earliestEnd);
		diff /= 60; // diff in minutes
		return (int)(diff * 1000 / MAGIC_NUMBER);
	}
}
