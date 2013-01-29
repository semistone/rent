package org.siraya.rent.rest;
import javax.ws.rs.core.StreamingOutput;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.io.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.OutputStream;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.File;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.Image;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.dropbox.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component("imageRestApi")
@Path("/image/")
public class ImageUploadApi {
	@Autowired
	private UserAuthorizeData userAuthorizeData;
    @Autowired
    private IApplicationConfig applicationConfig;
    @Autowired
    private IDropboxService dropboxService;
    private static Logger logger = LoggerFactory.getLogger(ImageUploadApi.class);
	
    
    @POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{image_group}/{image_target}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response upload(@PathParam("image_group") String imgGroup,@PathParam("image_target") String imgTarget,  InputStream requestBodyStream) throws Exception{
		java.util.Map<String,Object> setting = applicationConfig.get("general");
		String tmpDir = (String)setting.get("tmp_dir");
		String baseUrl = (String)setting.get("base_url");
		
		String dest = tmpDir + "/" + imgGroup + "/" + imgTarget;

		this.mkdir(tmpDir + "/" + imgGroup);
		File f = new File(dest);
		//
		// check file already exists
		//
		if (f.exists()) {
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,"file already exist");
		}
		
		
		//
		// copy file to tmp
		//
		saveFileToTmp(f, requestBodyStream);
		try{
			logger.info("image upload complete");
			Image image = new Image();
			image.setId(Image.genId());
			image.setUserId(userAuthorizeData.getUserId());
			image.setImgGroup(imgGroup);
			image.setImgTarget(dest);
			String shareUrl = baseUrl + "/image/"+image.getId();
			logger.debug("share url is "+shareUrl+ " target is "+dest+ " group "+imgGroup+" user "+image.getUserId());
			image.setShareUrl(shareUrl);
			dropboxService.save(image);
		}catch(Exception e){
			logger.debug("delete file");
			f.delete();
			throw e;
		}		
		return Response.status(HttpURLConnection.HTTP_OK).entity(new HashMap<String,String>()).build();
	}
    
    @DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
    public void delete(@PathParam("id") String id){
		Image image = new Image();
		image.setUserId(userAuthorizeData.getUserId());
		image.setId(id);
		dropboxService.delete(image);
    }
    
	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") String id) {
		final Image image = dropboxService.get(id);
		String ext = image.getImgTarget();
		ext = ext.substring(ext.lastIndexOf(".") + 1 );
		//
		// image not found
		//
		if (image == null) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"no image id ");
		}
		//
		// image had upload to dropbox, use redirect
		//
		if (image.getStatus() == 1) {
			try {
				return Response.seeOther(new java.net.URI(image.getShareUrl()))
						.build();
			} catch (Exception e) {
				logger.error("url error", e);
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral, "url error");
			}
		} else {
			//
			// image still in local, load into response.
			//
			StreamingOutput stream = new StreamingOutput() {
				public void write(OutputStream output) throws IOException {
					try {
						copyToOutputStream(new File(image.getImgTarget()),
								output);

					} catch (Exception e) {
						logger.error("copy stream error", e);
						throw new RentException(
								RentException.RentErrorCode.ErrorGeneral,
								"copy stream error");
					}
				}
			};

			return Response.ok(stream).status(HttpURLConnection.HTTP_OK)
					.type("image/" + ext).build();
		}
	}
 
	private void copyToOutputStream(File f, OutputStream os) throws Exception{
		java.io.FileInputStream fis = null;
		try{
			fis = new FileInputStream(f);
			byte[] buf = new byte[8192];
			int n =0;
			while (-1 != (n = fis.read(buf))) {
				os.write(buf, 0, n);
			}
		}finally{
			if (fis != null) {
				fis.close();				
			}
		}		
	}
    private void saveFileToTmp(File f, InputStream is) throws Exception{
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(f);
			byte[] buf = new byte[8192];
			int n =0;
			while (-1 != (n = is.read(buf))) {
				fos.write(buf, 0, n);
			}
		}finally{
			if (fos != null) {
				fos.flush();
				fos.close();				
			}
		}	
    }
	private void mkdir(String path) {
		logger.debug("mkdir "+path);
		File f = new File(path);
		if (f.exists()) {
			if (f.isDirectory()) {
				return;
			} else {
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral, "path "
								+ path + " is not dir");
			}
		} else {
			if (!f.mkdir()) {
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral, "mkdir "
								+ path + " fail");
			}
		}
	}
}
