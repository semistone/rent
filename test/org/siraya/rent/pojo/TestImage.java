package org.siraya.rent.pojo;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

public class TestImage {
	Validator validator;
	private Image image;

	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		image = new Image();
	}
	
	@Test
	public void testValidate(){
		Set<ConstraintViolation<Image>> constraintViolations = validator
				.validate(image);
		Assert.assertNotSame(0, constraintViolations.size());
	}
}
