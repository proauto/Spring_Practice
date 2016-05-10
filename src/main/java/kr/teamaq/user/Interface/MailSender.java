package kr.teamaq.user.Interface;

import org.springframework.mail.*;


public interface MailSender {
	void send(SimpleMailMessage simpleMessage) throws MailException;
	void send(SimpleMailMessage[] simpleMessages) throws MailException;

}
