package pt.base.incubator.prism.sigar;

import org.hyperic.sigar.Sigar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SigarConfiguration {

	@Bean
	protected Sigar sigarFactory() {
		return new Sigar();
	}
}
