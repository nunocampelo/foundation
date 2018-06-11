package pt.base.foundation.prism;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Prism {

	private static Sigar sigar;

	public static void main(String[] args) throws SigarException, InterruptedException {

		sigar = new Sigar();

		AlgorithmExecuter standardLinearAlgorithmExecuter = new AlgorithmExecuter(new StandardLinearAlgorithm());

		Thread standardLinearExecuterThread = new Thread(standardLinearAlgorithmExecuter);
		standardLinearExecuterThread.start();
		standardLinearExecuterThread.join();

		System.out.println("Standard: " + standardLinearAlgorithmExecuter.getTotalThreadCpuTime());

		sigar.close();
	}

	private static class AlgorithmExecuter implements Runnable {

		private Runnable algorithm;
		private long totalThreadCpuTime;

		public long getTotalThreadCpuTime() {
			return totalThreadCpuTime;
		}

		public AlgorithmExecuter(Runnable algorithm) {
			super();
			this.algorithm = algorithm;
		}

		@Override
		public void run() {

			try {

				algorithm.run();
				totalThreadCpuTime = sigar.getThreadCpu().getTotal();

			} catch (SigarException e) {
				e.printStackTrace();
			}
		}
	}

	private static class StandardLinearAlgorithm implements Runnable {

		@Override
		public void run() {

			int i = 0;
			while (i < 1000000000L) {
				i++;
			}
		}
	}

}
