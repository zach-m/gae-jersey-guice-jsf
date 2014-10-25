package com.tectonica.engine;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyImpl implements MyIntf
{
	@Inject
	protected Logger log;

	@PostConstruct
	private void postConstruct()
	{
		log.info("Created MySuperImpl()");
	}

	@Override
	public void foo()
	{
		log.info("MyImpl.foo()");
	}
}
