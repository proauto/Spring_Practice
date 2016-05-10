package kr.teamaq.user.dao;

import java.util.ArrayList;
import java.util.List;

import kr.teamaq.user.User;
import kr.teamaq.user.Interface.UserDao;

public class MockUserDao implements UserDao {

	private List<User> users;
	private List<User> updated = new ArrayList();

	public MockUserDao(List<User> users) {
		this.users = users;
	}

	public List<User> getUpdated() {
		return this.updated;
	}

	public List<User> getAll() {
		return this.users;
	}

	public void update(User user) {
		updated.add(user);
	}

	// 테스트에 사용되지 않는 메소드를 예외로 던진다
	public void add(User user) {
		throw new UnsupportedOperationException();
	}

	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

	public User get(String id) {
		throw new UnsupportedOperationException();
	}

	public int getCount() {
		throw new UnsupportedOperationException();
	}

}
