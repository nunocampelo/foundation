package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardQuadraticAlgorithm extends AbstractAlgorithm<Long> {

	@Override
	public boolean implementation(Long argument) {
		super.implementation(argument);

		int i = 0, j = 0;

		while (i < argument) {
			if (hasTimedOut()) {
				return false;
			}
			while (j < argument) {
				if (hasTimedOut()) {
					return false;
				}
				j++;
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
		return "StandardQuadraticAlgorithm";
	}

}
