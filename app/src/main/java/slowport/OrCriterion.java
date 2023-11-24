package slowport;

import java.util.*;

public class OrCriterion extends Criterion {
	private List<Criterion> criteria;

	public OrCriterion(List<Criterion> criteria) {
		this.criteria = criteria;
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criterion> criteria) {
		this.criteria = criteria;
	}

	public boolean validate(Session session) {
		for (Criterion criterion : criteria) {
			if (criterion.validate(session))
				return true;
		}
		return false;
	}
}
