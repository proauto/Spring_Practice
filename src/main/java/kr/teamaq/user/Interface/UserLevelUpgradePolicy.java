package kr.teamaq.user.Interface;

import kr.teamaq.user.User;

public interface UserLevelUpgradePolicy {
	boolean canUpgradeLevel(User user);
	void upgradeLevel(User user);

}
