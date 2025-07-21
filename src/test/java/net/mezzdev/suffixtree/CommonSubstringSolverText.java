package net.mezzdev.suffixtree;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommonSubstringSolverText {
    @Test
    public void testFindCommonSubstring() {
        String strA = "apple tree window";
        String strB = "trees app are cool";
        String strC = "widows eat apples";
        List<String> testStrings = List.of(strA, strB, strC);

        GeneralizedSuffixTree<Integer> tree = new GeneralizedSuffixTree<>();
        for (int i = 0; i < testStrings.size(); i++) {
            tree.put(testStrings.get(i), i);
        }

        List<Pair<CharSequence, List<Integer>>> allCommonSubstringsOfSizeInMinKeys = tree.findAllCommonSubstringsOfSizeInMinKeys(4, 2);
        assertEquals(6, allCommonSubstringsOfSizeInMinKeys.size());
    }
}
