package pt.base.incubator.prism.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import pt.base.incubator.prism.Prism;
import pt.base.incubator.prism.algorithm.AbstractAlgorithm;
import pt.base.incubator.prism.algorithm.AbstractAlgorithmTask;

@Configuration
@Profile(Prism.JMX_PROFILE)
public class JMXServerConfiguration {

	private static final String RMI_CONNECTOR_NAME = "connector:name=rmi";
	private static final String JMX_SERVICE_URL_PATTERN = "service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi";

	@Value("${machine.cpu.cores:8}")
	private int cpuCores;

	@Value("${jmx.server.host:localhost}")
	private String jmxServerHost;

	@Value("${jmx.server.port:8081}")
	private Integer jmxServerPort;

	private String jmxServerUrl;

	public ConnectorServerFactoryBean jmxFactoryBean;

	@Autowired
	private MBeanServerConnection jmxServerConnection;

	@PostConstruct
	public void init() {
		jmxServerUrl =
				String.format(JMX_SERVICE_URL_PATTERN, jmxServerHost, jmxServerPort, jmxServerHost, jmxServerPort);
	}

	@Bean
	@Lazy
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected ExecutorService generateExecutorService(int numberExecutions) {
		return Executors.newFixedThreadPool(cpuCores - 2);
	}

	@Bean
	@Lazy
	@Profile(Prism.JMX_PROFILE)
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected <A> AbstractAlgorithmTask<A, Long> wrapperFactory(AbstractAlgorithm<A> algorithm, A argument) {
		return new JMXAlgorithmTask<A>(jmxServerConnection, algorithm, argument);
	}

	@Bean
	public RmiRegistryFactoryBean rmiRegistry() {

		final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();

		rmiRegistryFactoryBean.setPort(jmxServerPort);
		rmiRegistryFactoryBean.setAlwaysCreate(true);

		return rmiRegistryFactoryBean;
	}

	@Bean
	@DependsOn("rmiRegistry")
	public ConnectorServerFactoryBean factoryBeanFactory() throws Exception {

		jmxFactoryBean = new ConnectorServerFactoryBean();
		jmxFactoryBean.setObjectName(RMI_CONNECTOR_NAME);

		jmxFactoryBean.setServiceUrl(jmxServerUrl);

		return jmxFactoryBean;
	}

	@Bean
	@DependsOn("factoryBeanFactory")
	public MBeanServerConnection jmxServerConnectionFactory() {

		JMXServiceURL url;
		MBeanServerConnection jmxServerConnection = null;
		try {

			url = new JMXServiceURL(jmxServerUrl);
			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			jmxc.connect();

			jmxServerConnection = jmxc.getMBeanServerConnection();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jmxServerConnection;
	}

	public void destroy() {
		try {
			jmxFactoryBean.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
