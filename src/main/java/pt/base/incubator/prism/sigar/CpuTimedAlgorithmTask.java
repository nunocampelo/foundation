package pt.base.incubator.prism.sigar;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
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

		long cpuTime = getCurrentCpuTime();

		LOGGER.debug("Running with arg: {}, stating at:{}", argument, cpuTime);

		boolean success = execute();

		long endCpuTime = getCurrentCpuTime();
		cpuTime = endCpuTime - cpuTime;

		LOGGER.debug("Executed with arg: {}, with result: {}, ended in: {}, terminated: {}", argument, cpuTime,
				endCpuTime, success);

		result = cpuTime > 0 && success ? cpuTime : null;
		return result;
	}

	private long getCurrentCpuTime() {

		long cpuTime = 0L;

		try {
			cpuTime = sigar.getCpu().getTotal();
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (cpuTime <= 0L) {
			LOGGER.error("Illegal CPU time");
		}

		return cpuTime;
	}
}
