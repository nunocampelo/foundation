package pt.base.incubator.numeric.regression;

import java.util.List;
import java.util.Map.Entry;

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

		System.out.println(regression.calculateRSquared());
		return regression.calculateRSquared();
	}

}
