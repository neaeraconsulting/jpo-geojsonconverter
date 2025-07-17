package us.dot.its.jpo.geojsonconverter.converter.rtcm;


import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.asn.j2735.r2024.Common.*;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCM_Revision;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrections;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrectionsMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.DecodedRTCMmessage;
import us.dot.its.jpo.geojsonconverter.pojos.rtcm.ProcessedRTCM;
import us.dot.its.jpo.geojsonconverter.validator.JsonValidatorResult;

import java.util.*;

import static us.dot.its.jpo.geojsonconverter.converter.rtcm.RtcmFieldConversions.*;

/**
 * Encapsulate methods for converting, decoding, and validating RTCM messages.
 */
@Component
@Slf4j
public class RTCMConverter {

    private final RTCMDecoder decoder;

    @Autowired
    public RTCMConverter(RTCMDecoder decoder) {
        this.decoder = decoder;
    }

    /**
     * Converts a J2735 RTCMcorrections Message Frame to a ProcessedRTCM
     * @param rtcmFrame The RTCM message frame
     * @return The processed RTCM
     */
    public ProcessedRTCM processRTCM(final RTCMcorrectionsMessageFrame rtcmFrame) {
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

        final String rev = rtcm.getRev().getName();
        processed.setRev(rev);
        if (!RTCM_Revision.RTCMREV3.getName().equals(rev)) {
            // CTI 4501 v01.01, Sec. 4.3.3.5.1: Revision 3 is required
            processed.addValidationMessage(
                    "CTI-4501 conformance issue: The RTCMcorrections 'rev' (DE_RTCM_Revision) is not 'rtcmRev3'.");
        }

        // CTI 4501 v01.01, Sec. 4.3.3.5.1: optional timestamp is forbidden
        MinuteOfTheYear timestamp = rtcm.getTimeStamp();
        if (timestamp != null) {

            processed.addValidationMessage(
              "CTI-4501 conformance issue: The RTCMcorrections optional field 'timestamp' (DE_MinuteOfTheYear) is " +
                      "present.  It is forbidden by CTI-4501.");
        }

        // FullPositionVector is mandatory in CTI 4501
        // See CTI 4501 v01.01, Sec. 4.3.3.1.1.11, Table 11
        FullPositionVector anchor = rtcm.getAnchorPoint();
        if (anchor != null) {
            processFullPosition(processed, anchor);
        } else {
            processed.addValidationMessage(
                "CTI-4501 conformance issue: The RTCMcorrections 'anchorPoint' (DF_FullPositionVector) is missing.");
        }

        // CTI 4501 v01.01, Sec. 4.3.3.5.1: optional RTCMheader is forbidden
        RTCMheader header = rtcm.getRtcmHeader();
        if (header != null) {
            processed.addValidationMessage(
                    "CTI-4501 conformance issue: The RTCMcorrections optional field 'rtcmHeader' (DF_RTCMheader) is" +
                            " present.  It is forbidden by CTI-4501.");
        }

        final RTCMmessageList messageList = rtcm.getMsgs();
        if (messageList != null) {
            decodeMessages(processed, messageList);
        } else {
            log.info("RTCM messageList is null");
        }

        // CTI 4501 v01.01, Sec. 4.3.3.5.1: optional regional extension is forbidden
        RTCMcorrections.SequenceOfRegional regionalSeq = rtcm.getRegional();
        if (regionalSeq != null && !regionalSeq.isEmpty()) {
            processed.addValidationMessage(
                    "CTI-4501 conformance issue: The RTCMcorrections has regional extensions present which are " +
                            "forbiddenb by CTI-4501.");
        }

        return processed;
    }




