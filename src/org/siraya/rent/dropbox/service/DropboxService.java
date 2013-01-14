package org.siraya.rent.dropbox.service;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Map;

import org.siraya.rent.donttry.service.DontTryService;
import org.siraya.rent.dropbox.dao.ImageDao;
import org.siraya.rent.pojo.Image;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


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

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void upload(Image img) throws Exception {
		img.setId(Image.genId());
		File src = new File(img.getImgTarget());
		if (!src.isFile()) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,src+ " is not file");
		}
		
		String ext = src.getName();
		ext = ext.substring(ext.lastIndexOf(".")+1);
		//
		// target = /user_id/img_group/img_id
		//
		String target = DIR + img.getUserId() + SEPERATOR
				+ img.getImgGroup() + SEPERATOR + img.getId()+ "."+ ext;
		logger.debug("target is "+target);
		img.setImgTarget(target);
		String url = this.upload(src, target);
		img.setShareUrl(url);
		imageDao.insert(img);
	}

	private String upload(File src, String target) throws Exception {
		FileInputStream fis = new FileInputStream(src);
		api.putFile(target, fis, src.length(), null, null);
		DropboxLink link = api.share(target);
		return link.url;
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

}
