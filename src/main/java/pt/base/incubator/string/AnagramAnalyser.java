package pt.base.incubator.string;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.stereotype.Service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.math.IntMath;

@Service
public class AnagramAnalyser {

	private static BiMap<Integer, Integer> characterToPrime = HashBiMap.create();

	static {
		characterToPrime.put((int) 'a', 2);
		characterToPrime.put((int) 'b', 3);
		characterToPrime.put((int) 'c', 5);
		characterToPrime.put((int) 'd', 7);
		characterToPrime.put((int) 'e', 11);
		characterToPrime.put((int) 'f', 13);
		characterToPrime.put((int) 'g', 17);
		characterToPrime.put((int) 'h', 19);
		characterToPrime.put((int) 'i', 23);
		characterToPrime.put((int) 'j', 29);
		characterToPrime.put((int) 'k', 31);
		characterToPrime.put((int) 'l', 37);
		characterToPrime.put((int) 'm', 41);
		characterToPrime.put((int) 'n', 43);
		characterToPrime.put((int) 'o', 47);
		characterToPrime.put((int) 'p', 53);
		characterToPrime.put((int) 'q', 59);
		characterToPrime.put((int) 'r', 61);
		characterToPrime.put((int) 's', 67);
		characterToPrime.put((int) 't', 71);
		characterToPrime.put((int) 'u', 73);
		characterToPrime.put((int) 'v', 79);
		characterToPrime.put((int) 'x', 83);
		characterToPrime.put((int) 'y', 89);
		characterToPrime.put((int) 'w', 97);
		characterToPrime.put((int) 'z', 101);
	}

	public boolean isAnagram(String a, String b) {

		if (a.length() != b.length()) {
			return false;
		}

		if (a.length() == 0) {
			return true;
		}

		return computeAnagramHash(a) == computeAnagramHash(b);
	}

	public String greatestCommonAnagram(String a, String b) {

		int aAnagramHash = computeAnagramHash(a);
		int bAnagramHash = computeAnagramHash(b);

		if (aAnagramHash == bAnagramHash) {
			return a;
		}

		int gcd = IntMath.gcd(aAnagramHash, bAnagramHash);
		return computeAnagramFromHash(gcd);
	}

	private int computeAnagramHash(String a) {
		return a.chars().reduce(1, (acc, curr) -> {
			return acc *= characterToPrime.get(curr);
		});
	}

	private String computeAnagramFromHash(int anagramHash) {

		Collection<Integer> primes = characterToPrime.values();
		String result = "";
		int remainder = anagramHash;
		int prime;

		while (remainder != 1) {
			for (Iterator<Integer> iterator = primes.iterator(); iterator.hasNext();) {
				prime = (int) iterator.next();

				if (remainder % prime == 0) {
					result += Character.toString((char) characterToPrime.inverse().get(prime).intValue());
					remainder /= prime;
				}
			}
		}

		return result;

	}
}
