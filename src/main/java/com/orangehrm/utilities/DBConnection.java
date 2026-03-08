package com.orangehrm.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.orangehrm.base.BaseClass;

public class DBConnection {

	private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/orangehrm";
	private static final String DB_Username = "root";
	private static final String DB_PASSWORD = "";
	public static final Logger logger = BaseClass.logger;
	
	
	public static Connection getDBConnection() {
		try {
			logger.info("Starting DB Connection...");
			Connection conn = DriverManager.getConnection(CONNECTION_URL, DB_Username, DB_PASSWORD);
			logger.info("DB Connection Successful");
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Error while establishing the DB connection");
		}
		return null;
	}

	public static Map<String, String> getEmployeeDetails(String emp_id) {

		String query = "SELECT emp_firstname , emp_lastname , emp_middle_name FROM `hs_hr_employee` WHERE employee_id="
				+ emp_id + ";";
		Map<String, String> employeeDetails = new HashMap<>();
		try (Connection conn = getDBConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);) {
			System.out.println("Executing query .." + query);

			if (rs.next()) {
				String firstname = rs.getString("emp_firstname");
				String middlename = rs.getString("emp_middle_name");
				String lastname = rs.getString("emp_lastname");

				// store in map

				employeeDetails.put("firstname", firstname);
				employeeDetails.put("middleName",(middlename != null && !middlename.isEmpty()) ? middlename : "");
				employeeDetails.put("lastname", lastname);

				logger.info("Query Executed Successfully");
				logger.info("Employee Data Fetched: "+employeeDetails);
			} else {
				logger.error("Employee not found");
			}
		} catch (Exception e) {
			logger.info("Errr while exeucting query");
			e.printStackTrace();
		}

		return employeeDetails;

	}
}
