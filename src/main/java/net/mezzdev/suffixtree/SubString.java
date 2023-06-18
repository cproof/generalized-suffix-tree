/*
 * Copyright 2012 Alessandro Bahgat Shehata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.mezzdev.suffixtree;

/**
 * Reduces memory usage by avoiding creating substrings from slicing.
 * Instead, one string is used and multiple SubString classes point to it.
 */
public class SubString implements CharSequence {
	private final String string;
	private final int offset;
	private final int length;

	public SubString(String string) {
		this(string, 0, string.length());
	}

	public SubString(String string, int offset) {
		this(string, offset, string.length() - offset);
	}

	public SubString(String string, int offset, int length) {
		if (length < 0) {
			throw new IllegalArgumentException("length (" + length + ") must be greater than or equal to 0 ");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("offset (" + offset + ") must be greater than or equal to 0 ");
		}
		if (offset + length > string.length()) {
			throw new IllegalArgumentException(
					"offset (" + offset + ") plus length (" + length +
					") must be less than or equal to the string's length (" + string.length() + ")"
			);
		}

		this.string = string;
		this.offset = offset;
		this.length = length;
	}

	public SubString subSequence(int start) {
		return subSequence(start, length);
	}

	@Override
	public SubString subSequence(int start, int end) {
		if (start < 0 || start > end || end > length) {
			throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", length " + length);
		}
		if (start == 0 && end == length) {
			return this;
		}
		return new SubString(string, offset + start, end - start);
	}

	@Override
	public boolean isEmpty() {
		return this.length == 0;
	}

	@Override
	public char charAt(int index) {
		return string.charAt(offset + index);
	}

	@Override
	public int length() {
		return length;
	}

	public SubString shorten(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount (" + amount + ") must be greater than or equal to 0 ");
		}
		if (length == 0 || amount == 0) {
			return this;
		}
		int newLength = Math.max(length - amount, 0);
		return new SubString(string, offset, newLength);
	}

	/**
	 * Extending a substring is the opposite of shortening,
	 * it will only work if the character matches the next character in the underlying string.
	 */
	public SubString extend(char newChar) {
		if (offset + length >= string.length()) {
			throw new IndexOutOfBoundsException("cannot extend the string past its maximum length " + length);
		}

		char expectedChar = charAt(length);
		if (expectedChar != newChar) {
			throw new IllegalArgumentException(
					"extend must be called with the next char. expected '" + expectedChar +
					"' but was given '" + newChar + "' instead."
			);
		}

		return new SubString(string, this.offset, this.length + 1);
	}

	public boolean startsWith(SubString prefix) {
		return startsWith(prefix, prefix.length());
	}

	@SuppressWarnings("StringEquality")
	public boolean startsWith(SubString prefix, int lenToMatch) {
		if (lenToMatch > length) {
			return false;
		}
		if (string == prefix.string && offset == prefix.offset) {
			return true;
		}
		return string.regionMatches(offset, prefix.string, prefix.offset, lenToMatch);
	}

	@Override
	public String toString() {
		return string.substring(offset, offset + length);
	}

	public String debugString() {
		return this.getClass().getSimpleName() + ": \"" + this + "\"\nBacking string: \"" + string + "\"";
	}
}
