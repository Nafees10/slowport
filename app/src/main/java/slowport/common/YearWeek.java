package slowport.common;

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
}
