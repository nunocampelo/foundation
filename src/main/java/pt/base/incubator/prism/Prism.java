package pt.base.incubator.prism;

import java.text.NumberFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pt.base.incubator.numeric.regression.MultipleRegressionProcessor;
import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AlgorithmTaskExecutor;
import pt.base.incubator.prism.algorithm.standard.StandardLinearAlgorithm;
import pt.base.incubator.prism.algorithm.standard.StandardQuadraticAlgorithm;
import pt.base.incubator.prism.algorithm.standard.StandardSixDegreeAlgorithm;
import pt.base.incubator.prism.data.DataProcessor;
import pt.base.incubator.prism.execution.LogExecutionTime;
import pt.base.incubator.prism.jmx.JMXServerConfiguration;

@Component
public class Prism {

	public static final String JMX_PROFILE = "jmx";
	public static final String SIGAR_PROFILE = "sigar";
	public static final String ABSOLUTE_TIME = "absolute";

	private boolean destroyed;
	private static final Logger LOGGER = LoggerFactory.getLogger(Prism.class);

	@Value("${prism.algorithm.executions:30}")
	private int numberAlgorithmExecutions;

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

	@Autowired(required = false)
	private JMXServerConfiguration serverConf;

	@LogExecutionTime
	public void analyse() {

		LOGGER.info("Starting PRISM... Buckle up for some cool computing!");

		List<AbstractAlgorithm<Long>> algorithms =
				Arrays.asList(standardLinearAlgorithm, standardQuadraticAlgorithm, standardSixDegreeAlgorithm);

		List<Integer> degrees = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

		algorithms.forEach(algorithm -> {

			List<Entry<Long, Long>> cpuTimesList =
					toLongEntryList(algorithmExecuter.execute(algorithm, numberAlgorithmExecutions));
			dataProcessor.sortByKey(cpuTimesList);

			LOGGER.info("Algorithm {}, CPU Times: {} ", algorithm, cpuTimesList);

			List<Entry<Long, Long>> withoutOutliers = dataProcessor.filterOutOutliers(cpuTimesList);
			LOGGER.info("CPU Times: {} ", withoutOutliers);

			Map<Long, Long> closestToMean = dataProcessor.getValuesClosestToMean(withoutOutliers);
			LOGGER.info("CPU Times: {} ", closestToMean);

			if (destroyed) {
				return;
			}

			Map<Integer, Double> rSquareValues = new LinkedHashMap<>();
			List<Double> changePercentages = new LinkedList<>();

			degrees.forEach(d -> {

				int index = degrees.indexOf(d);

				double changePercentage = 0;
				double rSquare = regressionProcessor.regress(cpuTimesList, d);

				rSquareValues.put(d, rSquare);

				if (index != 0) {
					double prevRSquare = rSquareValues.get(degrees.get(index - 1));
					changePercentage = Math.abs(rSquare - prevRSquare) / prevRSquare;
				}

				changePercentages.add(changePercentage);
			});

			doLogRegressionResults(rSquareValues, changePercentages);
		});
	}

	private void doLogRegressionResults(Map<Integer, Double> rSquareValues, List<Double> changePercentages) {

		NumberFormat percentageFormat = NumberFormat.getPercentInstance();
		percentageFormat.setMinimumFractionDigits(3);

		int index = 0;
		int size = rSquareValues.size();

		double rSquare;
		double nextCumChangePercentage = 0;

		for (int degree : rSquareValues.keySet()) {

			rSquare = rSquareValues.get(degree);

			if (index < size - 1) {
				nextCumChangePercentage += changePercentages.get(index + 1);
			}

			LOGGER.info("Regression degree: {}, R-square: {}, Perc. change: {}, Range: [{},{}[", degree, rSquare,
					percentageFormat.format(changePercentages.get(index)), (1 - nextCumChangePercentage) * rSquare,
					(1 + nextCumChangePercentage) * rSquare);

			index++;
		}
	}

	public void stop() {
		LOGGER.info("Stopping PRISM, see you next time...");

		if (serverConf != null) {
			serverConf.destroy();
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<Entry<Long, Long>> toLongEntryList(List<Entry<Long, Object>> source) {

		List<Entry<Long, Long>> result = new LinkedList<>();
		source.forEach(entry -> result.add(new SimpleEntry(entry.getKey(), entry.getValue())));

		return result;
	}

	@PreDestroy
	private void shutdown() {
		destroyed = true;
	}
}
