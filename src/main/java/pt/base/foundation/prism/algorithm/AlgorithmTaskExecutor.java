package pt.base.foundation.prism.algorithm;

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

	private static final int ALGORITHM_EXCUTION_TIMEOUT_MILIS = 5000;

	@Autowired
	private BeanFactory beanFactory;

	public List<Object> execute(Object algorithm, List<Object> arguments) {

		if (ObjectUtils.isEmpty(arguments)) {
			throw new IllegalArgumentException("Arguments must not be empty");
		}
		checkSupportedAlgorithmType(algorithm);

		return doExecute(algorithm, arguments);
	}

	public List<Map.Entry<Object, Object>> execute(Object algorithm, Supplier<Object> argumentSupplier,
			int numberResults) {

		if (numberResults <= 0) {
			throw new IllegalArgumentException("Number of results must be positive");
		}
		checkSupportedAlgorithmType(algorithm);

		return doExecuteUntilNumberResults(algorithm, argumentSupplier, numberResults);
	}

	private List<Object> doExecute(Object algorithm, List<Object> arguments) {

		System.out.println(MessageFormat.format("New algorithm execution with {0} arguments", arguments.size()));

		final ExecutorService algorithmContextExecutor = produceExecutorService(arguments.size());

		final List<Future<Object>> resultsFuture = submitExecutions(algorithmContextExecutor, algorithm, arguments);
		tryAwaitTermination(algorithmContextExecutor);

		return tryExtractResultsStream(resultsFuture).collect(Collectors.toList());
	}

	private List<Map.Entry<Object, Object>> doExecuteUntilNumberResults(Object algorithm,
			Supplier<Object> argumentSupplier, int numberResults) {

		int currentNumberResults = 0;
		List<Map.Entry<Object, Object>> results = new LinkedList<>();

		while (currentNumberResults < numberResults) {

			System.out.println(MessageFormat.format("Currently have {0} results of wanted {1}", currentNumberResults,
					numberResults));

			List<Object> newArguments = produceArguments(argumentSupplier, numberResults - currentNumberResults);
			List<Object> newResults = execute(algorithm, newArguments);

			results.addAll(createEntryMapList(newArguments, newResults));

			currentNumberResults = results.size();
		}

		return results;
	}

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

	private void tryAwaitTermination(ExecutorService executorService) {
		try {
			executorService.awaitTermination(ALGORITHM_EXCUTION_TIMEOUT_MILIS, TimeUnit.MILLISECONDS);
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
				return t.get(ALGORITHM_EXCUTION_TIMEOUT_MILIS, TimeUnit.MILLISECONDS);
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
