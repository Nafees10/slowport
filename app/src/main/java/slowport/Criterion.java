package slowport;

import java.util.*;

public abstract class Criterion {
	public abstract boolean validate(Session session);
	public final boolean validate(List<Session> sessions){
		for (Session session : sessions){
			if (!validate(session))
				return false;
		}
		return true;
	}
}
