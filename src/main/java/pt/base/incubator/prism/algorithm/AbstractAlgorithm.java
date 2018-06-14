package pt.base.incubator.prism.algorithm;

import java.time.Duration;
import java.time.Instant;

public abstract class AbstractAlgorithm<A> {

	protected int millisTimeout = 5000;
	protected Instant started;

	private int currentLongArgumentIndex;
	private long maxLongArgument = 10000000000000L;

	public void implementation(A argument) {
		started = Instant.now();
	}

	public abstract A argumentProducer();

	protected long defaultLongArgumentProducer() {
		return (long) (Math.random() * (maxLongArgument / Math.pow(10, currentLongArgumentIndex++ % 10)));
	}

	protected boolean hasTimedOut() {
		return Duration.between(started, Instant.now()).toMillis() > millisTimeout;
	}
}
