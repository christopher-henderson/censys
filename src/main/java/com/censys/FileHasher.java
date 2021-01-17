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

        @Description(
                "File pattern to read from. This is fed straight to FileIO.match.filepattern so it must "
                        + "be a shell regular expression of all of the files you want to hash (E.G. /tmp/data/*)")
        @Default.String("testdata/*")
        String getInput();

        void setInput(String value);

        @Description("Path of the file to write to")
        @Default.String("hashes.json")
        String getOutput();

        void setOutput(String value);
    }

    public static void main(String[] args) {
        HasherOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation().as(HasherOptions.class);
        Pipeline pipeline = Pipeline.create(options);
        pipeline.apply(FileIO.match().filepattern(options.getInput()))
                .apply(FileIO.readMatches())
                .apply(MapElements.via(new Hasher()))
                .apply(Filter.by(Objects::nonNull))
                .apply(Combine.globally(new Reducer()))
                .apply(MapElements.via(new Serializer()))
                .apply(TextIO.write().to(options.getOutput()));
        pipeline.run().waitUntilFinish();
    }
}
