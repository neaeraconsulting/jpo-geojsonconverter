package us.dot.its.jpo.geojsonconverter.pojos.geojson.srm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.geojsonconverter.pojos.common.*;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class SrmProperties {

    // Metadata properties
    private int schemaVersion = -1;
    private final String messageType = "SRM";
    private ZonedDateTime odeReceivedAt;
    private String originIp;
    private String asn1;

    // Payload properties

    // Top-level timestamp of the SignalRequestMessage
    private ZonedDateTime timeStamp;
    private Integer sequenceNumber;

    // Signal Request Package properties


    private Integer region;
    private Integer intersectionId;
    private Integer requestID;
    private ProcessedPriorityRequestType priorityRequestType;

    // Intersection Access Point may be either LaneID, ApproachID, or LaneConnectionID

    // Inbound access point is required
    private Integer inboundLaneID;
    private Integer inboundApproachID;
    private Integer inboundLaneConnectionID;

    // Outbound access point is optional
    private Integer outboundLaneID;
    private Integer outboundApproachID;
    private Integer outboundLaneConnectionID;

    // Fields from DF_RequestorDescription
    // VehicleID is required.  It may be a TemporaryID or StationID.
    private String vehicleID;

    // RequestorType
    private ProcessedBasicVehicleRole role;
    private ProcessedRequestSubRole subrole;
    private ProcessedRequestImportanceLevel importanceLevel;
    private Integer iso3833VehicleType;
    private ProcessedVehicleType hpmsType;

    // Fields from RequestorPositionVector
    private Double latitude;
    private Double longitude;
    private Double elevation;
    private Double heading;
    private ProcessedTransmissionState transmission;
    private Double speed;

    private String name;
    private String routeName;
    private ProcessedTransitVehicleStatus transitStatus;
    private ProcessedTransitVehicleOccupancy transitOccupancy;

    // DE_DeltaTime
    private Duration transitSchedule;

    // Timestamp of the DF_SignalRequestPackage
    private ZonedDateTime estimatedTimeOfArrival;
    private Duration estimatedTimeOfArrivalDuration;

}
