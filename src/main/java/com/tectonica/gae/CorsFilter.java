package com.tectonica.gae;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Jersey filter for adding CORS headers to the response of the REST APIs where needed
 * 
 * @author Zach Melamed
 */
public class CorsFilter implements ContainerResponseFilter
{
	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response)
	{
		if (!isCorsNeeded(request))
			return response;

		MultivaluedMap<String, Object> headers = response.getHttpHeaders();

		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

		String acrh = request.getHeaderValue("Access-Control-Request-Headers");
		if (acrh != null && !acrh.isEmpty())
			headers.add("Access-Control-Allow-Headers", acrh);

		return response;
	}

	/**
	 * returns whether or not a given request needs CORS header in its response. For example:
	 * 
	 * <pre>
	 * return request.getPath().startsWith(&quot;debug&quot;);
	 * </pre>
	 */
	protected boolean isCorsNeeded(ContainerRequest request)
	{
		return true;
	}
}
