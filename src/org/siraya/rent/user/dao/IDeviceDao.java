package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.siraya.rent.pojo.Device;
import org.springframework.stereotype.Repository;

@Repository("deviceDao")
public interface IDeviceDao {
    static final String INSERT_DEVICE_SQL = "INSERT INTO DEVICE (ID,USER_ID,TOKEN,STATUS,CREATED, MODIFIED) " +   
            " VALUES (#{id}, #{userId}, #{token}, #{status}, " +
            "#{created}, #{modified})";
  
    
    /**
     * 
     * @param device
     */
    @Insert(INSERT_DEVICE_SQL)
    public void newDevice(Device device);
    
    /**
     * 
     * @param userId
     * @return
     */
    @Select("select count(*) from DEVICE where USER_ID=#{userId}")
    public int getDeviceCountByUserId(@Param("userId")String userId);
    
    /**
     * 
     * 
     * @param id
     * @return
     */
    @Select("select * from DEVICE where ID=#{id}") 
    @ResultMap("rent.mapper.DeviceResultMap")
    public Device getDeviceByDeviceId(@Param("id")String id);
    
    @Update("update DEVICE set STATUS = #{newStatus}, AUTH_RETRY=AUTH_RETRY+1, MODIFIED=#{modified} where ID = #{id} and (STATUS= #{oldStatus} or STATUS=#{newStatus})")
    public int updateStatusAndRetryCount(@Param("id")String id,@Param("newStatus")int newStatus,@Param("oldStatus")int oldStatus,@Param("modified")Long modified);

    @Update("update DEVICE set STATUS = #{newStatus}, MODIFIED=#{modified} where ID = #{id} and STATUS= #{oldStatus}")    
    public int updateDeviceStatus(@Param("id")String id,@Param("newStatus")int newStatus,@Param("oldStatus")int oldStatus,@Param("modified")Long modified);
}
