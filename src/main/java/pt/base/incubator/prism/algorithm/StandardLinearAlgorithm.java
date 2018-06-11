package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardLinearAlgorithm extends AbstractAlgorithm<Long> {

	@Override
	public void implementation(Long argument) {
		int i = 0;
		while (i < argument && !Thread.currentThread().isInterrupted()) {
			i++;
		}
	}

	@Override
	public Long argumentProducer() {
		return defaultLongArgumentProducer();
	}
}
