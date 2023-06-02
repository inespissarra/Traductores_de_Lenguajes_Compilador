package Analizadores;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Errores {
    /* Archivos de texto(Errores): */
    FileWriter fileErr;
    PrintWriter pwErr;

    public Errores(String pathErr) {
        try {
            fileErr = new FileWriter(pathErr);
            pwErr = new PrintWriter(fileErr);
        } catch (Exception e) {
        }
    }

    public void writeErr(String msg) {
        pwErr.println(msg);
    }

    public void close() {
        try {
            fileErr.close();
        } catch (Exception e) {
        }
    }
}
