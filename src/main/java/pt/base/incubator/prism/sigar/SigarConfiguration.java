package pt.base.incubator.prism.sigar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.hyperic.sigar.Sigar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import pt.base.incubator.prism.Prism;
import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AbstractAlgorithmTask;

@Configuration
@Profile(Prism.SIGAR_PROFILE)
public class SigarConfiguration {

	@Value("${machine.cpu.cores:8}")
	private int cpuCores;

	private Sigar sigar = new Sigar();

	@Bean
	@Lazy
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected <A> AbstractAlgorithmTask<A, Long> wrapperFactory(AbstractAlgorithm<A> algorithm, A argument) {
		return new SigarAlgorithmTask<A>(sigar, algorithm, argument);
	}

	@Bean
	@Lazy
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected ExecutorService generateExecutorService(int numberExecutions) {
		return Executors.newFixedThreadPool(cpuCores - 1);
	}

	@PreDestroy
	protected void shutdown() {
		sigar.close();
	}
}
