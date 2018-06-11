package pt.base.foundation.prism.algorithm;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractAlgorithmTask implements Callable<Object> {

	protected Object algorithm;
	protected Object argument;

	public AbstractAlgorithmTask(Object algorithm, Object argument) {
		super();

		if (algorithm == null || argument == null) {
			throw new IllegalStateException("Algorithm context must not be null");
		}

		this.algorithm = algorithm;
		this.argument = argument;
	}

	@SuppressWarnings("unchecked")
	protected void run(Object algorithm, Object argument) {

		if (algorithm instanceof Function) {
			((Function<Object, ?>) algorithm).apply(argument);
		} else if (algorithm instanceof Consumer<?>) {
			((Consumer<Object>) algorithm).accept(argument);
		} else if (algorithm instanceof Runnable) {
			((Runnable) algorithm).run();
		} else if (algorithm instanceof Callable) {
			try {
				((Callable<?>) algorithm).call();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			throw new IllegalStateException("Unsupported algorithm type");
		}
	}
}
