package kr.teamaq.user;

public interface LineCallback<T> {
	T doSomethingWithLine(String line,T value);
}
