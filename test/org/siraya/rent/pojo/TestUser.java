package org.siraya.rent.pojo;

import org.junit.Before;
import org.junit.Test;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import javax.validation.Validation;
import java.util.Set;
import javax.validation.ConstraintViolation;
import junit.framework.Assert;
public class TestUser {
	Validator validator;
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test 
	public void testValidate(){
		User user = new User();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate( user );
        Assert.assertNotSame(0, constraintViolations.size());
        //System.out.println(constraintViolations.iterator().next().getMessage());
	}
}
