package net.mezzdev.suffixtree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommonSubstringSolverTest {
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

        List<Pair<CharSequence, Collection<Integer>>> allCommonSubstringsOfSizeInMinKeys = new ArrayList<>();
        tree.findAllCommonSubstringsOfSizeInMinKeys(4, 2, false, (substring, indexes) ->
                allCommonSubstringsOfSizeInMinKeys.add(new Pair<>(substring, indexes)));
        assertEquals(4, allCommonSubstringsOfSizeInMinKeys.size());
    }

    @Test
    public void testFindCommonSubstringShallow() {
        String strA = "1234567890987654321";
        String strB = "01234567890";
        String strC = "01234587654321";
        List<String> testStrings = List.of(strA, strB, strC);

        GeneralizedSuffixTree<Integer> tree = new GeneralizedSuffixTree<>();
        for (int i = 0; i < testStrings.size(); i++) {
            tree.put(testStrings.get(i), i);
        }


        GeneralizedSuffixTree<Integer> lookupTree = new GeneralizedSuffixTree<>();

        List<Pair<CharSequence, Collection<Integer>>> allCommonSubstringsOfSizeInMinKeys = new ArrayList<>();
        tree.findAllCommonSubstringsOfSizeInMinKeys(4, 2, true, (substring, indexes) -> {
            if (true || lookupTree.getSearchResults(substring.toString()).isEmpty()) {
                allCommonSubstringsOfSizeInMinKeys.add(new Pair<>(substring, indexes));
                lookupTree.put(substring.toString(), -1);
            }
                });
        allCommonSubstringsOfSizeInMinKeys.stream().map(Pair::first).forEach(System.out::println);
        assertEquals(4, allCommonSubstringsOfSizeInMinKeys.size());
    }
}
