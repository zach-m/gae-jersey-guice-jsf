package com.tectonica.api.v1;

import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.inject.servlet.RequestScoped;
import com.tectonica.engine.MyIntf;

@Path("/info")
@RequestScoped
public class InfoAPI
{
	private static final String TITLE = "Information Page";
	private static final String H1_TITLE = "Information Page";
	private static final String STYLE = createStyle();
	private static final String POWERED_BY_URL = "http://www.tectonica.co.il";
	private static final String POWERED_BY_NAME = "tectonica.co.il";

	@Inject
	private HttpServletRequest request; // the reason for the @RequestScoped

	@Inject
	private MyIntf myImpl;

	/**
	 * Returns environment information (headers, cookies, JVM, OS variables, etc)
	 * 
	 * @summary get environment
	 * @return HTML page with environment information
	 */
	@GET
	@Produces("text/html")
	public String getEnvironmentalHtml()
	{
		myImpl.foo(); // makes sure injection succeeded

		StringBuilder sb = new StringBuilder();
		String timeStamp = new Date().toString();

		try
		{
			sb.append("<h1>").append(H1_TITLE).append("</h1>");
			sb.append(timeStamp).append("<hr/>");
			printHeaders(sb);
			printCookies(sb);
			printJvmProperties(sb);
			printGaeProperties(sb);
			printEnvironment(sb);
			sb.append("<p>&nbsp;</p><hr/>");
			sb.append("Powered by <a href='").append(POWERED_BY_URL).append("'>").append(POWERED_BY_NAME).append("</a>");
		}
		catch (Exception e)
		{
			sb.append("<pre style='color:red'>Error:\n").append(e.getMessage()).append("\n").append(timeStamp).append("</pre>");
			e.printStackTrace();
		}

		return createHtml(TITLE, STYLE, sb.toString());
	}

