/* (C)2021 */
package com.censys;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.security.MessageDigest;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.grpc.v1p26p0.org.bouncycastle.util.encoders.Hex;

public class Hasher extends SimpleFunction<FileIO.ReadableFile, KV<String, String>> {

    // Thread safe queue that I'm using a lot like you would use channels in Go.
    private final LinkedBlockingDeque<byte[]> buffers = new LinkedBlockingDeque<>();

    public Hasher() {
        this(Math.max(Runtime.getRuntime().availableProcessors(), 3));
    }

    public Hasher(int numBuffers) {
        // So between the IO and the message digest we're going to need some
        // intermediate buffers handy. You COULD allocate a new buffer per file,
        // however that incurs an unnecessary number of heap allocations and
        // possibly leads to issues such as head fragmentation (especially if
        // what we are consuming a A LOT of small files).
        //
        // Instead, I found the below documentation that stated that the DirectRunner
        // defaults to the larger of two values (MAX(numCores, 3)) for the number of
        // threads of execution. If we have, say, eight threads then we can keep a
        // pool of eight pre-allocated buffers around.
        //
        // https://beam.apache.org/documentation/runners/direct/
        for (int i = 0; i < numBuffers; i++) {
            // A MB seemed fair for this exercise. However, there are a lot of
            // heuristics you can do for this depending on what exactly you're
            // consuming and from where.
            buffers.push(new byte[1024 * 1024]);
        }
    }

    @Override
    public KV<String, String> apply(FileIO.ReadableFile file) {
        try (InputStream stream = Channels.newInputStream(file.open())) {
            return KV.of(file.getMetadata().resourceId().toString(), this.hash(stream));
        } catch (Exception e) {
            // Just flipping through some of the docs for Beam I can see that there is
            // a notion of transient errors (say, network down) that you can configure
            // your pipeline to notice and thus act upon in a retry strategy (I reckon
            // the retry algorithm (E.G. exponential backoff and crew) are configurable
            // as well).
            //
            // I'll say that failing locally is not so likely to be transient, but
            // given only about the four hours I just did not have the time to devise
            // a reasonable strategy that wouldn't most likely just infinitely try
            // files that don't even exist. So, I guess just skip any files that fail
            // and let the pipeline filter out nulls...*sigh*
            //
            // Also, I would rather use Optional<KV<String, String> here as opposed to a null
            // (because null is the Devil) however in order to make the pipeline Optional aware
            // I would have to implement and register a "Coder" for the Optional type which, again,
            // I do not have time for.
            //
            // I gotta say, this API isn't the most satisfying take on streams and functional
            // pipelines that I have ever. But it looks like it's handling a pipeline that
            // is distributed over a network, so I guess you take what you can get because
            // that cannot be an easy problem to solve.
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String hash(InputStream stream) throws IOException {
        MessageDigest digest = this.getDigest();
        byte[] buf = this.getBuffer();
        try {
            for (int read = stream.read(buf); read != -1; read = stream.read(buf)) {
                digest.update(buf, 0, read);
            }
        } finally {
            this.returnBuffer(buf);
        }
        return Hex.toHexString(digest.digest());
    }

    private MessageDigest getDigest() {
        // This can technically fail, although I would say
        // that SHA256 not being available to us surely counts
        // as a fatal runtime exception.
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getBuffer() {
        try {
            return buffers.takeFirst();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void returnBuffer(byte[] buf) {
        try {
            buffers.putLast(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
