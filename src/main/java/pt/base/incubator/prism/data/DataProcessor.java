package pt.base.incubator.prism.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessor.class);

	public <A extends Comparable<? super A>, R> void sortByKey(List<Entry<A, R>> data) {
		data.sort(Comparator.comparing(Entry::getKey));
	}

	public <A> Map<A, Long> averageSameKeyResults(List<Entry<A, Long>> source) {

		final Map<A, Long> results = new TreeMap<>();
		final Map<A, Integer> argumentCount = new HashMap<>();

		source.forEach(e -> {

			A argument = e.getKey();

			if (!results.containsKey(argument)) {
				argumentCount.put(argument, 1);
				results.put(argument, e.getValue());
			} else {
				argumentCount.put(argument, argumentCount.get(argument) + 1);
				results.put(argument, results.get(argument) + e.getValue());
			}
		});

		results.forEach((a, r) -> {
			r = r / argumentCount.get(a);
		});

		return results;
	}

	public <A> Map<A, Long> getValuesClosestToMean(List<Entry<A, Long>> source) {

		final Map<A, Long> results = new TreeMap<>();
		final Map<A, List<Long>> argumentToValuesMap = new HashMap<>();

		source.forEach(e -> {

			A argument = e.getKey();

			if (!results.containsKey(argument)) {
				argumentToValuesMap.put(argument, new LinkedList<>());
			}
			argumentToValuesMap.get(argument).add(e.getValue());
		});

		argumentToValuesMap.forEach((a, l) -> {
			double mean = StatUtils.mean(l.stream().mapToDouble(i -> i).toArray());
			results.put(a, getClosestTo(l, mean));
		});

		return results;
	}

	private long getClosestTo(List<Long> source, double target) {

		return source.stream().reduce(source.get(0), (value, result) -> {

			if (value == result) {
				return result;
			}

			double prevDistance = Math.abs(target - result);
			double currentDistance = Math.abs(target - value);

			if (currentDistance < prevDistance) {
				return value;
			}

			return result;
		});
	}

	public <A> List<Entry<A, Long>> filterOutOutliers(List<Entry<A, Long>> source) {

		OutliersStats<A> outliersStats = computeOutliersStats(source);
		List<Entry<A, Long>> result = outliersStats.filterOutOutliers();

		LOGGER.debug("Found {} outliers in {} results", source.size() - result.size(), source.size());

		return result;
	}

	private <A> OutliersStats<A> computeOutliersStats(List<Entry<A, Long>> source) {
		return new OutliersStats<A>(source);
	}

	private class OutliersStats<A> {

		private double q25;
		private double q75;
		private double interQuartil;

		private double lowerLimit;
		private double upperLimit;

		private List<Entry<A, Long>> source;

		public OutliersStats(List<Entry<A, Long>> source) {
			super();

			this.source = source;
			compute(source.stream().map(Entry::getValue).collect(Collectors.toList()));
		}

		private void compute(List<Long> results) {

			DescriptiveStatistics statistics =
					new DescriptiveStatistics(results.stream().mapToDouble(i -> i).toArray());

			q25 = statistics.getPercentile(25d);
			q75 = statistics.getPercentile(75d);

			interQuartil = q75 - q25;

			lowerLimit = q25 - 1.5 * interQuartil;
			upperLimit = q75 + 1.5 * interQuartil;
		}

		private List<Entry<A, Long>> filterOutOutliers() {
			return source.stream().filter(entry -> entry.getValue() >= lowerLimit && entry.getValue() <= upperLimit)
					.collect(Collectors.toList());
		}
	}

}
