package pt.base.incubator.prism.algorithm;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class AlgorithmTaskExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlgorithmTaskExecutor.class);

	private static final int DEFAULT_ALGORITHM_TASK_EXECUTION_TIMEOUT_MILLIS = 10000;

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

	public <A, R> List<R> execute(AbstractAlgorithm<A> algorithm, List<A> arguments) {

		if (ObjectUtils.isEmpty(arguments)) {
			throw new IllegalArgumentException("Arguments must not be empty");
		}

		return doExecute(algorithm, arguments);
	}

	public <A, R> List<Map.Entry<A, R>> execute(AbstractAlgorithm<A> algorithm, int minNumberOfResults) {

		if (minNumberOfResults <= 0) {
			throw new IllegalArgumentException("Number of results must be positive");
		}

		return doExecuteUntilAtLeastNumberOfResults(algorithm, minNumberOfResults);
	}

	private <A, R> List<R> doExecute(AbstractAlgorithm<A> algorithm, List<A> arguments) {

		int numberOfArguments = arguments.size();

		LOGGER.debug("New algorithm execution with {} arguments", numberOfArguments);

		final ExecutorService algorithmContextExecutor = produceExecutorService(numberOfArguments);

		final List<Future<R>> resultsFuture = submitExecutions(algorithmContextExecutor, algorithm, arguments);
		tryAwaitTermination(algorithmContextExecutor,
				TimeUnit.MILLISECONDS.convert(algorithmTaskTimout, algorithmTaskTimeoutTimeUnit));

		return tryExtractResultsStream(resultsFuture).collect(Collectors.toList());
	}

	private <A, R> List<Map.Entry<A, R>> doExecuteUntilAtLeastNumberOfResults(AbstractAlgorithm<A> algorithm,
			int minNumberOfResults) {

		List<Entry<A, R>> results = new LinkedList<>();

		int currentNumberResults = 0;
		while (currentNumberResults < minNumberOfResults) {

			LOGGER.debug("Currently have {} results of wanted {}", currentNumberResults, minNumberOfResults);

			List<A> newArguments = produceArguments(algorithm::argumentProducer, minNumberOfResults);
			List<R> newResults = execute(algorithm, newArguments);

			results.addAll(createEntryMapList(newArguments, newResults));

			currentNumberResults = results.size();
		}

		return results;
	}

	private <A, R> List<Entry<A, R>> createEntryMapList(List<A> arguments, List<R> results) {
		return IntStream.range(0, arguments.size())
				.mapToObj(index -> new AbstractMap.SimpleEntry<A, R>(arguments.get(index), results.get(index)))
				.filter(entry -> !ObjectUtils.isEmpty(entry.getValue())).collect(Collectors.toList());
	}

	private <A> List<A> produceArguments(Supplier<A> argumentProducer, int number) {
		return IntStream.range(0, number).mapToObj(index -> argumentProducer.get()).collect(Collectors.toList());
	}

	private ExecutorService produceExecutorService(int maxThreads) {
		return beanFactory.getBean(ExecutorService.class, maxThreads);
	}

	@SuppressWarnings("unchecked")
	private <A, R> List<Future<R>> submitExecutions(ExecutorService executerService, AbstractAlgorithm<A> algorithm,
			List<A> arguments) {
		return arguments.stream().map(arg -> this.produceExecutionContext(algorithm, arg))
				.map(executionContext -> (Future<R>) executerService.submit(executionContext))
				.collect(Collectors.toList());
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

	private <R> Stream<R> tryExtractResultsStream(List<Future<R>> resultsFuture) {

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

	@SuppressWarnings("unchecked")
	private <A, R> AbstractAlgorithmTask<A, R> produceExecutionContext(AbstractAlgorithm<A> algorithm, A argument) {
		return beanFactory.getBean(AbstractAlgorithmTask.class, algorithm, argument);
	}
}
