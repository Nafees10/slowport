package slowport.rater;

import java.util.*;
import slowport.common.*;

public abstract class Rater{
	public abstract long rate(Timetable timetable);
	public static void normalize(List<Integer> scores){
		// TODO
	}
}
