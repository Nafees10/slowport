package slowport;

public abstract class SectionCriterion extends Criterion {
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
}
