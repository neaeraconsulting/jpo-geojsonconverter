package us.dot.its.jpo.geojsonconverter.converter.ssm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.asn.j2735.r2024.Common.*;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.*;
import us.dot.its.jpo.geojsonconverter.converter.FieldConversions;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static us.dot.its.jpo.geojsonconverter.converter.FieldConversions.*;

@Component
@Slf4j
public class SsmConverter {

    public List<ProcessedSsm> processedSsm(final SignalStatusMessageMessageFrame ssmFrame) {
        // We don't know what year it is, assume the time is now
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
                processSignalStatusPackage(pkg, processed);
                list.add(processed);
            }
        }
    }

    private void processSignalStatusPackage(final SignalStatusPackage pkg, final ProcessedSsm processed, final int year) {

        SignalRequesterInfo requester = pkg.getRequester();
        IntersectionAccessPoint inboundOn = pkg.getInboundOn();
        IntersectionAccessPoint outboundOn = pkg.getOutboundOn();

        // ETA
        MinuteOfTheYear moy = pkg.getMinute();
        DSecond dsec = pkg.getSecond();
        ZonedDateTime ts = convertMinuteOfYearAndDSecond(moy, year, dsec);
        processed.setEstimatedTimeOfArrival(ts);
        if (pkg.getDuration() != null) {
            Duration duration = Duration.ofMillis(pkg.getDuration().getValue());
            processed.setEstimatedTimeOfArrivalDuration(duration);
        }

        PrioritizationResponseStatus status = pkg.getStatus();




    }
}
