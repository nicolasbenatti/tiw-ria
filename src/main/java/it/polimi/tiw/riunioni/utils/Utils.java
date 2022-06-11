package it.polimi.tiw.riunioni.utils;

import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Utils {
	
	public static String sanitizeString(String s) {
		String res = null;
		res = StringUtils.normalizeSpace(s);
		res = StringEscapeUtils.escapeJava(res);
		
		return res;
	}
	
	public static int minutesFromDate(Date d) {
		return d.getHours() * 60 + d.getMinutes();
	}
}

