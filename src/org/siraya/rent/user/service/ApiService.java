package org.siraya.rent.user.service;
import org.siraya.rent.pojo.*;
import org.siraya.rent.user.dao.IDeviceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
public class ApiService implements IApiService{
	@Autowired
    private IDeviceDao deviceDao;
	
	/**
	 * 
	 * @param userId
	 * @param name
	 */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public Device apply(String userId,String name){
		Device device = new Device();
		device.setUserId(userId);
		device.setName(name);
		device.setId(Device.genId());
		device.setStatus(DeviceStatus.ApiKeyOnly.getStatus());
		device.setModified(0);
		device.genToken();
		this.deviceDao.newDevice(device);
		return device;
	}
}
