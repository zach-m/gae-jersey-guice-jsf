package com.tectonica.api;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.tectonica.model.MyImpl;
import com.tectonica.model.MyIntf;

/**
 * Servlet-listener for intercepting all REST requests and pass them through Guice before Jersey
 * 
 * @author Zach Melamed
 */
public class GuiceConfig extends GuiceServletContextListener
{
	private static final String URL_PATTERN = "/v1/*";
	private static final String JERSEY_ROOT_PACKAGE = GuiceConfig.class.getPackage().getName();

	@Override
	protected Injector getInjector()
	{
		return Guice.createInjector(new ServletModule()
		{
			@Override
			protected void configureServlets()
			{
				doCustomBinds();

				// bind Jersey-classes to Guice
				ResourceConfig rc = new PackagesResourceConfig(JERSEY_ROOT_PACKAGE);
				for (Class<?> resource : rc.getClasses())
					bind(resource);

				// configure Jersey: use Jackson + CORS-filter
				Map<String, String> initParams = new HashMap<>();
				initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
				initParams.put("com.sun.jersey.spi.container.ContainerResponseFilters", CorsFilter.class.getName());
//				initParams.put("com.sun.jersey.config.feature.Trace", "true");

				// route all requests through Guice
				serve(URL_PATTERN).with(GuiceContainer.class, initParams);
			}

			/**
			 * this routine performs applicative binding, typically between interfaces and concrete implementations
			 */
			private void doCustomBinds()
			{
				bind(MyIntf.class).to(MyImpl.class);
			}
		});
	}
}
