package slowport;

public class NotCriterion extends Criterion {
	Criterion criterion;

	public NotCriterion(Criterion criterion) {
		this.criterion = criterion;
	}

	public Criterion getCriterion() {
		return criterion;
	}

	public void setCriterion(Criterion criterion) {
		this.criterion = criterion;
	}

	public boolean validate(Session session){
		return !criterion.validate(session);
	}
}
