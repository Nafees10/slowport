package slowport.rater;

import java.util.*;
import java.time.*;
import slowport.common.*;

public class ConsistencyRater extends Rater{
	public long rate(Timetable timetable){
		if (timetable.getSessions().size() == 0)
			return 0;
		Map<DayOfWeek, Long> startTimes = new HashMap<>(),
			endTimes = new HashMap<>();
		long startSum = 0, endSum = 0, n = 0;
		for (DayOfWeek day : DayOfWeek.values()){
			List<Session> sessions = timetable.getSessions(day);
			if (sessions == null || sessions.size() == 0)
				continue;
			Session first = sessions.get(0),
							last = sessions.get(sessions.size() - 1);
			long startTime = first.getTime().toSecondOfDay(),
					 endTime = last.getTime().toSecondOfDay() +
						 last.getDuration().toSeconds();
			startTimes.put(day, startTime);
			endTimes.put(day, endTime);
			n ++;
			startSum += startTime;
			endSum += endTime;
		}
		if (n == 0)
			return 0;
		long startAvg = startSum / n, endAvg = endSum / n;
		// now just sum up difference from mean
		long inconsistency = 0;
		for (DayOfWeek day : DayOfWeek.values()){
			if (!startTimes.containsKey(day) || endTimes.containsKey(day))
				continue;
			inconsistency += Math.abs(startTimes.get(day) - startAvg);
			inconsistency += Math.abs(endTimes.get(day) - endAvg);
		}
		return inconsistency;
	}
}
