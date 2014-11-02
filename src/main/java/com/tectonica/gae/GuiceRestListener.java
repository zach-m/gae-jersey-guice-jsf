package com.tectonica.gae;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Servlet-listener for intercepting REST requests and pass them through Guice before Jersey. To use it you need to extend this class and
 * generate a constructor that would offer your own subclass of {@link GuiceRestModule}, reflect your configuration. At the minimum, this
 * subclass needs to implement {@link GuiceRestModule#getRootPackage()} or {@link GuiceRestModule#bindJaxrsResources()}. For example:
 * 
 * <pre>
 * public class RestConfig extends GuiceRestListener
 * {
 *    public RestConfig()
 *    {
 *       super(new GuiceRestModule()
 *       {
 *          {@literal @}Override
 *          protected String getServingUrl()
 *          {
 *             return ...;
 *          }
 * 
 *          {@literal @}Override
 *          protected String getRootPackage()
 *          {
 *             return ...;
 *          }
 *       });
 *    }
 * }
 * </pre>
 * 
 * Once implemented, register your listener with {@code web.xml}:
 * 
 * <pre>
 * &lt;listener&gt;
 *    &lt;listener-class&gt;com.example.api.RestConfig&lt;/listener-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * 
 * @author Zach Melamed
 */
public class GuiceRestListener extends GuiceServletContextListener
{
	protected final GuiceRestModule module;
	protected static Injector injector;

	protected GuiceRestListener(GuiceRestModule customModule)
	{
		module = customModule;
	}

	@Override
	protected Injector getInjector()
	{
		injector = Guice.createInjector(module);
		return injector;
	}

	/**
	 * given a class, generates an injected instance. Useful when an API call is needed internally.
	 */
	public static <T> T getInstance(Class<T> type)
	{
		return injector.getInstance(type);
	}

	/**
	 * given an injectable instance, injects its dependencies
	 */
	public static void injectMembers(Object instance)
	{
		injector.injectMembers(instance);
	}

	public static class GuiceRestModule extends ServletModule
	{
		@Override
		protected void configureServlets()
		{
			doCustomBinds();

			bindJaxrsResources();

			// add support for the @PostConstruct annotation for Guice-injected objects
			// if you choose to remove it, also modify GuiceJsfInjector.invokePostConstruct()
			bindListener(Matchers.any(), new PostConstructTypeListener(null));

			// configure Jersey: use Jackson + CORS-filter
			Map<String, String> initParams = new HashMap<>();
			if (isUseJackson())
				initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
			String reponseFilters = getResponseFilters();
			if (reponseFilters != null)
				initParams.put("com.sun.jersey.spi.container.ContainerResponseFilters", reponseFilters);

			doCustomJerseyParameters(initParams);

			// route all requests through Guice
			serve(getServingUrl()).with(GuiceContainer.class, initParams);

			doCustomServing();
		}

		/**
		 * override this to specify the URL-pattern of the REST service
		 */
		protected String getServingUrl()
		{
			return "/*";
		}

		/**
		 * override this to specify a root-package under which all your JAX-RS annotated class are located
		 */
		protected String getRootPackage()
		{
			return null;
		}

		/**
		 * bind JAX-RS annotated classes to be served through Guice. based on a recursive class scanning that starts from the package
		 * returned by {@link #getRootPackage()}. override this if you wish to avoid the scanning and bind your classes explicitly
		 */
		protected void bindJaxrsResources()
		{
			String rootPackage = getRootPackage();
			if (rootPackage == null)
				throw new NullPointerException(
						"to scan for JAX-RS annotated classes, either override getRootPackage() or bindJaxrsResources()");

			ResourceConfig rc = new PackagesResourceConfig(rootPackage);
			for (Class<?> resource : rc.getClasses())
				bind(resource);
		}

		/**
		 * override this to return a (comma-delimited) list of Jersey's ContainerResponseFilters. By default returns the {@link CorsFilter}.
		 */
		protected String getResponseFilters()
		{
			return CorsFilter.class.getName();
		}

		/**
		 * override this to avoid usage of Jackson
		 */
		protected boolean isUseJackson()
		{
			return true;
		}

		/**
		 * override to perform application-logic bindings, typically between interfaces and concrete implementations. For example:
		 * 
		 * <pre>
		 * bind(MyIntf.class).to(MyImpl.class);
		 * </per>
		 */
		protected void doCustomBinds()
		{}

		/**
		 * override to add additional Guice configuration. For example, to have non-REST servlets served through Guice, use:
		 * 
		 * <pre>
		 * serve(&quot;/my/*&quot;).with(MyServlet.class);
		 * </pre>
		 */
		protected void doCustomServing()
		{}

		/**
		 * override to change the context-parameters passed to Jersey's servlet. For example:
		 * 
		 * <pre>
		 * initParams.put(&quot;com.sun.jersey.config.feature.Trace&quot;, &quot;true&quot;);
		 * </Per>
		 */
		protected void doCustomJerseyParameters(Map<String, String> initParams)
		{}
	}
}
