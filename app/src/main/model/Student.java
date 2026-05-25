package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Student implements Serializable {

    private String nis;

    private String nama;

    private String kelas;

    private double uts;

    private double uas;

    private ArrayList<Double> tugasList;

    private double nilaiAkhir;

    private String status;

    public Student(

            String nis,
            String nama,
            String kelas,

            double uts,
            double uas,

            ArrayList<Double> tugasList,

            double nilaiAkhir
    ) {

        this.nis = nis;

        this.nama = nama;

        this.kelas = kelas;

        this.uts = uts;

        this.uas = uas;

        this.tugasList = tugasList;

        this.nilaiAkhir = nilaiAkhir;

        if (nilaiAkhir >= 75) {

            status = "Lulus";

        } else {

            status = "Tidak Lulus";
        }
    }

    public String getNis() {
        return nis;
    }

    public String getNama() {
        return nama;
    }

    public String getKelas() {
        return kelas;
    }

    public double getUts() {
        return uts;
    }

    public double getUas() {
        return uas;
    }

    public ArrayList<Double> getTugasList() {
        return tugasList;
    }

    public double getNilaiAkhir() {
        return nilaiAkhir;
    }

    public String getStatus() {
        return status;
    }

    // SETTER BARU

    public void setNilaiAkhir(
            double nilaiAkhir
    ) {

        this.nilaiAkhir = nilaiAkhir;
    }

    public void setStatus(
            String status
    ) {

        this.status = status;
    }
}