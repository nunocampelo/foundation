package pt.base.incubator.prism.algorithm.standard;

import org.springframework.stereotype.Component;

import pt.base.incubator.prism.algorithm.AbstractAlgorithm;

@Component
public class StandardQuadraticAlgorithm extends AbstractAlgorithm<Long> {

	public StandardQuadraticAlgorithm() {
		this.maxLongArgument = 70000L;
		this.minLongArgument = 1000L;
	}

	@Override
	public boolean implementation(Long argument) {

		for (int i = 0; i < argument; i++) {
			if (isCanceled()) {
				return false;
			}
			for (int j = 0; j < argument; j++) {
				if (isCanceled()) {
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
