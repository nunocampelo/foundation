package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardQuadraticAlgorithm extends AbstractAlgorithm<Long> {

	@Override
	public void implementation(Long argument) {
		super.implementation(argument);

		int i = 0, j = 0;

		while (i < argument && !hasTimedOut()) {
			while (j < argument && !hasTimedOut()) {
				j++;
			}
			i++;
		}
	}

	@Override
	public Long argumentProducer() {
		return defaultLongArgumentProducer();
	}

}
