package us.dot.its.jpo.geojsonconverter.pojos.ssm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import us.dot.its.jpo.geojsonconverter.pojos.common.*;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * A processed J2735 SignalStatusMessage.
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

    // ------------------------------------------------------------------------
    // Metadata from the ODE header
    private ZonedDateTime odeReceivedAt;
    private String originIp;
    private String asn1;

    // ------------------------------------------------------------------------
    // Data from the MessageFrame payload

    /**
     * Top-level timestamp of MSG_SignalStatusMessage
      */
    private ZonedDateTime timeStamp;

    /**
     * Top-level sequence number (DE_MsgCount) of MSG_SignalStatusMessage
     */
    private Integer sequenceNumber;

    /**
     * Sequence number (DE_MsgCount) of the DF_SignalStatus frame
     */
    private Integer statusSequenceNumber;

    /**
     * DE_RoadRequlatorID
     */
    private Integer region;

    /**
     * DE_IntersectionID
     */
    private Integer intersectionId;

    // Fields from DF_SignalRequesterInfo
    private String vehicleID;
    private Integer requestID;
    private Integer requesterSequenceNumber;
    private ProcessedBasicVehicleRole requesterRole;
    private ProcessedRequestSubRole requesterSubrole;
    private ProcessedRequestImportanceLevel requestImportanceLevel;
    private Integer requesterIso3833VehicleType;
    private ProcessedVehicleType requesterHpmsType;

    // Fields from inboundOn IntersectionAccessPoint
    private Integer inboundOnLaneID;
    private Integer inboundOnApproachID;
    private Integer inboundOnLaneConnectionID;

    // Fields from outboundOn IntersectionAccessPoint
    private Integer outboundOnLaneID;
    private Integer outboundOnApproachID;
    private Integer outboundOnLaneConnectionID;

    /**
     * ETA from the SignalStatusPackage MinuteOfYear and second/DSecond fields
     */
    private ZonedDateTime estimatedTimeOfArrival;

    /**
     * From DF_SignalStatusPackage.duration
     */
    private Duration estimatedTimeOfArrivalDuration;

    private ProcessedPrioritizationResponseStatus status;
}
