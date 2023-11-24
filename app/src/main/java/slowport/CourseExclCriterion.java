package slowport;

import java.util.List;

public class CourseExclCriterion extends CourseCriterion {

	public CourseExclCriterion(String course) {
		super(course);
	}

	public boolean validate(Session session) {
		return true; // TODO
	}
}
