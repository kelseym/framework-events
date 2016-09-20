/*
 * org.nrg.framework.net.JSESSIONIDCookie
 * XNAT http://www.xnat.org
 * Copyright (c) 2016, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.net;

import java.net.URLConnection;

/**
 * @author ehaas01
 * Represent the JSESSIONID cookie that we are manually inserting into REST calls as of Tomcat 7
 * (The JSESSIONID cookie already present is HttpOnly by default and not visible to applets/JavaScript).
 * Why not just use java.net.HttpCookie here?  It's new as of Java 1.6, and we still officially support 1.5.
 * Also, we're not using it as a "real" cookie, since HTTP headers are set manually within the HttpUrlConnection.
 * All we really need here is a name value pair.
 */
public final class JSESSIONIDCookie {
	private final String jsessionid;
	
	public JSESSIONIDCookie(final String jsessionid) {
	    this.jsessionid = "".equals(jsessionid) ? null : jsessionid;
	}
	
	public void setInRequestHeader(final URLConnection connection) {
	    if (null != jsessionid) {
	        connection.setRequestProperty("Cookie", this.toString());
	    }
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return null == jsessionid ? "" : String.format("JSESSIONID=%s", jsessionid);
	}
}
