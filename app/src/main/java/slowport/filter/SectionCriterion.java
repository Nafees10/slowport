package slowport.filter;

import slowport.common.*;

public class SectionCriterion extends Criterion {
	private String section;

	public SectionCriterion(String section) {
		this.section = section;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public boolean validate(Session session){
		if (session == null)
			return false;
		return session.getSection().equals(section);
	}
}
