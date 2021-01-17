/* (C)2021 */
package com.censys;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.values.KV;
import org.junit.Test;

public class ReducerTest {

    public static final List<KV<String, String>> testdata =
            List.of(
                    KV.of("English", "Hello world"),
                    KV.of("German", "Guten Tag"),
                    KV.of("French", "Bonjour"),
                    KV.of("Arabic", "السلام عليكم"),
                    KV.of("Hindi", "नमस्ते"),
                    KV.of("Russian", "Здравствуйте"),
                    KV.of("Japanese", "今日は"));

    @Test
    public void testAddInput() {
        Reducer reducer = new Reducer();
        Map<String, String> accum = reducer.createAccumulator();
        for (KV<String, String> greeting : testdata) {
            reducer.addInput(accum, greeting);
        }
        assertEquals(testdata.size(), accum.size());
        for (KV<String, String> greeting : testdata) {
            assertTrue(accum.containsKey(greeting.getKey()));
            assertEquals(accum.get(greeting.getKey()), greeting.getValue());
        }
    }

    @Test
    public void testMerge() {
        Reducer reducer = new Reducer();
        List<Map<String, String>> accums = new ArrayList<>();
        int i = 0;
        for (KV<String, String> greeting : testdata) {
            Map<String, String> accum;
            if (i % 3 == 0) {
                accum = reducer.createAccumulator();
                accums.add(accum);
            } else {
                accum = accums.get(accums.size() - 1);
            }
            reducer.addInput(accum, greeting);
            i++;
        }
        Map<String, String> accum = reducer.mergeAccumulators(accums);
        assertEquals(testdata.size(), accum.size());
        for (KV<String, String> greeting : testdata) {
            assertTrue(accum.containsKey(greeting.getKey()));
            assertEquals(accum.get(greeting.getKey()), greeting.getValue());
        }
    }
}
