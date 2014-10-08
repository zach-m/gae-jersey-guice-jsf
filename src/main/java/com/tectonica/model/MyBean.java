package com.tectonica.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

// the following XML annotation can be removed if only JSON responses are needed
@XmlRootElement
public class MyBean
{
	private String name;
	private Date date;

	public MyBean()
	{
		System.out.println("Created MyBean()");
		date = new Date();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}
}
