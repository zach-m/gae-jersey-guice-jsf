Skeleton project for a GAE REST Application
=

Relying on GAE-friendly versions of:

* Jersey (1.18.1)
* Jackson (1.9.2)
* Guice (3.0)

Available online at: <http://gae-jersey-guice.appspot.com> 

To build:

	mvn clean package
	
To execute locally (on <http://localhost:8080>):

	mvn appengine:devserver

To deploy to GAE:

	mvn appengine:update

