package slowport.filter;

import java.util.*;
import slowport.common.*;

public class Combinator {
	private Criterion criterion;

	public Combinator(Criterion criterion) {
		this.criterion = criterion;
	}

	public Criterion getCriterion() {
		return criterion;
	}

	public void setCriterion(Criterion criterion) {
		this.criterion = criterion;
	}

	List<List<Session>> combinations(List<Session> sessions) {
		return null; // TODO write combinator
	}
}
