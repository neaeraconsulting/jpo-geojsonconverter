package us.dot.its.jpo.geojsonconverter.standards;

public enum RtcmStandard {
    CTI4501_V1("CTI-4501 v01.01, June 2022"),
    J3258_DRAFT("SAE J3258, Draft 2026-03-11");

    private final String description;
    private RtcmStandard(String description) {
        this.description = description;
    }
}
