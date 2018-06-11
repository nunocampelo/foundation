package pt.base.incubator.numeric.fitting;

import java.util.Map;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class PolynomialFitter {

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

	public double[] error(Map<Long, Long> observations, double[] coeficients) {

		PolynomialFunction polymonial = new PolynomialFunction(coeficients);

		return observations.keySet().stream().mapToDouble(value -> observations.get(value) - polymonial.value(value))
				.toArray();
	}
}
