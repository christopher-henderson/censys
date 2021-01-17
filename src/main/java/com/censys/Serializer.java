/* (C)2021 */
package com.censys;

import com.google.gson.GsonBuilder;
import java.util.Map;
import org.apache.beam.sdk.transforms.SimpleFunction;

public class Serializer extends SimpleFunction<Map<String, String>, String> {
    @Override
    public String apply(Map<String, String> input) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(input);
    }
}
