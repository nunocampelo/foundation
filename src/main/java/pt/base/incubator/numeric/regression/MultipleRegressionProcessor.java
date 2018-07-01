package pt.base.incubator.numeric.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.springframework.stereotype.Service;

@Service
public class MultipleRegressionProcessor {

	public double regress(List<Entry<Long, Long>> observations, int degree) {

		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();

		double[][] x = new double[observations.size()][degree];

		Entry<Long, Long> obs;
		for (int i = 0; i < observations.size(); i++) {
			obs = observations.get(i);
			for (int j = 0; j < degree; j++) {
				x[i][j] = Math.pow(obs.getKey(), j + 1);
			}
		}

		double[] y = observations.stream().mapToDouble(observation -> observation.getValue()).toArray();

		regression.newSampleData(y, x);

		return regression.calculateRSquared();
	}

	public double regress(Map<Long, Long> observations, int degree) {

		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();

		List<Long> arguments = new ArrayList<>(observations.keySet());
		double[][] x = new double[observations.size()][degree];

		IntStream.range(0, observations.size()).forEach(i -> {

			Long argument = arguments.get(i);

			for (int j = 0; j < degree; j++) {
				x[i][j] = Math.pow(argument, j + 1);
			}
		});

		double[] y = observations.values().stream().mapToDouble(l -> l).toArray();
		regression.newSampleData(y, x);

		return regression.calculateRSquared();
	}

}
