package slowport;

import java.time.*;

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
