package slowport.filter;

import java.util.*;
import slowport.common.*;

public class AndCriterion extends Criterion {
	private List<Criterion> criteria;

	public AndCriterion(List<Criterion> criteria) {
		this.criteria = criteria;
	}

	public boolean validate(Session session) {
		for (Criterion criterion : criteria) {
			if (!criterion.validate(session))
				return false;
		}
		return true;
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criterion> criteria) {
		this.criteria = criteria;
	}
}
