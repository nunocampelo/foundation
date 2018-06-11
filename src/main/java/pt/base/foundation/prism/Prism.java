package pt.base.foundation.prism;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pt.base.foundation.numeric.fitting.PolynomialFitter;
import pt.base.foundation.prism.algorithm.AlgorithmTaskExecutor;
import pt.base.foundation.prism.algorithm.StandardLinearAlgorithm;

@Component
public class Prism {

	private static final int DEFAULT_NUMBER_ALGORITHM_EXECUTIONS = 10;

	@Autowired
	public PolynomialFitter polinomialFitter;

	@Autowired
	private AlgorithmTaskExecutor algorithmExecuter;

	@Autowired
	private StandardLinearAlgorithm standardLinearAlgorithm;

	@Autowired
	public void analyse() {

		System.out.println("Initiating PRISM, buckle up for some awesome computing...");

		List<Object> arguments = IntStream.range(0, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS)
				.mapToObj(index -> standardLinearAlgorithm.argumentProducer()).collect(Collectors.toList());

		List<Object> cpuTimes =
				algorithmExecuter.execute((Consumer<Long>) standardLinearAlgorithm::implementation, arguments);

		IntStream.range(0, cpuTimes.size()).forEach(index -> {
			System.out.println(arguments.get(index) + ": " + cpuTimes.get(index));
		});

		List<Map.Entry<Object, Object>> results =
				algorithmExecuter.execute((Consumer<Long>) standardLinearAlgorithm::implementation,
						standardLinearAlgorithm::argumentProducer, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS);

		System.out.println(results);
	}
}
