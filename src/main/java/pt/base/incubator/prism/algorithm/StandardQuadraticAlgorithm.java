package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardQuadraticAlgorithm extends AbstractAlgorithm<Long> {

	public StandardQuadraticAlgorithm() {
		this.minLongArgument = 100L;
	}

	@Override
	public boolean implementation(Long argument) {
		super.implementation(argument);

		for (int i = 0; i < argument; i++) {
			if (hasTimedOut()) {
				return false;
			}
			for (int j = 0; j < argument; j++) {
				if (hasTimedOut()) {
					return false;
				}
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
		return "StandardQuadraticAlgorithm";
	}

}
