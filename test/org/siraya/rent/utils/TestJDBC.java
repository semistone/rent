package org.siraya.rent.utils;


import java.sql.DriverManager;

import org.junit.Test;

public class TestJDBC {

	@Test 
	public void testGetMobileSetting()throws Exception{
		java.sql.Connection con = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1/time_base_rent", "rentuser",
					"rentuser");

		} finally {
			if (con != null && !con.isClosed())
				System.out.println("Successfully connected to "
						+ "MySQL server using TCP/IP...");
		}

	}
}
