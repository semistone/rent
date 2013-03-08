package org.siraya.rent.dropbox.service;
import java.util.HashMap;
import java.util.Map;
import org.siraya.rent.repl.service.ILocalQueueService;
import org.siraya.rent.mobile.dao.IServiceProviderDao;
import org.siraya.rent.pojo.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import org.siraya.rent.dropbox.dao.*;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.util.Assert;
public class TestDropboxService {
	ImageDao dao;
	ImageGroupDao groupDao;
	private Mockery context;
	private String userId;
	private String group;
	private IApplicationConfig config;
	private IServiceProviderDao serviceProviderDao;
	private ILocalQueueService localQueueService;
	DropboxService test;
	Image image;
	private String user = "user";
	private String done = "http://tw.yahoo.com";
	private Map<String, Object> setting;
	@Before
	public void setUp() throws Exception{
		test = new DropboxService();
		context = new JUnit4Mockery();
		dao = context.mock(ImageDao.class);	
		groupDao = context.mock(ImageGroupDao.class);	
		serviceProviderDao = context.mock(IServiceProviderDao.class);	

		localQueueService = context.mock(ILocalQueueService.class);
		test.setDropboxQuque(localQueueService);
		config = new org.siraya.rent.utils.ApplicationConfig();
		test.setApplicationConfig(config);
		test.setImageDao(dao);
		test.setImageGroupDao(groupDao);
		test.setServiceProviderDao(serviceProviderDao);
		userId="user";
		group = "img_group1";
		image = new Image();
		image.setUserId(userId);
		image.setImgGroup(group);
		image.setImgTarget("WebContent/apple-touch-icon.png");
		test.afterPropertiesSet();
		
	}
    @Test 
	public void testConfig(){
		Assert.notNull(config.get("dropbox").get("app_key"));
		Assert.notNull(config.get("dropbox").get("token_key"));
	}
    
    @Test 
    public void testDoLink(){
		context.checking(new Expectations() {
			{
				one(serviceProviderDao).updateProvider(with(any(MobileProvider.class)));
				will(returnValue(1));
			}
		});
    	String url = test.doLink(user, done);
    	System.out.println(url);
    }
    

    @Test(expected=org.siraya.rent.utils.RentException.class)
    public void testRetrieveWebAccessToken()throws Exception{
    	final MobileProvider mobileProvider = new MobileProvider();
    	mobileProvider.setUser("xxx");
    	mobileProvider.setPassword("password");
    	context.checking(new Expectations() {
			{
				one(serviceProviderDao).get(user, DropboxService.PROVIDER_TYPE_REQUSET);
				will(returnValue(mobileProvider));
			}
		});
    	test.retrieveWebAccessToken(user);
    }
    
    @Test 
	public void testUpload() throws Exception{
		context.checking(new Expectations() {
			{
				one(groupDao).getByUserAndPath(userId, group);
				one(groupDao).insert(with(any(ImageGroup.class)));
				will(returnValue(1));
				one(dao).groupCount();				
				will(returnValue(1));
				one(dao).insert(image);
				will(returnValue(1));
				
			}
		});
		test.upload(image);
	}
    
    @Test 
    public void testSave() throws Exception{
		context.checking(new Expectations() {
			{
				one(groupDao).getByUserAndPath(userId, group);
				one(groupDao).insert(with(any(ImageGroup.class)));
				will(returnValue(1));
				one(dao).groupCount();				
				will(returnValue(1));
				one(dao).insert(image);
				will(returnValue(1));
				one(localQueueService).insert(with(any(org.siraya.rent.pojo.Message.class)));
				
			}
		});
    	
    	test.save(image);
    }
    
    @Test 
    public void testSyncMeta(){
    	final List<Image> list = new ArrayList<Image>();   
    	image.setName("file1.png");
    	image.setId("12313");
    	list.add(image);
		context.checking(new Expectations() {
			{
				one(dao).getImage(userId, group);
				will(returnValue(list));
				one(dao).delete(image.getId(), userId);
				one(dao).insert(with(any(Image.class)));
			}
		});
    	test.syncMeta(userId, group);	
    }
}
