package kr.teamaq.user;

import java.util.List;

import kr.teamaq.user.Interface.Level;
import kr.teamaq.user.Interface.UserDao;
import kr.teamaq.user.Interface.UserLevelUpgradePolicy;

public class UserService {

	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;

	UserDao userDao;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void add(User user) {

		if (user.getLevel() == null)
			user.setLevel(Level.BASIC);

		userDao.add(user);
	}

	public boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();

		switch (currentLevel) {
		case BASIC:
			return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER:
			return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
		case GOLD:
			return false;
		default:
			throw new IllegalArgumentException("Unkown Level:" + currentLevel);
		}
	}

	// 테스트를 위해 접근제한자를 protected로 바꿨다!
	protected void upgradeLevel(User user) {

		user.upgradeLevel();
		userDao.update(user);
	}

	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for (User user : users)
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
	}

}
