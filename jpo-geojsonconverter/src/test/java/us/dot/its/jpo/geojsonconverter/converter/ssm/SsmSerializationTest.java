package us.dot.its.jpo.geojsonconverter.converter.ssm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;
import us.dot.its.jpo.geojsonconverter.utils.ProcessedSchemaVersions;
import static us.dot.its.jpo.geojsonconverter.TestResourceUtil.loadResource;

@Slf4j
public class SsmSerializationTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testProcessSsmSerialization() throws IOException {
        final String ssmJson = loadResource("classpath:json/ssm.message-frame.json");
        final String referenceProcessedSsmJson = loadResource("classpath:json/sample.processed-ssm.json");
        SsmConverter ssmConverter = new SsmConverter();
        SignalStatusMessageMessageFrame messageFrame = mapper.readValue(ssmJson, SignalStatusMessageMessageFrame.class);
        ProcessedSsm processedSsm = ssmConverter.processSsm(messageFrame);


        System.out.println("\n\n\nSerializing SSM Json" + ssmJson);
        System.out.println("\n\n\nSerializing Processed SSM" + processedSsm.toString());


        processedSsm.setOdeReceivedAt(ZonedDateTime.parse("2025-09-18T06:29:28Z"));
        processedSsm.setAsn1("001E1865B80579181E00F0BD480C95E46CC2981428200408430AA0");
        processedSsm.setSchemaVersion(ProcessedSchemaVersions.PROCESSED_SSM_SCHEMA_VERSION);
        processedSsm.setOriginIp("172.18.0.1");

        // Forcibly remove newlines and spaces to make sure IDE json formatting doesn't mess up test performance
        String compareString = processedSsm.toString().replaceAll("\n", "").replaceAll(" ", "");
        String compareReference = referenceProcessedSsmJson.replaceAll("\n", "").replaceAll(" ", "");
        assertEquals(compareReference, compareString);
    }
}
