package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import model.Student;
import model.WeightConfig;

import java.io.*;
import java.util.ArrayList;

public class StudentManager {

    private ArrayList<Student> students =
            new ArrayList<>();

    private final String FILE_NAME =
            "students.dat";

    public StudentManager() {

        loadData();
    }

    // =========================
    // LOAD DATA
    // =========================

    public void loadData() {

        try {

            ObjectInputStream in =
                    new ObjectInputStream(

                            new FileInputStream(
                                    FILE_NAME
                            )
                    );

            students =
                    (ArrayList<Student>)
                            in.readObject();

            in.close();

        } catch (Exception e) {

            students =
                    new ArrayList<>();
        }
    }

    // =========================
    // SAVE DATA
    // =========================

    public void saveData() {

        try {

            ObjectOutputStream out =
                    new ObjectOutputStream(

                            new FileOutputStream(
                                    FILE_NAME
                            )
                    );

            out.writeObject(students);

            out.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =========================
    // GET STUDENTS
    // =========================

    public ArrayList<Student> getStudents() {

        loadData();

        return students;
    }

    // =========================
    // TAMBAH SISWA
    // =========================

    public void addStudent(
            Student student
    ) {

        students.add(student);

        normalizeAllTasks();

        refreshAllStudentScores();

        saveData();
    }

    // =========================
    // HAPUS SISWA
    // =========================

    public void deleteStudent(
            Student student
    ) {

        // PERBAIKAN BUG 1: Hapus berdasarkan NIS agar instance objek yang berbeda 
        // dari hasil load data tidak menyebabkan gagal hapus
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getNis().equals(student.getNis())) {
                students.remove(i);
                break;
            }
        }

        refreshAllStudentScores();

        saveData();
    }

    // =========================
    // UPDATE SISWA
    // =========================

    public void updateStudent(

            Student oldStudent,

            Student newStudent
    ) {

        for (int i = 0; i < students.size(); i++) {

            Student current =
                    students.get(i);

            if (

                    current.getNis()
                            .equals(
                                    oldStudent.getNis()
                            )

            ) {

                students.set(
                        i,
                        newStudent
                );

                break;
            }
        }

        normalizeAllTasks();

        refreshAllStudentScores();

        saveData();
    }

    // =========================
    // DUPLIKAT
    // =========================

    public boolean isDuplicate(

            String nis,
            String nama
    ) {

        for (Student s : students) {

            if (

                    s.getNis()
                            .equalsIgnoreCase(nis)

                            ||

                            s.getNama()
                                    .equalsIgnoreCase(nama)

            ) {

                return true;
            }
        }

        return false;
    }

    // =========================
    // MAX TUGAS
    // =========================

    public int getMaxTugas() {

        int max = 0;

        for (Student s : students) {

            if (
                    s.getTugasList().size()
                            > max
            ) {

                max =
                        s.getTugasList().size();
            }
        }

        return max;
    }

    // =========================
    // NORMALIZE TUGAS
    // =========================

    public void normalizeAllTasks() {

        int maxTugas =
                getMaxTugas();

        for (Student s : students) {

            while (

                    s.getTugasList().size()
                            < maxTugas

            ) {

                s.getTugasList()
                        .add(0.0);
            }
        }
    }

    // =========================
    // HITUNG NILAI
    // =========================

    public double calculateFinalScore(

            double uts,
            double uas,

            ArrayList<Double> tugasList
    ) {

        double total = 0;

        for (double t : tugasList) {

            total += t;
        }

        double rataTugas = 0;

        if (!tugasList.isEmpty()) {

            rataTugas =
                    total
                            / tugasList.size();
        }

        return

                (uts
                        * WeightConfig.utsWeight
                        / 100.0)

                        +

                        (uas
                                * WeightConfig.uasWeight
                                / 100.0)

                        +

                        (rataTugas
                                * WeightConfig.tugasWeight
                                / 100.0);
    }

    // =========================
    // REFRESH NILAI
    // =========================

    public void refreshAllStudentScores() {

        for (Student s : students) {

            double akhir =
                    calculateFinalScore(

                            s.getUts(),

                            s.getUas(),

                            s.getTugasList()
                    );

            s.setNilaiAkhir(akhir);

            if (akhir >= 75) {

                s.setStatus(
                        "Lulus"
                );

            } else {

                s.setStatus(
                        "Tidak Lulus"
                );
            }
        }

        saveData();
    }

    // =========================
    // EXPORT PDF
    // =========================

    public void exportPDF() {

        try {

            Document document =
                    new Document();

            PdfWriter.getInstance(

                    document,

                    new FileOutputStream(
                            "laporan_hasil_belajar.pdf"
                    )
            );

            document.open();

            Paragraph title =
                    new Paragraph(

                            "LAPORAN HASIL BELAJAR SISWA",

                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    18
                            )
                    );

            title.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(title);

            document.add(
                    new Paragraph(" ")
            );

            PdfPTable table =
                    new PdfPTable(7);

            table.addCell("NIS");
            table.addCell("Nama");
            table.addCell("UTS");
            table.addCell("UAS");
            table.addCell("Tugas");
            table.addCell("Nilai Akhir");
            table.addCell("Status");

            for (Student s : students) {

                table.addCell(
                        s.getNis()
                );

                table.addCell(
                        s.getNama()
                );

                table.addCell(
                        String.valueOf(
                                s.getUts()
                        )
                );

                table.addCell(
                        String.valueOf(
                                s.getUas()
                        )
                );

                String tugasText = "";

                for (int i = 0;
                     i < s.getTugasList().size();
                     i++) {

                    tugasText +=

                            "T"
                                    + (i + 1)
                                    + ": "

                                    + s.getTugasList().get(i)

                                    + "\n";
                }

                table.addCell(
                        tugasText
                );

                table.addCell(
                        String.valueOf(
                                s.getNilaiAkhir()
                        )
                );

                table.addCell(
                        s.getStatus()
                );
            }

            document.add(table);

            document.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}