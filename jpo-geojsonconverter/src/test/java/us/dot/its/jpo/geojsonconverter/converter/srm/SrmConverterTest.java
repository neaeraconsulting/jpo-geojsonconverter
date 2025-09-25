package us.dot.its.jpo.geojsonconverter.converter.srm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.SignalRequestMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.ProcessedSrm;

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
public class SrmConverterTest {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Parameter(0)
    public String srmJson;

    @Parameter(1)
    public int expectNumberOfRequests;

    @Test
    public void testProcessSrm() throws JsonProcessingException {
        SrmConverter srmConverter = new SrmConverter();
        SignalRequestMessageMessageFrame messageFrame =
                mapper.readValue(srmJson, SignalRequestMessageMessageFrame.class);
        List<ProcessedSrm> processedSrm = srmConverter.processSrm(messageFrame);
        assertThat(processedSrm, notNullValue());
        assertThat(processedSrm.size(), equalTo(expectNumberOfRequests));
    }

    @Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(new Object[][] {
                { SRM, 1 }
        });
    }

    public static final String SRM = """
            {
              "messageId": 29,
              "value": {
                "SignalRequestMessage": {
                  "timeStamp": 374789,
                  "second": 0,
                  "sequenceNumber": 11,
                  "requests": [
                    {
                      "request": {
                        "id": {
                          "id": 12114
                        },
                        "requestID": 5,
                        "requestType": "priorityRequest",
                        "inBoundLane": {
                          "lane": 1
                        },
                        "outBoundLane": {
                          "lane": 16
                        }
                      },
                      "duration": 34325
                    }
                  ],
                  "requestor": {
                    "id": {
                      "stationID": 2031825062
                    },
                    "type": {
                      "role": "publicTransport"
                    },
                    "position": {
                      "position": {
                        "lat": 395534788,
                        "long": -1050850214,
                        "elevation": 16788
                      },
                      "heading": 1496,
                      "speed": {
                        "transmisson": "unavailable",
                        "speed": 512
                      }
                    }
                  }
                }
              }
            }
            """;
}
