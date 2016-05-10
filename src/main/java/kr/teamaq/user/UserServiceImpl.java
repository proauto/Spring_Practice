package kr.teamaq.user;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.mail.SimpleMailMessage;

import kr.teamaq.user.Interface.Level;
import kr.teamaq.user.Interface.MailSender;
import kr.teamaq.user.Interface.UserDao;
import kr.teamaq.user.Interface.UserService;

public class UserServiceImpl implements UserService {

	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;

	
	UserDao userDao;
	private DataSource dataSource;
	private MailSender mailSender;


	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setMailSender(MailSender mailSender){
		this.mailSender = mailSender;
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
		sendUpgradeEMail(user);
	}

	public void upgradeLevels(){

		List<User> users = userDao.getAll();
		for (User user : users)
			if (canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
	}
	

	private void sendUpgradeEMail(User user) {

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("creativestudioaq@gmail.com");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());

		this.mailSender.send(mailMessage);

	}

}
