package com.tectonica.gae;

import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

/**
 * PhaseListener to ensure the HttpSession data is written to the datastore.
 * 
 * <p>
 * If properly configured, an application deployed on the <a href="http://developers.google.com/appengine">Google App Engine<a> platform can
 * leverage HttpSessions to store data between requests. Session data is stored in the datastore and memcache is also used for speed.
 * Session data is persisted at the <strong>end</strong> of the request.
 * </p>
 * <p>
 * The App Engine session implementation will not recognize if properties of objects stored in the session are changed which is why we have
 * this <code>PhaseListener</code> which will modify a session attribute with the current date and time (in milliseconds) at the end of
 * every phase.
 * </p>
 * 
 * @author Derek Berube, Wildstar Technologies, LLC.
 * @version 2014-01-07, 1.0
 * 
 * @see https://developers.google.com/appengine/docs/java/config/appconfig#Java_appengine_web_xml_Enabling_sessions
 * @see http://stackoverflow.com/questions/19259457/session-lost-in-google-app-engine-using-jsf
 */
public class GaePhaseListener implements PhaseListener
{
	private static final long serialVersionUID = -8246272798261076270L;

	private static final String _CLASS = GaePhaseListener.class.getName();
	private static final Logger logger = Logger.getLogger(_CLASS);
	private static final String TIME_KEY = "NOW";

	@Override
	public void afterPhase(PhaseEvent event)
	{
		logger.entering(_CLASS, "afterPhase(PhasseEvent)", event);
		FacesContext ctx = null;
		ExternalContext eCtx = null;
		Map<String, Object> sessionMap = null;

		ctx = event.getFacesContext();
		eCtx = ctx.getExternalContext();
		sessionMap = eCtx.getSessionMap();
		sessionMap.put(TIME_KEY, System.currentTimeMillis());
		logger.exiting(_CLASS, "afterPhase(PhaseEvent)");
	}

	@Override
	public void beforePhase(PhaseEvent event)
	{
		logger.entering(_CLASS, "beforePhase(PhaseEvent)", event);
		logger.exiting(_CLASS, "beforePhase(PhaseEvent)");

	}

	@Override
	public PhaseId getPhaseId()
	{
		logger.entering(_CLASS, "getPhaseId(PhasseEvent)");
		PhaseId phaseId = PhaseId.ANY_PHASE;
		logger.exiting(_CLASS, "getPhaseId(PhaseEvent)", phaseId);
		return phaseId;
	}
}
