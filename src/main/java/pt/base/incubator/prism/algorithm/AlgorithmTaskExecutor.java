package pt.base.incubator.prism.algorithm;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class AlgorithmTaskExecutor {

	private static final int DEFAULT_ALGORITHM_TASK_EXECUTION_TIMEOUT_MILLIS = 1000;

	private TimeUnit algorithmTaskTimeoutTimeUnit = TimeUnit.MILLISECONDS;
	private long algorithmTaskTimout = DEFAULT_ALGORITHM_TASK_EXECUTION_TIMEOUT_MILLIS;

	@Autowired
	private BeanFactory beanFactory;

	public TimeUnit getAlgorithmTaskTimeoutTimeUnit() {
		return algorithmTaskTimeoutTimeUnit;
	}

	public void setAlgorithmTaskTimeoutTimeUnit(TimeUnit algorithmTaskTimeoutTimeUnit) {
		this.algorithmTaskTimeoutTimeUnit = algorithmTaskTimeoutTimeUnit;
	}

	public long getAlgorithmTaskTimout() {
		return algorithmTaskTimout;
	}

	public void setAlgorithmTaskTimout(long algorithmTaskTimout) {
		this.algorithmTaskTimout = algorithmTaskTimout;
	}

	public List<Object> execute(Object algorithm, List<Object> arguments) {

		if (ObjectUtils.isEmpty(arguments)) {
			throw new IllegalArgumentException("Arguments must not be empty");
		}
		checkSupportedAlgorithmType(algorithm);

		return doExecute(algorithm, arguments);
	}

	public List<Map.Entry<Object, Object>> execute(Object algorithm, Supplier<Object> argumentSupplier,
			int minNumberOfResults) {

		if (minNumberOfResults <= 0) {
			throw new IllegalArgumentException("Number of results must be positive");
		}
		checkSupportedAlgorithmType(algorithm);

		return doExecuteUntilAtLeastNumberOfResults(algorithm, argumentSupplier, minNumberOfResults);
	}

	private List<Object> doExecute(Object algorithm, List<Object> arguments) {

		int numberOfArguments = arguments.size();
		System.out.println(MessageFormat.format("New algorithm execution with {0} arguments", numberOfArguments));

		final ExecutorService algorithmContextExecutor = produceExecutorService(numberOfArguments);

		final List<Future<Object>> resultsFuture = submitExecutions(algorithmContextExecutor, algorithm, arguments);
		tryAwaitTermination(algorithmContextExecutor,
				numberOfArguments * TimeUnit.MILLISECONDS.convert(algorithmTaskTimout, algorithmTaskTimeoutTimeUnit));

		return tryExtractResultsStream(resultsFuture).collect(Collectors.toList());
	}

	private List<Map.Entry<Object, Object>> doExecuteUntilAtLeastNumberOfResults(Object algorithm,
			Supplier<Object> argumentSupplier, int minNumberOfResults) {

		// Recursive implementation is quite slower
		// List<Map.Entry<Object, Object>> results = new LinkedList<>();
		// doGenerateNumberResultsRecursively(results, algorithm, argumentSupplier, numberResults);

		return doGenerateAtLeastMinNumberOfResults(algorithm, argumentSupplier, minNumberOfResults);
	}

	private List<Map.Entry<Object, Object>> doGenerateAtLeastMinNumberOfResults(Object algorithm,
			Supplier<Object> argumentSupplier, int minNumberOfResults) {

		List<Map.Entry<Object, Object>> results = new LinkedList<>();

		int currentNumberResults = 0;
		while (currentNumberResults < minNumberOfResults) {

			System.out.println(MessageFormat.format("Currently have {0} results of wanted {1}", currentNumberResults,
					minNumberOfResults));

			List<Object> newArguments = produceArguments(argumentSupplier, minNumberOfResults);
			List<Object> newResults = execute(algorithm, newArguments);

			results.addAll(createEntryMapList(newArguments, newResults));

			currentNumberResults = results.size();
		}

		return results;
	}

	// Recursive implementation is quite slower
	// private void doGenerateNumberResultsRecursily(List<Map.Entry<Object, Object>> results, Object
	// algorithm,
	// Supplier<Object> argumentSupplier, int numberResults) {
	//
	// int currentNumberResults = results.size();
	// if (currentNumberResults == numberResults) {
	// return;
	// }
	//
	// System.out.println(
	// MessageFormat.format("Currently have {0} results of wanted {1}", currentNumberResults,
	// numberResults));
	//
	// List<Object> newArguments = produceArguments(argumentSupplier, numberResults -
	// currentNumberResults);
	// List<Object> newResults = execute(algorithm, newArguments);
	//
	// results.addAll(createEntryMapList(newArguments, newResults));
	//
	// doGenerateNumberResultsRecursily(results, algorithm, argumentSupplier, numberResults);
	// }

	private List<Map.Entry<Object, Object>> createEntryMapList(List<Object> arguments, List<Object> results) {
		return IntStream.range(0, arguments.size())
				.mapToObj(
						index -> new AbstractMap.SimpleEntry<Object, Object>(arguments.get(index), results.get(index)))
				.filter(entry -> !ObjectUtils.isEmpty(entry.getValue())).collect(Collectors.toList());
	}

	private List<Object> produceArguments(Supplier<Object> argumentProducer, int number) {
		return IntStream.range(0, number).mapToObj(index -> argumentProducer.get()).collect(Collectors.toList());
	}

	private ExecutorService produceExecutorService(int maxThreads) {
		return beanFactory.getBean(ExecutorService.class, maxThreads);
	}

	private List<Future<Object>> submitExecutions(ExecutorService executerService, Object algorithm,
			List<Object> arguments) {
		return (List<Future<Object>>) arguments.stream().map(arg -> this.produceExecutionContext(algorithm, arg))
				.map(executionContext -> executerService.submit(executionContext)).collect(Collectors.toList());
	}

	private void tryAwaitTermination(ExecutorService executorService, long awaitTerminationMillis) {
		try {
			executorService.awaitTermination(awaitTerminationMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			executorService.shutdownNow();
		}
	}

	private Stream<Object> tryExtractResultsStream(List<Future<Object>> resultsFuture) {

		return resultsFuture.stream().map(t -> {

			if (!t.isDone()) {
				return null;
			}

			try {
				return t.get(algorithmTaskTimout, algorithmTaskTimeoutTimeUnit);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	private AbstractAlgorithmTask produceExecutionContext(Object algorithm, Object argument) {
		return beanFactory.getBean(AbstractAlgorithmTask.class, algorithm, argument);
	}

	private void checkSupportedAlgorithmType(Object obj) {
		if (!(obj instanceof Function) && !(obj instanceof Consumer) && !(obj instanceof Runnable)
				&& !(obj instanceof Callable)) {
			throw new IllegalArgumentException("Algorithm must be of type Function, Consumer, Runnable or Callable");
		}
	}
}
