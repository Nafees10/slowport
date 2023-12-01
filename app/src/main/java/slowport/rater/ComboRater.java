package slowport.rater;

import java.util.*;
import slowport.common.*;

public class ComboRater extends Rater{
	private List<Rater> raters;
	private List<Integer> weights;

	public ComboRater(List<Rater> raters, List<Integer> weights){
		this.raters = new ArrayList<>(raters);
		this.weights = new ArrayList<>(weights);
	}

	public int rate(Timetable timetable){
		return 0; /// TODO
	}
}
