package us.dot.its.jpo.geojsonconverter.converter.ssm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.runners.Parameterized.Parameters;
import static org.junit.runners.Parameterized.Parameter;

@Slf4j
@RunWith(Parameterized.class)
public class SsmConverterTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Parameter(0)
    public String ssmJson;

    @Parameter(1)
    public int expectNumberOfRequests;

    @Test
    public void testProcessSsm() throws JsonProcessingException {
        SsmConverter ssmConverter = new SsmConverter();
        SignalStatusMessageMessageFrame messageFrame =
                mapper.readValue(ssmJson, SignalStatusMessageMessageFrame.class);
        List<ProcessedSsm> processedSsm = ssmConverter.processSsm(messageFrame);
        assertThat(processedSsm, notNullValue());
        assertThat(processedSsm.size(), equalTo(expectNumberOfRequests));
    }

    @Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(new Object[][] {
                { SSM, 1}
        });
    }

    public static final String SSM = """
            {
              "messageId": 30,
              "value": {
                "SignalStatusMessage": {
                  "timeStamp": 374789,
                  "second": 31000,
                  "sequenceNumber": 15,
                  "status": [
                    {
                      "sequenceNumber": 30,
                      "id": {
                        "id": 12114
                      },
                      "sigStatus": [
                        {
                          "requester": {
                            "id": {
                              "stationID": 2031825062
                            },
                            "request": 5,
                            "sequenceNumber": 5,
                            "role": "publicTransport"
                          },
                          "inboundOn": {
                            "lane": 1
                          },
                          "outboundOn": {
                            "lane": 16
                          },
                          "duration": 34325,
                          "status": "granted"
                        }
                      ]
                    }
                  ]
                }
              }
            }
            """;
}
