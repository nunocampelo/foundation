package pt.base.incubator.prism.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TimedAlgorithmTask<A> extends AbstractAlgorithmTask<A, Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimedAlgorithmTask.class);

	public TimedAlgorithmTask(AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
	}

	@Override
	public Long call() throws Exception {

		long time = getCurrentTime();
		LOGGER.debug("Running with arg: {}, stating at: {}", argument, time);

		boolean finished = execute();

		long endTime = getCurrentTime();
		time = endTime - time;

		LOGGER.debug("Executed with arg: {}, with time: {}, ended at: {}, finished: {}", argument, time, endTime,
				finished);

		if (endTime <= 0L) {
			LOGGER.info("Illegal time found: {}, with arg: {}", time, argument);
		}

		result = finished && time > 0 ? time : null;
		return result;
	}

	protected abstract long getCurrentTime();
}
