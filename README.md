Template for a Google AppEngine REST Application
=

(Online demo available online at: <http://gae-jersey-guice-jsf.appspot.com>) 

Uses GAE-friendly versions of the following products:

* JAX-RS with [Jersey (1.18.1)](https://jersey.java.net/documentation/1.18)
* JSON with [Jackson (1.9.2)](http://wiki.fasterxml.com/JacksonRelease19)
* DI with [Guice (3.0)](https://github.com/google/guice/wiki/Guice30)
* JSF with [Mojarra (2.2.8-02)](https://javaserverfaces.java.net/2.2/index.html) - in a [separate branch](https://github.com/zach-m/gae-jersey-guice-jsf/tree/jsf) 
* JSF with [PrimeFaces (5.1)](http://www.primefaces.org/documentation) - in a [separate branch](https://github.com/zach-m/gae-jersey-guice-jsf/tree/jsf-primefaces)

As a bonus, there's also a demonstration of how to use:

* Templating with [FreeMarker 2.3.21](http://freemarker.org/docs/index.html) - in a [separate branch](https://github.com/zach-m/gae-jersey-guice-jsf/tree/freemarker)

Even though there are newer generations for these products (Jersey 2.x and Jackson 2.x), Google AppEngine still works with Servlet specifications version 2.5 (part of JavaEE 5), and doesn't play nicely with those advanced generations (that were built to cater for Servlet 3.0 specifications).

This template project provides all the required configuration (`web.xml`, Jersey, Jackson, Guice, Mojarra) so that all features from all products work smoothly together. It also provides the code for 3 typical requirements (easily removable if unneeded):

* CORS filter - to add CORS headers that are mandatory in public REST APIs
* `@PostConstruct` annotation for injectable beans. This allows you the initialize your classes outside the constructor (which is invoked before the dependencies are injected)
* GAE Blobstore support

The code demonstrates several types of APIs (implemented as JAX-RS annotated classes): 

* `TestAPI` - Demonstrates how to return a Java bean (`MyBean`) as either JSON or XML (this is a `@Singleton` resource)
* `InfoAPI` - Returns an HTML page with the environmental settings, including those of the `HttpRequest` (hence it is `@RequestScoped`)
* `UploadAPI` - Assists in using GAE Blobstore

The code also demonstrates how to bind and inject an implementation class (`MyImpl`) into an interface placeholder (`MyIntf`).

All Jersey and Guice configuration parameters are set in the class `RestConfig`. Jackson's JSON configuration parameters are set in `JsonProvider`.

To create a new project of you own, using this project as a template:

	mvn archetype:generate -Dfilter=com.tectonica:
	
And then pick the number corresponding to `gae-jersey-guice-jsf-archetype`

To build:

	mvn clean package
	
To execute locally (on <http://localhost:8080>):

	mvn appengine:devserver

To deploy to GAE:

	mvn appengine:update
