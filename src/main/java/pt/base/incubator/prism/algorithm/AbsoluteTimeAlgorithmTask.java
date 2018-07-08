package pt.base.incubator.prism.algorithm;

public class AbsoluteTimeAlgorithmTask<A> extends TimedAlgorithmTask<A> {

	public AbsoluteTimeAlgorithmTask(AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
	}

	@Override
	protected long getCurrentTime() {
		return System.currentTimeMillis();
	}

}
