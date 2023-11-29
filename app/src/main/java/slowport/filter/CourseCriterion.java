package slowport.filter;

import slowport.common.*;

public class CourseCriterion extends Criterion{
	private String course;

	public CourseCriterion(String course) {
		this.course = course;
	}

	public String getCourse() {
		return course;
	}

	public boolean validate(Session session){
		if (session == null)
			return false;
		return session.getName().equals(course);
	}

	public void setCourse(String course) {
		this.course = course;
	}
}
