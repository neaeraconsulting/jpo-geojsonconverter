package us.dot.its.jpo.geojsonconverter.converter.srm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.asn.j2735.r2024.Common.*;
import us.dot.its.jpo.asn.j2735.r2024.SignalRequestMessage.*;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.ProcessedSrm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.SrmProperties;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static us.dot.its.jpo.geojsonconverter.converter.FieldConversions.convertMinuteOfYearAndDSecond;
import static us.dot.its.jpo.geojsonconverter.converter.FieldConversions.convertMsgCount;

@Component
@Slf4j
public class SrmConverter {

    public List<ProcessedSrm> processedSsm(final SignalRequestMessageMessageFrame ssmFrame) {
        // We don't know what year it is; assume it is this year.
        var now = ZonedDateTime.now();
        return processSrm(ssmFrame, now.getYear());
    }

    public List<ProcessedSrm> processSrm(final SignalRequestMessageMessageFrame srmFrame, final int year) {
        var list = new ArrayList<ProcessedSrm>();

        if (srmFrame == null) {
            log.error("MessageFrame is null");
            return list;
        }

        SignalRequestMessage srm = srmFrame.getValue();

        if (srm == null) {
            log.error("SignalRequestMessage is null");
            return list;
        }

        final Integer sequenceNumber = convertMsgCount(srm.getSequenceNumber());
        MinuteOfTheYear moy = srm.getTimeStamp();
        DSecond dsec = srm.getSecond();
        final ZonedDateTime timeStamp = convertMinuteOfYearAndDSecond(moy, year, dsec);

        RequestorDescription requestor = srm.getRequestor();

        SignalRequestList requests = srm.getRequests();
        for (SignalRequestPackage pkg : requests) {
            var props = new SrmProperties();
            props.setTimeStamp(timeStamp);
            props.setSequenceNumber(sequenceNumber);
            Point geometry = processRequestorDescription(requestor, props);
            processSignalRequestPackage(pkg, props);
            var processed = new ProcessedSrm(geometry, props);
            list.add(processed);
        }

        return list;
    }

    private Point processRequestorDescription(final RequestorDescription requestor, SrmProperties props) {
        RequestorPositionVector vector = requestor.getPosition();

        VehicleID id = requestor.getId();
        DescriptiveName name = requestor.getName();
        DescriptiveName routeName = requestor.getRouteName();
        TransitVehicleOccupancy occupancy = requestor.getTransitOccupancy();
        DeltaTime schedule = requestor.getTransitSchedule();
        TransitVehicleStatus status = requestor.getTransitStatus();
        RequestorType type = requestor.getType();
    }

    private void processSignalRequestPackage(SignalRequestPackage pkg, SrmProperties props) {

    }


}
