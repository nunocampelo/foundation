package pt.base.foundation.numeric.fitting;

import java.util.Map;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class PolinomialFitter {

	public double[] fit(int degree, Map<Long, Long> observations) {

		if (degree <= 0) {
			throw new IllegalStateException("Polinomial fitter degree must be positive");
		}

		if (CollectionUtils.isEmpty(observations)) {
			throw new IllegalStateException("Observations must not be empty");
		}

		final WeightedObservedPoints obs = new WeightedObservedPoints();
		observations.forEach(obs::add);

		final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);

		return fitter.fit(obs.toList());
	}

}
