package slowport.rater;

import java.util.*;
import slowport.common.*;

public class ComboRater extends Rater{
	private List<Rater> raters;
	private List<Integer> weights;

	public ComboRater(List<Rater> raters, List<Integer> weights){
		this.raters = raters;
		this.weights = weights;
	}

	public int rate(List<Session> sessions){
		return 0; /// TODO
	}
}
