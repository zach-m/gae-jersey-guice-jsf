package com.tectonica.gae;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.annotate.JsonPropertyOrder;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.tectonica.util.Jackson1;

/**
 * A generic servlet, implementing GAE's blobstore protocol to upload and download static content. To use it, you need an entry in your
 * {@code web.xml} file:
 * 
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;Blobstore&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.tectonica.gae.GaeBlobServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;Blobstore&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/blob&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * Additionally, you need to create a API that would generate an upload link. This API should check permissions etc., and ultimately return:
 * 
 * <pre>
 * return BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(GaeBlobServlet.BLOB_URI);
 * </pre>
 * 
 * The link returned by the above code will take care of the actual upload and storage, and then forward the request to this servlet for
 * analysis and generation of response (JSON in our implementation).
 * 
 * @author Zach Melamed
 */
public class GaeBlobServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * taken from the {@code web.xml} servlet mapping for {@link GaeBlobServlet}
	 */
	public static final String BLOB_URI = "/blob";

	private final BlobstoreService blobSvc = BlobstoreServiceFactory.getBlobstoreService();
	private ImagesService imgSvc = ImagesServiceFactory.getImagesService();

	/**
	 * a callback handler, invoked by GAE after an upload ended successfully. we use it to analyze what was uploaded and return it to
	 * the sender in an organized JSON
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		Map<String, List<BlobKey>> uploads = blobSvc.getUploads(req);
		Map<String, List<FileInfo>> infos = blobSvc.getFileInfos(req);

		List<UploadRec> uploadRecs = new ArrayList<>(uploads.size());
		for (Entry<String, List<BlobKey>> entry : uploads.entrySet())
		{
			String fieldName = entry.getKey();
			BlobKey blobKey = entry.getValue().get(0);
			FileInfo info = infos.get(fieldName).get(0);

			String contentType = info.getContentType();
			final String url;
			if (contentType.startsWith("image/"))
				url = imgSvc.getServingUrl(ServingUrlOptions.Builder.withBlobKey(blobKey));
			else
				url = baseUrlOf(req) + BLOB_URI + "?k=" + blobKey.getKeyString();

			UploadRec upload = new UploadRec();
			upload.setFieldName(fieldName);
			upload.setCreatedOn(new Date());
			upload.setUrl(url);
			upload.setFilename(info.getFilename());
			upload.setContentType(contentType);
			upload.setSize(info.getSize());

			uploadRecs.add(upload);
		}

		res.setContentType("application/json");
		res.getOutputStream().print(Jackson1.propsToJson(uploadRecs));
	}

	/**
	 * serves of a blob stored in the datastore, identified by its blob-key
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		blobSvc.serve(new BlobKey(req.getParameter("k")), res);
	}

	private String baseUrlOf(HttpServletRequest req)
	{
		String url = req.getRequestURL().toString();
		return url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath();
	}

	// //////////////////////////////////////////////////////////////////////////////

	@JsonPropertyOrder({ "fieldName", "createdBy", "createdOn", "url", "filename", "contentType", "size" })
	public static class UploadRec
	{
		private String fieldName;
		private Date createdOn;
		private String url;
		private String filename;
		private String contentType;
		private long size;

		public String getFieldName()
		{
			return fieldName;
		}

		public void setFieldName(String fieldName)
		{
			this.fieldName = fieldName;
		}

		public Date getCreatedOn()
		{
			return createdOn;
		}

		public void setCreatedOn(Date createdOn)
		{
			this.createdOn = createdOn;
		}

		public String getUrl()
		{
			return url;
		}

		public void setUrl(String url)
		{
			this.url = url;
		}

		public String getFilename()
		{
			return filename;
		}

		public void setFilename(String filename)
		{
			this.filename = filename;
		}

		public String getContentType()
		{
			return contentType;
		}

		public void setContentType(String contentType)
		{
			this.contentType = contentType;
		}

		public long getSize()
		{
			return size;
		}

		public void setSize(long size)
		{
			this.size = size;
		}
	}
}