package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
public class FiobenchmarkApplication {

	public static void main(String[] args) {
		Scheduler s=new Scheduler();
		s.start();
		//SpringApplication.run(FiobenchmarkApplication.class, args);
		
	}
}
