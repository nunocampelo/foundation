package pt.base.incubator.prism.algorithm;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAlgorithmTask<A, R> implements Callable<R> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAlgorithmTask.class);

	protected R result;
	protected A argument;
	protected AbstractAlgorithm<A> algorithm;
	private Status status = Status.CREATED;

	public AbstractAlgorithm<A> getAlgorithm() {
		return algorithm;
	}

	public Status getStatus() {
		return status;
	}

	public R getResult() {
		return result;
	}

	protected void setStatus(Status status) {

		if (status == null) {
			LOGGER.debug("Task status must not be null");
			return;
		}

		if (this.status == Status.FINISHED || this.status == Status.CANCELED) {
			LOGGER.debug("Status {} is final cant change it to {}", Status.CANCELED, status);
			return;
		}

		LOGGER.debug("Setting status {} on {}", status, this);
		this.status = status;

		if (status == Status.CANCELED) {
			algorithm.cancel();
		}
	}

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

	@Override
	public String toString() {
		return "Task [status=" + status + ", algorithm=" + algorithm + ", argument=" + argument + "]";
	}

	public enum Status {
		CREATED, STARTED, FINISHED, CANCELED;

		public boolean isFinal() {
			return this == FINISHED || this == Status.CANCELED;
		}
	}
}
