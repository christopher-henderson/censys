/* (C)2021 */
package com.censys;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Test;

public class HashTest {

    @Test
    public void testSHA256() throws IOException {
        // Shoutout to https://www.di-mgt.com.au/sha_testvectors.html for hunting
        // down these NIST test vectors.
        String[][] testdata =
                new String[][] {
                    {"abc", "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"},
                    {"", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"},
                    {
                        "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq",
                        "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1"
                    },
                    {
                        "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu",
                        "cf5b16a778af8380036ce59e7b0492370b249b11e8f07a51afac45037afee9d1"
                    },
                    // HA! The helpful folks in the link above gave a test on 1GB worth of data.
                    // This might be worth
                    // moving to its own test rather than being just another in this test table.
                    //
                    // Still, good to have just to know that we haven't surrounded SHA256 with
                    // something so bad that it
                    // blows up the JVM
                    {
                        "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmno"
                                .repeat(16777216),
                        "50e72a0e26442fe2552dc3938ac58658228c0cbfb1d2ca872ae435266fcd055e"
                    }
                };
        for (String[] test : testdata) {
            String want = test[1];
            String got = new Hasher(1).hash(new ByteArrayInputStream(test[0].getBytes()));
            assertEquals(want, got);
        }
    }
}
