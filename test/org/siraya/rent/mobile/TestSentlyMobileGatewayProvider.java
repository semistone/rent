package org.siraya.rent.mobile;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.mobile.provider.SentlyMobileGatewayProvider;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.donttry.service.IDontTryService;
import org.springframework.web.client.RestOperations;
import java.util.HashMap;

public class TestSentlyMobileGatewayProvider {
    private IMobileGatewayService mobileGatewayService;
	private Mockery context;
	private boolean isMock = true;
    private IApplicationConfig applicationConfig;
    private IDontTryService dontTryService;
    private RestOperations restTemplate;
    private HashMap<String,Object> setting;
    // for test 
    class SentlyMobileGatewayProviderForTest extends SentlyMobileGatewayProvider{
    	public SentlyMobileGatewayProviderForTest(IApplicationConfig iApplicationConfig,
    			IDontTryService dontTryService,
    			RestOperations restTemplate){
    		super(restTemplate);
    		this.dontTryService = dontTryService;
    		this.applicationConfig = iApplicationConfig;
    	}
    }
    
    @Before
	public void setUp(){
		if (isMock){
			context = new JUnit4Mockery();
			applicationConfig = context.mock(IApplicationConfig.class);	
			dontTryService = context.mock(IDontTryService.class);	
			restTemplate = context.mock(RestOperations.class);	
			mobileGatewayService = new SentlyMobileGatewayProviderForTest(applicationConfig,dontTryService,
					restTemplate);
			setting = new HashMap<String,Object>();
			setting.put("debug", false);
			setting.put("max_msgs_per_day", 99);
		}
	}
    @Test   
    public void testSendSMS()throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(applicationConfig).get("sently");
					will(returnValue(setting));
					one(restTemplate).getForObject(with(any(String.class)),
							with(any(Class.class)), with(any(HashMap.class)));
					will(returnValue("OK"));
					one(dontTryService).doTry("sently gateway", IDontTryService.DontTryType.Today, 99);
				}
			});
		}
    	mobileGatewayService.sendSMS("886936072281", "中文測試");
    }
    
    
    @Test(expected=org.siraya.rent.utils.RentException.class)
    public void testSendSMSWithError() throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(applicationConfig).get("sently");
					will(returnValue(setting));
					one(restTemplate).getForObject(with(any(String.class)),
							with(any(Class.class)), with(any(HashMap.class)));
					will(returnValue("Error:1"));
					one(dontTryService).doTry("sently gateway", IDontTryService.DontTryType.Today, 99);

				}
			});
		}
    	mobileGatewayService.sendSMS("886936072281", "Hello world");
    }
}
