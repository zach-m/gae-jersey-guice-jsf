package com.tectonica.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
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

				supportPostConstruct(null);
			}

			private void supportPostConstruct(final String packagePrefix)
			{
				bindListener(Matchers.any(), new TypeListener()
				{
					private <T extends Annotation> Method getAnnotatedMethod(Class<?> clz, Class<T> annotation)
					{
						for (Method method : clz.getMethods())
						{
							if (method.getAnnotation(annotation) != null)
								return method;
						}
						return null;
					}

					@Override
					public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter)
					{
						Class<? super I> clz = typeLiteral.getRawType();
						if (packagePrefix != null && !clz.getPackage().getName().startsWith(packagePrefix))
							return;

						final Method method = getAnnotatedMethod(clz, PostConstruct.class);
						if (method != null)
						{
							boolean eligible = (method.getReturnType() == void.class) && (method.getParameterTypes().length == 0);
							if (eligible)
							{
								typeEncounter.register(new InjectionListener<I>()
								{
									@Override
									public void afterInjection(Object i)
									{
										try
										{
											method.invoke(i);
										}
										catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
										{
											e.printStackTrace();
										}
									}
								});
							}
						}

					}
				});
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
