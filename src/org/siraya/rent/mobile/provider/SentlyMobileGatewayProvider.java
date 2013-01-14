<<<<<<< HEAD
package org.siraya.rent.mobile.provider;
import org.siraya.rent.donttry.service.IDontTryService;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.HashMap;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.springframework.web.client.RestOperations;;
public class SentlyMobileGatewayProvider implements IMobileGatewayService{
    @Autowired
    protected IApplicationConfig applicationConfig;
    private RestOperations restTemplate;
    @Autowired
    protected IDontTryService dontTryService;
	static String REQUEST_URI = "http://sent.ly/command/sendsms?username={username}&password={password}&text={text}&to={to}";

    public SentlyMobileGatewayProvider(RestOperations restTemplate){
    	this.restTemplate=restTemplate;
    }
    private static Logger logger = LoggerFactory.getLogger(SentlyMobileGatewayProvider.class);

    /**
     * send SMS message.
     * 
     */
	public void sendSMS(String to,String message) {
		Map<String,Object> setting=applicationConfig.get("sently");

		int maxCount = (Integer)setting.get("max_msgs_per_day");
		dontTryService.doTry("sently gateway", IDontTryService.DontTryType.Today, maxCount);
		logger.info("send sms to "+to+"\nmeesage:"+message+"\n");

		if (setting.get("debug").equals(true) ) {
			logger.info("send sms in debug mode don't send anything");
			return;
		}
		Map<String, String> vars=new HashMap<String, String>();
		vars.put("username", (String)setting.get("username"));
		vars.put("password", (String)setting.get("password"));
		vars.put("to", to);
		vars.put("text", message);
		String result = restTemplate.getForObject(REQUEST_URI, String.class,vars);
		if (result.startsWith("Error:")) {
			logger.debug("sently result is "+result);
			int code = Integer.parseInt(result.substring(6, 7));
			throw new RentException(RentErrorCode.ErrorMobileGateway,"send sms fail "+this.errorMsg(code));
		}
	}
	
	/**
	 * translate error code base on 
	 * https://docs.google.com/document/d/1MuFXPTWq7zNIChwZdzIrqiQ2-Mb3_rPrWqNlOZXJNws/edit?pli=1
	 * @param code
	 * @return
	 */
	private String errorMsg(int code){
		switch(code){
		case 0:
			return "Authentication Error";
		case 1:
			return "Malformed Parameters";
		case 3:
			return "No device to send SMS";
		case 4:
			return "No credits";
		default:
			return "unknwon error code "+code;

		}
	}
}
=======
package org.siraya.rent.mobile.provider;

import org.siraya.rent.donttry.service.IDontTryService;
import org.siraya.rent.filter.UserRole;
import org.siraya.rent.mobile.dao.IMobileProviderDao;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import org.siraya.rent.pojo.Role;
import java.util.HashMap;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;
import org.siraya.rent.pojo.MobileProvider;
import org.siraya.rent.user.dao.IRoleDao;
public class SentlyMobileGatewayProvider implements IMobileGatewayService {
	@Autowired
	protected IApplicationConfig applicationConfig;
	private RestOperations restTemplate;
	@Autowired
	protected IDontTryService dontTryService;
	@Autowired
	private IMobileProviderDao mobileProviderDao;
	@Autowired
	private EncodeUtility encodeUtility;
	@Autowired
	private IRoleDao roleDao;
	static String REQUEST_URI = "http://sent.ly/command/sendsms?username={username}&password={password}&text={text}&to={to}";
	static String PROVIDER_TYPE = "SENTLY";

	public SentlyMobileGatewayProvider(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	private static Logger logger = LoggerFactory
			.getLogger(SentlyMobileGatewayProvider.class);

	/**
	 * send SMS message.
	 * 
	 */
	public void sendSMS(String provider, String to, String message) {
		Map<String, Object> setting = applicationConfig.get("sently");

		int maxCount = (Integer) setting.get("max_msgs_per_day");
		dontTryService.doTry("sently gateway",
				IDontTryService.DontTryType.Today, maxCount);
		logger.info("send sms to " + to + "\nmeesage:" + message + "\n");

		if (setting.get("debug").equals(true)) {
			logger.info("send sms in debug mode don't send anything");
			return;
		}
		Map<String, String> vars = new HashMap<String, String>();
		if (provider == null) {
			//
			// get from default provider
			//
			vars.put("username", (String) setting.get("username"));
			vars.put("password", (String) setting.get("password"));
		} else {
			MobileProvider mobileProvider = this.mobileProviderDao.get(
					provider, PROVIDER_TYPE);
			if (mobileProvider == null) {
				throw new RentException(
						RentException.RentErrorCode.ErrorMobileGateway,
						"provider not found");
			}
			vars.put("username", mobileProvider.getUser());
			vars.put("password", this.encodeUtility.decrypt(
					mobileProvider.getPassword(), MobileProvider.ENCRYPT_KEY));
		}
		vars.put("to", to);
		vars.put("text", message);
		String result = restTemplate.getForObject(REQUEST_URI, String.class,
				vars);
		if (result.startsWith("Error:")) {
			logger.debug("sently result is " + result);
			int code = Integer.parseInt(result.substring(6, 7));
			throw new RentException(RentErrorCode.ErrorMobileGateway,
					"send sms fail " + this.errorMsg(code));
		}
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void newProvider(MobileProvider mobileProvider) {
		mobileProvider.setType(PROVIDER_TYPE);
		String password = encodeUtility.encrypt(mobileProvider.getPassword(),
				MobileProvider.ENCRYPT_KEY);
		mobileProvider.setPassword(password);
		try {
			mobileProviderDao.newProvider(mobileProvider);
			logger.debug("add new role mobile provider");
		}catch(org.springframework.dao.DuplicateKeyException e){
			int ret = mobileProviderDao.updateProvider(mobileProvider);			
			if (ret != 1) {
				throw new RentException(
						RentException.RentErrorCode.ErrorStatusViolate,
						"update null record");
			}
		}
		Role role = new Role();
		role.setUserId(mobileProvider.getId());
		role.setRoleId(UserRole.UserRoleId.MOBILE_PROVIDER.getRoleId());
		this.roleDao.newRole(role);

	}

	/**
	 * translate error code base on
	 * https://docs.google.com/document/d/1MuFXPTWq7zNIChwZdzIrqiQ2
	 * -Mb3_rPrWqNlOZXJNws/edit?pli=1
	 * 
	 * @param code
	 * @return
	 */
	private String errorMsg(int code) {
		switch (code) {
		case 0:
			return "Authentication Error";
		case 1:
			return "Malformed Parameters";
		case 3:
			return "No device to send SMS";
		case 4:
			return "No credits";
		default:
			return "unknwon error code " + code;

		}
	}
}
>>>>>>> master
