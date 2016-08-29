package com.andrewrobertclifton.pokesniper;

import java.util.Comparator;

/**
 * Created by user on 8/28/16.
 */
public class Comparators {

    private static class IDComparator implements Comparator<Pokemon> {

        @Override
        public int compare(Pokemon lhs, Pokemon rhs) {
            return Integer.valueOf(lhs.getId()).compareTo(rhs.getId());
        }
    }

    public static final IDComparator ID_COMPARATOR = new IDComparator();

    private static class NameComparator implements Comparator<Pokemon> {

        @Override
        public int compare(Pokemon lhs, Pokemon rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    public static final NameComparator NAME_COMPARATOR = new NameComparator();

    public static class DistanceComparator implements Comparator<Pokemon> {

        private double lat;
        private double lon;

        public DistanceComparator(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public int compare(Pokemon lhs, Pokemon rhs) {
            return Double.compare(lhs.distanceTo(lat, lon), rhs.distanceTo(lat, lon));
        }
    }
}
