package slowport;

import java.util.List;

public class SectionInclCriterion extends SectionCriterion {
	public SectionInclCriterion(String section) {
		super(section);
	}
	public boolean validate(Session session) {
		return true; /// TODO
	}
}
