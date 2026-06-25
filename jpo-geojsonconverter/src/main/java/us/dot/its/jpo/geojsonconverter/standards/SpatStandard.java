package us.dot.its.jpo.geojsonconverter.standards;

public enum SpatStandard {
    CTI4501_V1("CTI-4501 v01.01, June 2022"),
    CTI4501_V2_DRAFT("CTI-4501/1, Draft 2026-01-16");

    private final String description;
    private SpatStandard(String description) {
        this.description = description;
    }
}
