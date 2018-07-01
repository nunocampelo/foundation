package pt.base.incubator.prism.algorithm;

import java.time.Duration;
import java.time.Instant;

public abstract class AbstractAlgorithm<A> {

	protected int millisTimeout = 5000;
	protected Instant started;

	private int currentLongArgumentIndex;
	protected long maxLongArgument = 100000L;
	protected long minLongArgument = 10L;

	public boolean implementation(A argument) {
		started = Instant.now();
		return false;
	}

	public abstract A argumentProducer();

	protected long defaultLongArgumentProducer() {
		return (long) (Math.random() * (maxLongArgument / Math.pow(10, currentLongArgumentIndex++ % 5)))
				+ minLongArgument;
	}

	protected boolean hasTimedOut() {
		return Duration.between(started, Instant.now()).toMillis() > millisTimeout;
	}

}
