package pt.base.incubator.prism.algorithm;

import java.util.concurrent.Callable;

public abstract class AbstractAlgorithmTask<A, R> implements Callable<R> {

	protected AbstractAlgorithm<A> algorithm;
	protected A argument;

	public AbstractAlgorithmTask(AbstractAlgorithm<A> algorithm, A argument) {
		super();

		if (algorithm == null || argument == null) {
			throw new IllegalStateException("Algorithm context must not be null");
		}

		this.algorithm = algorithm;
		this.argument = argument;
	}

	protected void execute() {
		algorithm.implementation(argument);
	}
}
