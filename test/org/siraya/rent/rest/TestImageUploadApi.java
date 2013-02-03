package org.siraya.rent.rest;

import org.siraya.rent.pojo.Image;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.siraya.rent.dropbox.service.*;
import org.junit.Test;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.utils.*;
import java.io.*;
import java.util.HashMap;

public class TestImageUploadApi {
	ImageUploadApi test;
	private Mockery context;
	private IApplicationConfig applicationConfig;
	private HashMap<String, String> setting;
	private UserAuthorizeData userAuthorizeData;
	private IDropboxService dropboxService;
	private String tmp = "D:/tmp";
	private String group = "group";
	private String target = "target";

	@Before
	public void setUp() {

		context = new JUnit4Mockery();
		test = new ImageUploadApi();
		applicationConfig = context.mock(IApplicationConfig.class);
		dropboxService = context.mock(IDropboxService.class);
		test.setApplicationConfig(applicationConfig);
		test.setDropboxService(dropboxService);
		setting = new HashMap<String, String>();
		setting.put("base_url", "http://xxx.com");
		setting.put("tmp_dir", tmp);
		new File(tmp + "/" + group + "/" + target).delete();
		FileUtility fileUtility = new FileUtility();
		fileUtility.setApplicationConfig(applicationConfig);
		test.setFileUtilty(fileUtility);
		userAuthorizeData = new UserAuthorizeData();
		test.setUserAuthorizeData(userAuthorizeData);
		userAuthorizeData.setUserId("user");
	}

	@Test
	public void testUpload() throws Exception {

		context.checking(new Expectations() {
			{
				one(applicationConfig).get("general");
				will(returnValue(setting));
				one(applicationConfig).get("general");
				will(returnValue(setting));
				one(dropboxService).save(with(any(Image.class)));
			}
		});

		InputStream inputStream = new java.io.ByteArrayInputStream(
				"test".getBytes());
		test.upload(group, target, inputStream);

	}

	@Test
	public void testUpdate() throws Exception {
		final String id = "test";
		final Image image = new Image();
		image.setName(target);
		image.setImgGroup(group);
		image.setUserId(userAuthorizeData.getUserId());
		context.checking(new Expectations() {
			{
				one(applicationConfig).get("general");
				will(returnValue(setting));
				one(applicationConfig).get("general");
				will(returnValue(setting));
				one(dropboxService).get(id);
				will(returnValue(image));

				one(dropboxService).update(with(any(Image.class)));
			}
		});
		InputStream inputStream = new java.io.ByteArrayInputStream(
				"test".getBytes());
		test.update(id, inputStream);

	}
}
