package pt.base.incubator.prism;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.stereotype.Component;

import pt.base.incubator.numeric.regression.MultipleRegressionProcessor;
import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AlgorithmTaskExecutor;
import pt.base.incubator.prism.algorithm.StandardLinearAlgorithm;
import pt.base.incubator.prism.algorithm.StandardQuadraticAlgorithm;
import pt.base.incubator.prism.algorithm.StandardSixDegreeAlgorithm;

@Component
public class Prism {

	private static final int DEFAULT_NUMBER_ALGORITHM_EXECUTIONS = 10;

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

	public void init() {

		JMXServiceURL url;
		try {
			url = new JMXServiceURL(jmxServerConfiguration.getJmxServerUrl());
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			jmxc.connect();

			System.out.println(jmxc.getMBeanServerConnection()
					.getAttribute(new javax.management.ObjectName("java.lang:type=OperatingSystem"), "ProcessCpuTime"));

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AttributeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void analyse() {

		List<AbstractAlgorithm<Long>> algorithms =
				Arrays.asList(standardLinearAlgorithm, standardQuadraticAlgorithm, standardSixDegreeAlgorithm);

		System.out.println("Initiating PRISM, buckle up for some awesome computing...");

		List<Integer> degrees = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

		algorithms.forEach(algorithm -> {

			List<Entry<Long, Long>> results =
					toLongEntryList(algorithmExecuter.execute(algorithm, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS));

			System.out.println("Results: " + results);
			degrees.stream().map(degree -> regressionProcessor.regress(results, degree)).forEach(System.out::println);
		});

	}

	public void stop() {
		try {
			jmxServerFactoryBean.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<Entry<Long, Long>> toLongEntryList(List<Entry<Long, Object>> source) {

		List<Entry<Long, Long>> result = new LinkedList<>();
		source.forEach(entry -> result.add(new SimpleEntry(entry.getKey(), entry.getValue())));

		return result;
	}
}
