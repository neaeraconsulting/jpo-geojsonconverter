package us.dot.its.jpo.geojsonconverter.converter.rtcm;


import lombok.extern.slf4j.Slf4j;
import us.dot.its.jpo.asn.j2735.r2024.Common.*;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrections;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrectionsMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.DecodedRTCMmessage;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.ProcessedRTCM;

import java.util.ArrayList;
import java.util.List;

import static us.dot.its.jpo.geojsonconverter.converter.rtcm.RtcmFieldConversions.*;

@Slf4j
public class RTCMConverter {

    public static ProcessedRTCM processRTCM(final RTCMcorrectionsMessageFrame rtcmFrame) {
        var processed = new ProcessedRTCM();

        if (rtcmFrame == null) {
            log.error("RTCM frame is null");
            return processed;
        }

        RTCMcorrections rtcm = rtcmFrame.getValue();

        if (rtcm == null) {
            log.error("RTCM corrections is null");
            return processed;
        }

        processed.setMsgCnt((int)rtcm.getMsgCnt().getValue());
        processed.setRev(rtcm.getRev().getName());

        FullPositionVector anchor = rtcm.getAnchorPoint();
        if (anchor != null) {
            processFullPosition(processed, anchor);
        } else {
            log.error("RTCM anchor is null");
        }

        final RTCMmessageList messageList = rtcm.getMsgs();
        if (messageList != null) {
            decodeMessages(processed, messageList);
        } else {
            log.info("RTCM messageList is null");
        }

        return processed;
    }

    private static void processFullPosition(ProcessedRTCM processed, FullPositionVector anchor) {
        DDateTime utcTime = anchor.getUtcTime();
        if (utcTime != null) {
            processed.setUtcTime(convertDDateTime(utcTime));
        } else {
            log.error("utcTime is null");
        }

        Longitude lon = anchor.getLong_();
        if (lon != null) {
            processed.setLongitude(convertLong(lon.getValue()));
        } else {
            log.error("long_ is null");
        }

        Latitude lat = anchor.getLat();
        if (lat != null) {
            processed.setLatitude(convertLat(lat.getValue()));
        }

        Elevation elevation = anchor.getElevation();
        if (elevation != null) {
            processed.setElevation(convertElevation(elevation.getValue()));
        }
    }

    private static void decodeMessages(ProcessedRTCM processed, RTCMmessageList messageList) {
        List<DecodedRTCMmessage> decodedMessages = new ArrayList<>();
        for (RTCMmessage message : messageList) {
            var decodedMessage = new DecodedRTCMmessage();
            decodedMessage.setHex(message.getValue());
        }
    }


}
