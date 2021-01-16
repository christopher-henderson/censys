/* (C)2021 */
package com.censys;

import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.channels.Channels;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.grpc.v1p26p0.org.bouncycastle.util.encoders.Hex;

public class FileHasher {

    public interface HasherOptions extends PipelineOptions {

        /**
         * By default, this example reads from a public dataset containing the text of King Lear.
         * Set this option to choose a different input file or glob.
         */
        @Description("Path of the file to read from")
        @Default.String("/home/chris/projects/censys/pom.xml")
        String getInputFile();

        void setInputFile(String value);

        /** Set this required option to specify where to write the output. */
        @Description("Path of the file to write to")
        @Default.String("/home/chris/projects/censys/pom.xml.sha256")
        String getOutput();

        void setOutput(String value);
    }

    public static void main(String[] args) throws Exception {
        HasherOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(HasherOptions.class);
        Pipeline pipeline = Pipeline.create(options);
        //        https://beam.apache.org/documentation/runners/direct/
        LinkedBlockingDeque<byte[]> chan = new LinkedBlockingDeque();
        for (int i = 0; i < 8; i++) {
            chan.push(new byte[1024 * 1024]);
        }
        pipeline.apply(FileIO.match().filepattern("/home/chris/projects/censys/testdata/*"))
                .apply(FileIO.readMatches())
                .apply(
                        MapElements.via(
                                new SimpleFunction<FileIO.ReadableFile, KV<String, String>>() {
                                    @Override
                                    public KV<String, String> apply(FileIO.ReadableFile file) {
                                        byte[] buf = null;
                                        try {
                                            buf = chan.takeFirst();
                                            //                            long mb = 1024 * 1024;
                                            //                            long fsize =
                                            // file.getMetadata().sizeBytes();
                                            //                            byte[] buf = new
                                            // byte[(int) (Math.min(fsize, mb))];
                                            MessageDigest digest =
                                                    MessageDigest.getInstance("SHA-256");
                                            InputStream i = Channels.newInputStream(file.open());
                                            for (int read = i.read(buf);
                                                    read != -1;
                                                    read = i.read(buf)) {
                                                digest.update(buf, 0, read);
                                            }
                                            return KV.of(
                                                    file.getMetadata().resourceId().getFilename(),
                                                    Hex.toHexString(digest.digest()));
                                        } catch (Exception e) {
                                            return null;
                                        } finally {
                                            if (buf != null) {
                                                try {
                                                    chan.putLast(buf);
                                                } catch (Exception e) {
                                                    //
                                                }
                                            }
                                        }
                                    }
                                }))
                .apply(Filter.by(Objects::nonNull))
                .apply(
                        Combine.globally(
                                new Combine.CombineFn<
                                        KV<String, String>,
                                        Map<String, String>,
                                        Map<String, String>>() {
                                    @Override
                                    public Map<String, String> createAccumulator() {
                                        return new HashMap<>();
                                    }

                                    @Override
                                    public Map<String, String> addInput(
                                            Map<String, String> mutableAccumulator,
                                            KV<String, String> input) {
                                        mutableAccumulator.put(input.getKey(), input.getValue());
                                        return mutableAccumulator;
                                    }

                                    @Override
                                    public Map<String, String> mergeAccumulators(
                                            Iterable<Map<String, String>> accumulators) {
                                        Map<String, String> merged = new HashMap<>();
                                        accumulators.forEach(merged::putAll);
                                        return merged;
                                    }

                                    @Override
                                    public Map<String, String> extractOutput(
                                            Map<String, String> accumulator) {
                                        return accumulator;
                                    }
                                }))
                .apply(
                        MapElements.via(
                                new SimpleFunction<Map<String, String>, String>() {
                                    @Override
                                    public String apply(Map<String, String> input) {
                                        return new GsonBuilder()
                                                .setPrettyPrinting()
                                                .create()
                                                .toJson(input);
                                    }
                                }))
                .apply(TextIO.write().to(options.getOutput()));
        pipeline.run().waitUntilFinish();
    }
}
