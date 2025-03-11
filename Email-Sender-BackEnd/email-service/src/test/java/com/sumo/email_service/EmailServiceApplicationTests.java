package com.sumo.email_service;

import com.sumo.email_service.dto.Messages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.sumo.email_service.service.EmailService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
class EmailServiceApplicationTests {
	@Autowired
	private EmailService emailService;
	@Test
	void contextLoads() {
	}
	@Test
	void emailSenderTest() {
		emailService.sendEmail(
				"soumojitmaka12@gmail.com",
				"Email Send from Spring Boot ",
				" I Test"
		);
	}
	@Test
	void emailSenderMultipleUserTest() {
		emailService.sendEmail(
                new String[]{"hydragaming9382@gmail.com", "soumojitmaka12@gmail.com"},
				"Email Send from Spring Boot ",
				" I Test"
		);
	}
	@Test
	void emailSenderHTMLTest() {
		String html=" "+
				"<h1 style='color:red;border:1px'> Welcome To Soumojit Makar </h1>";
		emailService.sendEmailWithHtml(
				"hydragaming9382@gmail.com",
				"Email Send from Spring Boot  With HTML",
				html
		);
	}
	@Test
	void emailSenderHTMLMultipleUserTest() {
		emailService.sendEmailWithFile(
				"hydragaming9382@gmail.com",
				"Email Send from Spring Boot  With File",
				"Hi ",
				new File("D:/Downlod/if-else-switch-mcq.pdf")
		);
	}
	@Test
	void emailSenderWithInputStreamUserTest()  {
		File file = new File("D:/Downlod/if-else-switch-mcq.pdf");
        try {
            InputStream inputStream = new FileInputStream(file);
			emailService.sendEmailWithFile(
					"soumojitmakar1230@gmail.com",
					"Email Send from Spring Boot  With File",
					"Hi ",
					inputStream,
					file.getName()
			);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

	}
	@Test
	void getInbox(){
		List< Messages >messages= emailService.getInboxMessages();
		messages.forEach(item->{
			System.out.println(item.getSubject());
			System.out.println(item.getFrom());
			System.out.println(item.getMessage());
			System.out.println(item.getFiles());
			System.out.println("--------------------------------------");
		});
	}


}
