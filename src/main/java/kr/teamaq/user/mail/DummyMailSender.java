package kr.teamaq.user.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import kr.teamaq.user.Interface.MailSender;

public class DummyMailSender implements MailSender {
	
	public void send(SimpleMailMessage mailMessage) throws MailException{
		System.out.println("mailMessage");
	}
	
	public void send(SimpleMailMessage[] mailMessage) throws MailException{
		System.out.println("mailMessages");
	}

}
