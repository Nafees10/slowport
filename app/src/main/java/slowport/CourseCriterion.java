package slowport;

public class CourseCriterion extends Criterion{
	private String course;

	public CourseCriterion(String course) {
		this.course = course;
	}

	public String getCourse() {
		return course;
	}

	public boolean validate(Session session){
		return true; /// TODO
	}

	public void setCourse(String course) {
		this.course = course;
	}
}
