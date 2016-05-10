package kr.teamaq.user.test;

import static kr.teamaq.user.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static kr.teamaq.user.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import kr.teamaq.user.User;
import kr.teamaq.user.UserServiceImpl;
import kr.teamaq.user.UserServiceTx;
import kr.teamaq.user.Interface.Level;
import kr.teamaq.user.Interface.MailSender;
import kr.teamaq.user.Interface.UserDao;
import kr.teamaq.user.Interface.UserService;
import kr.teamaq.user.dao.MockUserDao;
import kr.teamaq.user.mail.MockMailSender;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-applicationContext.xml")
public class UserServiceTest {

	@Autowired
	UserService userService;
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	UserDao userDao;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	MailSender mailSender;

	List<User> users;

	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("11", "11", "11", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "hg1771@naver.com"),
				new User("12", "12", "12", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "hg1286@naver.com"),
				new User("22", "22", "22", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1, "aq1771@naver.com"),
				new User("23", "23", "23", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "aq1771@naver.com"),
				new User("33", "33", "33", Level.GOLD, 100, Integer.MAX_VALUE, "hg1771@naver.com"));

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
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();

		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "12", Level.SILVER);
		checkUserAndLevel(updated.get(1), "23", Level.GOLD);

		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}

	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	@Test
	public void mockUpgreadeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();

		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);

		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);

		userServiceImpl.upgradeLevels();

		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));

		ArgumentCaptor<SimpleMailMessage> mailMessageArg = 
				ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}

	@Test
	public void upgradeAllorNothing() throws Exception {
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao); // 수동 DI를 해준다.
		testUserService.setMailSender(mailSender);

		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);

		userDao.deleteAll();
		for (User user : users)
			userDao.add(user);

		try {
			txUserService.upgradeLevels();
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

	static class TestUserService extends UserServiceImpl {

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
