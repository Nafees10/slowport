package slowport.common;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class YearWeek{
	private int year;
	private int week;

	public YearWeek(int year, int week){
		this.year = year;
		this.week = week;
	}

	public YearWeek(String str) throws Exception{
		String[] splits = str.split("-");
		if (splits.length != 2)
			throw new Exception();
		this.year = Integer.parseInt(splits[0]);
		this.week = Integer.parseInt(splits[1]);
	}

	public static YearWeek fromToday() {
		LocalDate currentDate = LocalDate.now();
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		int weekNumber = currentDate.get(weekFields.weekOfWeekBasedYear());
		int year = currentDate.get(weekFields.weekBasedYear());
		return new YearWeek(year, weekNumber);
	}

	public String toString(){
		return this.year + "-" + week;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public boolean equals(YearWeek other) {
		if (this == other)
			return true;
		if (other == null || getClass() != other.getClass())
			return false;
		return year == other.year && week == other.week;
	}

	public boolean isBefore(YearWeek other) {
		if (year < other.year)
			return true;
		return year == other.year && week < other.week;
	}

	public boolean isAfter(YearWeek other) {
		if (year > other.year)
			return true;
		return year == other.year && week > other.week;
	}
}
