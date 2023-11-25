package slowport;

import java.time.*;

public class TimeCriterion extends Criterion{
	private LocalTime start;
	private LocalTime end;

	public TimeCriterion(LocalTime start, LocalTime end) {
		this.start = start;
		this.end = end;
	}

	public LocalTime getStart() {
		return start;
	}

	public void setStart(LocalTime start) {
		this.start = start;
	}

	public LocalTime getEnd() {
		return end;
	}

	public void setEnd(LocalTime end) {
		this.end = end;
	}

	public boolean validate(Session session){
		if (session == null)
			return false;
		LocalTime start = session.getTime();
		LocalTime end = session.getTime().plusMinutes(
				session.getDuration().toMinutes());
		// start1.before(end2) && start2.before(end1)
		return start.isBefore(this.end) && this.start.isBefore(end);
	}
}
