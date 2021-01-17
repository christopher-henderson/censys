/* (C)2021 */
package com.censys;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.Test;

public class MainTest {

    String want =
            "\\{\n"
                    + "  \".*src/test/resources/long\": \"cf5b16a778af8380036ce59e7b0492370b249b11e8f07a51afac45037afee9d1\",\n"
                    + "  \".*src/test/resources/medium\": \"248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1\",\n"
                    + "  \".*src/test/resources/short\": \"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad\",\n"
                    + "  \".*src/test/resources/empty\": \"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"\n"
                    + "\\}\n";

    @Test
    public void TestMain() throws Exception {
        String input = new File("src/test/resources").getAbsoluteFile().toString() + "/*";
        File output = File.createTempFile("hashtests", "");
        FileHasher.main(new String[] {"--input=" + input, "--output=" + output.getAbsolutePath()});
        String got = Files.readString(Path.of(output.toPath().toString() + "-00000-of-00001"));
        Pattern pattern = Pattern.compile(want);
        assertTrue(pattern.matcher(got).matches());
    }
}
