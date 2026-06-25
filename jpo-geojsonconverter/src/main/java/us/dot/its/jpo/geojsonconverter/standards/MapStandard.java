package us.dot.its.jpo.geojsonconverter.standards;

import lombok.Getter;

@Getter
public enum MapStandard{
    CTI4501_V1("CTI-4501 v01.01, June 2022"),
    CTI4501_V2_DRAFT("CTI-4501/2, Draft 2026-01-05");

    private final String description;
    private MapStandard(String description) {
        this.description = description;
    }
}
