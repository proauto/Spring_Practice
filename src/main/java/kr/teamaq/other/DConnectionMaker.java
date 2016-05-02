package kr.teamaq.other;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker {
	
	public Connection makeConnection() throws ClassNotFoundException, SQLException{
		//D사의 독자적인 Conncetion code
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://proautodb.cy9crgteqp0p.ap-northeast-2.rds.amazonaws.com:5432/MemKey","memkey","ghdrb0422");
		return c;
	}

}
