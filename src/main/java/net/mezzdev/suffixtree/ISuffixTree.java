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

public interface ISuffixTree<T> {
	void put(String key, T value);

	void getSearchResults(String token, Consumer<Collection<T>> resultsConsumer);

	default Set<T> getSearchResults(String token) {
		Set<T> results = Collections.newSetFromMap(new IdentityHashMap<>());
		getSearchResults(token, results::addAll);
		return results;
	}

	void getAllElements(Consumer<Collection<T>> resultsConsumer);

	default Set<T> getAllElements() {
		Set<T> results = Collections.newSetFromMap(new IdentityHashMap<>());
		getAllElements(results::addAll);
		return results;
	}

	String statistics();
}
