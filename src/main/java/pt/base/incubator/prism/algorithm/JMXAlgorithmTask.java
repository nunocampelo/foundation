package pt.base.incubator.prism.algorithm;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMXAlgorithmTask<A> extends AbstractAlgorithmTask<A, Long> {

	private static final String PROCESS_CPU_TIME_PROP_NAME = "ProcessCpuTime";
	private static final String OPERATING_SYSTEM_OBJECT_NAME = "java.lang:type=OperatingSystem";
	private static final Logger LOGGER = LoggerFactory.getLogger(JMXAlgorithmTask.class);
	private MBeanServerConnection jmxServerConnection;

	public JMXAlgorithmTask(MBeanServerConnection jmxServerConnection, AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
		this.jmxServerConnection = jmxServerConnection;
	}

	@Override
	public Long call() throws Exception {

		long cpuTime = getCurrentCpuTime();
		LOGGER.debug("Running with arg: {}, on thread: {}, stating at:{}", argument, Thread.currentThread(), cpuTime);

		boolean success = execute();

		if (algorithm.hasTimedOut()) {
			LOGGER.debug("Algorithm Task timeout returning thread: {}", Thread.currentThread());
			setStatus(Status.CANCELED);
			return null;
		}

		long endCpuTime = getCurrentCpuTime();
		cpuTime = endCpuTime - cpuTime;

		LOGGER.debug("Executed with arg: {}, with result: {}, on thread: {}, ended in: {}, terminated: {}", argument,
				cpuTime, Thread.currentThread(), endCpuTime, success);

		return cpuTime > 0 && success ? cpuTime : null;
	}

	private long getCurrentCpuTime() {

		long cpuTime = 0;

		try {
			cpuTime = (long) (jmxServerConnection.getAttribute(new ObjectName(OPERATING_SYSTEM_OBJECT_NAME),
					PROCESS_CPU_TIME_PROP_NAME));
		} catch (AttributeNotFoundException | InstanceNotFoundException | MalformedObjectNameException | MBeanException
				| ReflectionException | IOException e) {
			e.printStackTrace();
		}

		if (cpuTime <= 0L) {
			LOGGER.error("Illegal CPU time");
		}

		return cpuTime;
	}

}
