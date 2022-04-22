package it.polimi.tiw.riunioni.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class SanitizeUtils {
	
	public static String sanitizeString(String s) {
		String res = null;
		res = StringUtils.normalizeSpace(s);
		res = StringEscapeUtils.escapeJava(res);
		
		return res;
	}
}

