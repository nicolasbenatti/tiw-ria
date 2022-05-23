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
	
	public static String timeFromSeconds(int seconds) {
		int hours = seconds / 3600;
		int minutes = seconds/60;
		String ret = new String();
		ret = hours + " h : " + minutes + " m";
		return ret;
	}
}

