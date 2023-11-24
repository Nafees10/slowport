package slowport;

import java.util.List;

public class SectionExclCriterion extends SectionCriterion {
	public SectionExclCriterion(String section) {
		super(section);
	}

	public boolean validate(Session session) {
		return true; /// TODO
	}
}
