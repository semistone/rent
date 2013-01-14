<<<<<<< HEAD
package org.siraya.rent.mobile.service;

public interface IMobileGatewayService {
	
	public void sendSMS(String phone,String message);
}
=======
package org.siraya.rent.mobile.service;

import org.siraya.rent.pojo.MobileProvider;

public interface IMobileGatewayService {
	
	public void sendSMS(String provider, String phone,String message);
	
	public void newProvider(MobileProvider mobileProvider);
}
>>>>>>> master
