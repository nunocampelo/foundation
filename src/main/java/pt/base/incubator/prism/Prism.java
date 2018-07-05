package pt.base.incubator.prism;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import pt.base.incubator.numeric.regression.MultipleRegressionProcessor;
import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AlgorithmTaskExecutor;
import pt.base.incubator.prism.algorithm.standard.StandardLinearAlgorithm;
import pt.base.incubator.prism.algorithm.standard.StandardQuadraticAlgorithm;
import pt.base.incubator.prism.algorithm.standard.StandardSixDegreeAlgorithm;
import pt.base.incubator.prism.data.DataProcessor;

@Component
public class Prism {

	public static final String JMX_PROFILE = "jmx";
	public static final String SIGAR_PROFILE = "sigar";

	private static final Logger LOGGER = LoggerFactory.getLogger(Prism.class);
	private static final int DEFAULT_NUMBER_ALGORITHM_EXECUTIONS = 30;

	@Autowired
	private StandardLinearAlgorithm standardLinearAlgorithm;
	@Autowired
	private StandardQuadraticAlgorithm standardQuadraticAlgorithm;
	@Autowired
	private StandardSixDegreeAlgorithm standardSixDegreeAlgorithm;

	@Autowired
	private AlgorithmTaskExecutor algorithmExecuter;
	@Autowired
	private DataProcessor dataProcessor;
	@Autowired
	public MultipleRegressionProcessor regressionProcessor;

	@Autowired
	private ApplicationContext context;

	public void analyse() {

		LOGGER.info("Starting PRISM... Buckle up for some awesome computing!");

		// List<AbstractAlgorithm<Long>> algorithms =
		// Arrays.asList(standardLinearAlgorithm, standardQuadraticAlgorithm,
		// standardSixDegreeAlgorithm);

		// List<AbstractAlgorithm<Long>> algorithms =
		// Arrays.asList(standardQuadraticAlgorithm, standardSixDegreeAlgorithm);

		List<AbstractAlgorithm<Long>> algorithms = Arrays.asList(standardQuadraticAlgorithm);
		// List<AbstractAlgorithm<Long>> algorithms = Arrays.asList(standardSixDegreeAlgorithm);

		List<Integer> degrees = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

		algorithms.forEach(algorithm -> {

			List<Entry<Long, Long>> cpuTimesList =
					toLongEntryList(algorithmExecuter.execute(algorithm, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS));
			dataProcessor.sortByKey(cpuTimesList);

			LOGGER.info("Algorithm {}, CPU Times: {} ", algorithm, cpuTimesList);

			// List<Entry<Long, Long>> withoutOutliers =
			// dataProcessor.filterOutOutliers(cpuTimesList);
			// LOGGER.info("CPU Times: {} ", withoutOutliers);
			//
			// Map<Long, Long> closestToMean =
			// dataProcessor.getValuesClosestToMean(withoutOutliers);
			// LOGGER.info("CPU Times: {} ", closestToMean);

			degrees.forEach(degree -> {
				LOGGER.info("Regression degree: {}, R-square: {}", degree,
						regressionProcessor.regress(cpuTimesList, degree));
			});
		});

	}

	public void stop() {
		LOGGER.info("Stopping PRISM, see you next time...");
		((ConfigurableApplicationContext) context).close();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<Entry<Long, Long>> toLongEntryList(List<Entry<Long, Object>> source) {

		List<Entry<Long, Long>> result = new LinkedList<>();
		source.forEach(entry -> result.add(new SimpleEntry(entry.getKey(), entry.getValue())));

		return result;
	}
}
