package us.dot.its.jpo.geojsonconverter.pojos.rtcm;

import lombok.Data;
import us.dot.its.jpo.asn.j2735.r2024.Common.FullPositionVector;
import us.dot.its.jpo.asn.j2735.r2024.RTCMcorrections.RTCMcorrectionsMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.ProcessedValidationMessage;

import java.util.List;

/**
 * A processed J2735 {@link RTCMcorrectionsMessageFrame}.
 * <p>Contains only fields from the J2735 messagerequired by CIMMS and CTI4501</p>
 *
 * <p>Includes the decoded RTCM payloads with station ID and message type</p>
 *
 * <p>CTI 4501 v01.01: Sec. 4.3.3.5.1 specification:</p>
 * <p>On what to include from the {@link FullPositionVector}:</p>
 *
 * <p>"The anchorPoint (DF_FullPositionVector) for a connection intersection
 *  shall  include UTC, latitude, longitude, and elevation. UTC is the time
 *  at which the CI receives the corrections information included in
 *  RTCMmessage list from the reference station. The latitude, longitude,
 *  and elevation are at the location of the RTCM reference station antenna.
 *  All other fields in DF_FullPositionVector shall not be included."
 *  </p>
 *
 * <p>Items that must NOT be included per CTI4501 are:
 *  <ul>
 *      <li>MinuteOfTheYear timestamp</li>
 *      <li>RegionalExtension</li>
 *      <li>RTCMheader</li>
 *  </ul>
 *  and are not included in this class, see the validation messages which
 *  indicate if present.</p>
 */
@Data
public class ProcessedRTCM {

    /* -----------------------------------------------------------------------
     * CTI 4501 required fields from RTCMcorrections message frame
     * ----------------------------------------------------------------------*/

    /**
     * MsgCount
     */
    private int msgCnt;

    /**
     * RTCM-Revision.  Must equal "rtcmRev3".
     */
    private String rev;

    /* -----------------------------------------------------------------------
     * CTI 4501 required fields from the FullPositionVector
     * ---------------------------------------------------------------------*/

    /**
     * UTC Timestamp in epoch milliseconds
     */
    private Long utcTime;

    /**
     * Longitude in decimal degrees.
     */
    private Double longitude;

    /**
     * Latitude in decimal degrees.
     */
    private Double latitude;

    /**
     * Elevation in meters.
     */
    private Double elevation;

    private List<DecodedRTCMmessage> messages;
    private List<ProcessedValidationMessage> validationMessages;
}
