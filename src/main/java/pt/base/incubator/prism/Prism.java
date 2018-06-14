package pt.base.incubator.prism;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pt.base.incubator.numeric.regression.MultipleRegressionProcessor;
import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AlgorithmTaskExecutor;
import pt.base.incubator.prism.algorithm.StandardLinearAlgorithm;
import pt.base.incubator.prism.algorithm.StandardSixDegreeAlgorithm;

@Component
public class Prism {

	private static final int DEFAULT_NUMBER_ALGORITHM_EXECUTIONS = 10;

	@Autowired
	public MultipleRegressionProcessor regressionProcessor;

	@Autowired
	private AlgorithmTaskExecutor algorithmExecuter;

	@Autowired
	private StandardLinearAlgorithm standardLinearAlgorithm;

	@Autowired
	private StandardLinearAlgorithm standardQuadraticAlgorithm;

	@Autowired
	private StandardSixDegreeAlgorithm standardSixDegreeAlgorithm;

	@Autowired
	public void analyse() {

		List<AbstractAlgorithm<Long>> algorithms =
				Arrays.asList(standardLinearAlgorithm, standardQuadraticAlgorithm, standardSixDegreeAlgorithm);

		System.out.println("Initiating PRISM, buckle up for some awesome computing...");

		// List<Object> arguments = IntStream.range(0, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS)
		// .mapToObj(index -> algorithm.argumentProducer()).collect(Collectors.toList());
		//
		// List<Object> cpuTimes =
		// algorithmExecuter.execute((Consumer<Long>) algorithm::implementation, arguments);
		//
		// IntStream.range(0, cpuTimes.size()).forEach(index -> {
		// System.out.println(arguments.get(index) + ": " + cpuTimes.get(index));
		// });

		algorithms.forEach(algorithm -> {
			List<Entry<Long, Long>> results =
					toLongEntryList(algorithmExecuter.execute((Consumer<Long>) algorithm::implementation,
							algorithm::argumentProducer, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS));

			System.out.println("Results: " + results);
			regressionProcessor.regress(results, 1);
			regressionProcessor.regress(results, 2);
			regressionProcessor.regress(results, 3);
			regressionProcessor.regress(results, 4);
			regressionProcessor.regress(results, 5);
			regressionProcessor.regress(results, 6);
			regressionProcessor.regress(results, 7);
		});
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<Entry<Long, Long>> toLongEntryList(List<Entry<Object, Object>> source) {
		List<Entry<Long, Long>> result = new LinkedList<>();

		source.forEach(entry -> result.add(new SimpleEntry(entry.getKey(), entry.getValue())));

		return result;
	}
}
