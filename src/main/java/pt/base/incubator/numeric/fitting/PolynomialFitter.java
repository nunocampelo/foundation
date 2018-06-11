package pt.base.incubator.numeric.fitting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.primitives.Doubles;

@Service
public class PolynomialFitter {

	public List<Double> fit(int degree, Map<Long, Long> observations) {

		if (degree <= 0) {
			throw new IllegalStateException("Polinomial fitter degree must be positive");
		}

		if (CollectionUtils.isEmpty(observations)) {
			throw new IllegalStateException("Observations must not be empty");
		}

		final WeightedObservedPoints obs = new WeightedObservedPoints();
		observations.forEach(obs::add);

		final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);

		return Doubles.asList(fitter.fit(obs.toList()));
	}

	public List<Double> error(Map<Long, Long> observations, List<Double> coeficients) {

		PolynomialFunction polymonial = new PolynomialFunction(Doubles.toArray(coeficients));

		return observations.keySet().stream().mapToDouble(value -> observations.get(value) - polymonial.value(value))
				.boxed().collect(Collectors.toList());
	}
}
