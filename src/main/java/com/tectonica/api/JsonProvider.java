package com.tectonica.api;

import javax.inject.Singleton;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;

import com.tectonica.gae.Jackson1;

/**
 * Overrides the default JSON serialization configuration provided by Jackson.<br/>
 * We don't want to serialize nulls, fail on unknown properties, etc.
 * 
 * @author Zach Melamed
 */
@Provider
@Singleton
public class JsonProvider implements ContextResolver<ObjectMapper>
{
	final ObjectMapper json = Jackson1.createPropsMapper();

	@Override
	public ObjectMapper getContext(Class<?> type)
	{
		return json;
	}
}
