package view;

import controller.StudentManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Student;

import java.util.ArrayList;

public class EditStudentView {

    private VBox root = new VBox(15);
    private Student student;
    private StudentManager manager = new StudentManager();
    
    private ArrayList<TextField> tugasFields = new ArrayList<>();
    private VBox tugasBox = new VBox(10);

    public EditStudentView(Student student) {
        this.student = student;

        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        Label title = new Label("Edit Data Siswa");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        TextField nisField = createField("NIS");
        nisField.setText(student.getNis());

        TextField namaField = createField("Nama Lengkap");
        namaField.setText(student.getNama());

        TextField kelasField = createField("Kelas");
        kelasField.setText(student.getKelas());

        TextField utsField = createField("Nilai UTS");
        utsField.setText(String.valueOf(student.getUts()));

        TextField uasField = createField("Nilai UAS");
        uasField.setText(String.valueOf(student.getUas()));

        for (Double nilaiTugas : student.getTugasList()) {
            addTugasRow(String.valueOf(nilaiTugas));
        }

        Button tambahTugasBtn = new Button("+ Tambah Tugas");
        tambahTugasBtn.setStyle(mainButton());
        tambahTugasBtn.setMaxWidth(Double.MAX_VALUE);
        tambahTugasBtn.setOnAction(e -> addTugasRow(""));

        Button simpanBtn = new Button("Simpan Perubahan");
        simpanBtn.setStyle(mainButton());
        simpanBtn.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(simpanBtn, new Insets(15, 0, 0, 0));

        simpanBtn.setOnAction(e -> {
            try {
                String nis = nisField.getText();
                String nama = namaField.getText();

                if (!nis.matches("\\d+")) { showError("NIS harus berupa angka bulat."); return; }
                if (nama.matches(".*\\d.*")) { showError("Nama tidak boleh mengandung angka."); return; }

                manager.loadData();
                for (Student s : manager.getStudents()) {
                    if (!s.getNis().equals(this.student.getNis()) && s.getNis().equals(nis)) {
                        showError("NIS sudah digunakan oleh siswa lain!"); return;
                    }
                    if (!s.getNama().equalsIgnoreCase(this.student.getNama()) && s.getNama().equalsIgnoreCase(nama)) {
                        showError("Nama sudah digunakan oleh siswa lain!"); return;
                    }
                }

                double uts = Double.parseDouble(utsField.getText());
                double uas = Double.parseDouble(uasField.getText());

                if (uts < 0 || uts > 100 || uas < 0 || uas > 100) {
                    showError("Nilai UTS dan UAS harus berada di antara 0 - 100."); return;
                }

                ArrayList<Double> newTugasList = new ArrayList<>();
                for (TextField tf : tugasFields) {
                    String text = tf.getText();
                    if (text.isEmpty()) {
                        newTugasList.add(0.0);
                    } else {
                        double nilai = Double.parseDouble(text);
                        if (nilai < 0 || nilai > 100) {
                            showError("Nilai tugas harus berada di antara 0 - 100."); return;
                        }
                        newTugasList.add(nilai);
                    }
                }

                double akhir = manager.calculateFinalScore(uts, uas, newTugasList);
                manager.deleteStudent(this.student);
                
                Student updatedStudent = new Student(nis, nama, kelasField.getText(), uts, uas, newTugasList, akhir);
                manager.addStudent(updatedStudent);

                // PERBAIKAN: Menggunakan AlertHelper
                AlertHelper.showInfo("Sukses", "Data siswa berhasil diupdate!");

                Stage stage = (Stage) simpanBtn.getScene().getWindow();
                stage.close();

            } catch (NumberFormatException ex) {
                showError("Pastikan semua form nilai telah diisi dengan angka yang benar.");
            } catch (Exception ex) {
                showError("Terjadi kesalahan sistem saat memperbarui data.");
            }
        });

        root.getChildren().addAll(title, nisField, namaField, kelasField, utsField, uasField, tambahTugasBtn, tugasBox, simpanBtn);
    }

    private void addTugasRow(String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        TextField tugasField = createField("Nilai Tugas " + (tugasFields.size() + 1));
        tugasField.setText(value);
        HBox.setHgrow(tugasField, Priority.ALWAYS); 
        
        tugasFields.add(tugasField);

        Button hapusBtn = new Button("Hapus");
        hapusBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 15; -fx-cursor: hand;");
        
        hapusBtn.setOnAction(e -> {
            tugasBox.getChildren().remove(row);
            tugasFields.remove(tugasField);
            
            for (int i = 0; i < tugasFields.size(); i++) {
                tugasFields.get(i).setPromptText("Nilai Tugas " + (i + 1));
            }
        });

        row.getChildren().addAll(tugasField, hapusBtn);
        tugasBox.getChildren().add(row);
    }

    private TextField createField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 12;");
        return field;
    }

    private String mainButton() {
        return "-fx-background-color: #1E6FD9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;";
    }

    // PERBAIKAN: Dialihkan ke AlertHelper
    private void showError(String text) {
        AlertHelper.showError(text);
    }

    public Parent getView() {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0f172a; -fx-background-color: #0f172a; -fx-border-color: transparent;");
        return scrollPane;
    }
}