package com.tectonica.api.v1;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.tectonica.gae.BlobServlet;

@Path("/upload")
@Singleton
public class UploadAPI
{
	private BlobstoreService blobSvc = BlobstoreServiceFactory.getBlobstoreService();

	/**
	 * Generates a link that can be consequently used in a {@code POST} command (with content {@code multipart/form-data}) to upload a file
	 * into the server so that it becomes publicly available for online access.
	 * <p>
	 * Before generating the link, it's possible to check whether the upload should be permitted in the given context.
	 * <p>
	 * Example of usage in a web page:
	 * 
	 * <pre>
	 * &lt;script type=&quot;text/javascript&quot;&gt;
	 *    function setLink() {
	 *       var xhr;
	 *       if (window.XMLHttpRequest) {
	 *          xhr = new XMLHttpRequest();
	 *       } else {
	 *          xhr = new ActiveXObject(&quot;Microsoft.XMLHTTP&quot;);
	 *       }
	 *       xhr.open(&quot;GET&quot;, &quot;../upload/..&quot;, false); // false = not a-sync
	 *       xhr.send();
	 *       document.getElementById(&quot;uploadForm&quot;).action = xhr.responseText;
	 *    }
	 * &lt;/script&gt;
	 * 
	 * &lt;fieldset&gt;
	 *    &lt;form id=&quot;uploadForm&quot; onsubmit=&quot;setLink();&quot; method=&quot;post&quot; enctype=&quot;multipart/form-data&quot;&gt;
	 *       &lt;p&gt;
	 *          Select file 1: &lt;input type=&quot;file&quot; name=&quot;file1&quot; /&gt;
	 *       &lt;/p&gt;
	 *       &lt;p&gt;
	 *          Select file 2: &lt;input type=&quot;file&quot; name=&quot;file2&quot; /&gt;
	 *       &lt;/p&gt;
	 *       &lt;input type=&quot;submit&quot; value=&quot;Upload&quot; /&gt;
	 *    &lt;/form&gt;
	 * &lt;/fieldset&gt;
	 * </pre>
	 */
	@GET
	@Path("/user/{uid}")
	@Produces("text/plain")
	public String generateLink(@PathParam("uid") String uid)
	{
		// TODO: check if uid is allowed to upload stuff..
		return blobSvc.createUploadUrl(BlobServlet.BLOB_URI);
	}
}
