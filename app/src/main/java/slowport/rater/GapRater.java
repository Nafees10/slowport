package slowport.rater;

import java.util.*;
import java.time.*;
import slowport.common.*;

public class GapRater extends Rater{
	public long rate(Timetable timetable){
		long gaps = 0; /// total gaps, in seconds
		for (DayOfWeek day : DayOfWeek.values()){
			List<Session> sessions = timetable.getSessions(day);
			if (sessions == null || sessions.size() == 0)
				continue;
			long time = sessions.get(0).getTime().toSecondOfDay() +
				sessions.get(0).getDuration().toSeconds();
			Session prev = sessions.get(0);
			for (int i = 1; i < sessions.size(); i ++){
				Session curr = sessions.get(i);
				if (curr.getTime().isAfter(prev.getTime()))
					gaps += time - curr.getTime().toSecondOfDay();
				time = curr.getTime().toSecondOfDay() +
					curr.getDuration().toSeconds();
			}
		}
		return gaps;
	}
}
