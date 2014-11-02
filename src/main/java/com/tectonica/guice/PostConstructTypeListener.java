package com.tectonica.guice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class PostConstructTypeListener implements TypeListener
{
	private final String packagePrefix;

	public PostConstructTypeListener(String packagePrefix)
	{
		this.packagePrefix = packagePrefix;
	}

	@Override
	public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter)
	{
		Class<? super I> clz = typeLiteral.getRawType();
		if (packagePrefix != null && !clz.getName().startsWith(packagePrefix))
			return;

		final Method method = getPostConstructMethod(clz);
		if (method != null)
		{
			typeEncounter.register(new InjectionListener<I>()
			{
				@Override
				public void afterInjection(Object i)
				{
					try
					{
						// call the @PostConstruct annotated method after all dependencies have been injected
						method.invoke(i);
					}
					catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
					{
						throw new RuntimeException(e);
					}
				}
			});
		}
	}

	/**
	 * checks whether the provided class, or one of its super-classes, has a method with the {@code PostConstruct} annotation. the method
	 * may be public, protected, package-private or private. if it's inaccessible by Java rules, this method will also make
	 * it accessible before returning it.
	 * 
	 * @return
	 *         the method that meets all requirements, or null if none found
	 */
	private Method getPostConstructMethod(Class<?> clz)
	{
		for (Method method : clz.getDeclaredMethods())
		{
			if (method.getAnnotation(PostConstruct.class) != null && isPostConstructEligible(method))
			{
				method.setAccessible(true);
				return method;
			}
		}
		Class<?> superClz = clz.getSuperclass();
		return (superClz == Object.class || superClz == null) ? null : getPostConstructMethod(superClz);
	}

	/**
	 * apply restrictions as defined in the <a
	 * href="http://docs.oracle.com/javaee/5/api/javax/annotation/PostConstruct.html">JavaEE specifications</a>
	 */
	private boolean isPostConstructEligible(final Method method)
	{
		return (method.getReturnType() == void.class) && (method.getParameterTypes().length == 0)
				&& (method.getExceptionTypes().length == 0);
	}
}
