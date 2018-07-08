package pt.base.incubator.prism.jmx;

import java.io.IOException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.TimedAlgorithmTask;

public class JMXAlgorithmTask<A> extends TimedAlgorithmTask<A> {

	private MBeanServerConnection jmxServerConnection;
	private static final String PROCESS_CPU_TIME_PROP_NAME = "ProcessCpuTime";
	private static final String OPERATING_SYSTEM_OBJECT_NAME = "java.lang:type=OperatingSystem";

	public JMXAlgorithmTask(MBeanServerConnection jmxServerConnection, AbstractAlgorithm<A> algorithm, A argument) {
		super(algorithm, argument);
		this.jmxServerConnection = jmxServerConnection;
	}

	@Override
	protected long getCurrentTime() {

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
