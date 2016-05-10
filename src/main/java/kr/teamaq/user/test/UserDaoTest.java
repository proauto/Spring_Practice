
package kr.teamaq.user.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.teamaq.user.User;
import kr.teamaq.user.Interface.Level;
import kr.teamaq.user.dao.UserDaoJdbc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-applicationContext.xml")
public class UserDaoTest {

	@Autowired
	private UserDaoJdbc dao;
	private User user1;
	private User user2;
	private User user3;
	// JUnit 프레임워크는 테스트 메소드를 실행하기 전에 먼저 실행시켜주는 기능 존재

	@Before
	public void setUp() {

		this.user1 = new User("11", "11", "11",Level.BASIC,1,0,"hg1771@naver.com");
		this.user2 = new User("22", "22", "22",Level.SILVER,55,10,"hg1286@naver.com");
		this.user3 = new User("33", "33", "33",Level.GOLD,100,40,"aq1771@naver.com");

	}

	@Test
	public void addAndGet() throws ClassNotFoundException, SQLException {

		dao.deleteAll();
		assertThat(dao.getCount(), is(0));

		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));

		User userget1 = dao.get(user1.getId());

		checkSameUser(userget1,user1);

		User userget2 = dao.get(user2.getId());
		
		checkSameUser(userget2,user2);

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

	@Test(expected = EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {

		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		dao.get("unkown_id");
	}
	
	@Test
	public void getAll() throws ClassNotFoundException, SQLException{
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(),is(0));
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(),is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(),is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(),is(3));
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
	}
	
	@Test
	public void update(){
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("이홍규");
		user1.setPassword("spring");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1,user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	@Test(expected=DataAccessException.class)
	public void duplicateKey(){
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);
	}
	
	private void checkSameUser(User user1, User user2){
		assertThat(user1.getId(),is(user2.getId()));
		assertThat(user1.getName(),is(user2.getName()));
		assertThat(user1.getPassword(),is(user2.getPassword()));
		assertThat(user1.getLevel(),is(user2.getLevel()));
		assertThat(user1.getLogin(),is(user2.getLogin()));
		assertThat(user1.getRecommend(),is(user2.getRecommend()));
	}

}
