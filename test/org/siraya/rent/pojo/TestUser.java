<<<<<<< HEAD
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
	User user = new User();

	@Before
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		user.setCc("886");
		user.setMobilePhone("88693112222");
		user.setStatus(0);
		user.setEmail("xxx@test.com");
	}

	@Test
	public void testValidate() {
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertEquals(0, constraintViolations.size());
	}

	@Test
	public void testInvalidMobilePhone() {
		user.setMobilePhone("xxx");
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setMobilePhone("123456789012345");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());
	}

	@Test
	public void testCountryCode() {
		user.setCc("text");
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setCc("1234");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());
	}

	@Test
	public void testEmail() {
		user.setEmail("xxxxxx");
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setEmail("xxxxxx@xxx");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setEmail("xxxx!!xx@xxx");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());
	}
}
=======
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
	User user = new User();

	@Before
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		user.setCc("886");
		user.setMobilePhone("+88693112222");
		user.setStatus(0);
		user.setEmail("xxx@test.com");
	}

	@Test
	public void testValidate() {
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertEquals(0, constraintViolations.size());
	}

	@Test
	public void testInvalidMobilePhone() {
		user.setMobilePhone("xxx");
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setMobilePhone("123456789012345");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());
	}

	@Test
	public void testCountryCode() {
		user.setCc("text");
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setCc("1234");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());
	}

	@Test
	public void testEmail() {
		user.setEmail("xxxxxx");
		Set<ConstraintViolation<User>> constraintViolations = validator
				.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setEmail("xxxxxx@xxx");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());

		user.setEmail("xxxx!!xx@xxx");
		constraintViolations = validator.validate(user);
		Assert.assertNotSame(0, constraintViolations.size());
	}
}
>>>>>>> master
