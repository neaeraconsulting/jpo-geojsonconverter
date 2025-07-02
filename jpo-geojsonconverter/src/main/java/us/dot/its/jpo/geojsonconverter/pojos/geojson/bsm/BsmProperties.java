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
    private Double angle; // Degrees
    private BrakeSystemStatus brakes;
    private Double heading; // Degrees
    private String id;
    private Long msgCnt;
    private Long secMark;
    private VehicleSize size; // Width and height in cm
    private Double speed; // meters per second (m/s)
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
