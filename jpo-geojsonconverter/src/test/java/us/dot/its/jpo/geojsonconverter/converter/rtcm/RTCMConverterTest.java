package us.dot.its.jpo.geojsonconverter.converter.rtcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrections;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrectionsMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.ProcessedRTCM;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class RTCMConverterTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testProcessRtcm() throws JsonProcessingException {
        RTCMDecoder decoder = new RTCMDecoder(false);
        RTCMConverter converter = new RTCMConverter(decoder);
        RTCMcorrectionsMessageFrame messageFrame = mapper.readValue(RTCM, RTCMcorrectionsMessageFrame.class);
        ProcessedRTCM processedRtcm = converter.processRTCM(messageFrame);
        assertThat(processedRtcm, notNullValue());
        assertThat(processedRtcm.getMsgCnt(), equalTo(82));
        assertThat(processedRtcm.getRev(), equalTo("rtcmRev3"));
        log.info(mapper.writeValueAsString(processedRtcm));
    }

    public static final String RTCM = """
        {
            "messageId": 28,
            "value": {
                "RTCMcorrections": {
                    "msgCnt": 82,
                    "rev": "rtcmRev3",
                    "anchorPoint": {
                        "long": -1115094349,
                        "lat": 406603360,
                        "elevation": 20630
                    },
                    "msgs": [
                        "D300133ED980037BDD1A80C6358121DD4E499FFC6712E91F0D",
                        "D3003E409980144144564E554C4C414E54454E4E4120204E4F4E4500000D5452494D424C4520414C4C4F59144E617620352E3434202F20426F6F7420352E3434008C65F9"
                    ]
                }
            }
        }
        """;



}
