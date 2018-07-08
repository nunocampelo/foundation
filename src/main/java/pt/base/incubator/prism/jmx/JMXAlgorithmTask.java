package pt.base.incubator.prism.jmx;

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

import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AbstractAlgorithmTask;

public class JMXAlgorithmTask<A> extends AbstractAlgorithmTask<A, Long> {

	private MBeanServerConnection jmxServerConnection;
	private static final Logger LOGGER = LoggerFactory.getLogger(JMXAlgorithmTask.class);
	private static final String PROCESS_CPU_TIME_PROP_NAME = "ProcessCpuTime";
	private static final String OPERATING_SYSTEM_OBJECT_NAME = "java.lang:type=OperatingSystem";

	public JMXAlgorithmTask(MBeanServerConnection jmxServerConnection, AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
		this.jmxServerConnection = jmxServerConnection;
	}

	@Override
	public Long call() throws Exception {

		long cpuTime = getCurrentCpuTime();
		LOGGER.debug("Running with arg: {}, stating at: {}", argument, cpuTime);

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

		long cpuTime = 0;

		try {
			cpuTime = (long) (jmxServerConnection.getAttribute(new ObjectName(OPERATING_SYSTEM_OBJECT_NAME),
					PROCESS_CPU_TIME_PROP_NAME));
		} catch (AttributeNotFoundException | InstanceNotFoundException | MalformedObjectNameException | MBeanException
				| ReflectionException | IOException e) {
			e.printStackTrace();
		}

		return cpuTime;
	}

}
