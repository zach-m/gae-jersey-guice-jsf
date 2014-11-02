package com.tectonica.jsf;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

@ManagedBean(name = "editor")
public class EditorBean
{
	@Inject
	private Logger LOG;

	@PostConstruct
	private void init()
	{
		LOG.info("Injection is even easier is non-session-bean!");
	}

	private String value = "This editor is provided by PrimeFaces";

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}