    private void processFullPosition(ProcessedRTCM processed, FullPositionVector anchor) {
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

    private void decodeMessages(ProcessedRTCM processed, RTCMmessageList messageList) {
        List<DecodedRTCMmessage> decodedMessages = new ArrayList<>();
        Set<Integer> types = new LinkedHashSet<>();
        for (RTCMmessage message : messageList) {
            var decodedMessage = new DecodedRTCMmessage();
            decodedMessage.setHex(message.getValue());
            decodedMessages.add(decodedMessage);
            JsonNode node = decoder.decodeRtcm(decodedMessage.getHex());
            decodedMessage.setDecodedMessage(node);

            if (node.has("type")) {
                types.add(node.get("type").asInt());
            }

            if (node.has("station_id")) {
                processed.setStationId(node.get("station_id").asInt());
            }
        }

        processed.setMessageTypes(types);
        if (types.isEmpty()) {
            processed.addValidationMessage("No RTCM message types found.");
        }
        if (processed.getStationId() == null) {
            processed.addValidationMessage("RTCM station ID not found.");
        }

        processed.setMessages(decodedMessages);

        // CTI 4501 v01.01, Sec. 4.3.3.5.1. Rules for grouping message types.
        // See also https://www.use-snip.com/kb/knowledge-base/an-rtcm-message-cheat-sheet/
        //
        // Descriptors and system parameters 1005, 1006, 1013, 1033 should be grouped together.
        //
        // MSM messages should be grouped together.
        // MSM 4 support is mandatory, MSM5,6,7 are allowed.
        // Must support GPS and one other constellation (GLONASS, Galileo, or BeiDou)
        //
        // MSM4 - 1074 (GPS), 1084 (GLONASS), 1094 (Galileo), 1104 (SBAS), 1114 (QZSS), 1124 (BeiDou)
        // MSM5 - 1075, 1085, 1095, 1195, 1115, 1125
        // MSM6 - 1076, 1086, 1096, 1196, 1116, 1126
        // MSM7 - 1077, 1087, 1097, 1197, 1117, 1127
        //
        // Groups should not be mixed.
        //
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        boolean isDescriptors = categorize(DESCRIPTOR_TYPES, types);
        if (isDescriptors) {
            categories.add("Descriptor types: 1005, 1006, 1013, 1033");
        }
        boolean isMsm4 = categorize(MSM4_TYPES, types);
        if (isMsm4) {
            categories.add("MSM4");
        }
        boolean isMsm5 = categorize(MSM5_TYPES, types);
        if (isMsm5) {
            categories.add("MSM5");
        }
        boolean isMsm6 = categorize(MSM6_TYPES, types);
        if (isMsm6) {
            categories.add("MSM6");
        }
        boolean isMsm7 = categorize(MSM7_TYPES, types);
        if (isMsm7) {
            categories.add("MSM7");
        }

        if (categories.isEmpty()) {
            processed.addValidationMessage(
                    "CTI-4501 conformance issue: None of the message types are in categories mentioned in CTI-4501");
        }
        if (categories.size() > 1) {
            processed.addValidationMessage(
                    String.format(
                            "CTI-4501 conformance issue: The message list contains message types from more than" +
                                    " one category: %s", categories));
        }
        String category = categories.iterator().next();
        checkMsmTypes(category, types, "MSM4", MSM4_GPS, processed);
        checkMsmTypes(category, types, "MSM5", MSM5_GPS, processed);
        checkMsmTypes(category, types, "MSM6", MSM6_GPS, processed);
        checkMsmTypes(category, types, "MSM7", MSM7_GPS, processed);
    }

    private void checkMsmTypes(String category, Set<Integer> types, String msmVersion, int gpsType, ProcessedRTCM processed) {
        if (msmVersion.equals(category)) {
            if (!types.contains(gpsType)) {
                processed.addValidationMessage(
                        String.format("CTI-4501 conformance issue: The message list contains %s messages but does " +
                                "not contain an %s GPS message: %s", msmVersion, msmVersion, gpsType));
            } else if (types.size() == 1) {
                processed.addValidationMessage(
                        String.format("CTI-4501 conformance issue: The message list contains an %s GPS message, " +
                                "but no %s messages for other constellations", msmVersion, msmVersion));
            }
        }
    }

    private final static Set<Integer> DESCRIPTOR_TYPES = Set.of(1005, 1006, 1013, 1033);
    private final static int MSM4_GPS = 1074;
    private final static int MSM5_GPS = 1075;
    private final static int MSM6_GPS = 1076;
    private final static int MSM7_GPS = 1077;
    private final static Set<Integer> MSM4_TYPES = Set.of(MSM4_GPS, 1084, 1094, 1104, 1114, 1124);
    private final static Set<Integer> MSM5_TYPES = Set.of(MSM5_GPS, 1085, 1095, 1195, 1115, 1125);
    private final static Set<Integer> MSM6_TYPES = Set.of(MSM6_GPS, 1086, 1096, 1196, 1116, 1126);
    private final static Set<Integer> MSM7_TYPES = Set.of(MSM7_GPS, 1087, 1097, 1197, 1117, 1127);

    private boolean categorize(Set<Integer> category, Set<Integer> types) {
        Set<Integer> categorized = new LinkedHashSet<>(types);
        categorized.retainAll(category);
        return !categorized.isEmpty();
    }


    /**
     * Add JSON schema validation results for J2735 and Metadata validation.
     * @param processed The ProcessedRTCM to add validation messages to
     * @param validatorResult the schema validator result
     */
    public void jsonValidation(ProcessedRTCM processed, JsonValidatorResult validatorResult) {
        var messages = new ArrayList<ProcessedValidationMessage>();
        for (Exception exception : validatorResult.getExceptions()) {
            var msg = new ProcessedValidationMessage();
            msg.setMessage(exception.getMessage());
            msg.setException(Arrays.toString(exception.getStackTrace()));
            messages.add(msg);
        }
        for (ValidationMessage vm : validatorResult.getValidationMessages()) {
            var msg = new ProcessedValidationMessage();
            msg.setMessage(vm.getMessage());
            msg.setSchemaPath(vm.getSchemaPath());
            msg.setJsonPath(vm.getPath());
            messages.add(msg);
        }
        if (processed.getElevation() == null) {
            processed.setValidationMessages(new ArrayList<>());
        }
        processed.getValidationMessages().addAll(messages);
    }
}
