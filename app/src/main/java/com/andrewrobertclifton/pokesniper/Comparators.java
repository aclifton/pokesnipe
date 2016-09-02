package com.andrewrobertclifton.pokesniper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public static class OrderedNameComparator implements Comparator<Pokemon> {

        private final List<String> strings;

        public OrderedNameComparator(List<String> strings) {
            this.strings = strings;
        }

        public OrderedNameComparator(String[] strings){
            this.strings = new ArrayList<>(strings.length);
            for (String s: strings){
                this.strings.add(s);
            }
        }

        @Override
        public int compare(Pokemon lhs, Pokemon rhs) {
            String name = lhs.getName();
            Integer index = strings.indexOf(name);
            return Integer.compare(strings.indexOf(lhs.getName()), strings.indexOf(rhs.getName()));
        }
    }
}
