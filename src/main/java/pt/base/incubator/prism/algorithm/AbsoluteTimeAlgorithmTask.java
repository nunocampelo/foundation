package pt.base.incubator.prism.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbsoluteTimeAlgorithmTask<A> extends AbstractAlgorithmTask<A, Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteTimeAlgorithmTask.class);

	public AbsoluteTimeAlgorithmTask(AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
	}

	@Override
	public Long call() throws Exception {

		long cpuTime = getCurrentCpuTime();
		LOGGER.debug("Running with arg: {}, stating at: {}", argument, cpuTime);

		boolean finished = execute();

		long endCpuTime = getCurrentCpuTime();
		cpuTime = endCpuTime - cpuTime;

		LOGGER.debug("Executed with arg: {}, with cpu time: {}, ended at: {}, finished: {}", argument, cpuTime,
				endCpuTime, finished);

		if (cpuTime <= 0L) {
			LOGGER.info("Illegal CPU time found: {}, with arg: {}", cpuTime, argument);
		}

		result = finished && cpuTime > 0 ? cpuTime : null;
		return result;
	}

	private long getCurrentCpuTime() {
		return System.currentTimeMillis();
	}

}
