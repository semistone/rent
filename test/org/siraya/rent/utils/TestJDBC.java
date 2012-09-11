package org.siraya.rent.utils;


import java.sql.DriverManager;

public class TestJDBC {


	public  static void main(String[] args)throws Exception{
		java.sql.Connection con = null;
		try{
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(
				"jdbc:mysql://10.1.128.235/time_base_rent", "rentuser", "rentuser");

		}finally{
			if (con != null && !con.isClosed())
				System.out.println("Successfully connected to "
						+ "MySQL server using TCP/IP...");			
		}
	
	}
}
