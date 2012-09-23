package org.siraya.rent.mobile;

import org.junit.Test;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})
public class TestSentlyMobileGatewayProvider  extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IMobileGatewayService mobileGatewayService;
    
    @Test   
    public void testSendSMS()throws Exception{
    	mobileGatewayService.sendSMS("886936072281", "Hello world");
    }
}
