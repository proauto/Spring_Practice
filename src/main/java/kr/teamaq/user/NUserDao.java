package kr.teamaq.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NUserDao extends UserDao{
	
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://proautodb.cy9crgteqp0p.ap-northeast-2.rds.amazonaws.com:5432/MemKey","memkey","ghdrb0422");
		return c;
	}

}
