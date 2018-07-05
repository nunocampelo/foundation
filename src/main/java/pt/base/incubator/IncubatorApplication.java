package pt.base.incubator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import pt.base.incubator.prism.Prism;

@SpringBootApplication
public class IncubatorApplication {

	public static void main(String[] args) throws InterruptedException {

		ConfigurableApplicationContext context = SpringApplication.run(IncubatorApplication.class, args);

		Prism prism = context.getBean(Prism.class);
		prism.analyse();
		prism.stop();
	}
}
