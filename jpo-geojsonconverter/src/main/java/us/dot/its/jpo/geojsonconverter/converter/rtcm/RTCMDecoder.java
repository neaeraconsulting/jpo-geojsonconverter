package us.dot.its.jpo.geojsonconverter.converter.rtcm;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * Call the native gpsdecode command line tool to decode RTCMs.
 * <p>Requires gpsd-client to be installed on Linux.  Will not work on Windows.</p>
 */
@Slf4j
public class RTCMDecoder {

    private static final HexFormat hexFormat = HexFormat.of();

    private static final String EXECUTABLE = "/usr/bin/gpsdecode";

    public static String decodeRtcm(String hex)  {
        File executable = new File(EXECUTABLE);
        if (!executable.exists()) {
            log.warn("Executable {} does not exist.",
                    EXECUTABLE);
            return "{}";
        }
        byte[] bytes = hexFormat.parseHex(hex);
        var pb = new ProcessBuilder(EXECUTABLE);

        String json = null;
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var is = process.getInputStream()) {
            try (var out = process.getOutputStream()) {
                out.write(bytes);
                out.flush();
            }
            byte[] outBytes = is.readAllBytes();
            json = new String(outBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            int exitCode = process.waitFor();
            log.info("RTCM process exited with code {}", exitCode);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

}
