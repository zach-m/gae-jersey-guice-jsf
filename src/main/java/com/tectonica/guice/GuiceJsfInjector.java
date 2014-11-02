package com.tectonica.guice;

import javax.faces.bean.ManagedBean;

import com.sun.faces.spi.InjectionProviderException;
import com.sun.faces.vendor.WebContainerInjectionProvider;

/**
 * An optional injector for Mojarra-based applications where dependency injection is required into the {@code @ManagedBean}s. It extends
 * {@link WebContainerInjectionProvider}, which normally handles invocations of {@code @PostConstruct} and {@code @PreDestroy}, by also
 * adding dependency-injection for {@code @ManagedBean}s using the Guice injector created in {@link GuiceListener}. This creator, by the
 * way, also handles {@code @PostConstruct} methods, so we make sure to avoid double invocation here.
 * <p>
 * To use, add the following paragraph to {@code web.xml} alongside your other JSF configuration:
 * 
 * <pre>
 * &lt;context-param&gt;
 *    &lt;param-name&gt;com.sun.faces.injectionProvider&lt;/param-name&gt;
 *    &lt;param-value&gt;com.tectonica.guice.GuiceJsfInjector&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * 
 * <b>NOTE:</b> make sure your {@link GuiceListener}-subclass is an active listener in the {@code web.xml}, or NullPointerExceptions
 * will be thrown
 * <p>
 * 
 * @author Zach Melamed
 */
public class GuiceJsfInjector extends WebContainerInjectionProvider
{
	@Override
	public void inject(Object managedBean) throws InjectionProviderException
	{
		if (isToBeInjectedByGuice(managedBean))
			GuiceListener.injectMembers(managedBean);
	}

	/**
	 * as an arbitrary choice, the choice here is to inject only into {@code @ManagedBean} instances, so that other classes - not written by
	 * us - wouldn't be injected too. This choice could be altered.
	 * 
	 * @param managedBean
	 * @return
	 */
	private boolean isToBeInjectedByGuice(Object managedBean)
	{
		return managedBean.getClass().getAnnotation(ManagedBean.class) != null;
	}

	@Override
	public void invokePostConstruct(Object managedBean) throws InjectionProviderException
	{
		// @PostConstruct is already handled in classes we injected
		if (!isToBeInjectedByGuice(managedBean))
			super.invokePostConstruct(managedBean);
	}

	@Override
	public void invokePreDestroy(Object managedBean) throws InjectionProviderException
	{
		super.invokePreDestroy(managedBean);
	}
}
