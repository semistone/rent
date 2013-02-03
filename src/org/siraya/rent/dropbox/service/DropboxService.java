package org.siraya.rent.dropbox.service;

import java.io.*;
import java.net.URI;
import java.util.Map;

import org.siraya.rent.dropbox.dao.*;
import org.siraya.rent.pojo.*;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.imageio.ImageIO;
import java.util.*;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.ThumbFormat;
import com.dropbox.client2.DropboxAPI.ThumbSize;
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
	@Autowired
	private ImageGroupDao imageGroupDao;

	private static Logger logger = LoggerFactory.getLogger(DropboxService.class);

	private static String DIR = "/";
	private DropboxAPI<WebAuthSession> api;
	private static String SEPERATOR = "/";
	private static Map<String,Object> imgSetting;
	
	private static final int IO_BUFFER_SIZE = 4 * 1024;  
	private boolean isInit =false;
	public DropboxAPI<WebAuthSession> getApi() {
		init();
		return api;
	}

	public void init() {
		if (isInit) return;
		Map<String, Object> settings = applicationConfig.get("dropbox");
		AppKeyPair consumerTokenPair = new AppKeyPair(
				(String) settings.get("app_key"),
				(String) settings.get("app_secret"));
		imgSetting = applicationConfig.get("image");

		WebAuthSession session = new WebAuthSession(consumerTokenPair,
				AccessType.APP_FOLDER);
		session.setAccessTokenPair(new AccessTokenPair((String) settings
				.get("token_key"), (String) settings.get("token_secret")));
		api = new DropboxAPI<WebAuthSession>(session);
		isInit =true;
	}

	/**
	 * check img file name extention
	 * @param ext
	 */
	private void checkExtend(String ext) {
		this.init();
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

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void update(Image img){
		File src = new File(img.getImgTarget());
		this.validateImageSize(src, img);
		String ext = src.getName();
		ext = ext.substring(ext.lastIndexOf(".")+1);
		this.checkExtend(ext);
		imageDao.update(img);
	}
	
	private void save(Image img, boolean isUpload) {
		if (img.getId() == null) {
			img.setId(Image.genId());
		}
		this.loadOrInsertImageGroup(img);
		
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
			String url = this.upload(src, img);
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

	private void loadOrInsertImageGroup(Image image){
		if (image.getImageGroup() != null) {
			return;
		} 
		String groupId = image.getGroupId();
		if (groupId == null) {
			ImageGroup group = this.imageGroupDao.getByUserAndPath(image.getUserId(), image.getImgGroup());			
			if (group != null) {
				image.setImageGroup(group);
				return;
			}
			group = new ImageGroup();
			image.setImageGroup(group);
			this.imageGroupDao.insert(group);
		}  else {
			ImageGroup group = this.imageGroupDao.get(groupId);
			image.setImageGroup(group);
		}
	}
	
	private String upload(File src,Image image) {
		try {
			String target = image.getImgTarget();
			FileInputStream fis = new FileInputStream(src);
			if (api == null) init();
			
			if (image.getRev() == null) {
				Entry entry = api.putFileOverwrite(target, fis, src.length(), null);
				image.setRev(entry.rev);				
			} else {
				Entry entry = api.putFile(target, fis, src.length(),image.getRev(), null);
				image.setRev(entry.rev);								
			}
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
				+ SEPERATOR + img.getName();
		logger.debug("sync id " + img.getId() + " target is " + target);
		img.setImgTarget(target);
		String url = this.upload(src,img);
		img.setShareUrl(url);
		img.setModified(0);
		img.setStatus(1);
		int ret = this.imageDao.update(img);
		if (ret != 1) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"update fail");
		}
		src.delete();
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Image> getGroup(String groupId){
		return this.imageDao.getImageByGroup(groupId);
	}
	
	
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void delete(Image image){
		String id = image.getId();
		logger.info("delete image id " + id+" user id "+image.getUserId());
		image=  this.get(id);
		int ret = this.imageDao.delete(image.getId(), image.getUserId());
		if (ret != 1) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"delete id not found ret is "+ret);
		}
		if (image.getStatus() == 0) {
			File f = new File(image.getImgTarget());
			if (!f.exists()) {
				logger.info("file already not exist");
				return;
			}
			if (!f.delete()) {
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral,
						"delete file error");
			} else {
				logger.info("delete file "+id +" success");
			}
		} else {
			DropboxAPI<WebAuthSession> api = this.getApi();
			try {
				api.delete(image.getImgTarget());
				logger.info("delete file "+id +" success");
			} catch (Exception e) {
				logger.error("delete file from dropbox error", e);
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral,
						"delete from dropbox error");
			}
		}
	}
	
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public Image get(String id) {
		Image image=  this.imageDao.get(id);
		//
		// image not found
		//
		if (image == null) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"image not found");
		}
		return image;
	}
	
	public void thumbnail(Image image, String size, java.io.OutputStream out){
		if (image.getStatus() == 0){
			throw new RentException(RentException.RentErrorCode.ErrorStatusViolate,"only uploaded pic can show thumbnail");
		}
		DropboxAPI<WebAuthSession> api = getApi();
		try {
			api.getThumbnail(image.getImgTarget(), out,
					this.getSize(size), this.getFormat(image.getExt()), null);
		}catch (Exception e){
			logger.error("get thumbnail fail", e);
			throw new RentException(RentException.RentErrorCode.ErrorMobileGateway,"remote exception");
		}
	}
	
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void syncMeta(String userId, String group) {
		DropboxAPI<WebAuthSession> api = getApi();
		String target = DIR + userId + SEPERATOR + group;
		Entry ent = null;
		//
		// get meta from dropbox
		//
		try { 
			logger.debug("fetch meta in "+target);
			ent = api.metadata(target, 1000, null, true, null);
			if (ent == null) {
				logger.warn("dir "+target+" not exist");
			}
		} catch(Exception e){
			logger.error("get meta fail", e);
			throw new RentException(RentException.RentErrorCode.ErrorMobileGateway,"get dropbox meta fail");
		}
		if (!ent.isDir) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,target+" is not dir");
		}
		if (ent.isDeleted) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,target+" has deleted");			
		}
		//
		// get images from database.
		//
		List<Image> images=this.imageDao.getImage(userId, group);
		HashMap<String,Image> map = new HashMap<String,Image>();
		for (Image image : images) {
			logger.debug("file "+image.getName());
			map.put(image.getName(), image);
		}
		//
		// compare database and entrys from dropbox.
		//
		List<Entry> contents = ent.contents;
		for(Entry entry : contents) {
			String filename = entry.fileName();
			logger.debug(filename+" in dropbox");
			if (map.containsKey(filename)){
				logger.debug("remove "+filename+" from map");
				map.remove(filename);
			} else {
				logger.debug("insert "+filename+" into database");
				Image image = new Image();
				image.setUserId(userId);
				image.setId(Image.genId());
				image.setName(filename);
				image.setStatus(1);
				image.setImgGroup(group);
				String imgTarget = target + SEPERATOR + filename;
				image.setImgTarget(imgTarget);
				try {
					DropboxLink link = api.share(imgTarget);
					image.setShareUrl(link.url);
				}catch(Exception e){
					logger.debug("fetch share url fail");
				}
		
				this.imageDao.insert(image);
			}			
		}
		//
		// remove database entry which not exist in dropbox.
		//
		for(String key: map.keySet()) {
			logger.debug("delete "+key);
			String id = ((Image)map.get(key)).getId();
			this.imageDao.delete(id, userId);
		}
	}
	
	private ThumbSize getSize(String size){
		switch (size) {
		case "small":
			return ThumbSize.ICON_32x32;
		case "medium":
			return ThumbSize.ICON_64x64;
		case "large":
			return ThumbSize.ICON_128x128;
		default:
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"thumb size not support");
		}
	}
	private ThumbFormat getFormat(String format){
		switch (format) {
		case "png":
			return ThumbFormat.PNG;
		case "jpg":
			return ThumbFormat.JPEG;
		default:
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"thumb format not support");
		}
	}
	
	public ImageGroupDao getImageGroupDao() {
		return imageGroupDao;
	}

	public void setImageGroupDao(ImageGroupDao imageGroupDao) {
		this.imageGroupDao = imageGroupDao;
	}

}
