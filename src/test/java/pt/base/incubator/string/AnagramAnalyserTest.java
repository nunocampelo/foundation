package pt.base.incubator.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.util.StringUtils;

public class AnagramAnalyserTest {

	private AnagramAnalyser checker = new AnagramAnalyser();

	@Test
	public void isAnagramShouldReturnFalseIfArgumentsHaveDiffLength() {
		assertFalse(checker.isAnagram("", " "));
		assertFalse(checker.isAnagram("", "1"));
		assertFalse(checker.isAnagram("", "a"));
		assertFalse(checker.isAnagram(" ", "  "));
		assertFalse(checker.isAnagram("a", "aa"));

		assertFalse(checker.isAnagram(" ", ""));
		assertFalse(checker.isAnagram("3", ""));
		assertFalse(checker.isAnagram("b", ""));
		assertFalse(checker.isAnagram("  ", " "));
		assertFalse(checker.isAnagram("cc", "c"));
	}

	@Test
	public void isAnagramShouldReturnTrue() {
		assertTrue(checker.isAnagram("a", "a"));
		assertTrue(checker.isAnagram("ab", "ba"));
		assertTrue(checker.isAnagram("cab", "bac"));
	}

	@Test
	public void isAnagramShouldReturnFalse() {
		assertFalse(checker.isAnagram("ab", "a"));
		assertFalse(checker.isAnagram("ab", "bb"));
		assertFalse(checker.isAnagram("cab", "dac"));
	}

	@Test
	public void greatestCommonAnagramShouldReturnEmpty() {
		assertTrue(StringUtils.isEmpty(checker.greatestCommonAnagram("y", "as")));
		assertTrue(StringUtils.isEmpty(checker.greatestCommonAnagram("aba", "cd")));
	}

	@Test
	public void greatestCommonAnagramShouldReturnCorrectAnagram() {

		assertEquals(checker.greatestCommonAnagram("aa", "aa"), "aa");
		assertEquals(checker.greatestCommonAnagram("aba", "aa"), "aa");
		assertEquals(checker.greatestCommonAnagram("ab", "aa"), "a");

		assertEquals(checker.greatestCommonAnagram("abca", "bar"), "ab");
		assertEquals(checker.greatestCommonAnagram("tab", "aat"), "at");
		assertEquals(checker.greatestCommonAnagram("ay", "as"), "a");
		assertEquals(checker.greatestCommonAnagram("ayew", "aye"), "aey");
		assertEquals(checker.greatestCommonAnagram("abbade", "aabbesd"), "abdeab");
	}
}
