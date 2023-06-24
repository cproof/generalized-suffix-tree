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

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An interface for interacting with a suffix tree.
 * This is designed to allow nested suffix trees and other combinations to expose a unified interface.
 *
 * @param <T>  the type of values that can be stored in the {@link ISuffixTree}
 */
public interface ISuffixTree<T> {
	/**
	 * Put a new value into the {@code ISuffixTree}.
	 *
	 * @param key    the search key to use for the given value
	 * @param value  the value to add
	 */
	void put(String key, T value);

	/**
	 * Gets the search results for the given token and gives them to the results consumer.
	 * The results consumer may be called multiple times with different sections of data as
	 * the {@code ISuffixTree} is traversed.
	 *
	 * @param token            the search token to search for.
	 * @param resultsConsumer  the consumer that can accept results.
	 */
	void getSearchResults(String token, Consumer<Collection<T>> resultsConsumer);

	/**
	 * Gets the search results for the given token.
	 *
	 * @param token  the search token to search for.
	 * @return an identity set of all the search results.
	 */
	default Set<T> getSearchResults(String token) {
		Set<T> results = Collections.newSetFromMap(new IdentityHashMap<>());
		getSearchResults(token, results::addAll);
		return results;
	}

	/**
	 * Get all the data in the {@code ISuffixTree} and give it to the consumer.
	 * The results consumer may be called multiple times with different sections of data as
	 * the {@code ISuffixTree} is traversed.
	 *
	 * @param resultsConsumer  the consumer that can accept results.
	 */
	void getAllElements(Consumer<Collection<T>> resultsConsumer);

	/**
	 * Get all the data in the {@code ISuffixTree}.
	 * @return an identity set of all the search results.
	 */
	default Set<T> getAllElements() {
		Set<T> results = Collections.newSetFromMap(new IdentityHashMap<>());
		getAllElements(results::addAll);
		return results;
	}

	/**
	 * @return debug statistics for the {@code ISuffixTree}.
	 */
	String statistics();
}
