package slowport.filter;

import java.util.*;
import slowport.common.*;
import slowport.filter.*;

public class Combinator {
	private Timetable timetable;

	public Combinator(Timetable timetable) {
		this.timetable = timetable;
	}

	private List<Timetable> result;
	private Map<String, Set<String>> sections;
	private HashMap<String, String> picks;

	private void commit(){
		List<Session> subTable = new ArrayList<>();
		for (Session session : timetable.getSessions()){
			if (picks.containsKey(session.getName()) &&
					picks.get(session.getName()).equals(session.getSection())){
				subTable.add(session);
				System.out.println("picked " + session.toString());
			}
		}
		result.add(new Timetable(subTable));
		System.out.println("\nadios\n");
	}

	private void genComb(List<String> courses, int index){
		if (index >= courses.size())
			return;
		boolean isLeaf = index + 1 == courses.size();
		String course = courses.get(index);
		for (String section : sections.get(course)){
			// does it clash?
			boolean clashes = false;
			for (String pickedCourse : picks.keySet()){
				String pickedSection = picks.get(pickedCourse);
				if (timetable.clashes(pickedCourse, pickedSection, course, section)){
					clashes = true;
					break;
				}
			}
			if (clashes)
				continue;
			picks.put(course, section);
			if (isLeaf)
				commit();
			else
				genComb(courses, index + 1);
			picks.remove(course);
		}
	}

	public List<Timetable> combinations(Map<String, Set<String>> sections) {
		this.sections = sections;
		this.result = new ArrayList<>();
		this.picks = new HashMap<>();
		List<String> courses = new ArrayList<>(sections.keySet());
		genComb(courses, 0);
		return this.result;
	}
}
