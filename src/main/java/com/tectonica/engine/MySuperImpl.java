package com.tectonica.engine;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class MySuperImpl
{
	@Inject
	protected Logger log;

	@PostConstruct
	private void postConstruct()
	{
		log.info("Created MySuperImpl()");
	}
}
