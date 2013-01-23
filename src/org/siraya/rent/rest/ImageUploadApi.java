package org.siraya.rent.rest;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.io.FileOutputStream;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
@Path("/upload")
public class ImageUploadApi {
	@Autowired
	private UserAuthorizeData userAuthorizeData;
    @Autowired
    private IApplicationConfig applicationConfig;
    @Autowired
    private DropboxService dropboxService;
    private static Logger logger = LoggerFactory.getLogger(ImageUploadApi.class);
	
    
    @POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/image/{image_group}/{image_target}")
	public Response upload(@PathParam("image_group") String imgGroup,@PathParam("image_target") String imgTarget,  InputStream requestBodyStream) throws Exception{
		java.util.Map<String,Object> setting = applicationConfig.get("general");
		String tmpDir = (String)setting.get("tmp_dir");
		String baseUrl = (String)setting.get("base_url");
		String dest = tmpDir + "/" + imgGroup + "/" + imgTarget;

		this.mkdir(tmpDir + "/" + imgGroup);
		File f = new File(dest);
		if (f.exists()) {
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,"file already exist");
		}
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(f);
			byte[] buf = new byte[8192];
			while (true) {
				int length = requestBodyStream.read(buf);
				if (length < 0)
					break;
				fos.write(buf, 0, length);
			}
		}finally{
			if (fos != null) {
				fos.flush();
				fos.close();				
			}
		}
		Image image = new Image();
		image.setUserId(userAuthorizeData.getUserId());
		image.setImgGroup(imgGroup); 
		image.setImgTarget(dest);
		String shareUrl = baseUrl+"/image/"+imgGroup+"/"+imgTarget;
		image.setShareUrl(shareUrl);
		dropboxService.upload(image);
		return Response.status(HttpURLConnection.HTTP_OK).entity(new HashMap<String,String>()).build();
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
