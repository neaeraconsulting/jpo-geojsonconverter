
package us.dot.its.jpo.geojsonconverter.pojos.spat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.SPAT.IntersectionStatusObject;

@Data
@Generated
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class ProcessedIntersectionStatusObject {
    private Boolean manualControlIsEnabled = false;
    private Boolean stopTimeIsActivated = false;
    private Boolean failureFlash = false;
    private Boolean preemptIsActive = false;
    private Boolean signalPriorityIsActive = false;
    private Boolean fixedTimeOperation = false;
    private Boolean trafficDependentOperation = false;
    private Boolean standbyOperation = false;
    private Boolean failureMode = false;
    private Boolean off = false;
    private Boolean recentMAPmessageUpdate = false;
    private Boolean recentChangeInMAPassignedLanesIDsUsed = false;
    private Boolean noValidMAPisAvailableAtThisTime = false;
    private Boolean noValidSPATisAvailableAtThisTime = false;

    public ProcessedIntersectionStatusObject(IntersectionStatusObject status) {
        setStatus(status);
    }

    public void setStatus(IntersectionStatusObject status) {
        if (status == null) {
            return;
        }

        // Check each bit position and set corresponding boolean values
        setManualControlIsEnabled(status.get(0));
        setStopTimeIsActivated(status.get(1));
        setFailureFlash(status.get(2));
        setPreemptIsActive(status.get(3));
        setSignalPriorityIsActive(status.get(4));
        setFixedTimeOperation(status.get(5));
        setTrafficDependentOperation(status.get(6));
        setStandbyOperation(status.get(7));
        setFailureMode(status.get(8));
        setOff(status.get(9));
        setRecentMAPmessageUpdate(status.get(10));
        setRecentChangeInMAPassignedLanesIDsUsed(status.get(11));
        setNoValidMAPisAvailableAtThisTime(status.get(12));
        setNoValidSPATisAvailableAtThisTime(status.get(13));
    }
}
