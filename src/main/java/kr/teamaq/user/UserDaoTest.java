package kr.teamaq.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="applicationContext.xml")
public class UserDaoTest {
	
	
	@Autowired
	private UserDao dao;
	private User user1;
	private User user2;
	private User user3;
	//JUnit 프레임워크는 테스트 메소드를 실행하기 전에 먼저 실행시켜주는 기능 존재
	
	@Before
	public void setUp(){
		
		
		this.user1 = new User("11", "11", "11");
		this.user2 = new User("22", "22", "22");
		this.user3 = new User("33", "33", "33");
		
	}

	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {


		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));

		User userget1 = dao.get(user1.getId());

		assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));

		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));

	}

	@Test
	public void count() throws ClassNotFoundException, SQLException {
		
		

		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		assertThat(dao.getCount(), is(1));

		dao.add(user2);
		assertThat(dao.getCount(), is(2));

		dao.add(user3);
		assertThat(dao.getCount(), is(3));

	}
	
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException{
		
		dao.deleteAll();
		assertThat(dao.getCount(),is(0));
		dao.get("unkown_id");
	}

}
