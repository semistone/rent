package org.siraya.rent.dropbox.service;
import org.w3c.dom.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Map;
import java.awt.Dimension;
import org.siraya.rent.donttry.service.DontTryService;
import org.siraya.rent.dropbox.dao.ImageDao;
import org.siraya.rent.pojo.Image;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.imageio.ImageIO;
import java.util.*;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.Session.AccessType;

import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.*;
@Service("dropboxService")
public class DropboxService implements IDropboxService {
	@Autowired
	private IApplicationConfig applicationConfig;
	@Autowired
	private ImageDao imageDao;
    private static Logger logger = LoggerFactory.getLogger(DropboxService.class);

	private static String DIR = "/";
	private DropboxAPI<WebAuthSession> api;
	private static String SEPERATOR = "/";
	private static Map<String,Object> imgSetting;
	

	public DropboxAPI<WebAuthSession> getApi() {
		if (api == null) {
			init();
		}
		return api;
	}

	public void init() {
		Map<String, Object> settings = applicationConfig.get("dropbox");
		AppKeyPair consumerTokenPair = new AppKeyPair(
				(String) settings.get("app_key"),
				(String) settings.get("app_secret"));
		

		WebAuthSession session = new WebAuthSession(consumerTokenPair,
				AccessType.APP_FOLDER);
		session.setAccessTokenPair(new AccessTokenPair((String) settings
				.get("token_key"), (String) settings.get("token_secret")));
		api = new DropboxAPI<WebAuthSession>(session);
	}

	/**
	 * check img file name extention
	 * @param ext
	 */
	private void checkExtend(String ext) {
		if (imgSetting == null && applicationConfig != null ) 
			imgSetting = applicationConfig.get("image");
		logger.debug("check ext for "+ext);
		List<String> exts = (List<String>) imgSetting.get("ext");
		if (!exts.contains(ext)) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter, "ext "
							+ ext + " not support");
		}
	}
    
	/**
	 * save into database without upload
	 * @param img
	 */
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void save(Image img) {
		this.save(img, false);
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void upload(Image img) {
		this.save(img, true);
	}

	private void save(Image img, boolean isUpload) {
		img.setId(Image.genId());
		File src = new File(img.getImgTarget());
		if (!src.isFile()) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,src+ " is not file");
		}
		this.validateImageSize(src, img);

		String ext = src.getName();
		ext = ext.substring(ext.lastIndexOf(".")+1);
		this.checkExtend(ext);
		//
		// target = /user_id/img_group/img_id
		//
		if (isUpload){
			String target = DIR + img.getUserId() + SEPERATOR
					+ img.getImgGroup() + SEPERATOR + img.getId()+ "."+ ext;
			logger.debug("target is "+target);
			img.setImgTarget(target);
			String url = this.upload(src, target);
			img.setShareUrl(url);			
		}
		try {	
			int count = imageDao.groupCount();
			int maxCount = (int) imgSetting.get("max_pics_per_group");
			if (count > maxCount) {
				throw new RentException(
						RentException.RentErrorCode.ErrorExceedLimit,
						"can't upload too manay images");
			}
			imageDao.insert(img);
		}catch(Exception e){
			logger.error("insert image fail",e);			
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,"insert image fail");
		}
	}

	private String upload(File src, String target) {
		try {
			FileInputStream fis = new FileInputStream(src);
			if (api == null) init();
			api.putFile(target, fis, src.length(), null, null);
			DropboxLink link = api.share(target);
			return link.url;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new RentException(RentException.RentErrorCode.ErrorGeneral, e.getMessage());
		}
	}

	public String doLink() {
		try {
			Map<String, Object> settings = applicationConfig.get("dropbox");
			AppKeyPair appKeyPair = new AppKeyPair(
					(String) settings.get("app_key"),
					(String) settings.get("app_secret"));
			WebAuthSession was = new WebAuthSession(appKeyPair,
					Session.AccessType.APP_FOLDER);

			// Make the user log in and authorize us.
			WebAuthSession.WebAuthInfo info = was.getAuthInfo();
			return info.url;
		} catch (DropboxException e) {
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,
					e.getMessage());
		}
	}

	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public ImageDao getImageDao() {
		return imageDao;
	}

	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}

	private void validateImageSize(File f, Image image){
		java.awt.image.BufferedImage readImage = null;
		if (imgSetting == null && applicationConfig != null ) 
			imgSetting = applicationConfig.get("image");
		try {
		    readImage = ImageIO.read(f);
		    int h = readImage.getHeight();
		    int w = readImage.getWidth();
		    image.setHeight(h);
		    image.setWidth(w);
		    image.setSize(f.length());
		    List<Integer> limit = (List<Integer>)imgSetting.get("limit");
		    if (limit.get(0) < h || limit.get(1) < w) {
		    	throw new RentException(RentException.RentErrorCode.ErrorInvalidParameter,"image size not match current "+h+":"+w);
		    }
		} catch (Exception e) {
		    readImage = null;
		    logger.error("validate image size error", e);
		    throw new RentException(RentException.RentErrorCode.ErrorInvalidParameter," input is not image ");
		}
	}

	/**
	 * 
	 */
	public void sync(){
		logger.info("execute sync");
		List<Image> images = this.imageDao.fetchImgNeedSync();
		for(Image image: images){
			this.sync(image,new File(image.getImgTarget()));

		}
	}
	
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	private void sync(Image img, File src) {
		String ext = img.getImgTarget();
		ext = ext.substring(ext.lastIndexOf(".") + 1);
		String target = DIR + img.getUserId() + SEPERATOR + img.getImgGroup()
				+ SEPERATOR + img.getId() + "." + ext;
		logger.debug("sync id " + img.getId() + " target is " + target);
		img.setImgTarget(target);
		String url = this.upload(src, target);
		img.setShareUrl(url);
		img.setModified(0);
		img.setStatus(1);
		int ret = this.imageDao.update(img);
		if (ret != 1) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"update fail");
		}
	}

}
