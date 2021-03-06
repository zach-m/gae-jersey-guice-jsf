package com.tectonica.api;

import com.tectonica.engine.MyImpl;
import com.tectonica.engine.MyIntf;
import com.tectonica.guice.GuiceListener;
import com.tectonica.jee5.GuiceRestModule;

public class RestConfig extends GuiceListener
{
	private static final String URL_PATTERN = "/v1/*";
	private static final String JERSEY_ROOT_PACKAGE = RestConfig.class.getPackage().getName();

	public RestConfig()
	{
		super(new GuiceRestModule()
		{
			@Override
			protected String getServingUrl()
			{
				return URL_PATTERN;
			}

			@Override
			protected String getRootPackage()
			{
				return JERSEY_ROOT_PACKAGE;
			}

			@Override
			protected void doCustomBinds()
			{
				bind(MyIntf.class).to(MyImpl.class);
			}
		});
	}
}
