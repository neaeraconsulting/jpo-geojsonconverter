package us.dot.its.jpo.geojsonconverter.converter;

import org.junit.Test;
import us.dot.its.jpo.asn.j2735.r2024.Common.MinuteOfTheYear;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FieldConversionsTest {
    @Test
    public void testConvertMinuteOfYear() {
        // Normal case: middle of year
        ZonedDateTime ingestTime = ZonedDateTime.of(2025, 10, 3, 0, 0, 1, 0, ZoneOffset.UTC);
        final int dayOfYear = ingestTime.getDayOfYear();
        final int minuteOfYear = dayOfYear * 24 * 60 + 5;
        final var moy = new MinuteOfTheYear(minuteOfYear);
        final ZonedDateTime minuteDate = FieldConversions.convertMinuteOfYear(moy, ingestTime);
        final int year = minuteDate.getYear();
        assertThat(year, equalTo(2025));
    }


    @Test
    public void testConvertMinuteOfYear_YearEnd() {
        // Test edge case of message sent at the end of the year
        final var moy = new MinuteOfTheYear(525600);  // Last minute of the year

        // Last minute of this year
        final ZonedDateTime ingestTimeThisYear = ZonedDateTime.of(2022, 12, 31, 23, 59, 59, 500, ZoneOffset.UTC);
        final ZonedDateTime minuteDate1 = FieldConversions.convertMinuteOfYear(moy, ingestTimeThisYear);
        final int year1 = minuteDate1.getYear();
        assertThat(year1, equalTo(moy));
        assertThat(year1, equalTo(2022));

        // First minute of next year
        final ZonedDateTime ingestTimeNextYear = ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        final ZonedDateTime minuteDate2 = FieldConversions.convertMinuteOfYear(moy, ingestTimeNextYear);
        final int year2 = minuteDate2.getYear();
        assertThat(year2, equalTo(2022));
    }
}
