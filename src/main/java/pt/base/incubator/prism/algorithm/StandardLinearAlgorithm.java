package pt.base.incubator.prism.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StandardLinearAlgorithm extends AbstractAlgorithm<Long> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StandardLinearAlgorithm.class);

	public StandardLinearAlgorithm() {
		this.maxLongArgument = 10000000000L;
		this.minLongArgument = 1000L;
	}

	@Override
	public boolean implementation(Long argument) {

		for (int i = 0; i < argument; i++) {
			if (isCanceled()) {
				LOGGER.debug("Checked cancelation on {}", this);
				return false;
			}
		}

		return true;
	}

	@Override
	public Long argumentProducer() {
		return defaultLongArgumentProducer();
	}

	@Override
	public String toString() {
		return "StandardLinearAlgorithm [canceled=" + canceled + "]";
	}
}
