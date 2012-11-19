package org.siraya.rent.page.service;

import java.util.List;

import org.siraya.rent.pojo.Space;
import org.siraya.rent.page.dao.ISpaceDao;
import org.siraya.rent.utils.RentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service("pageService")
public class PageService implements IPageService{
	@Autowired
	private ISpaceDao spaceDao;
	
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Space> getSpaces(String pageName){
		
		List<Space> ret =  this.spaceDao.getByPageName(pageName);
		if (ret.size() == 0) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"get space fail");
		}
		return ret;
	}
}
