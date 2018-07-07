package pt.base.incubator.prism.sigar;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AbstractAlgorithmTask;

public class SigarAlgorithmTask<A> extends AbstractAlgorithmTask<A, Long> {

	private Sigar sigar;
	private static final Logger LOGGER = LoggerFactory.getLogger(SigarAlgorithmTask.class);

	public SigarAlgorithmTask(Sigar sigar, AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
		this.sigar = sigar;
	}

	@Override
	public Long call() throws Exception {

		long cpuTime = getCurrentCpuTime();
		LOGGER.debug("Running with arg: {}, stating at:{}", argument, cpuTime);

		boolean finished = execute();

		long endCpuTime = getCurrentCpuTime();
		cpuTime = endCpuTime - cpuTime;

		LOGGER.debug("Executed with arg: {}, with cpu time: {}, ended at: {}, finished: {}", argument, cpuTime,
				endCpuTime, finished);

		if (cpuTime <= 0L) {
			LOGGER.info("Illegal CPU time found: {}, with arg: {}", cpuTime, argument);
		}

		result = finished && cpuTime > 0 ? cpuTime : null;
		return result;
	}

	private long getCurrentCpuTime() {

		long cpuTime = 0L;

		try {
			cpuTime = sigar.getThreadCpu().getTotal();
		} catch (SigarException e) {
			e.printStackTrace();
		}

		return cpuTime;
	}
}
