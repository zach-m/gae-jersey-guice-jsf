package com.tectonica.api;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Overrides the default JSON serialization configuration provided by Jackson.<br/>
 * We don't want to serialize nulls, fail on unknown properties, etc.
 * 
 * @author Zach Melamed
 */
@Provider
public class JsonProvider implements ContextResolver<ObjectMapper>
{
	final ObjectMapper json;

	public JsonProvider()
	{
		json = new ObjectMapper();

		// when writing a JSON text
		json.disable(SerializationConfig.Feature.AUTO_DETECT_FIELDS);
		json.enable(SerializationConfig.Feature.AUTO_DETECT_GETTERS);
		json.enable(SerializationConfig.Feature.AUTO_DETECT_IS_GETTERS);
		json.setSerializationInclusion(Inclusion.NON_NULL);

		// when reading a JSON text
		json.disable(DeserializationConfig.Feature.AUTO_DETECT_FIELDS);
		json.enable(DeserializationConfig.Feature.AUTO_DETECT_SETTERS);
		json.enable(DeserializationConfig.Feature.AUTO_DETECT_CREATORS);
		json.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
//		json.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override
	public ObjectMapper getContext(Class<?> type)
	{
		return json;
	}
}
