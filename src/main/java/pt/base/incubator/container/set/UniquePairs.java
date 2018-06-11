package pt.base.incubator.container.set;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

public class UniquePairs {

	public List<Integer> process(List<Entry<String, String>> pairs) {

		List<Integer> count = new ArrayList<Integer>(pairs.size());
		Set<Entry<String, String>> visited = new HashSet<Entry<String, String>>();

		IntStream.range(0, pairs.size()).forEach(index -> {
			visited.add(pairs.get(index));
			count.add(visited.size());
		});

		return count;
	}

	public List<Integer> processRecursivly(List<Entry<String, String>> pairs) {
		List<Integer> count = new ArrayList<Integer>(pairs.size());
		return traverse(0, pairs, new HashSet<>(), count);
	}

	private List<Integer> traverse(int index, List<Entry<String, String>> pairs, Set<Entry<String, String>> visited,
			List<Integer> count) {

		if (index == pairs.size()) {
			return count;
		}

		visited.add(pairs.get(index));
		count.add(visited.size());
		index++;

		return traverse(index, pairs, visited, count);
	}
}
