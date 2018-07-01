package pt.base.incubator.prism.algorithm;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAlgorithmTask<A, R> implements Callable<R> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAlgorithmTask.class);

	private Status status = Status.CREATED;
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

	protected boolean execute() {

		setStatus(Status.STARTED);
		boolean executionSuccess = algorithm.implementation(argument);
		setStatus(Status.FINISHED);

		return executionSuccess;
	}

	protected void setStatus(Status status) {

		if (status == null) {
			LOGGER.debug("Task status must not be null");
			return;
		}

		if (this.status == Status.CANCELED) {
			LOGGER.debug("Status {} is final", Status.CANCELED);
			return;
		}

		this.status = status;
		LOGGER.debug("{}", this);
	}

	@Override
	public String toString() {
		return "Task [status=" + status + ", algorithm=" + algorithm + ", argument=" + argument + "]";
	}

	protected enum Status {
		CREATED, STARTED, FINISHED, CANCELED;
	}
}
