package pt.base.foundation.string;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.base.foundation.string.PalindromeChecker;

public class PalindromeCheckerTest {

	private PalindromeChecker tester = new PalindromeChecker();

	@Test
	public void isPalindromeOfSingleLenghtWorkShouldReturnTrue() {
		assertTrue(tester.isPalindrome("o"));
		assertTrue(tester.isPalindrome("#"));
		assertTrue(tester.isPalindrome("@"));
		assertTrue(tester.isPalindrome("1"));
	}

	@Test
	public void isPalindromeOfTwoLenghtWorkShouldReturnTrue() {
		assertTrue(tester.isPalindrome("oo"));
		assertTrue(tester.isPalindrome("##"));
		assertTrue(tester.isPalindrome("@@"));
		assertTrue(tester.isPalindrome("11"));
	}

	@Test
	public void isPalindromeOfTwoLenghtWorkShouldReturnFalse() {
		assertFalse(tester.isPalindrome("o0"));
		assertFalse(tester.isPalindrome("#1"));
		assertFalse(tester.isPalindrome("@/"));
		assertFalse(tester.isPalindrome("12"));
	}

	@Test
	public void isPalindromeOfMultipleLenghtWorkShouldReturnTrue() {

		assertTrue(tester.isPalindrome("ooo"));
		assertTrue(tester.isPalindrome("###"));
		assertTrue(tester.isPalindrome("@@@"));
		assertTrue(tester.isPalindrome("111"));

		assertTrue(tester.isPalindrome("o0o"));
		assertTrue(tester.isPalindrome("#$#"));
		assertTrue(tester.isPalindrome("@1@"));
		assertTrue(tester.isPalindrome("121"));

		assertTrue(tester.isPalindrome("# $ #"));
		assertTrue(tester.isPalindrome("@111@"));
		assertTrue(tester.isPalindrome("1221"));
	}

	@Test
	public void isPalindromeOfMultipleLenghtWorkShouldReturnFalse() {
		assertFalse(tester.isPalindrome("o00"));
		assertFalse(tester.isPalindrome("#11"));
		assertFalse(tester.isPalindrome("@//"));
		assertFalse(tester.isPalindrome("122"));

		assertFalse(tester.isPalindrome("o0 "));
		assertFalse(tester.isPalindrome("#1 10"));
		assertFalse(tester.isPalindrome("@/ 1/@"));
		assertFalse(tester.isPalindrome(" 122"));
	}

}
