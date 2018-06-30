package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardSixDegreeAlgorithm extends AbstractAlgorithm<Long> {

	@Override
	public boolean implementation(Long argument) {
		super.implementation(argument);

		int i = 0, j = 0, k = 0, l = 0, m = 0, n = 0;

		while (i < argument) {
			if (hasTimedOut()) {
				return false;
			}
			while (j < argument) {
				if (hasTimedOut()) {
					return false;
				}
				while (k < argument) {
					if (hasTimedOut()) {
						return false;
					}
					while (l < argument) {
						if (hasTimedOut()) {
							return false;
						}
						while (m < argument) {
							if (hasTimedOut()) {
								return false;
							}
							while (n < argument) {
								if (hasTimedOut()) {
									return false;
								}
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

		return true;
	}

	@Override
	public Long argumentProducer() {
		return defaultLongArgumentProducer();
	}

	@Override
	public String toString() {
		return "StandardSixDegreeAlgorithm";
	}
}
