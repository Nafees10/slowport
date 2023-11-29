package slowport.rater;

import java.util.*;
import slowport.common.*;

public abstract class Rater{
	public abstract int rate(List<Session> sessions);
}
