package us.dot.its.jpo.geojsonconverter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.dot.its.jpo.geojsonconverter.converter.rtcm.RTCMDecoder;

@RestController
public class TestController {

    @GetMapping("/rtcm")
    String rtcm() {
        return """
                Hello rtcm
                """;
    }

    @PostMapping(value = "/decode", consumes = "text/plain", produces = "application/json")
    String decode(@RequestBody String hex) {
        return RTCMDecoder.decodeRtcm(hex);
    }

}
