package com.tectonica.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.tectonica.jee5.GuiceRestModule;

/**
 * Servlet-listener for intercepting REST requests and pass them through Guice before they are actually served by Jersey. This allows you to
 * inject instances into your JAX-RS annotated classes. This class can be used along with {@link GuiceJsfInjector} to also support injection
 * into JSF ManagedBeans.
 * <p>
 * To use it, first make sure to include the following dependency in your {@code pom.xml}:
 * 
 * <pre>
 * &lt;dependency&gt;
 *    &lt;groupId&gt;com.sun.jersey.contribs&lt;/groupId&gt;
 *    &lt;artifactId&gt;jersey-guice&lt;/artifactId&gt;
 * &lt;/dependency&gt;
 * </pre>
 * 
 * Then, you need to extend this class and provide a constructor that would offer your own subclass of {@link GuiceRestModule}, reflecting
 * your configuration. At the minimum, this subclass needs to implement {@link GuiceRestModule#getRootPackage()} or
 * {@link GuiceRestModule#bindJaxrsResources()}. For example:
 * 
 * <pre>
 * public class RestConfig extends GuiceListener
 * {
 * 	public RestConfig()
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
public class GuiceListener extends GuiceServletContextListener
{
	protected final AbstractModule module;
	protected static Injector injector;

	protected GuiceListener(AbstractModule customModule)
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
}
