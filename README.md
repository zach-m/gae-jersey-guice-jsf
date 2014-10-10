Template for a Google AppEngine REST Application
=

Relying on GAE-friendly versions of:

* Jersey (1.18.1)
* Jackson (1.9.2)
* Guice (3.0)

Available online at: <http://gae-jersey-guice.appspot.com> 

Even though there are newer generations for these products (Jersey 2.x and Jackson 2.x), Google AppEngine still works with Servlet specifications version 2.5, and doesn't play well with those advanced generations (that were built to cater for Servlet 3.0 specifications).

This template project provides all the required configuration (`web.xml`, Jersey, Jackson, Guice) so that all features from all products work smoothly with each other. It also provides the code for two important functions (easily removable if unneeded):

* CORS filter - to add CORS headers that are mandatory for public REST APIs
* `@PostConstruct` annotation for injectable beans. This allows you the initialize your classes outside the constructor (which is invoked before the dependencies are injected)

The code demonstrates two types of APIs (implemented as JAX-RS annotated classes): 

* `InfoAPI` - Returns an HTML page with the environmental settings, including those of the `HttpRequest` (hence it is RequestScoped)
* `DebugAPI` - Demonstrates how to return a Java bean (`MyBean`) as either JSON or XML (this one is a `@Singleton`)

The code also demonstrates how to bind and inject an implementation class (`MyImpl`) into an interface placeholder (`MyIntf`).

All Jersey and Guice configuration parameters are set in `GuiceConfig`, and Jackson's are set in `JsonProvider`.

To build:

	mvn clean package
	
To execute locally (on <http://localhost:8080>):

	mvn appengine:devserver

To deploy to GAE:

	mvn appengine:update

