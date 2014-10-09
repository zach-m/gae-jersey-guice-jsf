package com.tectonica.model;

import javax.inject.Singleton;

@Singleton
public class MyImpl implements MyIntf
{
	public MyImpl()
	{
		System.out.println("Created MyImpl()");
	}

	@Override
	public void foo()
	{
		System.out.println("MyImpl.foo()");
	}
}
