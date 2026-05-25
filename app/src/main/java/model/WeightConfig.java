package model;

public class WeightConfig {

    public static double utsWeight = 30;

    public static double uasWeight = 40;

    public static double tugasWeight = 30;

    public static boolean isValid() {

        return (utsWeight + uasWeight + tugasWeight) == 100;
    }
}