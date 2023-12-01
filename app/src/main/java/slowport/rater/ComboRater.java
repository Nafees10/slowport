package slowport.rater;

import java.util.*;
import slowport.common.*;

public class ComboRater extends Rater{
	private List<Rater> raters;
	private List<Integer> weights;

	public ComboRater(List<Rater> raters, List<Integer> weights) throws Exception{
		if (raters.size() != weights.size())
			throw new Exception("raters.size() != weights.size()");
		this.raters = new ArrayList<>(raters);
		this.weights = new ArrayList<>(weights);
	}

	public int rate(Timetable timetable){
		// just a simple weighted average
		long score = 0, weight = 0;
		for (int i = 0; i < raters.size(); i ++){
			score += raters.get(i).rate(timetable) * weights.get(i);
			weight += weights.get(i);
		}
		return (int)(score / weight);
	}
}
