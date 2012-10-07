package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.siraya.rent.pojo.Device;
import org.springframework.stereotype.Repository;
import java.util.List;
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
     * @param deviceId
     * @return
     */
    @Select("select count(*) from DEVICE where ID=#{deviceId}")
    public int getDeviceCountByDeviceId(@Param("deviceId")String deviceId);
    /**
     * 
     * 
     * @param id
     * @return
     */
    @Select("select * from DEVICE where ID=#{deviceId} and USER_ID=#{userId}") 
    @ResultMap("rent.mapper.DeviceResultMap")
    public Device getDeviceByDeviceIdAndUserId(@Param("deviceId")String deviceId,@Param("userId")String userId);
    
    @Update("update DEVICE set STATUS = #{newStatus}, AUTH_RETRY=AUTH_RETRY+1, MODIFIED=#{modified} where ID = #{id} and USER_ID=#{userId} and (STATUS in ('0','1','3'))")
    public int updateStatusAndRetryCount(@Param("id")String id,@Param("userId")String userId,@Param("newStatus")int newStatus,@Param("oldStatus")int oldStatus,@Param("modified")Long modified);

    @Update("update DEVICE set STATUS = #{newStatus}, MODIFIED=#{modified} where ID = #{id} and USER_ID=#{userId} and  STATUS= #{oldStatus}")    
    public int updateDeviceStatus(@Param("id")String id,@Param("userId")String userId,@Param("newStatus")int newStatus,@Param("oldStatus")int oldStatus,@Param("modified")Long modified);

    @Update("update DEVICE set STATUS = '3', MODIFIED=#{modified} where ID = #{id} and USER_ID=#{userId}")    
    public int updateRemovedDeviceStatus(@Param("id")String id,@Param("userId")String userId,@Param("modified")Long modified);

    @Select("select * from DEVICE where USER_ID=#{userId} and STATUS='1' order by MODIFIED desc") 
    @ResultMap("rent.mapper.DeviceResultMap")
    public Device getDeviceByUserIdAndStatusAuthing(String userId);
    
    @Update("update DEVICE set NAME = #{name}, MODIFIED=#{modified} where ID = #{id} and USER_ID=#{userId} and STATUS='2'")
    public int nameDevice(Device device);
    
    @Select("select * from DEVICE where USER_ID=#{userId} and STATUS !='3' order by MODIFIED desc limit #{limit} offset #{offset}")     
    @ResultMap("rent.mapper.DeviceResultMap")
    public List<Device> getUserDevices(@Param("userId")String userId, @Param("limit")int limit, @Param("offset")int offset);
}
