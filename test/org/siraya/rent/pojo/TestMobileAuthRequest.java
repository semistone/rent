package org.siraya.rent.pojo;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestMobileAuthRequest {
	Validator validator;
	MobileAuthRequest mobileAuthRequest;
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		mobileAuthRequest = new MobileAuthRequest();
		mobileAuthRequest.setRequestId("123565623422");
		mobileAuthRequest.setCountryCode("886");
		mobileAuthRequest.setSign("test");
		mobileAuthRequest.setMobilePhone("8869234242");
		mobileAuthRequest.setRequestFrom("2343242");
		mobileAuthRequest.setRequestTime(12413131);
	}
	@Test
	public void testValidateRequestId() {
		Set<ConstraintViolation<MobileAuthRequest>> constraintViolations = validator
				.validate(mobileAuthRequest);
		Assert.assertEquals(0, constraintViolations.size());
	}
	@Test
	public void testRequestId(){
		mobileAuthRequest.setRequestId("12432424324242342342424");// too long
		Set<ConstraintViolation<MobileAuthRequest>> constraintViolations = validator
				.validate(mobileAuthRequest);
		Assert.assertEquals(1, constraintViolations.size());
		
	}
}
