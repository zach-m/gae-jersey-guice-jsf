package com.tectonica.api.v1;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.tectonica.model.MyImpl;
import com.tectonica.model.MyIntf;

@Path("/info")
public class InfoAPI
{
	private static final String TITLE = "Information Page";
	private static final String H1_TITLE = "Information Page";

	@Context
	private HttpServletRequest request;
	
	private MyIntf myImpl = MyImpl.getInstance();

	/**
	 * Returns environment information (headers, cookies, JVM and OS variables)
	 * 
	 * @summary get environment
	 * @return HTML page with environment information
	 */
	@GET
	@Produces("text/html")
	public String getEnvironmentalHtml()
	{
		StringBuilder sb = new StringBuilder();
		String timeStamp = new Date().toString();

		try
		{
			myImpl.foo();
			sb.append(timeStamp).append("<HR/>");
			printHeaders(sb);
			printCookies(sb);
			printJvmProperties(sb);
			printGaeProperties(sb);
			printEnvironment(sb);
			sb.append("<HR/>").append(timeStamp);
		}
		catch (Exception e)
		{
			sb.append("<pre style='color:red'>Error:\n").append(e.getMessage()).append("\n").append(timeStamp).append("</pre>");
			e.printStackTrace();
		}

		return createHtml(TITLE, H1_TITLE, sb.toString());
	}

	private String createHtml(String title, String header, String body)
	{
		return String.format("<html><title>%s</title><body><h1>%s</h1>%s</body></html>", title, header, body);
	}

	@SuppressWarnings("unchecked")
	private void printHeaders(StringBuilder sb)
	{
		sb.append("<h2>Headers</h2>");
		Enumeration<String> headerNames = request.getHeaderNames();
		sb.append("<table border='1'><tr><th>Name</th><th>Value</th></tr>");
		while (headerNames.hasMoreElements())
		{
			sb.append("<tr><td>");
			String headerName = headerNames.nextElement();
			sb.append(headerName).append("</td>");
			Enumeration<String> headerValues = request.getHeaders(headerName);
			boolean isFirst = true;
			while (headerValues.hasMoreElements())
			{
				if (isFirst)
					sb.append("<td>");
				else
					sb.append("</td></tr><tr><td></td><td>");
				String headerValue = headerValues.nextElement();
				sb.append(headerValue);
				isFirst = false;
			}
			sb.append("</td></tr>");
		}
		sb.append("</table><br/>");

		// General details
		sb.append("Protocol = ").append(request.getProtocol()).append("<br/>");
		sb.append("Scheme = ").append(request.getScheme()).append("<br/>");
		sb.append("isSecure = ").append(request.isSecure()).append("<br/>");
		sb.append("Remote Address = ").append(request.getRemoteAddr()).append("<br/>");
	}

	private void printCookies(StringBuilder sb)
	{
		sb.append("<h2>Cookies</h2>");
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			sb.append(cookies.length).append(" cookies found<br/>");
			sb.append("<table border='1'><tr><th>Name</th><th>Value</th></tr>");
			for (Cookie cookie : cookies)
				sb.append("<tr><td>").append(cookie.getName()).append("</td><td>").append(cookie.getValue()).append("</td></tr>");
		}
		else
			sb.append("No cookies found<br/>");

		sb.append("</table><br/>");
	}

	private void printJvmProperties(StringBuilder sb)
	{
		sb.append("<h2>JVM Properties</h2>");
		Iterator<Entry<Object, Object>> iter = System.getProperties().entrySet().iterator();
		sb.append("<table border='1'><tr><th>Name</th><th>Value</th></tr>");
		while (iter.hasNext())
		{
			Entry<Object, Object> entry = iter.next();
			String paramName = entry.getKey().toString();
			String paramValue = entry.getValue().toString();
			sb.append("<tr><td>").append(paramName).append("</td><td>").append(paramValue).append("</td></tr>");
		}
		sb.append("</table><br/>");
	}

	private void printGaeProperties(StringBuilder sb)
	{
		sb.append("<h2>GAE Properties</h2>");
		Environment env = ApiProxy.getCurrentEnvironment();
		Iterator<Entry<String, Object>> iter = env.getAttributes().entrySet().iterator();
		sb.append("<table border='1'><tr><th>Name</th><th>Value</th></tr>");
		sb.append("<tr><td>").append("getAppId").append("</td><td>").append(env.getAppId()).append("</td></tr>");
		sb.append("<tr><td>").append("getVersionId").append("</td><td>").append(env.getVersionId()).append("</td></tr>");
		sb.append("<tr><td>").append("getModuleId").append("</td><td>").append(env.getModuleId()).append("</td></tr>");
		sb.append("<tr><td>").append("getAuthDomain").append("</td><td>").append(env.getAuthDomain()).append("</td></tr>");
		sb.append("<tr><td>").append("getEmail").append("</td><td>").append(env.getEmail()).append("</td></tr>");
		while (iter.hasNext())
		{
			Entry<String, Object> entry = iter.next();
			String paramName = entry.getKey();
			String paramValue = entry.getValue().toString();
			sb.append("<tr><td>").append(paramName).append("</td><td>").append(paramValue).append("</td></tr>");
		}
		sb.append("</table><br/>");
	}

	private void printEnvironment(StringBuilder sb)
	{
		sb.append("<h2>Environment Variables</h2>");
		Iterator<Entry<String, String>> iter = System.getenv().entrySet().iterator();
		sb.append("<table border='1'><tr><th>Name</th><th>Value</th></tr>");
		while (iter.hasNext())
		{
			Entry<String, String> entry = iter.next();
			String paramName = entry.getKey();
			String paramValue = entry.getValue();
			sb.append("<tr><td>").append(paramName).append("</td><td>").append(paramValue).append("</td></tr>");
		}
		sb.append("</table><br/>");
	}
}
