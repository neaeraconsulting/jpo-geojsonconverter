package us.dot.its.jpo.geojsonconverter.pojos.ssm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * A processed J2735 SignalStatusMessageFrame.
 * <p>Similar to a SPAT, the SSM is associated with an intersection, but does not contain geographic information
 * itself.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessedSsm {
    private int schemaVersion = -1;
    private final String messageType = "SSM";

    // Metadata from the ODE header
    private ZonedDateTime odeReceivedAt;
    private String originIp;
    private String asn1;

    // Data from the MessageFrame payload
    private Integer region;
    private Integer intersectionId;
    private ZonedDateTime utcTimeStamp;
    private Integer sequenceNumber;
    private Integer statusSequenceNumber;
    private String requesterId;
    private Integer requestId;
    private Integer requesterSequenceNumber;
    private String requesterRole;
    private Integer inboundLaneId;
    private String status;
}
