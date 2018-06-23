package pt.base.incubator.prism.sigar;

import org.hyperic.sigar.Sigar;

import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AbstractAlgorithmTask;

public class CpuTimedAlgorithmTask<A> extends AbstractAlgorithmTask<A, Long> {

	private Sigar sigar;

	public CpuTimedAlgorithmTask(Sigar sigar, AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
		this.sigar = sigar;
	}

	@Override
	public Long call() throws Exception {

		if (Thread.interrupted()) {
			return null;
		}

		System.out.println("Running with arg: " + argument + ", on thread: " + Thread.currentThread());
		execute();

		long result = sigar.getThreadCpu().getTotal();
		System.out.println("Finished with result: " + result + ", on thread: " + Thread.currentThread());
		return result > 0 ? result : null;
	}
}
