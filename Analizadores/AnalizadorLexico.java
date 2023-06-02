package Analizadores;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TablaDeSimbolos.ListaTS;

public class AnalizadorLexico {
    /* Palabras Reservadas y Tablas de Simbolos: */
    public Map<String, Integer> PR;
    public ListaTS listaTS;

    /* Tokens: */
    public List<Token> listaTokens;

    /* Archivos de texto(Fichero Fuente): */
    File file;
    FileInputStream fileReader;

    /* Errores: */
    Errores err;

    /* Otros: */
    char caracter;
    int linea;

    public AnalizadorLexico(String path, ListaTS listaTS, Errores err) {
        PR = new HashMap<String, Integer>();
        crearTablaPR();
        this.listaTokens = new ArrayList<>();
        try {
            file = new File(path);
            fileReader = new FileInputStream(file);
            this.err = err;
        } catch (IOException e) {
        }
        this.linea = 1;
        this.listaTS = listaTS;
    }

    public void crearTablaPR() {
        PR.put("function", 0);
        PR.put("let", 1);
        PR.put("int", 2);
        PR.put("string", 3);
        PR.put("boolean", 4);
        PR.put("true", 5);
        PR.put("false", 6);
        PR.put("return", 7);
        PR.put("if", 8);
        PR.put("while", 9);
        PR.put("print", 10);
        PR.put("input", 11);
        PR.put("=", 12);
        PR.put("+", 13);
        PR.put("<", 14);
        PR.put("&&", 15);
        PR.put("%=", 16);
        PR.put("(", 17);
        PR.put(")", 18);
        PR.put("{", 19);
        PR.put("}", 20);
        PR.put(",", 21);
        PR.put(";", 22);
    }

    public void leerCaracter() {
        try {
            caracter = (char) this.fileReader.read();
            if (caracter == '\n') {
                linea++;
            }
        } catch (IOException e) {
        }
    }

