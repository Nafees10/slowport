package slowport.common;

public class Note{
	private int week;
	private String course;
	private int sessionIndex;
	private String note;

	public Note(int week, String course, int sessionIndex, String note){
		this.week = week;
		this.course = course;
		this.sessionIndex = sessionIndex;
		this.note = note;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public int getSessionIndex() {
		return sessionIndex;
	}

	public void setSessionIndex(int sessionIndex) {
		this.sessionIndex = sessionIndex;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
