package com.spatial.index.application.locationPoint;

public final class LocationGeometry {

    public static final int SRID = 4326;

    private LocationGeometry() {
    }

    public static String point(double lat, double lng) {
        return String.format("POINT(%s %s)", lat, lng);
    }

    /** BBox 사각형. 첫 점과 끝 점이 같아야 닫힌 링이 된다. */
    public static String boundsPolygon(double swLat, double swLng,
                                       double neLat, double neLng) {
        return String.format(
                "POLYGON((%s %s,%s %s,%s %s,%s %s,%s %s))",
                swLat, swLng,
                swLat, neLng,
                neLat, neLng,
                neLat, swLng,
                swLat, swLng);
    }


}
