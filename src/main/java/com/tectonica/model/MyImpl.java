package com.tectonica.model;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyImpl implements MyIntf
{
	@Inject
	private Logger log;

	@PostConstruct
	public void postConstruct()
	{
		log.info("Created MyImpl()");
	}

	@Override
	public void foo()
	{
		log.info("MyImpl.foo()");
	}
}
