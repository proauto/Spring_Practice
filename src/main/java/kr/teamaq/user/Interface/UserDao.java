package kr.teamaq.user.Interface;

import java.util.List;

import kr.teamaq.user.User;

public interface UserDao {
	
	void add(User user);
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
	public void update(User user1);

}
