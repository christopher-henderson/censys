/* (C)2021 */
package com.censys;

import java.util.Objects;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;

public class FileHasher {

    public interface HasherOptions extends PipelineOptions {

        /**
         * By default, this example reads from a public dataset containing the text of King Lear.
         * Set this option to choose a different input file or glob.
         */
        @Description("Path of the file to read from")
        @Default.String("testdata/*")
        String getInputFile();

        void setInputFile(String value);

        /** Set this required option to specify where to write the output. */
        @Description("Path of the file to write to")
        @Default.String("hashes.json")
        String getOutput();

        void setOutput(String value);
    }

    public static void main(String[] args) throws Exception {
        HasherOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(HasherOptions.class);
        Pipeline pipeline = Pipeline.create(options);
        pipeline.apply(FileIO.match().filepattern(options.getInputFile()))
                .apply(FileIO.readMatches())
                .apply(MapElements.via(new Hasher()))
                .apply(Filter.by(Objects::nonNull))
                .apply(Combine.globally(new Reducer()))
                .apply(MapElements.via(new Serializer()))
                .apply(TextIO.write().to(options.getOutput()));
        pipeline.run().waitUntilFinish();
    }
}
