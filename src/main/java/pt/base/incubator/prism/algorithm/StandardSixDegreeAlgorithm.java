package pt.base.incubator.prism.algorithm;

import org.springframework.stereotype.Component;

@Component
public class StandardSixDegreeAlgorithm extends AbstractAlgorithm<Long> {

	public StandardSixDegreeAlgorithm() {
		this.maxLongArgument = 20L;
		this.minLongArgument = 3L;
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

				for (int k = 0; k < argument; k++) {
					if (hasTimedOut()) {
						return false;
					}

					for (int l = 0; l < argument; l++) {
						if (hasTimedOut()) {
							return false;
						}

						for (int m = 0; m < argument; m++) {
							if (hasTimedOut()) {
								return false;
							}

							for (int n = 0; n < argument; n++) {
								if (hasTimedOut()) {
									return false;
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public Long argumentProducer() {
		return (long) (Math.random() * maxLongArgument) + minLongArgument;
	}

	@Override
	public String toString() {
		return "StandardSixDegreeAlgorithm";
	}
}
