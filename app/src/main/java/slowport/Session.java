package slowport;

import java.time.*;
import java.time.format.*;

public class Session {
	private DayOfWeek day;
	private LocalTime time;
	private Duration duration;
	private String name;
	private String section;
	private String venue;

	public Session(DayOfWeek day, LocalTime time, Duration duration, String name,
			String section, String venue) {
		this.day = day;
		this.time = time;
		this.duration = duration;
		this.name = name;
		this.section = section;
		this.venue = venue;
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

	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
}
