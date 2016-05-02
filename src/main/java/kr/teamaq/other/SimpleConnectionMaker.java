package kr.teamaq.other;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionMaker {
	
	public Connection makeNewConnection() throws ClassNotFoundException,SQLException{
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://proautodb.cy9crgteqp0p.ap-northeast-2.rds.amazonaws.com:5432/MemKey","memkey","ghdrb0422");
		return c;
	}

}
