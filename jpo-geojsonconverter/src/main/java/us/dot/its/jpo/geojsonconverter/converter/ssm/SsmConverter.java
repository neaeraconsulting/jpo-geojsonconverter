package us.dot.its.jpo.geojsonconverter.converter.ssm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.dot.its.jpo.asn.j2735.r2024.Common.DSecond;
import us.dot.its.jpo.asn.j2735.r2024.Common.MinuteOfTheYear;
import us.dot.its.jpo.asn.j2735.r2024.Common.MsgCount;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusList;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessage;
import us.dot.its.jpo.asn.j2735.r2024.SignalStatusMessage.SignalStatusMessageMessageFrame;
import us.dot.its.jpo.geojsonconverter.pojos.ssm.ProcessedSsm;

@Component
@Slf4j
public class SsmConverter {

    public ProcessedSsm processSsm(final SignalStatusMessageMessageFrame ssmFrame) {
        var processed = new ProcessedSsm();

        if (ssmFrame == null) {
            log.error("SSM Message Frame is null");
            return processed;
        }

        SignalStatusMessage ssm = ssmFrame.getValue();

        if (ssm == null) {
            log.error("SignalStatusMessage is null");
            return processed;
        }

        MsgCount sequenceNumber = ssm.getSequenceNumber();
        if (sequenceNumber != null) {
            processed.setSequenceNumber((int)sequenceNumber.getValue());
        }

        MinuteOfTheYear moy = ssm.getTimeStamp();
        DSecond dsec = ssm.getSecond();

        SignalStatusList sslist = ssm.getStatus();

    }
}
