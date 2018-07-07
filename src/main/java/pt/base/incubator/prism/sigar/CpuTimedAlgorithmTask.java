package pt.base.incubator.prism.sigar;

import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AbstractAlgorithmTask;

public class CpuTimedAlgorithmTask<A> extends AbstractAlgorithmTask<A, Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpuTimedAlgorithmTask.class);
	private Sigar sigar;

	public CpuTimedAlgorithmTask(Sigar sigar, AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
		this.sigar = sigar;
	}

	@Override
	public Long call() throws Exception {

		LOGGER.debug("Running with arg: {}", argument);

		boolean success = execute();
		long cpuTime = sigar.getThreadCpu().getTotal();

		LOGGER.debug("Executed with arg: {}, with result: {}, terminated: {}", argument, cpuTime, success);

		result = cpuTime > 0 && success ? cpuTime : null;
		return result;
	}
}
