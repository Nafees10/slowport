package slowport.common;

public class Note{
	private YearWeek week;
	private String course;
	private String section;
	private int sessionIndex;
	private String note;

	public Note(YearWeek week, String course, String section, int sessionIndex,
			String note){
		this.week = week;
		this.course = course;
		this.section = section;
		this.sessionIndex = sessionIndex;
		this.note = note;
	}

	public YearWeek getWeek() {
		return week;
	}

	public String getCourse() {
		return course;
	}

	public String getSection() {
		return section;
	}

	public int getSessionIndex() {
		return sessionIndex;
	}

	public String getNote() {
		return note;
	}
}
