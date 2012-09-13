package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.Device;
import org.springframework.stereotype.Repository;

@Repository("deviceDao")
public interface IDeviceDao {
    static final String INSERT_DEVICE_SQL = "INSERT INTO DEVICE (ID,USER_ID,TOKEN,STATUS,CREATED, MODIFIED) " +   
            " VALUES (#{id}, #{userId}, #{token}, #{status}, " +
            "#{created}, #{modified})";
    @Insert(INSERT_DEVICE_SQL)
    
    public void newDevice(Device device);
    
    @Select("select count(*) from DEVICE where USER_ID=#{userId}")
    public int deviceCountByUserId(@Param("loginId")String userId);

}
