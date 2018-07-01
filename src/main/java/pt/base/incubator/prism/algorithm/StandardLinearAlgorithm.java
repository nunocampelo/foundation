package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardLinearAlgorithm extends AbstractAlgorithm<Long> {

	public StandardLinearAlgorithm() {
		this.maxLongArgument = 100000000L;
		this.minLongArgument = 10000L;
	}

	@Override
	public boolean implementation(Long argument) {
		super.implementation(argument);

		for (int i = 0; i < argument; i++) {
			if (hasTimedOut()) {
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
		return "StandardLinearAlgorithm";
	}
}
