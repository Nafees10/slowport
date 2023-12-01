package slowport.common;

import java.util.*;
import java.time.*;

public class Timetable{
	private List<Session> sessions;

	public static void sort(List<Session> sessions){
		boolean repeat = true;
		while (repeat){
			repeat = false;
			for (int i = 1; i < sessions.size(); i ++){
				Session prev = sessions.get(i - 1),
								curr = sessions.get(i);
				int compare = curr.getDay().compareTo(prev.getDay());
				if (compare > 0){
					// curr day is after prev day
					continue;
				}
				if (compare < 0){ // curr before prev
					// curr day is before prev day
					Session temp = prev;
					sessions.set(i - 1, curr);
					sessions.set(i, temp);
					repeat = true;
					continue;
				}
				// curr same day as prev
				if (!curr.getTime().isBefore(prev.getTime())){
					// curr is after prev, or same time as prev
					continue;
				}
				// curr is before prev
				Session temp = prev;
				sessions.set(i - 1, curr);
				sessions.set(i, temp);
				repeat = true;
			}
		}
	}

	public Timetable(List<Session> sessions){
		this.sessions = new ArrayList<>(sessions);
		sort(this.sessions);
	}

	public List<Session> getSessions(){
		return sessions;
	}

	public List<Session> getSessions(DayOfWeek day){
		List<Session> ret = new ArrayList<>();
		for (Session session : sessions){
			if (session.getDay().equals(day))
				ret.add(session);
		}
		return ret;
	}
}
