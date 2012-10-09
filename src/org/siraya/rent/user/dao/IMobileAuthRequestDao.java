package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.springframework.stereotype.Repository;

@Repository("mobileAuthRequestDao")
public interface IMobileAuthRequestDao {
	
	
	@Insert("INSERT INTO MOBILE_AUTH_REQUEST (REQUEST_ID,FORCE_REAUTH,DONE,MOBILE_PHONE,AUTH_USER_ID,CALLBACK,REQUEST_TIME,REQUEST_FROM) values (#{requestId},#{forceReauth}" +
			",#{done},#{mobilePhone},#{authUserId},#{callback},#{requestTime},#{requestFrom})")
    public void newRequest(MobileAuthRequest request);
}
