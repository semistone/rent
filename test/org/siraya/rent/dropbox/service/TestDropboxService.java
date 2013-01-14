package org.siraya.rent.dropbox.service;
import java.util.HashMap;
import java.util.Map;

import org.siraya.rent.pojo.Image;
import org.siraya.rent.pojo.Member;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.dropbox.dao.ImageDao;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.util.Assert;
public class TestDropboxService {
	ImageDao dao;
	private Mockery context;
	private IApplicationConfig config;
	DropboxService test;
	Image image;
	private Map<String, Object> setting;
	@Before
	public void setUp() throws Exception{
		test = new DropboxService();
		context = new JUnit4Mockery();
		dao = context.mock(ImageDao.class);	
		config = new org.siraya.rent.utils.ApplicationConfig();
		test.setApplicationConfig(config);
		test.setImageDao(dao);
		image = new Image();
		image.setUserId("user");
		image.setImgGroup("img_group1");
		image.setImgTarget("WebContent/apple-touch-icon.png");
		test.init();
		
	}
    @Test 
	public void testConfig(){
		Assert.notNull(config.get("dropbox").get("app_key"));
		Assert.notNull(config.get("dropbox").get("token_key"));
	}
    @Test 
	public void testUpload() throws Exception{
		context.checking(new Expectations() {
			{
				one(dao).insert(image);
				will(returnValue(1));
			}
		});
		test.upload(image);
	}
}
