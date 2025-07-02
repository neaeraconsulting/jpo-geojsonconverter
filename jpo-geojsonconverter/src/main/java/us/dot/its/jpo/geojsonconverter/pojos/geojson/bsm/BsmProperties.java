package us.dot.its.jpo.geojsonconverter.pojos.geojson.bsm;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import us.dot.its.jpo.asn.j2735.r2024.Common.BrakeSystemStatus;
import us.dot.its.jpo.asn.j2735.r2024.Common.TransmissionState;
import us.dot.its.jpo.asn.j2735.r2024.Common.VehicleSize;
import us.dot.its.jpo.geojsonconverter.DateJsonMapper;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a processed BSM (Basic Safety Message) J2735 PositionalAccuracy.
 * <p>
 * schemaVersion - origin OdeBsmJson message's schema version
 * <p>
 * messageType - BSM
 * <p>
 * odeReceivedAt - The time the origin OdeBsmJson message was received by the ODE, in UTC
 * <p>
 * originIp - The IP address the origin OdeBsmJson message was received from
 * <p>
 * logName - The log file name the origin OdeBsmJson message was consumed from
 * <p>
 * asn1 - The ASN.1 encoded string of the origin J2735 BSM message
 * <p>
 * validationMessages - List of validation messages based on the OdeBsmJson schema
 * <p>
 * timeStamp - The time the origin BSM messages was generated, in UTC
 * <p>
 * accelSet - ProcessedBsmAccelerationSet4Way
 * <p>
 * accuracy - ProcessedBsmPositionalAccuracy
 * <p>
 * angle - Degrees
 * <p>
 * brakes - BrakeSystemStatus
 * <p>
 * heading - Degrees
 * <p>
 * id - The unique identifier for the vehicle/OBU the BSM was generated from at the time.
 * <p>
 * msgCnt - The message count of the BSM
 * <p>
 * secMark - Milliseconds within a minute
 * <p>
 * size - VehicleSize (Width and height in cm)
 * <p>
 * speed - Speed in meters per second (m/s)
 * <p>
 * transmission - TransmissionState enum
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"schemaVersion", "messageType", "odeReceivedAt", "timeStamp", "originIp", "logName", "asn1",
        "validationMessages", "accelSet", "accuracy", "angle", "brakes", "heading", "id", "msgCnt", "secMark", "size",
        "speed", "transmission"})
@Slf4j
public class BsmProperties {
    private static Logger logger = LoggerFactory.getLogger(BsmProperties.class);

    // Metadata properties
    // Default schemaVersion is -1 for older messages that lack a schemaVersion value
    private int schemaVersion = -1;
    private String messageType = "BSM";
    private String odeReceivedAt;
    private String originIp;
    private String logName;
    private String asn1;
    private List<ProcessedValidationMessage> validationMessages = null;
    private ZonedDateTime timeStamp;

    // Payload properties
    private ProcessedBsmAccelerationSet4Way accelSet;
    private ProcessedBsmPositionalAccuracy accuracy;
    private Double angle;
    private BrakeSystemStatus brakes;
    private Double heading;
    private String id;
    private Long msgCnt;
    private Long secMark;
    private VehicleSize size;
    private Double speed;
    private TransmissionState transmission;

    @Override
    public String toString() {
        ObjectMapper mapper = DateJsonMapper.getInstance();
        String testReturn = "";
        try {
            testReturn = (mapper.writeValueAsString(this));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return testReturn;
    }
}
