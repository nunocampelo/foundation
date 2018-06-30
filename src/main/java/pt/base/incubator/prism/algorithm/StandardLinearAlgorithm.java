package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardLinearAlgorithm extends AbstractAlgorithm<Long> {

	@Override
	public boolean implementation(Long argument) {
		super.implementation(argument);

		int i = 0;
		while (i < argument) {
			if (hasTimedOut()) {
				return false;
			}
			i++;
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
