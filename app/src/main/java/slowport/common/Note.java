package slowport.common;

public class Note{
	private String course;
	private String section;
	private String note;

	public Note(String course, String section, String note){
		this.course = course;
		this.section = section;
		this.note = note;
	}

	public String getCourse() {
		return course;
	}

	public String getSection() {
		return section;
	}

	public String getNote() {
		return note;
	}
}
