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
		str = str.strip();
		String[] strs = str.split("\t");
		if (strs.length != 6)
			return null;
		try{
			String name = strs[0],
			section = strs[1],
			venue = strs[2];
			DayOfWeek day;
			switch (strs[3]){
				case "mon": day = DayOfWeek.MONDAY; break;
				case "tue": day = DayOfWeek.TUESDAY; break;
				case "wed": day = DayOfWeek.WEDNESDAY; break;
				case "thu": day = DayOfWeek.THURSDAY; break;
				case "fri": day = DayOfWeek.FRIDAY; break;
				case "sat": day = DayOfWeek.SATURDAY; break;
				case "sun": day = DayOfWeek.SUNDAY; break;
				default: throw new Exception("Invalid day `" + strs[3] + "`");
			}
			LocalTime time = LocalTime.parse(strs[4],
					DateTimeFormatter.ofPattern("HHmmss"));
			Duration dur = Duration.ofMinutes(Integer.parseInt(strs[5]));
			return new Session(day, time, dur, name, section, venue);
		} catch (Exception e){
			System.out.println("Error deserializing Session:\n\t" + str + "\n" +
					e.getMessage());
		}
		return null;
	}

	public static List<Session> deserializeAll(String str){
		List<Session> ret = new ArrayList<>();
		String[] lines = str.split("\n");
		for (String line : lines){
			Session session = Session.deserialize(line);
			if (session == null)
				return null;
			ret.add(session);
		}
		return ret;
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
}
