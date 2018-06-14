package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardSixDegreeAlgorithm extends AbstractAlgorithm<Long> {

	@Override
	public void implementation(Long argument) {
		super.implementation(argument);

		int i = 0, j = 0, k = 0, l = 0, m = 0, n = 0;

		while (i < argument && !hasTimedOut()) {
			while (j < argument && !hasTimedOut()) {
				while (k < argument && !hasTimedOut()) {
					while (l < argument && !hasTimedOut()) {
						while (m < argument && !hasTimedOut()) {
							while (n < argument && !hasTimedOut()) {
								n++;
							}
							m++;
						}
						l++;
					}
					k++;
				}
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
