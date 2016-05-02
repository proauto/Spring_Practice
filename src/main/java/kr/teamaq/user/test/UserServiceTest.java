package kr.teamaq.user.test;

import static kr.teamaq.user.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static kr.teamaq.user.UserService.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.teamaq.user.User;
import kr.teamaq.user.UserService;
import kr.teamaq.user.Interface.Level;
import kr.teamaq.user.Interface.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-applicationContext.xml")
public class UserServiceTest {

	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;

	List<User> users;

	@Before
	public void setUp() {
		users = Arrays.asList(new User("11", "11", "11", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
				new User("12", "12", "12", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("22", "22", "22", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
				new User("23", "23", "23", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
				new User("33", "33", "33", Level.GOLD, 100, Integer.MAX_VALUE));

	}

	@Test
	public void add() {
		userDao.deleteAll();

		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}

	@Test
	public void upgradeLevels() {
		userDao.deleteAll();

		for (User user : users)
			userDao.add(user);

		userService.upgradeLevels();

		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);

	}

	@Test
	public void upgradeAllorNothing() {
		UserService testUserService = new TestUserService(users.get(2).getId());
		testUserService.setUserDao(this.userDao); // 수동 DI를 해준다.

		userDao.deleteAll();
		for (User user : users)
			userDao.add(user);

		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");

		} catch (TestUserServiceException e) {

		} finally {

			checkLevelUpgraded(users.get(1), false);
		}
	}

	public void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}

	static class TestUserService extends UserService {

		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id))
				throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}

	static class TestUserServiceException extends RuntimeException {

	}

}
