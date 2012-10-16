package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Update;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.springframework.stereotype.Repository;

@Repository("mobileAuthResponseDao")
public interface IMobileAuthResponseDao {
	
    @Update("UPDATE MOBILE_AUTH_REQUEST SET RESPONSE_TIME=#{responseTime}, STATUS=#{status} WHERE REQUEST_ID=#{requestId}")
    public int updateResponse(MobileAuthResponse response);
}
