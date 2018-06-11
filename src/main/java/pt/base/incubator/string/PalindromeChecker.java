package pt.base.incubator.string;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PalindromeChecker {

	public boolean isPalindrome(String str) {

		if (StringUtils.isEmpty(str)) {
			return false;
		}

		if (str.charAt(0) != str.charAt(str.length() - 1)) {
			return false;
		}

		if (str.length() <= 2) {
			return true;
		}

		return isPalindrome(str.substring(1, str.length() - 1));
	}

}
