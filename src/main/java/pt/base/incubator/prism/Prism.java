package pt.base.incubator.prism;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.stereotype.Component;

import pt.base.incubator.numeric.regression.MultipleRegressionProcessor;
import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AbstractAlgorithmTask;
import pt.base.incubator.prism.algorithm.AlgorithmTaskExecutor;
import pt.base.incubator.prism.algorithm.JMXAlgorithmTask;
import pt.base.incubator.prism.algorithm.StandardLinearAlgorithm;
import pt.base.incubator.prism.algorithm.StandardQuadraticAlgorithm;
import pt.base.incubator.prism.algorithm.StandardSixDegreeAlgorithm;
import pt.base.incubator.prism.data.DataProcessor;

@Component
public class Prism {

	private static final Logger LOGGER = LoggerFactory.getLogger(Prism.class);
	private static final int DEFAULT_NUMBER_ALGORITHM_EXECUTIONS = 50;

	@Autowired
	public ConnectorServerFactoryBean jmxServerFactoryBean;

	@Autowired
	public JMXServerConfiguration jmxServerConfiguration;

	@Autowired
	public MultipleRegressionProcessor regressionProcessor;

	@Autowired
	private AlgorithmTaskExecutor algorithmExecuter;

	@Autowired
	private StandardLinearAlgorithm standardLinearAlgorithm;

	@Autowired
	private StandardQuadraticAlgorithm standardQuadraticAlgorithm;

	@Autowired
	private StandardSixDegreeAlgorithm standardSixDegreeAlgorithm;

	@Autowired
	private DataProcessor dataProcessor;

	private MBeanServerConnection jmxServerConnection;

	public void init() {

		LOGGER.info("Initializing Prism Library...");

		JMXServiceURL url;
		try {

			url = new JMXServiceURL(jmxServerConfiguration.getJmxServerUrl());
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			jmxc.connect();

			jmxServerConnection = jmxc.getMBeanServerConnection();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void analyse() {

		LOGGER.info("PRISM analysing... buckle up for some awesome computing!");

		List<AbstractAlgorithm<Long>> algorithms =
				Arrays.asList(standardLinearAlgorithm, standardQuadraticAlgorithm, standardSixDegreeAlgorithm);
		List<Integer> degrees = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

		algorithms.forEach(algorithm -> {

			List<Entry<Long, Long>> cpuTimesList =
					toLongEntryList(algorithmExecuter.execute(algorithm, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS));
			dataProcessor.sortByKey(cpuTimesList);

			LOGGER.info("Algorithm {}, CPU Times: {} ", algorithm, cpuTimesList);
			Map<Long, Long> averagedCpuTimes = dataProcessor.averageSameKeyResults(cpuTimesList);
			LOGGER.info("Averaged CPU Times: {} ", averagedCpuTimes);

			degrees.forEach(degree -> {
				LOGGER.info("Regression degree: {}, R-square: {}", degree,
						regressionProcessor.regress(averagedCpuTimes, degree));
			});
		});

	}

	public void stop() {

		LOGGER.info("Closing Prism, see you next time.");

		try {
			jmxServerFactoryBean.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Bean
	@Lazy
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected <A> AbstractAlgorithmTask<A, Long> wrapperFactory(AbstractAlgorithm<A> algorithm, A argument) {
		return new JMXAlgorithmTask<A>(jmxServerConnection, algorithm, argument);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<Entry<Long, Long>> toLongEntryList(List<Entry<Long, Object>> source) {

		List<Entry<Long, Long>> result = new LinkedList<>();
		source.forEach(entry -> result.add(new SimpleEntry(entry.getKey(), entry.getValue())));

		return result;
	}
}
