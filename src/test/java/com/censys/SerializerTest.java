/* (C)2021 */
package com.censys;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.apache.beam.sdk.values.KV;
import org.junit.Test;

public class SerializerTest {

    public static String want =
            "{\n"
                    + "  \"English\": \"Hello world\",\n"
                    + "  \"French\": \"Bonjour\",\n"
                    + "  \"Arabic\": \"السلام عليكم\",\n"
                    + "  \"Russian\": \"Здравствуйте\",\n"
                    + "  \"German\": \"Guten Tag\",\n"
                    + "  \"Hindi\": \"नमस्ते\",\n"
                    + "  \"Japanese\": \"今日は\"\n"
                    + "}";

    @Test
    public void testSerializer() {
        Map<String, String> accum = new HashMap<>();
        for (KV<String, String> greeting : ReducerTest.testdata) {
            accum.put(greeting.getKey(), greeting.getValue());
        }
        String got = new Serializer().apply(accum);
        assertEquals(got, want);
    }

    @Test
    public void testEmpty() {
        Map<String, String> accum = new HashMap<>();
        String got = new Serializer().apply(accum);
        String want = "{}";
        assertEquals(got, want);
    }
}
