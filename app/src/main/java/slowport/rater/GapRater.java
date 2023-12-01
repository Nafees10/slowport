package slowport.rater;

import java.util.*;
import java.time.*;
import slowport.common.*;

public class GapRater extends Rater{
	// the worse the univeristy at making timetables, the higher MAGIC_NUMBER
	// should be. 8 is really bad btw, normally it should be no more than 4
	private static final int MAGIC_NUMBER = 8,
					SEC_PER_DAY = MAGIC_NUMBER * 60 * 60,
					SEC_PER_WEEK = SEC_PER_DAY * 7;
	public int rate(Timetable timetable){
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
		return (int)(gaps * 1000 / SEC_PER_WEEK);
	}
}
