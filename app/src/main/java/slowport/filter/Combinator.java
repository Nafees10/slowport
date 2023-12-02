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

	public List<List<Session>> combinations(List<Session> sessions) {
		List<List<Session>> result = new ArrayList<>();
		int totalSessions = sessions.size();
		int totalCombinations = 1 << totalSessions; // 2^totalSessions

		for (int i = 0; i < totalCombinations; i++) {
			List<Session> currentCombination = new ArrayList<>();
			for (int j = 0; j < totalSessions; j++) {
				if ((i & (1 << j)) != 0) {
					currentCombination.add(sessions.get(j));
				}
			}
			result.add(currentCombination);
		}

		return result;
	}
}
