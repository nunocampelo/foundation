package pt.base.incubator.prism.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

@Service
public class DataProcessor {

	public <A extends Comparable<? super A>, R> void sortByKey(List<Entry<A, R>> data) {
		data.sort(Comparator.comparing(Entry::getKey));
	}

	// public <A> List<Entry<A, Long>> averageSameKeyResults(List<Entry<A, Long>> source) {
	//
	// final List<Entry<A, Long>> results = new LinkedList<>();
	//
	// Map<A, List<Entry<A, Long>>> groupedMap =
	// source.stream().collect(Collectors.groupingBy(Entry::getKey));
	// groupedMap.forEach((a, l) -> results
	// .add(new SimpleEntry<A, Long>(a,
	// l.stream().map(Entry::getValue).mapToLong(Long::intValue).sum())));
	//
	// return results;
	// }

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

}
