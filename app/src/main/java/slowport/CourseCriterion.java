package slowport;

public abstract class CourseCriterion extends Criterion{
	private String course;

	public CourseCriterion(String course) {
		this.course = course;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}
}
