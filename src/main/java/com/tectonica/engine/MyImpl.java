package com.tectonica.engine;

import javax.inject.Singleton;

@Singleton
public class MyImpl extends MySuperImpl implements MyIntf
{
	@Override
	public void foo()
	{
		log.info("MyImpl.foo()");
	}
}
