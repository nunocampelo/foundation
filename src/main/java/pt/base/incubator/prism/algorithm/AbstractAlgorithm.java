package pt.base.incubator.prism.algorithm;

public abstract class AbstractAlgorithm<A> {

	private int currentLongArgumentIndex;
	private long maxLongArgument = 10000000000000L;

	public abstract void implementation(A argument);

	public abstract A argumentProducer();

	protected long defaultLongArgumentProducer() {
		return (long) (Math.random() * (maxLongArgument / Math.pow(10, currentLongArgumentIndex++ % 10)));
	}
}
