package slowport.rater;

import java.util.*;
import slowport.common.*;

public abstract class Rater{
	private final static long RANGE_MAX = 1000, RANGE_MIN = 0;
	public abstract long rate(Timetable timetable);
	public static void normalize(List<Long> scores){
		long min = scores.get(0), max = scores.get(0);
		for (long score : scores){
			min = min > score ? score : min;
			max = max < score ? score : max;
		}
		// subtract toSub from all, multiply each with (RANGE_MAX / (max - toSub))
		long toSub = min - RANGE_MIN,
				 multiplier = RANGE_MAX / (max - toSub);
		for (int i = 0; i < scores.size(); i ++)
			scores.set(i, (scores.get(i) - toSub) * multiplier);
	}
}