    public Token getToken() {
        String atributo = "";
        int valor = 0;
        // Delimitador:
        if (caracter == '\n' || caracter == ' ' || caracter == '\r' || caracter == '\t') {
            leerCaracter();
            return null;
            // Comentarios:
        } else if (caracter == '/') {
            leerCaracter();
            if (caracter == '*') {
                while (caracter != '/') {
                    while (caracter != '*') {
                        leerCaracter();
                    }
                    leerCaracter();
                }
            } else {
                // Error
                System.err.println("Analizador Lexico - Formato incorrecto al comentar, se esperaba * pero se obtuvo "
                        + caracter + ". Linea: " + linea);
                err.writeErr("Analizador Lexico - Formato incorrecto al comentar, se esperaba * pero se obtuvo "
                        + caracter + ". Linea: " + linea);
            }
            // Variables:
        } else if ((caracter >= 'A' && caracter <= 'Z') || (caracter >= 'a' && caracter <= 'z')) {
            atributo = atributo + caracter;
            leerCaracter();
            while ((caracter >= 'A' && caracter <= 'Z') || (caracter >= 'a' && caracter <= 'z')
                    || (caracter >= '0' && caracter <= '9') || caracter == '_') {
                atributo = atributo + caracter;
                leerCaracter();
            }
            // Comprobamos si es una palabra reservada:
            if (PR.get(atributo) != null) {
                atributo = atributo.toUpperCase();
                return createToken(atributo, "");
            } else {
                // Comprobamos si ya está en la Tabla de Simbolos:
                int pos;
                pos = this.listaTS.anadirLexema(atributo);
                return createToken("ID", String.valueOf(pos));
            }
            // Cadenas;
        } else if (caracter == '\'') {
            leerCaracter();
            while (caracter != '\'' && caracter != '\0' && caracter != '\n') {
                atributo = atributo + caracter;
                leerCaracter();
            }
            if (caracter == '\'') {
                // Máximo de carecteres 64:
                if (atributo.length() <= 64) {
                    leerCaracter();
                    return createToken("CAD", "\"" + atributo + "\"");
                } else {
                    System.err.println("Analizador Lexico - Cadena fuera de rango " + (atributo.length() - 2) + ">"
                            + "64" + ". Linea: " + linea);
                    err.writeErr("Analizador Lexico - Cadena fuera de rango " + (atributo.length() - 2) + ">" + "64"
                            + ". Linea: " + linea);
                }
            } else {
                System.err.println("Analizador Lexico - Formato incorrecto de cadena" + ". Linea: " + linea);
                err.writeErr("Analizador Lexico - Formato incorrecto de cadena" + ". Linea: " + linea);
            }
            // Enteros:
        } else if (caracter >= '0' && caracter <= '9') {
            valor = valor * 10 + (caracter - '0');
            leerCaracter();
            while (caracter >= '0' && caracter <= '9') {
                valor = valor * 10 + (caracter - '0');
                leerCaracter();
            }
            // Si el número es superior a 32767 lanzará error:
            if (valor <= 32767) {
                return createToken("ENT", String.valueOf(valor));
            } else {
                System.err.println(
                        "Analizador Lexico - Numero fuera de rango " + valor + ">" + "32767" + ". Linea: " + linea);
                err.writeErr(
                        "Analizador Lexico - Numero fuera de rango " + valor + ">" + "32767" + ". Linea: " + linea);
            }
            return null;
            // Signos y operandos reservados:
        } else if (caracter == '=') {
            leerCaracter();
            return createToken("ASIG", "");
        } else if (caracter == '+') {
            leerCaracter();
            return createToken("SUM", "");
        } else if (caracter == '<') {
            leerCaracter();
            return createToken("MENORQUE", "");
        } else if (caracter == '(') {
            leerCaracter();
            return createToken("PARIZQ", "");
        } else if (caracter == ')') {
            leerCaracter();
            return createToken("PARDCH", "");
        } else if (caracter == '{') {
            leerCaracter();
            return createToken("KEYIZQ", "");
        } else if (caracter == '}') {
            leerCaracter();
            return createToken("KEYDCH", "");
        } else if (caracter == ',') {
            leerCaracter();
            return createToken("COMA", "");
        } else if (caracter == ';') {
            leerCaracter();
            return createToken("PYC", "");
        } else if (caracter == '&') {
            leerCaracter();
            if (caracter == '&') {
                leerCaracter();
                return createToken("CONJUN", "");
            } else {
                System.err.println("Analizador Lexico - Formato incorrecto del operador &&. Linea: " + linea);
                err.writeErr("Analizador Lexico - Formato incorrecto del operador &&. Linea: " + linea);
            }
        } else if (caracter == '%') {
            leerCaracter();
            if (caracter == '=') {
                leerCaracter();
                return createToken("ASIGREST", "");
            } else {
                System.err.println("Analizador Lexico - Formato incorrecto del operador %=. Linea: " + linea);
                err.writeErr("Analizador Lexico - Formato incorrecto del operador %=. Linea: " + linea);
            }
        } else {
            System.err.println("Analizador Lexico - Caracter " + caracter + " erroneo. Linea: " + linea);
            err.writeErr("Analizador Lexico - Caracter " + caracter + " erroneo. Linea: " + linea);
        }
        leerCaracter();
        return null;
    }

    public Token createToken(String tipo, String atributo) {
        Token t = new Token(tipo, atributo);
        listaTokens.add(t);
        return t;
    }

    /* Funciones principales para ejecutar en el Main: */

    public void generarTokens() {
        try {
            leerCaracter();
            while (fileReader.available() > 0) {
                getToken();
            }
            createToken("EOF", "");
            fileReader.close();
            err.close();
        } catch (IOException e) {
        }
    }

    public void imprimirTokens() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("Ficheros/tokens.txt");
            pw = new PrintWriter(fichero);
            for (int i = 0; i < listaTokens.size(); i++) {
                pw.println("<" + listaTokens.get(i).getId() + ", " + listaTokens.get(i).getLexema() + ">");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fichero != null)
                    fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
