package org.siraya.rent.rest;

import javax.ws.rs.core.StreamingOutput;
import java.net.HttpURLConnection;
import java.util.*;
import java.io.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.OutputStream;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.File;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.Image;
import org.siraya.rent.utils.*;
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
	private FileUtility fileUtilty;
	@Autowired
	private IDropboxService dropboxService;

	private static Logger logger = LoggerFactory
			.getLogger(ImageUploadApi.class);

	
	/**
	 * test script
	 * curl 'http://192.168.56.1:8080/rent/rest/image/test1/test2.jpg' --data-binary  @/mnt/hgfs/Desktop/Desktop/theme/images/test.jpg  --cookie D=xxx
	 * @param imgGroup
	 * @param imgTarget
	 * @param requestBodyStream
	 * @return
	 * @throws Exception
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{image_group}/{image_target}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response upload(@PathParam("image_group") String imgGroup,
			@PathParam("image_target") String imgTarget,
			InputStream requestBodyStream) throws Exception {
		java.util.Map<String, Object> setting = applicationConfig
				.get("general");

		String baseUrl = (String) setting.get("base_url");

		String dest = imgGroup + "/" + imgTarget;
		File f = fileUtilty.copyInputToTmp(dest, requestBodyStream, false);

		Image image = null;
		try {
			logger.info("image upload complete");
			image = new Image();
			image.setId(Image.genId());
			image.setName(imgTarget);
			image.setUserId(userAuthorizeData.getUserId());
			image.setImgGroup(imgGroup);
			image.setImgTarget(f.toString());
			String shareUrl = baseUrl + "/image/" + image.getId();
			logger.debug("share url is " + shareUrl + " target is " + dest
					+ " group " + imgGroup + " user " + image.getUserId());
			image.setShareUrl(shareUrl);
			dropboxService.save(image);
		} catch (Exception e) {
			logger.debug("delete file");
			//f.delete();
			throw e;
		}
		return Response.status(HttpURLConnection.HTTP_OK).entity(image).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response update(@PathParam("id") String id,
			InputStream requestBodyStream) throws Exception {
		java.util.Map<String, Object> setting = applicationConfig
				.get("general");
		String baseUrl = (String) setting.get("base_url");
		final Image image = dropboxService.get(id);
		if (!image.getUserId().equals(userAuthorizeData.getUserId())) {
			throw new RentException(
					RentException.RentErrorCode.ErrorPermissionDeny, "deny");
		}
		String dest = image.getImgGroup() + "/" + image.getName();
		File f = fileUtilty.copyInputToTmp(dest, requestBodyStream, true);
		image.setImgTarget(f.toString());
		image.setStatus(0);
		String shareUrl = baseUrl + "/image/" + image.getId();
		image.setShareUrl(shareUrl);
		dropboxService.update(image);
		return Response.status(HttpURLConnection.HTTP_OK).entity(image).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public void delete(@PathParam("id") String id) {
		Image image = new Image();
		image.setUserId(userAuthorizeData.getUserId());
		image.setId(id);
		dropboxService.delete(image);
	}

	@GET
	@Path("/thumbnail/{size}/{id}")
	public Response thumbnail(@PathParam("id") String id,
			@PathParam("size") final String size) {
		int idx = id.indexOf(".");
		String ext = null;
		if (idx > 0) {
			ext = id.substring(idx + 1);
			id = id.substring(0, idx);
		}

		final Image image = dropboxService.get(id);
		StreamingOutput stream = new StreamingOutput() {
			public void write(OutputStream output) throws IOException {
				try {
					dropboxService.thumbnail(image, size, output);
				} catch (Exception e) {
					logger.error("copy stream error", e);
					throw new RentException(
							RentException.RentErrorCode.ErrorGeneral,
							"copy stream error");
				}
			}
		};
		if (ext != null && !ext.equals(image.getExt())) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"ext not match");
		}
		return Response.ok(stream).status(HttpURLConnection.HTTP_OK)
				.type("image/" + image.getExt()).build();
	}

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") String id) {
		final Image image = dropboxService.get(id);
		int idx = id.indexOf(".");
		String ext = image.getExt();
		String ext2 = null;
		if (idx > 0) {
			ext2 = id.substring(idx + 1);
			id = id.substring(0, idx);
		}

		if (ext2 != null && !ext2.equals(ext)) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"ext not match");
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
						fileUtilty.copyToOutputStream(new File(image.getImgTarget()),
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

	@GET
	@Path("/sync_meta/{image_group}")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public void syncMeta(@PathParam("image_group") String imgGroup) {
		this.dropboxService.syncMeta(userAuthorizeData.getUserId(), imgGroup);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get_by_group_id/{group_id}")
	public List<Image> getGroup(@PathParam("group_id") String groupId) {
		return this.dropboxService.getGroup(groupId);
	}



	public FileUtility getFileUtilty() {
		return fileUtilty;
	}

	public void setFileUtilty(FileUtility fileUtilty) {
		this.fileUtilty = fileUtilty;
	}

	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}

	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}

	public IDropboxService getDropboxService() {
		return dropboxService;
	}

	public void setDropboxService(IDropboxService dropboxService) {
		this.dropboxService = dropboxService;
	}

}
