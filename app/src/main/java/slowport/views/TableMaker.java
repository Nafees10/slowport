package slowport.views;

import slowport.common.*;
import java.util.*;
import java.time.*;

public class TableMaker{
	private static String style="""
<!-- Stolen from https://github.com/Nafees10/slowtable -->
<style>
table{border-collapse:collapse;text-align:center;width:100%;}
table td, table th{border:1px solid black;}
table tr:first-child th{border-top:0;}
table tr:last-child td{border-bottom:0;}
table tr td:first-child,table tr th:first-child{border-left:0;}
table tr td:last-child,table tr th:last-child{border-right:0;}
tr:nth-child(even){background-color:#f2f2f2;}
</style>
""";

	private static LocalTime timeMin(ArrayList<Session> sessions){
		if (sessions == null || sessions.size() == 0)
			return null;
		LocalTime ret = sessions.get(0).getTime();
		for (int i = 1; i < sessions.size(); i ++){
			if (sessions.get(i).getTime().isBefore(ret))
				ret = sessions.get(i).getTime();
		}
		return ret;
	}

	private static LocalTime timeMax(ArrayList<Session> sessions){
		if (sessions == null || sessions.size() == 0)
			return null;
		LocalTime ret = sessions.get(0).getTime().plusMinutes(
				sessions.get(0).getDuration().toMinutes());
		for (int i = 1; i < sessions.size(); i ++){
			LocalTime thisTime = sessions.get(i).getTime().plusMinutes(
						sessions.get(i).getDuration().toMinutes());
			if (thisTime.isAfter(ret))
				ret = thisTime;
		}
		return ret;
	}

	private static HashMap<String, ArrayList<Session>> separateByVenue(
			ArrayList<Session> sessions){
		HashMap<String, ArrayList<Session>> map = new HashMap<>();
		for (int i = 0; i < sessions.size(); i ++){
			Session session = sessions.get(i);
			if (!map.containsKey(session.getVenue()))
				map.put(session.getVenue(), new ArrayList<>());
			map.get(session.getVenue()).add(session);
		}
		return map;
	}

	private static HashMap<String, ArrayList<Session>> separateByDay(
			ArrayList<Session> sessions){
		HashMap<String, ArrayList<Session>> map = new HashMap<>();
		for (int i = 0; i < sessions.size(); i ++){
			Session session = sessions.get(i);
			if (!map.containsKey(session.getDay().toString()))
				map.put(session.getDay().toString(), new ArrayList<>());
			map.get(session.getDay().toString()).add(session);
		}
		return map;
	}

	private static String generateHeaderDayVenue(LocalTime timeMin, LocalTime timeMax){
		String ret = "<tr><th rowspan=2>Day</th><th rowspan=2>Venue</th>";
		for (int hour = timeMin.getHour(); hour <= timeMax.getHour(); hour ++)
			ret += "<th colspan=6>" + hour + "</th>";
		ret += "</tr><tr>";
		for (int hour = timeMin.getHour(); hour <= timeMax.getHour(); hour ++)
			for (int minute = 0; minute < 6; minute ++)
				ret += "<th style='table-style:fixed;'>" + minute * 10+ "</th>";
		return ret + "</tr>";
	}

	private static String generateHeaderDay(LocalTime timeMin, LocalTime timeMax){
		String ret = "<tr><th rowspan=2>Day</th>";
		for (int hour = timeMin.getHour(); hour <= timeMax.getHour(); hour ++)
			ret += "<th colspan=6>" + hour + "</th>";
		ret += "</tr><tr>";
		for (int hour = timeMin.getHour(); hour <= timeMax.getHour(); hour ++)
			for (int minute = 0; minute < 6; minute ++)
				ret += "<th style='table-style:fixed;'>" + minute * 10 + "</th>";
		return ret + "</tr>";
	}

	private static String generateSerial(ArrayList<Session> sessions,
			LocalTime timeMin, LocalTime timeMax){
		String ret = "";
		int x = timeMin.getHour() * 60;
		int minutesMax = (timeMax.getHour() + 1) * 60 + timeMax.getMinute();
		for (int i = 0; i < sessions.size(); i ++){
			Session session = sessions.get(i);
			int minutes = session.getTime().getHour() * 60 +
				session.getTime().getMinute();
			if (minutes > x)
				ret += "<td colspan=\"" + ((minutes - x) / 10) + "\"></td>";
			ret += "<td colspan=\"" + (session.getDuration().toMinutes() / 10) +
				"\" style='background-color: #8be9fd;'>" +
				session.getName() + "-" + session.getSection() + "</td>";
			x = minutes + (int)session.getDuration().toMinutes();
		}
		if (minutesMax > x)
			ret += "<td colspan=\"" + ((minutesMax - x) / 10) + "\"></td>";
		return ret;
	}

	private static String generateDay(ArrayList<Session> sessions,
			LocalTime timeMin, LocalTime timeMax){
		if (sessions == null || timeMin == null || timeMax == null)
			return "";
		String ret = generateHeaderDay(timeMin, timeMax);
		HashMap<String, ArrayList<Session>> map = separateByDay(sessions);
		for (DayOfWeek day : DayOfWeek.values()){
			ArrayList<Session> daySessions = map.get(day.toString());
			ret += "<tr><th>" + day.toString() + "</th>" +
				generateSerial(daySessions, timeMin, timeMax) + "</tr>";
		}
		return ret;
	}

	private static String generateDayVenue(ArrayList<Session> sessions,
			LocalTime timeMin, LocalTime timeMax){
		if (sessions == null || timeMin == null || timeMax == null)
			return "";
		String ret = generateHeaderDayVenue(timeMin, timeMax);
		HashMap<String, ArrayList<Session>> map = separateByDay(sessions);
		for (DayOfWeek day : DayOfWeek.values()){
			ArrayList<Session> daySessions = map.get(day.toString());
			if (daySessions == null || daySessions.size() == 0)
				continue;
			HashMap<String, ArrayList<Session>> dayMap = separateByVenue(daySessions);
			ret += "<tr><th rowspan=" + dayMap.size() + ">" + day.toString() + "</th>";
			for (Map.Entry<String, ArrayList<Session>> entry : dayMap.entrySet()){
				ArrayList<Session> vSessions = entry.getValue();
				String venue = entry.getKey();
				ret += "<th>" + venue + "</th>" +
					generateSerial(vSessions, timeMin, timeMax) + "</tr><tr>";
			}
			ret += "</tr>";
		}
		return ret;
	}

	public static String generateForSingleVenue(ArrayList<Session> sessions){
		if (sessions == null || sessions.size() == 0)
			return "";
		return "<html><!DOCTYPE html>" + style + "<table>" +
			generateDay(sessions, timeMin(sessions), timeMax(sessions)) +
			"</table></html>";
	}

	public static String generate(ArrayList<Session> sessions){
		if (sessions == null || sessions.size() == 0)
			return "";
		return "<html><!DOCTYPE html>" + style + "<table>" +
			generateDayVenue(sessions, timeMin(sessions), timeMax(sessions)) +
			"</table></html>";
	}
}
