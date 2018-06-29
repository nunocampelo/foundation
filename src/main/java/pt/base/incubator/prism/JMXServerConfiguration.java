package pt.base.incubator.prism;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

@Configuration
public class JMXServerConfiguration {

	private static final String JMX_SERVICE_URL = "service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi";

	@Value("${jmx.server.host:localhost}")
	private String jmxServerHost;

	@Value("${jmx.server.port:8081}")
	private Integer jmxServerPort;

	private String jmxServerUrl;

	public String getJmxServerUrl() {
		return jmxServerUrl;
	}

	@PostConstruct
	public void init() {
		jmxServerUrl = String.format(JMX_SERVICE_URL, jmxServerHost, jmxServerPort, jmxServerHost, jmxServerPort);
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
	public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {

		final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
		connectorServerFactoryBean.setObjectName("connector:name=rmi");

		connectorServerFactoryBean.setServiceUrl(jmxServerUrl);
		return connectorServerFactoryBean;
	}
}
