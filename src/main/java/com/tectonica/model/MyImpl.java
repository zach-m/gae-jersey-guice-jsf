package com.tectonica.model;

public class MyImpl implements MyIntf
{
	private MyImpl()
	{
		System.out.println("Created MyImpl()");
	}

	@Override
	public void foo()
	{
		System.out.println("MyImpl.foo()");
	}

	// /////////////////////////////////////////////////////////////////////////

	private static MyImpl instance = null;

	public static MyImpl getInstance()
	{
		if (instance == null)
			instance = new MyImpl();
		return instance;
	}
}
