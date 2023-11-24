package slowport;

import java.util.*;

public class CourseInclCriterion extends CourseCriterion {

	public CourseInclCriterion(String course) {
		super(course);
	}

	public boolean validate(Session session) {
		return true; // TODO
	}
}
