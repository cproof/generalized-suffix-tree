/**
 * Copyright 2012 Alessandro Bahgat Shehata
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.mezzdev.suffixtree;

import java.util.Set;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {
    
    public UtilsTest(String testName) {
        super(testName);
    }

    public void testGetSubstrings() {
        String in = "banana";
        Set<String> out = Utils.getSubstrings(in);
        String[] outArr = new String[] { "b" , "a", "n", "ba", "an", "na", "ban", "ana", "nan", "bana", "anan", "nana", "banan", "anana", "banana"};

        for (String s : outArr) {
            assertTrue(out.remove(s));
        }

        assertTrue(out.isEmpty());
    }

}
