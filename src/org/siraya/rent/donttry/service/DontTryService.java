package org.siraya.rent.donttry.service;

import org.siraya.rent.donttry.dao.IDontTryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.siraya.rent.pojo.DontTry;
import java.util.Calendar;


import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("dontTryService")
public class DontTryService implements IDontTryService{
    @Autowired
    private IDontTryDao dontTryDao;
    private static Logger logger = LoggerFactory.getLogger(DontTryService.class);
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void doTry(String name,DontTryType limitType,int maxCount){
    	DontTry dontTry = new DontTry();
    	dontTry.setName(name);

		if (limitType.getType() == DontTryType.Life.getType()) {
			dontTry.setStartDate(new java.util.Date(0));			
		}else {
			Calendar cal = Calendar.getInstance();
	    	
			switch (limitType.getType()) {
			case 2:
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.clear(Calendar.HOUR_OF_DAY);
				cal.clear(Calendar.HOUR);
				cal.clear(Calendar.MINUTE);
				cal.clear(Calendar.SECOND);
				cal.clear(Calendar.MILLISECOND);
				break;
			case 1:
				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				cal.clear(Calendar.DAY_OF_WEEK);
				cal.clear(Calendar.HOUR_OF_DAY);
				cal.clear(Calendar.HOUR);
				cal.clear(Calendar.MINUTE);
				cal.clear(Calendar.SECOND);
				cal.clear(Calendar.MILLISECOND);
				break;
			case 0:
				cal.clear(Calendar.HOUR_OF_DAY);
				cal.clear(Calendar.HOUR);
				cal.clear(Calendar.MINUTE);
				cal.clear(Calendar.SECOND);
				cal.clear(Calendar.MILLISECOND);
				break;

			}
			dontTry.setStartDate(cal.getTime());
		}
		logger.debug("do try "+name+" start date is "+dontTry.getStartDate());
		dontTryDao.newCounter(dontTry);
    	dontTry = dontTryDao.getByName(name, dontTry.getStartDate());
    	if (dontTry.getCount() > maxCount) {
    		throw new RentException(RentException.RentErrorCode.ErrorExceedLimit,"exceed donttry limit try "+dontTry.getCount());
    	}
    }

	public IDontTryDao getDontTryDao() {
		return dontTryDao;
	}

	public void setDontTryDao(IDontTryDao dontTryDao) {
		this.dontTryDao = dontTryDao;
	}
}
