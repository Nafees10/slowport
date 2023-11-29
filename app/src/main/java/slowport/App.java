package slowport;

import slowport.common.*;

public class App{
	public static void main(String[] args){
		System.out.println("Hallo");
		Session s = Session.deserialize(
				"	statistical and mathematical methods for data science	MDS-1A	Seminar Hall	mon	083000	170");
		System.out.println(s);
	}
}
