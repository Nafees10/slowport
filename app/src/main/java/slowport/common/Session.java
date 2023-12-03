package slowport.common;

import java.util.*;
import java.time.*;
import java.time.format.*;

public class Session {
	private DayOfWeek day;
	private LocalTime time;
	private Duration duration;
	private String name;
	private String section;
	private String venue;
	private int index;
	private boolean recurring;
	private YearWeek week;

	private static DayOfWeek parseDay(String day) throws Exception{
		switch (day){
			case "mon": return DayOfWeek.MONDAY;
			case "tue": return DayOfWeek.TUESDAY;
			case "wed": return DayOfWeek.WEDNESDAY;
			case "thu": return DayOfWeek.THURSDAY;
			case "fri": return DayOfWeek.FRIDAY;
			case "sat": return DayOfWeek.SATURDAY;
			case "sun": return DayOfWeek.SUNDAY;
			default: throw new Exception("Invalid day `" + day + "`");
		}
	}

	public Session(DayOfWeek day, LocalTime time, Duration duration, String name,
			String section, String venue) {
		this.day = day;
		this.time = time;
		this.duration = duration;
		this.name = name;
		this.section = section;
		this.venue = venue;
		this.recurring = true;
		this.week = null;
		this.index = 0;
	}

	public Session(DayOfWeek day, LocalTime time, Duration duration, String name,
			String section, String venue, int index) {
		this.day = day;
		this.time = time;
		this.duration = duration;
		this.name = name;
		this.section = section;
		this.venue = venue;
		this.recurring = true;
		this.week = null;
		this.index = index;
	}

	public Session(DayOfWeek day, LocalTime time, Duration duration, String name,
			String section, String venue, YearWeek week) {
		this.day = day;
		this.time = time;
		this.duration = duration;
		this.name = name;
		this.section = section;
		this.venue = venue;
		this.recurring = false;
		this.week = week;
		this.index = 0;
	}

	public String toString(){
		return "\t" + name + "\t" + section + "\t" + day.toString() + "\t" +
			time.toString() + "\t" + duration.toMinutes();
	}

	public static Session deserialize(String str){
		if (str == null)
			return null;
		String[] strs = str.strip().split("\t");
		if (strs.length != 6)
			return null;
		try{
			DayOfWeek day = parseDay(strs[3]);
			LocalTime time = LocalTime.parse(strs[4],
					DateTimeFormatter.ofPattern("HHmmss"));
			Duration dur = Duration.ofMinutes(Integer.parseInt(strs[5]));
			return new Session(day, time, dur, strs[0], strs[1], strs[2]);
		} catch (Exception e){
			System.out.println("Error deserializing Session:\n\t" + str + "\n" +
					e.getMessage());
		}
		return null;
	}

	public static Session deserializeMakeup(String str){
		if (str == null)
			return null;
		String[] strs = str.strip().split("\t");
		if (strs.length != 7)
			return null;
		try{
			DayOfWeek day = parseDay(strs[3]);
			LocalTime time = LocalTime.parse(strs[4],
					DateTimeFormatter.ofPattern("HHmmss"));
			Duration dur = Duration.ofMinutes(Integer.parseInt(strs[5]));
			YearWeek week = new YearWeek(strs[6]);
			return new Session(day, time, dur, strs[0], strs[1], strs[2], week);
		} catch (Exception e){
			System.out.println("Error deserializing Session:\n\t" + str + "\n" +
					e.getMessage());
		}
		return null;
	}

	public static List<Session> deserializeAll(String str){
		List<Session> ret = new ArrayList<>();
		String[] lines = str.split("\n");
		Map<String, Integer> counts = new HashMap<>();
		for (String line : lines){
			Session session = Session.deserialize(line);
			if (session == null)
				return null;
			String key = session.getName() + "\t" + session.getSection();
			if (counts.containsKey(key)){
				session.index = counts.get(key);
				counts.put(key, counts.get(key) + 1);
			}else{
				counts.put(key, 1);
			}
			ret.add(session);
		}
		return ret;
	}

	public static List<Session> deserializeMakeupAll(String str){
		List<Session> ret = new ArrayList<>();
		String[] lines = str.split("\n");
		for (String line : lines){
			Session session = Session.deserializeMakeup(line);
			if (session == null)
				return null;
			ret.add(session);
		}
		return ret;
	}

	public boolean overlaps(Session other){
		if (this.day != other.day)
			return false;
		LocalTime thisEndTime = this.time.plus(this.duration);
		LocalTime otherEndTime = other.time.plus(other.duration);
		return this.time.isBefore(otherEndTime) && thisEndTime.isAfter(other.time);
	}

	public DayOfWeek getDay() {
		return day;
	}

	public LocalTime getTime() {
		return time;
	}

	public Duration getDuration() {
		return duration;
	}

	public String getName() {
		return name;
	}
	public String getSection() {
		return section;
	}

	public String getVenue() {
		return venue;
	}

	public int getIndex() {
		return index;
	}

	public boolean isRecurring() {
		return recurring;
	}

	public YearWeek getWeek() {
		return week;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		return day == other.day &&
			time.equals(other.time) &&
			duration.equals(other.duration) &&
			name.equals(other.name) &&
			section.equals(other.section) &&
			venue.equals(other.venue);
	}
}
