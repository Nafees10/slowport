package slowport.common;

import java.util.*;
import java.time.*;

public class Timetable{
	private List<Session> sessions;
	private Map<String, Set<String>> overlaps;
	private List<String> courses;
	private Map<String, Set<String>> sections;

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

	private void findOverlaps(){
		for (Session session : sessions){
			for (Session other : sessions){
				if (!session.overlaps(other))
					continue;
				// VERY BAD! overlapping pieces of sessions
				String key = session.getName() + "\t" + session.getSection();
				String val = other.getName() + "\t" + other.getSection();
				if (!overlaps.containsKey(key))
					overlaps.put(key, new HashSet<>());
				overlaps.get(key).add(val);
			}
		}
	}

	private void findCoursesSections(){
		for (Session session : sessions){
			String course = session.getName();
			String section = session.getSection();
			if (!sections.containsKey(course)){
				courses.add(course);
				sections.put(course, new HashSet<>());
			}
			if (!sections.get(course).contains(section))
				sections.get(course).add(section);
		}
	}

	public Timetable(List<Session> sessions){
		this.sessions = new ArrayList<>(sessions);
		this.overlaps = new HashMap<>();
		this.courses = new ArrayList<>();
		this.sections = new HashMap<>();
		sort(this.sessions);
		findCoursesSections();
		findOverlaps();
	}

	public Timetable(Timetable timetable){
		this.sessions = new ArrayList<>(timetable.sessions);
		this.overlaps = new HashMap<>(timetable.overlaps);
		this.courses = new ArrayList<>(timetable.courses);
		this.sections = new HashMap<>(timetable.sections);
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

	public List<String> getCourses(){
		return courses;
	}

	public Set<String> getSections(String course){
		if (sections.containsKey(course))
			return sections.get(course);
		return null;
	}

	public boolean clashes(String courseA, String sectionA,
			String courseB, String sectionB){
		String a = courseA + "\t" + sectionA,
					 b = courseB + "\t" + sectionB;
		return overlaps.containsKey(a) && overlaps.get(a).contains(b);
	}
}
