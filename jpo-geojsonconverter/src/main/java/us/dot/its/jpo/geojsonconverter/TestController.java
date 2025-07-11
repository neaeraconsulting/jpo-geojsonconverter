package us.dot.its.jpo.geojsonconverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrections;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrectionsMessageFrame;
import us.dot.its.jpo.geojsonconverter.converter.rtcm.RTCMDecoder;
import us.dot.its.jpo.geojsonconverter.converter.rtcm.RTCMConverter;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.ProcessedRTCM;

@RestController
public class TestController {



    @PostMapping(value = "/decode", consumes = "text/plain", produces = "application/json")
    String decode(@RequestBody String hex) {
        return RTCMDecoder.decodeRtcm(hex);
    }

    @PostMapping(value = "/rtcm", consumes = "application/json", produces = "application/json")
    ProcessedRTCM rtcm(@RequestBody String rtcm) {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        RTCMcorrectionsMessageFrame messageFrame = null;
        try {
            messageFrame = mapper.readValue(rtcm, RTCMcorrectionsMessageFrame.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return RTCMConverter.processRTCM(messageFrame);
    }

}
