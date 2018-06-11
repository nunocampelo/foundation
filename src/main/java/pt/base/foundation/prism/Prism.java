package pt.base.foundation.prism;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.annotation.PreDestroy;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import pt.base.foundation.numeric.fitting.PolinomialFitter;

@Component
public class Prism {

	private static final int DEFAULT_NUMBER_ALGORITHM_EXECUTIONS = 10;

	private Sigar sigar;
	public PolinomialFitter polinomialFitter;

	@Autowired
	public Prism(PolinomialFitter polimonialFitter, Sigar sigar) {
		super();

		this.sigar = new Sigar();
		this.polinomialFitter = polimonialFitter;

		final int numberExecutions = 10;
		analyse(3, arg -> new StandardLinearAlgorithm(arg), this::getArg, numberExecutions);
	}

	public void analyse(int assumptionOrder, Function<Long, Runnable> algorithmFunction,
			Supplier<Long> argumentProducer) {
		analyse(assumptionOrder, algorithmFunction, argumentProducer, DEFAULT_NUMBER_ALGORITHM_EXECUTIONS);
	}

	public void analyse(int assumptionOrder, Function<Long, Runnable> algorithmFunction,
			Supplier<Long> argumentProducer, int numberExecutions) {

		Map<Long, Long> observations = executeAlgorithm(algorithmFunction, argumentProducer, numberExecutions);
		double[] coeficients = polinomialFitter.fit(assumptionOrder, observations);

		for (double d : coeficients) {
			System.out.println(d);
		}
	}

	@PreDestroy
	public void shutdown() {
		sigar.close();
	}

	private Map<Long, Long> executeAlgorithm(Function<Long, Runnable> algorithmFunction,
			Supplier<Long> argumentProducer, int numberExecutions) {

		System.out.println("Executing algorithm...");

		List<Long> args = LongStream.range(0, numberExecutions).mapToObj(index -> argumentProducer.get())
				.collect(Collectors.toList());
		List<Long> images = args.stream().parallel().map(arg -> executeAlgorithmOnArg(algorithmFunction, arg))
				.collect(Collectors.toList());

		return toMap(args, images);
	}

	private Long executeAlgorithmOnArg(Function<Long, Runnable> algorithmFunction, Long arg) {

		AlgorithmExecuter standardLinearAlgorithmExecuter = new AlgorithmExecuter(algorithmFunction.apply(arg));

		Thread executerThread = new Thread(standardLinearAlgorithmExecuter);
		executerThread.start();
		try {
			executerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("(" + arg + "," + standardLinearAlgorithmExecuter.getTotalThreadCpuTime() + ")");
		return standardLinearAlgorithmExecuter.getTotalThreadCpuTime();
	}

	private long getArg() {
		return (long) (Math.random() * Integer.MAX_VALUE);
	}

	private Map<Long, Long> toMap(List<Long> args, List<Long> images) {

		if (CollectionUtils.isEmpty(args) || CollectionUtils.isEmpty(images) || args.size() != images.size()) {
			throw new IllegalStateException("Args and images must be non empty and equal sized");
		}

		Map<Long, Long> observations = new HashMap<>(args.size());
		IntStream.range(0, args.size()).forEach(index -> observations.put(args.get(index), images.get(index)));

		return observations;
	}

	public class BigTheta {

		private int order;

		public int getOrder() {
			return order;
		}

		public BigTheta(int order) {

			if (order < 0) {
				throw new IllegalStateException("Big theta order must be positive");
			}

			this.order = order;
		}
	}

	private class AlgorithmExecuter implements Runnable {

		private Runnable algorithm;
		private long totalThreadCpuTime;

		public long getTotalThreadCpuTime() {
			return totalThreadCpuTime;
		}

		public AlgorithmExecuter(Runnable algorithm) {
			super();
			this.algorithm = algorithm;
		}

		@Override
		public void run() {

			try {

				algorithm.run();
				totalThreadCpuTime = sigar.getThreadCpu().getTotal();

			} catch (SigarException e) {
				e.printStackTrace();
			}
		}
	}

	private class StandardLinearAlgorithm implements Runnable {

		private long numberIterations;

		private StandardLinearAlgorithm(long numberIterations) {
			this.numberIterations = numberIterations;
		}

		@Override
		public void run() {

			int i = 0;
			while (i < numberIterations) {
				i++;
			}
		}
	}

}
