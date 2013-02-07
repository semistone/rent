package org.siraya.rent.utils;


import java.sql.*;

import junit.framework.Assert;

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
	
	
	@Test 
	public void testSqlite() throws Exception{
		java.sql.Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.

			statement.executeUpdate("drop table if exists person");
			statement
					.executeUpdate("create table person (id integer, name string)");
			statement.executeUpdate("insert into person values(1, 'leo')");
			statement.executeUpdate("insert into person values(2, 'yui')");
			ResultSet rs = statement.executeQuery("select * from person");
			if (rs.next()) {
				// read the result set
				Assert.assertEquals("leo", rs.getString("name"));
				Assert.assertEquals(1, rs.getInt("id"));
			}
		} finally {
			if (connection != null && !connection.isClosed())
				;

		}
	}
}
