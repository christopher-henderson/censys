/* (C)2021 */
package com.censys;

import java.util.HashMap;
import java.util.Map;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.values.KV;

public class Reducer
        extends Combine.CombineFn<KV<String, String>, Map<String, String>, Map<String, String>> {

    @Override
    public Map<String, String> createAccumulator() {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> addInput(
            Map<String, String> mutableAccumulator, KV<String, String> input) {
        mutableAccumulator.put(input.getKey(), input.getValue());
        return mutableAccumulator;
    }

    @Override
    public Map<String, String> mergeAccumulators(Iterable<Map<String, String>> accumulators) {
        Map<String, String> merged = new HashMap<>();
        accumulators.forEach(merged::putAll);
        return merged;
    }

    @Override
    public Map<String, String> extractOutput(Map<String, String> accumulator) {
        return accumulator;
    }
}