	private static String createStyle()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("article,body,div,figure,form,h1,h2,h3,h4,html,img,label,li,nav,p,span,strong,ul{font-size:100%;vertical-align:baseline;margin:0;padding:0;outline:0;border:0;background:0 0}");
		sb.append("html{overflow-y:scroll;overflow-x:hidden}");
		sb.append("body{font-family:'Proxima Nova Regular','Segoe UI',Roboto,'Droid Sans','Helvetica Neue',Arial,sans-serif;font-style:normal;font-weight:400;padding:10px;overflow:hidden;line-height:1.46em;color:#333}");
		sb.append("h1,h2{font-family:'Skolar Bold','Segoe UI Bold','Roboto Slab','Droid Serif',AvenirNext-Bold,'Avenir Bold',Georgia,'Times New Roman',Times,serif;font-weight:700;font-style:normal}");
		sb.append("h1{font-size:2em;line-height:1.1em;padding-top:.5em}");
		sb.append("h2{font-size:1.5em;line-height:1.3em;padding:1.5em 0 .5em}");
		sb.append("table{font-family:'Lucida Sans Unicode','Lucida Grande',Sans-Serif;font-size:12px;text-align:left;border-collapse:collapse;margin:10px;max-width:1200px}");
		sb.append("th{font-weight:400;font-size:13px;color:#039;background:#b9c9fe;padding:8px}");
		sb.append("td{background:#e8edff;border-top:1px solid #fff;color:#669;padding:8px;word-break:break-all;min-width:250px}");
		sb.append("tbody tr:hover td{background:#d0dafd}");
		return sb.toString();
	}

	private String createHtml(String title, String style, String body)
	{
		return String.format("<html><head><title>%s</title><style>%s</style></head><body>%s</body></html>", title, style, body);
	}

	@SuppressWarnings("unchecked")
	private void printHeaders(StringBuilder sb)
	{
		sb.append("<h2>Headers</h2>");
		Enumeration<String> headerNames = request.getHeaderNames();
		sb.append("<table><tr><th>Name</th><th>Value</th></tr>");
		while (headerNames.hasMoreElements())
		{
			String headerName = headerNames.nextElement();
			sb.append("<tr><td>").append(headerName).append("</td><td>");
			Enumeration<String> headerValues = request.getHeaders(headerName);
			boolean isFirst = true;
			while (headerValues.hasMoreElements())
			{
				String headerValue = headerValues.nextElement();
				if (!isFirst)
					sb.append("</td></tr><tr><td>&nbsp;</td><td>");
				sb.append(headerValue);
				isFirst = false;
			}
			sb.append("</td></tr>");
		}
		sb.append("</table>");

		// General details
		sb.append("<p>Protocol = ").append(request.getProtocol()).append("</p>");
		sb.append("<p>Scheme = ").append(request.getScheme()).append("</p>");
		sb.append("<p>isSecure = ").append(request.isSecure()).append("</p>");
		sb.append("<p>Remote Address = ").append(request.getRemoteAddr()).append("</p>");
	}

	private void printCookies(StringBuilder sb)
	{
		sb.append("<h2>Cookies</h2>");
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			sb.append("<p>").append(cookies.length).append(" cookies found</p>");
			sb.append("<table><tr><th>Name</th><th>Value</th></tr>");
			for (Cookie cookie : cookies)
				sb.append("<tr><td>").append(cookie.getName()).append("</td><td>").append(cookie.getValue()).append("</td></tr>");
		}
		else
			sb.append("<p>No cookies found</p>");

		sb.append("</table>");
	}

	private void printJvmProperties(StringBuilder sb)
	{
		sb.append("<h2>JVM Properties</h2>");
		Iterator<Entry<Object, Object>> iter = System.getProperties().entrySet().iterator();
		sb.append("<table><tr><th>Name</th><th>Value</th></tr>");
		while (iter.hasNext())
		{
			Entry<Object, Object> entry = iter.next();
			String paramName = entry.getKey().toString();
			String paramValue = entry.getValue().toString();
			sb.append("<tr><td>").append(paramName).append("</td><td>").append(paramValue).append("</td></tr>");
		}
		sb.append("</table>");
	}

	private void printGaeProperties(StringBuilder sb)
	{
		sb.append("<h2>GAE Properties</h2>");
		Environment env = ApiProxy.getCurrentEnvironment();
		Iterator<Entry<String, Object>> iter = env.getAttributes().entrySet().iterator();
		sb.append("<table><tr><th>Name</th><th>Value</th></tr>");
		sb.append("<tr><td>").append("getAppId()").append("</td><td>").append(env.getAppId()).append("</td></tr>");
		sb.append("<tr><td>").append("getVersionId()").append("</td><td>").append(env.getVersionId()).append("</td></tr>");
		sb.append("<tr><td>").append("getModuleId()").append("</td><td>").append(env.getModuleId()).append("</td></tr>");
		sb.append("<tr><td>").append("getAuthDomain()").append("</td><td>").append(env.getAuthDomain()).append("</td></tr>");
		sb.append("<tr><td>").append("getEmail()").append("</td><td>").append(env.getEmail()).append("</td></tr>");

		UserService userService = UserServiceFactory.getUserService();
		boolean userLoggedIn = userService.isUserLoggedIn();
		sb.append("<tr><td>").append("isUserLoggedIn()").append("</td><td>").append(userLoggedIn).append("</td></tr>");
		if (userLoggedIn)
		{
			String logoutUrl = "Click <a href=\"" + userService.createLogoutURL(request.getRequestURI()) + "\">here</a> to Logout";
			sb.append("<tr><td>").append("createLogoutURL()").append("</td><td>").append(logoutUrl).append("</td></tr>");
		}
		else
		{
			String loginUrl = "Click <a href=\"" + userService.createLoginURL(request.getRequestURI()) + "\">here</a> to Login";
			sb.append("<tr><td>").append("createLoginURL()").append("</td><td>").append(loginUrl).append("</td></tr>");
		}

		while (iter.hasNext())
		{
			Entry<String, Object> entry = iter.next();
			String paramName = entry.getKey();
			String paramValue = entry.getValue().toString();
			sb.append("<tr><td>").append(paramName).append("</td><td>").append(paramValue).append("</td></tr>");
		}
		sb.append("</table>");
	}

	private void printEnvironment(StringBuilder sb)
	{
		sb.append("<h2>Environment Variables</h2>");
		Iterator<Entry<String, String>> iter = System.getenv().entrySet().iterator();
		sb.append("<table><tr><th>Name</th><th>Value</th></tr>");
		while (iter.hasNext())
		{
			Entry<String, String> entry = iter.next();
			String paramName = entry.getKey();
			String paramValue = entry.getValue();
			sb.append("<tr><td>").append(paramName).append("</td><td>").append(paramValue).append("</td></tr>");
		}
		sb.append("</table>");
	}
}
