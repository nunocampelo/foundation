package pt.base.incubator.prism.algorithm;

public abstract class AbstractAlgorithm<A> {

	protected boolean canceled;

	protected long maxLongArgument = 1000000L;
	protected long minLongArgument = 10L;

	public abstract boolean implementation(A argument);

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		this.canceled = true;
	}

	public void reset() {
		this.canceled = false;
	}

	public abstract A argumentProducer();

	protected long defaultLongArgumentProducer() {
		return (long) (minLongArgument + (maxLongArgument - minLongArgument) * Math.random());
	}

}
