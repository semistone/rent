<<<<<<< HEAD
package org.siraya.rent.donttry.service;

import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import org.siraya.rent.pojo.DontTry;
import org.siraya.rent.donttry.dao.IDontTryDao;
import org.siraya.rent.donttry.service.IDontTryService;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.web.client.RestOperations;
public class TestDontTryService {
	DontTryService test = null;
	private Mockery context;
	private IDontTryDao dontTryDao;
	private boolean isMock = true;
	private DontTry dontTry;
    @Before
	public void setUp(){
    	test = new DontTryService();
		if (isMock){
			dontTry= new DontTry();
			context = new JUnit4Mockery();
			dontTryDao = context.mock(IDontTryDao.class);	
			test.setDontTryDao(dontTryDao);
			dontTry.setCount(0);
		}
	}
    @Test 
    public void testDoTry(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(dontTryDao).newCounter(with(any(DontTry.class)));
					will(returnValue(1));
					one(dontTryDao).getByName((with(any(String.class))), 
							(with(any(Date.class))));
					will(returnValue(dontTry));
					
				}
			});
		}
    	test.doTry("test1", IDontTryService.DontTryType.Today, 10);
    }
    
    @Test (expected=org.siraya.rent.utils.RentException.class)
    public void testDoTryExpectError(){
		if (isMock) {
			dontTry.setCount(100);
			context.checking(new Expectations() {
				{
					one(dontTryDao).newCounter(with(any(DontTry.class)));
					will(returnValue(1));
					one(dontTryDao).getByName((with(any(String.class))), 
							(with(any(Date.class))));
					will(returnValue(dontTry));
					
				}
			});
		}
    	test.doTry("test1", IDontTryService.DontTryType.Today, 10);
    }
    
    
}
=======
package org.siraya.rent.donttry.service;

import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import org.siraya.rent.pojo.DontTry;
import org.siraya.rent.donttry.dao.IDontTryDao;
import org.siraya.rent.donttry.service.IDontTryService;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.web.client.RestOperations;
public class TestDontTryService {
	DontTryService test = null;
	private Mockery context;
	private IDontTryDao dontTryDao;
	private boolean isMock = true;
	private DontTry dontTry;
    @Before
	public void setUp(){
    	test = new DontTryService();
		if (isMock){
			dontTry= new DontTry();
			context = new JUnit4Mockery();
			dontTryDao = context.mock(IDontTryDao.class);	
			test.setDontTryDao(dontTryDao);
			dontTry.setCount(0);
		}
	}
    @Test 
    public void testDoTry(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(dontTryDao).newCounter(with(any(DontTry.class)));
					will(returnValue(1));
					one(dontTryDao).getByName((with(any(String.class))), 
							(with(any(Date.class))));
					will(returnValue(dontTry));
					
				}
			});
		}
    	test.doTry("test1", IDontTryService.DontTryType.Today, 10);
    }
    
    @Test (expected=org.siraya.rent.utils.RentException.class)
    public void testDoTryExpectError(){
		if (isMock) {
			dontTry.setCount(100);
			context.checking(new Expectations() {
				{
					one(dontTryDao).newCounter(with(any(DontTry.class)));
					will(returnValue(1));
					one(dontTryDao).getByName((with(any(String.class))), 
							(with(any(Date.class))));
					will(returnValue(dontTry));
					
				}
			});
		}
    	test.doTry("test1", IDontTryService.DontTryType.Today, 10);
    }
    
    
}
>>>>>>> master
