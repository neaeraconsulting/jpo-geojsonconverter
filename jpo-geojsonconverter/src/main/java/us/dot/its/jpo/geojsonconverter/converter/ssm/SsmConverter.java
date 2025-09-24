package us.dot.its.jpo.geojsonconverter.converter.ssm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.asn.j2735.r2024.Common.*;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.*;
import us.dot.its.jpo.geojsonconverter.pojos.common.ProcessedPrioritizationResponseStatus;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static us.dot.its.jpo.geojsonconverter.converter.FieldConversions.*;

@Component
@Slf4j
public class SsmConverter {

    public List<ProcessedSsm> processSsm(final SignalStatusMessageMessageFrame ssmFrame) {
        // We don't know what year it is; assume it is this year.
        var now = ZonedDateTime.now();
        return processSsm(ssmFrame, now.getYear());
    }

    public List<ProcessedSsm> processSsm(final SignalStatusMessageMessageFrame ssmFrame, final int year) {
        var list = new ArrayList<ProcessedSsm>();


        if (ssmFrame == null) {
            log.error("SSM Message Frame is null");
            return list;
        }

        SignalStatusMessage ssm = ssmFrame.getValue();

        if (ssm == null) {
            log.error("SignalStatusMessage is null");
            return list;
        }

        final Integer sequenceNumber = convertMsgCount(ssm.getSequenceNumber());

        MinuteOfTheYear moy = ssm.getTimeStamp();
        DSecond dsec = ssm.getSecond();
        final ZonedDateTime timeStamp = convertMinuteOfYearAndDSecond(moy, year, dsec);

        SignalStatusList sslist = ssm.getStatus();
        if (sslist == null) return list;
        for (var status : sslist) {
            final Integer statusSequenceNumber = convertMsgCount(status.getSequenceNumber());
            final RegionIntersectionId regInt = convertIntersectionReferenceID(status.getId());
            SignalStatusPackageList packageList = status.getSigStatus();
            if (packageList == null) continue;
            for (SignalStatusPackage pkg : packageList) {
                var processed = new ProcessedSsm();
                processed.setSequenceNumber(sequenceNumber);
                processed.setTimeStamp(timeStamp);
                processed.setStatusSequenceNumber(statusSequenceNumber);
                processed.setRegion(regInt.region());
                processed.setIntersectionId(regInt.intersectionId());
                processSignalStatusPackage(pkg, processed,year);
                list.add(processed);
            }
        }
        return list;
    }


    private void processSignalStatusPackage(final SignalStatusPackage pkg, final ProcessedSsm processed, final int year) {

        SignalRequesterInfo requester = pkg.getRequester();
        processRequester(requester, processed);

        AccessPointID inbound = convertIntersectionAccessPointID(pkg.getInboundOn());
        processed.setInboundOnLaneID(inbound.laneID());
        processed.setInboundOnApproachID(inbound.approachID());
        processed.setInboundOnLaneConnectionID(inbound.connectionID());

        AccessPointID outbound = convertIntersectionAccessPointID(pkg.getOutboundOn());
        processed.setOutboundOnLaneID(outbound.laneID());
        processed.setOutboundOnApproachID(outbound.approachID());
        processed.setOutboundOnLaneConnectionID(outbound.connectionID());

        PrioritizationResponseStatus status = pkg.getStatus();
        if (status != null) {
            ProcessedPrioritizationResponseStatus processedStatus
                    = ProcessedPrioritizationResponseStatus.fromName(status.getName());
            processed.setStatus(processedStatus);
        }

        // ETA
        processETA(pkg, processed, year);

    }

    private void processRequester(final SignalRequesterInfo requester, final ProcessedSsm processed) {
        VehicleID vehicleId = requester.getId();
        processed.setVehicleID(convertVehicleID(vehicleId));

        MsgCount requestSequenceNumber = requester.getSequenceNumber();
        processed.setRequesterSequenceNumber(convertMsgCount(requestSequenceNumber));

        RequestID requestId = requester.getRequest();
        if (requestId != null) {
            processed.setRequestID((int)requestId.getValue());
        }

        processed.setRequesterRole(convertBasicVehicleRole(requester.getRole()));

        RequestorType requestorType = requester.getTypeData();
        processRequestorType(requestorType, processed);
    }


    private void processETA(final SignalStatusPackage pkg, final ProcessedSsm processed, final int year) {
        ZonedDateTime ts = convertMinuteOfYearAndDSecond(pkg.getMinute(), year, pkg.getSecond());
        processed.setEstimatedTimeOfArrival(ts);
        if (pkg.getDuration() != null) {
            Duration duration = Duration.ofMillis(pkg.getDuration().getValue());
            processed.setEstimatedTimeOfArrivalDuration(duration);
        }
    }

    private void processRequestorType(final RequestorType requestorType, final ProcessedSsm processed) {
        // Use this role if top-level role is missing
        if (processed.getRequesterRole() == null) {
            processed.setRequesterRole(convertBasicVehicleRole(requestorType.getRole()));
        }
        processed.setRequesterSubrole(convertRequestSubRole(requestorType.getSubrole()));
        processed.setRequesterHpmsType(convertVehicleType(requestorType.getHpmsType()));
        Iso3833VehicleType iso = requestorType.getIso3883();
        if (iso != null) {
            processed.setRequesterIso3833VehicleType((int)iso.getValue());
        }
        processed.setRequestImportanceLevel(convertRequestImportanceLevel(requestorType.getRequest()));
    }

}
