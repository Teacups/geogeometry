package com.jillesvangurp.geo;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

@Test
public class ConcavePolygonTest {

    public double[][] concavePolygon = new double[][] { { 13.401260375976562, 52.5296422146409 }, { 13.398771286010742, 52.533036069990786 },
            { 13.396196365356445, 52.53810025868709 }, { 13.404264450073242, 52.54050162853957 }, { 13.412160873413086, 52.54081484101059 },
            { 13.412675857543945, 52.53251395547031 }, { 13.419198989868164, 52.530529863676335 }, { 13.421087265014648, 52.5249948180297 },
            { 13.41653823852539, 52.52160034123976 }, { 13.414220809936523, 52.525725901775196 }, { 13.41156005859375, 52.528545682238736 },
            { 13.406410217285156, 52.529381137980764 }, { 13.401260375976562, 52.5296422146409 } };

    public double[][] concavePolygon2 = new double[][] { { 13.401260375976562, 52.5296422146409 }, { 13.396282196044922, 52.53799584832261 },
            { 13.411645889282227, 52.540919244670995 }, { 13.405466079711914, 52.53444574818791 }, { 13.418769836425781, 52.53037322103258 },
            { 13.406753540039062, 52.53084314728766 } };

    public void shouldCoverWithHashes() {
        printGeoJsonPolygon(concavePolygon2);
        int length = GeoHashUtils.suitableHashLength(50, 52.5381, 13.39619);
        Set<String> hashesForPolygon = GeoHashUtils.geoHashesForPolygon(length+1,concavePolygon2);
        printHashes(hashesForPolygon);
    }

    public void shouldGeneratePointCloud() {
        double[][] cloud = polygon2pointCloud(concavePolygon2);
        double[][] convexPolygon = GeoGeometry.polygonForPoints(cloud);

        System.out.println("convex polygon");
        printGeoJsonPolygon(convexPolygon);

        double[] bbox = GeoGeometry.boundingBox(cloud);
        double diagonal = GeoGeometry.distance(bbox[0], bbox[2], bbox[1], bbox[3]);
        int hashLength = GeoHashUtils.suitableHashLength(diagonal, bbox[0], bbox[2]);

        Set<String> boxHashes = GeoHashUtils.geoHashesForPolygon(hashLength+1,GeoGeometry.bbox2polygon(bbox));
        System.out.println("box hashes");
        printHashes(boxHashes);
        Set<String> cloudHashes = new TreeSet<String>();
        for(double[] point: cloud){
            cloudHashes.add(GeoHashUtils.encode(point[1], point[0], hashLength+2));
        }
        System.out.println("cloud hashes");
        printHashes(cloudHashes);

    }

    private double[][] polygon2pointCloud(double[][] polygon) {
        int length = GeoHashUtils.suitableHashLength(50, 52.5381, 13.39619);
        Set<String> hashesForPolygon = GeoHashUtils.geoHashesForPolygon(length+1,concavePolygon2);
        double[][] result = new double[hashesForPolygon.size()*4][0];
        int i=0;
        for(String hash: hashesForPolygon) {
            double[] decode_bbox = GeoHashUtils.decode_bbox(hash);
            double[][] bbox2polygon = GeoGeometry.bbox2polygon(decode_bbox);
            for(int j=0;j<bbox2polygon.length-1;j++) {
                result[i*4+j]=bbox2polygon[j];
            }
            i++;
        }

        return result;
    }

    private void printHashes(Set<String> hashesForPolygon) {
        System.out.println("[\""+StringUtils.join(hashesForPolygon,"\",\"")+"\"]");
    }

    private void printGeoJsonPolygon(double[][] polygon) {
        System.out.print("{\"type\":\"Polygon\",\"coordinates\":[");
        System.out.print(GeoGeometry.toJson(polygon));
        System.out.println("]}");
    }
}
