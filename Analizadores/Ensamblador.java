package Analizadores;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Ensamblador {
    /* Archivos de texto(Ensablador): */
    FileWriter fileEnsamblador;
    PrintWriter pwEnsamblador;
    FileWriter fileEnsambladorMain;
    PrintWriter pwEnsambladorMain;
    FileWriter fileEnsambladorFun;
    PrintWriter pwEnsambladorFun;
    String pathMain = "Ficheros/ensambladorMain.txt";
    String pathFun = "Ficheros/ensambladorFun.txt";

    public Ensamblador() {
        try {
            fileEnsambladorMain = new FileWriter(pathMain);
            pwEnsambladorMain = new PrintWriter(fileEnsambladorMain);
            fileEnsambladorFun = new FileWriter(pathFun);
            pwEnsambladorFun = new PrintWriter(fileEnsambladorFun);
            fileEnsamblador = fileEnsambladorMain;
            pwEnsamblador = pwEnsambladorMain;
        } catch (Exception e) {
        }
    }

    public void changeEnsambladorFun() {
        //close();
        //try {
        //    fileEnsamblador = new FileWriter(pathFun);
        //    pwEnsamblador = new PrintWriter(fileEnsamblador);
        //} catch (IOException e) {
        //}
        fileEnsamblador = fileEnsambladorFun;
        pwEnsamblador = pwEnsambladorFun;
    }

    public void changeEnsambladorMain() {
        //close();
        //try {
        //    fileEnsamblador = new FileWriter(pathMain);
        //    pwEnsamblador = new PrintWriter(fileEnsamblador);
        //} catch (IOException e) {
        //}
        fileEnsamblador = fileEnsambladorMain;
        pwEnsamblador = pwEnsambladorMain;
    }

    public void writeEnsamblador(String msg) {
        pwEnsamblador.println(msg);
    }

    public void close() {
        try {
            fileEnsambladorMain.close();
            fileEnsambladorFun.close();
        } catch (Exception e) {
        }
    }
}
