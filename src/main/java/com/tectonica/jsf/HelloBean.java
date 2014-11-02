package com.tectonica.jsf;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

@ManagedBean
@SessionScoped
public class HelloBean implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Inject
	// session-bean goes through serialization, where injectables are not desired, hence transient
	private transient Logger LOG;

	@PostConstruct
	private void init()
	{
		LOG.info("Injection works!");
	}

	private String name = "Zach";

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}