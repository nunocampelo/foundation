package pt.base.foundation.prism.sigar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.hyperic.sigar.Sigar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pt.base.foundation.prism.algorithm.AbstractAlgorithmTask;

@Component
public class SigarManager {

	@Autowired
	private Sigar sigar;

	@Bean
	@Lazy
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected AbstractAlgorithmTask wrapperFactory(Object alg, Object arg) {
		return new CpuTimedAlgorithmTask(sigar, alg, arg);
	}

	@Bean
	@Lazy
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected ExecutorService generateExecutorService(int maxThreads) {
		return new ThreadPoolExecutor(0, maxThreads, 0, TimeUnit.SECONDS, new SynchronousQueue<>());
	}

	@PreDestroy
	protected void shutdown() {
		sigar.close();
	}
}
