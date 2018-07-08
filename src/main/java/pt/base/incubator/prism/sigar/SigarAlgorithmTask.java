package pt.base.incubator.prism.sigar;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.TimedAlgorithmTask;

public class SigarAlgorithmTask<A> extends TimedAlgorithmTask<A> {

	private Sigar sigar;

	public SigarAlgorithmTask(Sigar sigar, AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
		this.sigar = sigar;
	}

	@Override
	protected long getCurrentTime() {

		long cpuTime = 0L;

		try {
			cpuTime = sigar.getThreadCpu().getTotal();
		} catch (SigarException e) {
			e.printStackTrace();
		}

		return cpuTime;
	}
}
