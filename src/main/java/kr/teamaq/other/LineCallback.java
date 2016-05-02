package kr.teamaq.other;

public interface LineCallback<T> {
	T doSomethingWithLine(String line,T value);
}
