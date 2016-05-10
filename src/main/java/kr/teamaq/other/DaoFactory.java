package kr.teamaq.other;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import kr.teamaq.user.dao.UserDaoJdbc;

@Configuration
public class DaoFactory {

	@Bean
	public UserDaoJdbc userDao() {

		UserDaoJdbc userDao = new UserDaoJdbc();
		userDao.setDataSource(dataSource());
		return userDao;
	}

	@Bean
	public ConnectionMaker connectionMaker() {
		return new DConnectionMaker();
	}
	
	@Bean
	public DataSource dataSource(){
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(org.postgresql.Driver.class);
		dataSource.setUrl("jdbc:postgresql://proautodb.cy9crgteqp0p.ap-northeast-2.rds.amazonaws.com:5432/MemKey");
		dataSource.setUsername("memkey");
		dataSource.setPassword("ghdrb0422");
		
		return dataSource;
	}

}
