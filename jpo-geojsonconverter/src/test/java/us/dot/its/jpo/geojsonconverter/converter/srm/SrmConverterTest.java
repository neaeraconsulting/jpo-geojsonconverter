package us.dot.its.jpo.geojsonconverter.converter.srm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.SignalRequestMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.ProcessedSignalRequest;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.ProcessedSrm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.SrmProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
        ProcessedSrm processedSrm = srmConverter.processSrm(messageFrame);
        assertThat(processedSrm, notNullValue());
        SrmProperties props = processedSrm.getProperties();
        assertThat(props, hasProperty("requests", notNullValue()));
        List<ProcessedSignalRequest> requests = props.getRequests();
        assertThat(requests, hasSize(expectNumberOfRequests));
    }

    @Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(new Object[][] {
                { SRM, 1 },
                { SRM_2LANES, 2},
                { SRM_4LANES, 4}
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

    public static final String SRM_2LANES = """
            {
              "messageId": 29,
              "value": {
                "SignalRequestMessage": {
                  "timeStamp": 374789,
                  "second": 0,
                  "sequenceNumber": 8,
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
                    },
                    {
                      "request": {
                        "id": {
                          "id": 12114
                        },
                        "requestID": 4,
                        "requestType": "priorityCancellation",
                        "inBoundLane": {
                          "lane": 2
                        },
                        "outBoundLane": {
                          "lane": 15
                        }
                      },
                      "duration": 36145
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
                        "lat": 395533186,
                        "long": -1050850893,
                        "elevation": 16791
                      },
                      "heading": 1312,
                      "speed": {
                        "transmisson": "unavailable",
                        "speed": 418
                      }
                    }
                  }
                }
              }
            }
            """;

    public static final String SRM_4LANES = """
            {
              "messageId": 29,
              "value": {
                "SignalRequestMessage": {
                  "timeStamp": 374532,
                  "second": 0,
                  "sequenceNumber": 9,
                  "requests": [
                    {
                      "request": {
                        "id": {
                          "id": 8802
                        },
                        "requestID": 4,
                        "requestType": "priorityCancellation",
                        "inBoundLane": {
                          "lane": 28
                        },
                        "outBoundLane": {
                          "lane": 26
                        }
                      },
                      "duration": 5624
                    },
                    {
                      "request": {
                        "id": {
                          "id": 8802
                        },
                        "requestID": 3,
                        "requestType": "priorityCancellation",
                        "inBoundLane": {
                          "lane": 1
                        },
                        "outBoundLane": {
                          "lane": 19
                        }
                      },
                      "duration": 6078
                    },
                    {
                      "request": {
                        "id": {
                          "id": 8802
                        },
                        "requestID": 1,
                        "requestType": "priorityCancellation",
                        "inBoundLane": {
                          "lane": 3
                        },
                        "outBoundLane": {
                          "lane": 17
                        }
                      },
                      "duration": 6173
                    },
                    {
                      "request": {
                        "id": {
                          "id": 8802
                        },
                        "requestID": 2,
                        "requestType": "priorityCancellation",
                        "inBoundLane": {
                          "lane": 2
                        },
                        "outBoundLane": {
                          "lane": 18
                        }
                      },
                      "duration": 6101
                    }
                  ],
                  "requestor": {
                    "id": {
                      "stationID": 312977750
                    },
                    "type": {
                      "role": "publicTransport"
                    },
                    "position": {
                      "position": {
                        "lat": 395939481,
                        "long": -1048844050,
                        "elevation": 17525
                      },
                      "heading": 12568,
                      "speed": {
                        "transmisson": "unavailable",
                        "speed": 1417
                      }
                    }
                  }
                }
              }
            }
            """;
}
