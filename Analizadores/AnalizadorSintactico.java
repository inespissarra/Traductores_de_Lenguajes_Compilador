package Analizadores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TablaDeSimbolos.ListaTS;

public class AnalizadorSintactico {
    /* Pilas, token y parse: */
    public Stack<ElementoPila> pila = new Stack<ElementoPila>();
    public Stack<ElementoPila> pilaAux = new Stack<ElementoPila>();
    private Token tokenActual;
    private String parse;

    /* Analizador Léxico: */
    AnalizadorLexico aL;

    /* Tabla de Simbolos: */
    ListaTS listaTS;

    /* Errores: */
    Errores err;

    /* Codigo Objeto */
    Ensamblador ensamblador;
    Map<String, Integer> tamRA_fun = new HashMap<String, Integer>();
    Map<String, String> lista_cadenas = new HashMap<String, String>();

    /* Generador de código intermedio: */
    int temp = 1;
    int etiquetas = 1;
    int ret = 1;
    int cadenas = 1;
    private List<Cuarteto> cuartetos = new ArrayList<>();

    public AnalizadorSintactico(String path, ListaTS listaTS, Errores err) {
        aL = new AnalizadorLexico(path, listaTS, err);
        this.listaTS = listaTS;
        this.err = err;
        this.ensamblador = new Ensamblador();
        parse = "Descendente ";
    }

    private void Prod_P() {
        if (tokenActual.getId().equals("LET") || tokenActual.getId().equals("ID") || tokenActual.getId().equals("IF")
                || tokenActual.getId().equals("WHILE") || tokenActual.getId().equals("PRINT")
                || tokenActual.getId().equals("INPUT") || tokenActual.getId().equals("RETURN")) {
            pila.push(new ElementoPila("SEM", "regla_1"));
            pila.push(new ElementoPila("P", ""));
            pila.push(new ElementoPila("B", ""));
            pila.push(new ElementoPila("SEM", "regla_56"));
            parse = parse + "1 ";
        } else if (tokenActual.getId().equals("FUNCTION")) {
            pila.push(new ElementoPila("SEM", "regla_2"));
            pila.push(new ElementoPila("P", ""));
            pila.push(new ElementoPila("F", ""));
            pila.push(new ElementoPila("SEM", "regla_57"));
            parse = parse + "2 ";
        } else if (tokenActual.getId().equals("EOF")) {
            pila.push(new ElementoPila("SEM", "regla_3"));
            pila.push(new ElementoPila("eof", ""));
            parse = parse + "3 ";
        } else {
            err.writeErr("Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                    + "' no esperado. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_B() {
        if (tokenActual.getId().equals("LET")) {
            pila.push(new ElementoPila("SEM", "regla_4"));
            pila.push(new ElementoPila(";", ""));
            pila.push(new ElementoPila("id", ""));
            pila.push(new ElementoPila("T", ""));
            pila.push(new ElementoPila("let", ""));
            pila.push(new ElementoPila("SEM", "regla_50"));
            parse = parse + "4 ";
        } else if (tokenActual.getId().equals("IF")) {
            pila.push(new ElementoPila("SEM", "regla_5"));
            pila.push(new ElementoPila("S", ""));
            pila.push(new ElementoPila("SEM", "regla_58"));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("E", ""));
            pila.push(new ElementoPila("(", ""));
            pila.push(new ElementoPila("if", ""));
            parse = parse + "5 ";
        } else if (tokenActual.getId().equals("ID") || tokenActual.getId().equals("PRINT")
                || tokenActual.getId().equals("INPUT") || tokenActual.getId().equals("RETURN")) {
            pila.push(new ElementoPila("SEM", "regla_6"));
            pila.push(new ElementoPila("S", ""));
            pila.push(new ElementoPila("SEM", "regla_59"));
            parse = parse + "6 ";
        } else if (tokenActual.getId().equals("WHILE")) {
            pila.push(new ElementoPila("SEM", "regla_7"));
            pila.push(new ElementoPila("}", ""));
            pila.push(new ElementoPila("C", ""));
            pila.push(new ElementoPila("SEM", "regla_60"));
            pila.push(new ElementoPila("{", ""));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("E", ""));
            pila.push(new ElementoPila("SEM", "regla_66"));
            pila.push(new ElementoPila("(", ""));
            pila.push(new ElementoPila("while", ""));
            parse = parse + "7 ";
        } else {
            err.writeErr("Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                    + "' no esperado. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_T() {
        if (tokenActual.getId().equals("INT")) {
            pila.push(new ElementoPila("SEM", "regla_8"));
            pila.push(new ElementoPila("int", ""));
            parse = parse + "8 ";
        } else if (tokenActual.getId().equals("STRING")) {
            pila.push(new ElementoPila("SEM", "regla_9"));
            pila.push(new ElementoPila("string", ""));
            parse = parse + "9 ";
        } else if (tokenActual.getId().equals("BOOLEAN")) {
            pila.push(new ElementoPila("SEM", "regla_10"));
            pila.push(new ElementoPila("boolean", ""));
            parse = parse + "10 ";
        } else {
            err.writeErr("Analizador Sintactico - Tipo de dato incorrecto. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_S() {
        if (tokenActual.getId().equals("ID")) {
            pila.push(new ElementoPila("SEM", "regla_11"));
            pila.push(new ElementoPila(";", ""));
            pila.push(new ElementoPila("N", ""));
            pila.push(new ElementoPila("SEM", "regla_64"));
            pila.push(new ElementoPila("id", ""));
            parse = parse + "11 ";
        } else if (tokenActual.getId().equals("PRINT")) {
            pila.push(new ElementoPila("SEM", "regla_12"));
            pila.push(new ElementoPila(";", ""));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("E", ""));
            pila.push(new ElementoPila("(", ""));
            pila.push(new ElementoPila("print", ""));
            parse = parse + "12 ";
        } else if (tokenActual.getId().equals("INPUT")) {
            pila.push(new ElementoPila("SEM", "regla_13"));
            pila.push(new ElementoPila(";", ""));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("id", ""));
            pila.push(new ElementoPila("(", ""));
            pila.push(new ElementoPila("input", ""));
            parse = parse + "13 ";
        } else if (tokenActual.getId().equals("RETURN")) {
            pila.push(new ElementoPila("SEM", "regla_14"));
            pila.push(new ElementoPila(";", ""));
            pila.push(new ElementoPila("X", ""));
            pila.push(new ElementoPila("return", ""));
            parse = parse + "14 ";
        } else {
            err.writeErr("Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                    + "' no esperado. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_N() {
        if (tokenActual.getId().equals("ASIG")) {
            pila.push(new ElementoPila("SEM", "regla_15"));
            pila.push(new ElementoPila("E", ""));
            pila.push(new ElementoPila("=", ""));
            parse = parse + "15 ";
        } else if (tokenActual.getId().equals("PARIZQ")) {
            pila.push(new ElementoPila("SEM", "regla_16"));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("L", ""));
            pila.push(new ElementoPila("(", ""));
            parse = parse + "16 ";
        } else if (tokenActual.getId().equals("ASIGREST")) {
            pila.push(new ElementoPila("SEM", "regla_17"));
            pila.push(new ElementoPila("E", ""));
            pila.push(new ElementoPila("%=", ""));
            parse = parse + "17 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_X() {
        if (tokenActual.getId().equals("ID") || tokenActual.getId().equals("PARIZQ")
                || tokenActual.getId().equals("ENT") || tokenActual.getId().equals("CAD")
                || tokenActual.getId().equals("TRUE") || tokenActual.getId().equals("FALSE")) {
            pila.push(new ElementoPila("SEM", "regla_18"));
            pila.push(new ElementoPila("E", ""));
            parse = parse + "18 ";
        } else if (tokenActual.getId().equals("PYC")) {
            pila.push(new ElementoPila("SEM", "regla_19"));
            parse = parse + "19 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_C() {
        if (tokenActual.getId().equals("LET") || tokenActual.getId().equals("ID") || tokenActual.getId().equals("IF")
                || tokenActual.getId().equals("WHILE") || tokenActual.getId().equals("PRINT")
                || tokenActual.getId().equals("INPUT") || tokenActual.getId().equals("RETURN")) {
            pila.push(new ElementoPila("SEM", "regla_20"));
            pila.push(new ElementoPila("C", ""));
            pila.push(new ElementoPila("B", ""));
            pila.push(new ElementoPila("SEM", "regla_61"));
            parse = parse + "20 ";
        } else if (tokenActual.getId().equals("KEYDCH")) {
            pila.push(new ElementoPila("SEM", "regla_21"));
            parse = parse + "21 ";
        } else {
            err.writeErr("Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                    + "' no esperado. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_L() {
        if (tokenActual.getId().equals("ID") || tokenActual.getId().equals("PARIZQ")
                || tokenActual.getId().equals("ENT") || tokenActual.getId().equals("CAD")
                || tokenActual.getId().equals("TRUE") || tokenActual.getId().equals("FALSE")) {
            pila.push(new ElementoPila("SEM", "regla_22"));
            pila.push(new ElementoPila("Q", ""));
            pila.push(new ElementoPila("E", ""));
            parse = parse + "22 ";
        } else if (tokenActual.getId().equals("PARDCH")) {
            pila.push(new ElementoPila("SEM", "regla_23"));
            parse = parse + "23 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_Q() {
        if (tokenActual.getId().equals("COMA")) {
            pila.push(new ElementoPila("SEM", "regla_24"));
            pila.push(new ElementoPila("Q", ""));
            pila.push(new ElementoPila("E", ""));
            pila.push(new ElementoPila(",", ""));
            parse = parse + "24 ";
        } else if (tokenActual.getId().equals("PARDCH")) {
            pila.push(new ElementoPila("SEM", "regla_25"));
            parse = parse + "25 ";
        } else {
            err.writeErr("Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                    + "' no esperado. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_F() {
        if (tokenActual.getId().equals("FUNCTION")) {
            pila.push(new ElementoPila("SEM", "regla_26"));
            pila.push(new ElementoPila("}", ""));
            pila.push(new ElementoPila("SEM", "regla_63"));
            pila.push(new ElementoPila("C", ""));
            pila.push(new ElementoPila("SEM", "regla_62"));
            pila.push(new ElementoPila("{", ""));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("SEM", "regla_53"));
            pila.push(new ElementoPila("A", ""));
            pila.push(new ElementoPila("(", ""));
            pila.push(new ElementoPila("H", ""));
            pila.push(new ElementoPila("SEM", "regla_52"));
            pila.push(new ElementoPila("id", ""));
            pila.push(new ElementoPila("SEM", "regla_51"));
            pila.push(new ElementoPila("function", ""));
            parse = parse + "26 ";
        } else {
            err.writeErr("Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                    + "' no esperado. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_H() {
        if (tokenActual.getId().equals("INT") || tokenActual.getId().equals("STRING")
                || tokenActual.getId().equals("BOOLEAN")) {
            pila.push(new ElementoPila("SEM", "regla_27"));
            pila.push(new ElementoPila("T", ""));
            parse = parse + "27 ";
        } else if (tokenActual.getId().equals("PARIZQ")) {
            pila.push(new ElementoPila("SEM", "regla_28"));
            parse = parse + "28 ";
        } else {
            err.writeErr("Analizador Sintactico - Tipo de dato incorrecto. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_A() {
        if (tokenActual.getId().equals("INT") || tokenActual.getId().equals("STRING")
                || tokenActual.getId().equals("BOOLEAN")) {
            pila.push(new ElementoPila("SEM", "regla_29"));
            pila.push(new ElementoPila("K", ""));
            pila.push(new ElementoPila("SEM", "regla_54"));
            pila.push(new ElementoPila("id", ""));
            pila.push(new ElementoPila("T", ""));
            parse = parse + "29 ";
        } else if (tokenActual.getId().equals("PARDCH")) {
            pila.push(new ElementoPila("SEM", "regla_30"));
            parse = parse + "30 ";
        } else {
            err.writeErr("Analizador Sintactico - Tipo de dato incorrecto. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_K() {
        if (tokenActual.getId().equals("COMA")) {
            pila.push(new ElementoPila("SEM", "regla_31"));
            pila.push(new ElementoPila("K", ""));
            pila.push(new ElementoPila("SEM", "regla_55"));
            pila.push(new ElementoPila("id", ""));
            pila.push(new ElementoPila("T", ""));
            pila.push(new ElementoPila(",", ""));
            parse = parse + "31 ";
        } else if (tokenActual.getId().equals("PARDCH")) {
            pila.push(new ElementoPila("SEM", "regla_32"));
            parse = parse + "32 ";
        } else {
            err.writeErr("Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                    + "' no esperado. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_E() {
        if (tokenActual.getId().equals("ID") || tokenActual.getId().equals("PARIZQ")
                || tokenActual.getId().equals("ENT") || tokenActual.getId().equals("CAD")
                || tokenActual.getId().equals("TRUE") || tokenActual.getId().equals("FALSE")) {
            pila.push(new ElementoPila("SEM", "regla_33"));
            pila.push(new ElementoPila("E2", ""));
            pila.push(new ElementoPila("W", ""));
            parse = parse + "33 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_E2() {
        if (tokenActual.getId().equals("PYC") || tokenActual.getId().equals("PARDCH")
                || tokenActual.getId().equals("COMA")) {
            pila.push(new ElementoPila("SEM", "regla_34"));
            parse = parse + "34 ";
        } else if (tokenActual.getId().equals("CONJUN")) {
            pila.push(new ElementoPila("SEM", "regla_35"));
            pila.push(new ElementoPila("E2", ""));
            pila.push(new ElementoPila("W", ""));
            pila.push(new ElementoPila("&&", ""));
            parse = parse + "35 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_W() {
        if (tokenActual.getId().equals("ID") || tokenActual.getId().equals("PARIZQ")
                || tokenActual.getId().equals("ENT") || tokenActual.getId().equals("CAD")
                || tokenActual.getId().equals("TRUE") || tokenActual.getId().equals("FALSE")) {
            pila.push(new ElementoPila("SEM", "regla_36"));
            pila.push(new ElementoPila("W2", ""));
            pila.push(new ElementoPila("R", ""));
            parse = parse + "36 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_W2() {
        if (tokenActual.getId().equals("PYC") || tokenActual.getId().equals("PARDCH")
                || tokenActual.getId().equals("COMA") || tokenActual.getId().equals("CONJUN")) {
            pila.push(new ElementoPila("SEM", "regla_37"));
            parse = parse + "37 ";
        } else if (tokenActual.getId().equals("MENORQUE")) {
            pila.push(new ElementoPila("SEM", "regla_38"));
            pila.push(new ElementoPila("W2", ""));
            pila.push(new ElementoPila("R", ""));
            pila.push(new ElementoPila("<", ""));
            parse = parse + "38 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_R() {
        if (tokenActual.getId().equals("ID") || tokenActual.getId().equals("PARIZQ")
                || tokenActual.getId().equals("ENT") || tokenActual.getId().equals("CAD")
                || tokenActual.getId().equals("TRUE") || tokenActual.getId().equals("FALSE")) {
            pila.push(new ElementoPila("SEM", "regla_39"));
            pila.push(new ElementoPila("R2", ""));
            pila.push(new ElementoPila("V", ""));
            parse = parse + "39 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_R2() {
        if (tokenActual.getId().equals("PYC") || tokenActual.getId().equals("PARDCH")
                || tokenActual.getId().equals("COMA") || tokenActual.getId().equals("CONJUN")
                || tokenActual.getId().equals("MENORQUE")) {
            pila.push(new ElementoPila("SEM", "regla_40"));
            parse = parse + "40 ";
        } else if (tokenActual.getId().equals("SUM")) {
            pila.push(new ElementoPila("SEM", "regla_41"));
            pila.push(new ElementoPila("R2", ""));
            pila.push(new ElementoPila("V", ""));
            pila.push(new ElementoPila("+", ""));
            parse = parse + "41 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_V() {
        if (tokenActual.getId().equals("ID")) {
            pila.push(new ElementoPila("SEM", "regla_42"));
            pila.push(new ElementoPila("Z", ""));
            pila.push(new ElementoPila("SEM", "regla_65"));
            pila.push(new ElementoPila("id", ""));
            parse = parse + "42 ";
        } else if (tokenActual.getId().equals("PARIZQ")) {
            pila.push(new ElementoPila("SEM", "regla_43"));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("E", ""));
            pila.push(new ElementoPila("(", ""));
            parse = parse + "43 ";
        } else if (tokenActual.getId().equals("ENT")) {
            pila.push(new ElementoPila("SEM", "regla_46"));
            pila.push(new ElementoPila("entero", ""));
            parse = parse + "46 ";
        } else if (tokenActual.getId().equals("CAD")) {
            pila.push(new ElementoPila("SEM", "regla_47"));
            pila.push(new ElementoPila("cadena", ""));
            parse = parse + "47 ";
        } else if (tokenActual.getId().equals("TRUE")) {
            pila.push(new ElementoPila("SEM", "regla_48"));
            pila.push(new ElementoPila("true", ""));
            parse = parse + "48 ";
        } else if (tokenActual.getId().equals("FALSE")) {
            pila.push(new ElementoPila("SEM", "regla_49"));
            pila.push(new ElementoPila("false", ""));
            parse = parse + "49 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_Z() {
        if (tokenActual.getId().equals("PARIZQ")) {
            pila.push(new ElementoPila("SEM", "regla_44"));
            pila.push(new ElementoPila(")", ""));
            pila.push(new ElementoPila("L", ""));
            pila.push(new ElementoPila("(", ""));
            parse = parse + "44 ";
        } else if (tokenActual.getId().equals("PYC") || tokenActual.getId().equals("PARDCH")
                || tokenActual.getId().equals("COMA") || tokenActual.getId().equals("CONJUN")
                || tokenActual.getId().equals("MENORQUE") || tokenActual.getId().equals("SUM")) {
            pila.push(new ElementoPila("SEM", "regla_45"));
            parse = parse + "45 ";
        } else {
            err.writeErr("Analizador Sintactico - Expresion incorrecta. Linea: " + aL.linea);
            pila.push(new ElementoPila("ERROR", ""));
        }
    }

    private void Prod_Sem(String attr) {
        int regla = Integer.parseInt(attr);
        // Aplicamos la regla correspondiente que tuviera asociada la regla semántica en
        // la pila:
        if (regla == 1 || regla == 2) {
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 3) {
            pilaAux.pop();
        } else if (regla == 4) {
            if (listaTS.getTipoDecl(
                    Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"))) != null) {
                pilaAux.get(pilaAux.size() - 5).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - No se puede redeclarar variables");
            } else {
                pilaAux.get(pilaAux.size() - 5).setAtributo("tipo_ok");
                listaTS.anadirTipoYDespl(Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")),
                        pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo"));

            }
            listaTS.zonaDeclaracion = false;
            for (int i = 0; i < 4; i++) {
                pilaAux.pop();
            }
        } else if (regla == 5) {
            if (!pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo").equals("logico")) {
                pilaAux.get(pilaAux.size() - 6).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - La condicion del IF no es de tipo logica. Linea: " + aL.linea);
            } else if (!pilaAux.peek().getAtributos().get("tipo").equals("ok")) {
                pilaAux.get(pilaAux.size() - 6).setAtributo("tipo_error");
            } else {
                pilaAux.get(pilaAux.size() - 6).setAtributo("tipo_ok");
                String etiq = pilaAux.peek().getAtributos().get("despues");
                cuartetos.add(new Cuarteto(":", etiq, "", ""));
                obtenerCO(":", etiq, "", "");
            }
            for (int i = 0; i < 5; i++) {
                pilaAux.pop();
            }
        } else if (regla == 6) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_" + pilaAux.peek().getAtributos().get("tipo"));
            pilaAux.pop();
        } else if (regla == 7) {
            if (!pilaAux.get(pilaAux.size() - 5).getAtributos().get("tipo").equals("logico")) {
                pilaAux.get(pilaAux.size() - 8).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - La condicion del WHILE no es de tipo logica. Linea: " + aL.linea);
            } else if (!pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("ok")) {
                pilaAux.get(pilaAux.size() - 8).setAtributo("tipo_error");
            } else {
                pilaAux.get(pilaAux.size() - 8).setAtributo("tipo_ok");
                /* GCI */
                String etiq = pilaAux.get(pilaAux.size() - 8).getAtributos().get("inicio");
                cuartetos.add(new Cuarteto("goto", "", "", etiq));
                obtenerCO("goto", "", "", etiq);
                etiq = pilaAux.get(pilaAux.size() - 8).getAtributos().get("despues");
                cuartetos.add(new Cuarteto(":", etiq, "", ""));
                obtenerCO(":", etiq, "", "");
            }
            for (int i = 0; i < 7; i++) {
                pilaAux.pop();
            }
        } else if (regla == 8) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_entero");
            pilaAux.pop();
        } else if (regla == 9) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_cadena");
            pilaAux.pop();
        } else if (regla == 10) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_logico");
            pilaAux.pop();
        } else if (regla == 11) {
            String tipo = listaTS
                    .getTipoID(Integer.parseInt(pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS")));
            if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("error")) {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
            } else if (!tipo.equals(pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"))) {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - No se puede operar con valores de distinto tipo. Linea: " + aL.linea);
            } else {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_ok");
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 12) {
            if (pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo").equals("cadena")
                    || pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo").equals("entero")) {
                pilaAux.get(pilaAux.size() - 6).setAtributo("tipo_ok");
                /* GCI */
                String lugar = pilaAux.get(pilaAux.size() - 3).getAtributos().get("lugar");
                cuartetos.add(new Cuarteto("print", "", "", lugar));
                String posTS = pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS");
                obtenerCO("print", "", "", posTS);
            } else {
                pilaAux.get(pilaAux.size() - 6).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - Print solo puede recibir valores de tipo cadena o entero. Linea: "
                        + aL.linea);
            }
            for (int i = 0; i < 5; i++) {
                pilaAux.pop();
            }
        } else if (regla == 13) {
            String tipo = listaTS
                    .getTipoID(Integer.parseInt(pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS")));
            if (tipo.equals("cadena") || tipo.equals("entero")) {
                pilaAux.get(pilaAux.size() - 6).setAtributo("tipo_ok");
                /* GCI */
                String res = listaTS
                        .getNombreID(Integer.parseInt(pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS")));
                cuartetos.add(new Cuarteto("input", "", "", res));
                String posTS = pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS");
                obtenerCO("input", "", "", posTS);
            } else {
                pilaAux.get(pilaAux.size() - 6).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - Input solo puede recibir variables de tipo cadena o entero. Linea: "
                                + aL.linea);
            }
            for (int i = 0; i < 5; i++) {
                pilaAux.pop();
            }
        } else if (regla == 14) {
            if (pilaAux.get(pilaAux.size() - 4).getAtributos().get("return").equals("true")) {
                if (pilaAux.get(pilaAux.size() - 4).getAtributos().get("valorRet")
                        .equals(pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"))) {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_ok");

                    /* GCI */
                    if (!pilaAux.get(pilaAux.size() - 4).getAtributos().get("valorRet").equals("vacio")) {
                        String lugar = pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar");
                        cuartetos.add(new Cuarteto("return", lugar, "", ""));
                        String posTS = pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS");
                        obtenerCO("return", posTS, "", "");
                    } else {
                        cuartetos.add(new Cuarteto("return", "", "", ""));
                        obtenerCO("return", "", "", "");
                    }
                } else {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                    err.writeErr("Analizador Semantico - El valor de retorno es de tipo "
                            + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo")
                            + " cuando se esperaba un valor de tipo "
                            + pilaAux.get(pilaAux.size() - 4).getAtributos().get("valorRet") + ". Linea: " + aL.linea);
                }
            } else {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - La sentencia return solo puede utilizarse dentro de funciones. Linea: "
                                + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 15) {
            if (listaTS.getTipoID(Integer.parseInt(pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS")))
                    .equals("funcion")) {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - El operador '=' no puede recibir funciones como asignacion. Linea: "
                                + aL.linea);
            } else {
                if (pilaAux.peek().getAtributos().get("tipo").equals("funcion")) {
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("tipo_" + pilaAux.peek().getAtributos().get("valorRet"));
                } else {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_" + pilaAux.peek().getAtributos().get("tipo"));
                }
                /* GCI */
                String res = listaTS
                        .getNombreID(Integer.parseInt(pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS")));
                String arg1 = pilaAux.peek().getAtributos().get("lugar");
                cuartetos.add(new Cuarteto("=", arg1, "", res));
                String N_posTS = pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS");
                String E_posTS = pilaAux.peek().getAtributos().get("posTS");
                obtenerCO("=", E_posTS, "", N_posTS);
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 16) {
            if (listaTS.getTipoID(Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")))
                    .equals("funcion")) {
                if (listaTS.compararNumParams(
                        Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")),
                        pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"))) {
                    if (listaTS.compararParams(
                            Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")),
                            pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"))) {
                        pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_funcion");
                        /* GCI */
                        if (!pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("vacio")) {
                            int longi = Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("long"));
                            int newPos = 1; // Empezamos en 1 debido a EM
                            for (int i = 1; i <= longi; i++) {
                                String param = pilaAux.get(pilaAux.size() - 2).getAtributos().get("param" + i);
                                cuartetos.add(new Cuarteto("param", "", "", param));
                                String posTS = pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTSparam" + i);
                                obtenerCO("param", Integer.toString(newPos), "", posTS);
                                newPos = newPos + this.listaTS.getDespParam(Integer.parseInt(posTS));
                            }
                        }
                        int posTS = Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS"));
                        String etiq = listaTS.getEtiq(posTS);
                        cuartetos.add(new Cuarteto("call", etiq, "", ""));
                        obtenerCO("call", Integer.toString(posTS), "", "");
                    } else {
                        pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                        String cabecera = listaTS
                                .getCabecera(
                                        Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")));
                        err.writeErr("Analizador Semantico - La funcion " + cabecera
                                + " no es aplicable para la cabecera ("
                                + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo") + ")." + " Linea: "
                                + aL.linea);
                    }
                } else {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                    String cabecera = listaTS
                            .getCabecera(Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")));
                    err.writeErr("Analizador Semantico - La funcion " + cabecera + " no es aplicable para la cabecera ("
                            + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo") + ")." + " Linea: "
                            + aL.linea);
                }
            } else {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                String nombre = listaTS
                        .getNombreID(Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")));
                err.writeErr("Analizador Semantico - La variable " + nombre
                        + " no puede ser utilizada como una funcion. Linea: " + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 17) {
            if (listaTS.getTipoID(Integer.parseInt(pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS")))
                    .equals("funcion")) {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - El operador '%=' no puede recibir funciones como asignacion. Linea: "
                                + aL.linea);
            } else {
                if (pilaAux.peek().getAtributos().get("tipo").equals("funcion")) {
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("tipo_" + pilaAux.peek().getAtributos().get("valorRet"));
                } else {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_" + pilaAux.peek().getAtributos().get("tipo"));
                }
                /* GCI */
                String N_posTS = pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS");
                String E_posTS = pilaAux.peek().getAtributos().get("posTS");
                String res = listaTS.getNombreID(Integer.parseInt(N_posTS));
                String arg2 = pilaAux.peek().getAtributos().get("lugar");
                cuartetos.add(new Cuarteto("%", res, arg2, res));
                obtenerCO("%", N_posTS, E_posTS, N_posTS);
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 18) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_" + pilaAux.peek().getAtributos().get("tipo"));
            /* GCI */
            pilaAux.get(pilaAux.size() - 2).setAtributo("lugar_" + pilaAux.peek().getAtributos().get("lugar"));
            pilaAux.get(pilaAux.size() - 2).setAtributo("posTS_" + pilaAux.peek().getAtributos().get("posTS"));
            pilaAux.pop();
        } else if (regla == 19 || regla == 21 || regla == 23 || regla == 25 || regla == 28 || regla == 30 || regla == 32
                || regla == 34 || regla == 37 || regla == 40 || regla == 45) {
            pilaAux.peek().setAtributo("tipo_vacio");
        } else if (regla == 20) {
            if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("ok")
                    && (pilaAux.peek().getAtributos().get("tipo").equals("vacio")
                            || pilaAux.peek().getAtributos().get("tipo").equals("ok"))) {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_ok");
            } else {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 22) {
            if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion") &&
                    pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("vacio")) {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - No se permiten funciones de tipo void como parametro. Linea: "
                                + aL.linea);
            } else if (pilaAux.peek().getAtributos().get("tipo").equals("error")
                    || pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("error")) {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
            } else {
                if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                    if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion")) {
                        pilaAux.get(pilaAux.size() - 3)
                                .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                        pilaAux.get(pilaAux.size() - 3)
                                .setAtributo("lugar_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                    } else {
                        pilaAux.get(pilaAux.size() - 3)
                                .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"));
                        pilaAux.get(pilaAux.size() - 3)
                                .setAtributo("lugar_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                    }
                    /* GCI */
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("param1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("posTSparam1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                    pilaAux.get(pilaAux.size() - 3).setAtributo("long_1");
                } else {
                    if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion")) {
                        pilaAux.get(pilaAux.size() - 3)
                                .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet")
                                        + pilaAux.peek().getAtributos().get("tipo"));
                    } else {
                        pilaAux.get(pilaAux.size() - 3)
                                .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo")
                                        + pilaAux.peek().getAtributos().get("tipo"));
                    }
                    /* GCI */
                    int longi = 1 + Integer.parseInt(pilaAux.peek().getAtributos().get("long"));
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("param1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("posTSparam1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                    for (int i = 2; i <= longi; i++) {
                        pilaAux.get(pilaAux.size() - 3).setAtributo(
                                "param" + i + "_" + pilaAux.peek().getAtributos().get("param" + (i - 1)));
                        pilaAux.get(pilaAux.size() - 3).setAtributo(
                                "posTSparam" + i + "_" + pilaAux.peek().getAtributos().get("posTSparam" + (i - 1)));
                    }
                    pilaAux.get(pilaAux.size() - 3).setAtributo("long_" + longi);
                }
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 24) {
            if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion") &&
                    pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("vacio")) {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - No se permiten funciones de tipo void como parametro. Linea: "
                                + aL.linea);
            } else if (pilaAux.peek().getAtributos().get("tipo").equals("error")
                    || pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("error")) {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
            } else {
                if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                    if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion")) {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("tipo_," + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                    } else {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("tipo_," + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"));
                    }
                    /* GCI */
                    pilaAux.get(pilaAux.size() - 4)
                            .setAtributo("param1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                    pilaAux.get(pilaAux.size() - 4)
                            .setAtributo("posTSparam1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                    pilaAux.get(pilaAux.size() - 4).setAtributo("long_1");
                } else {
                    if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion")) {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("tipo_," + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet")
                                        + pilaAux.peek().getAtributos().get("tipo"));
                    } else {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("tipo_," + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo")
                                        + pilaAux.peek().getAtributos().get("tipo"));
                    }
                    /* GCI */
                    int longi = 1 + Integer.parseInt(pilaAux.peek().getAtributos().get("long"));
                    pilaAux.get(pilaAux.size() - 4)
                            .setAtributo("param1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                    pilaAux.get(pilaAux.size() - 4)
                            .setAtributo("posTSparam1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                    for (int i = 2; i <= longi; i++) {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("param" + i + "_" + pilaAux.peek().getAtributos().get("param" + (i - 1)));
                        pilaAux.get(pilaAux.size() - 4).setAtributo(
                                "posTSparam" + i + "_" + pilaAux.peek().getAtributos().get("posTSparam" + (i - 1)));
                    }
                    pilaAux.get(pilaAux.size() - 4).setAtributo("long_" + longi);
                }
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 26) {
            listaTS.borrarTabla();
            for (int i = 0; i < 9; i++) {
                pilaAux.pop();
            }
        } else if (regla == 27) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_" + pilaAux.peek().getAtributos().get("tipo"));
            pilaAux.pop();
        } else if (regla == 29) {
            if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                pilaAux.get(pilaAux.size() - 4)
                        .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo"));
            } else {
                pilaAux.get(pilaAux.size() - 4)
                        .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo")
                                + pilaAux.peek().getAtributos().get("tipo"));
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 31) {
            if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                pilaAux.get(pilaAux.size() - 5)
                        .setAtributo("tipo_," + pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo"));
            } else {
                pilaAux.get(pilaAux.size() - 5)
                        .setAtributo("tipo_," + pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo")
                                + pilaAux.peek().getAtributos().get("tipo"));
            }
            for (int i = 0; i < 4; i++) {
                pilaAux.pop();
            }
        } else if (regla == 33) {
            if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"));
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                /* GCI */
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("lugar_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("posTS_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
            } else if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("logico") ||
                    (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion") &&
                            pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("logico"))) {
                if (pilaAux.peek().getAtributos().get("tipo").equals("error")) {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                } else {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_logico");
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                    /* GCI */
                    String t = "t" + temp;
                    pilaAux.get(pilaAux.size() - 3).setAtributo("lugar_" + t);
                    int posTS = this.listaTS.anadirTemporal(t, "logico");
                    pilaAux.get(pilaAux.size() - 3).setAtributo("posTS_" + posTS);
                    String arg1 = pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar");
                    String arg2 = pilaAux.peek().getAtributos().get("lugar1");
                    cuartetos.add(new Cuarteto("&&", arg1, arg2, t));
                    String W_posTS = pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS");
                    String E_posTS = pilaAux.peek().getAtributos().get("posTSlugar1");
                    String T_posTS = "" + posTS;
                    obtenerCO("&&", W_posTS, E_posTS, T_posTS);
                    int elems = Integer.parseInt(pilaAux.peek().getAtributos().get("elems"));
                    for (int i = 2; i <= elems; i++) {
                        arg2 = pilaAux.peek().getAtributos().get("lugar" + i);
                        cuartetos.add(new Cuarteto("&&", t, arg2, t));
                        E_posTS = pilaAux.peek().getAtributos().get("posTSlugar" + i);
                        obtenerCO("&&", T_posTS, E_posTS, T_posTS);
                    }
                    temp++;
                }
            } else {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - El operador '&&' solo puede recibir valores de tipo logico. Linea: "
                                + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 35) {
            if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("logico")
                    || (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion")
                            && pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("logico"))) {
                if (pilaAux.peek().getAtributos().get("tipo").equals("error")) {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                } else {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_logico");
                    /* GCI */
                    if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("lugar1_" + (pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar")));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo(
                                        "posTSlugar1_" + (pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")));
                        pilaAux.get(pilaAux.size() - 4).setAtributo("elems_1");
                    } else {
                        int elems = 1 + Integer.parseInt(pilaAux.peek().getAtributos().get("elems"));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("lugar1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo(
                                        "posTSlugar1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                        for (int i = 2; i <= elems; i++) {
                            pilaAux.get(pilaAux.size() - 4).setAtributo(
                                    "lugar" + i + "_" + pilaAux.peek().getAtributos().get("lugar" + (i - 1)));
                            pilaAux.get(pilaAux.size() - 4).setAtributo(
                                    "posTSlugar" + i + "_" + pilaAux.peek().getAtributos().get("posTSlugar" + (i - 1)));
                        }
                        pilaAux.get(pilaAux.size() - 4).setAtributo("elems_" + elems);
                    }
                }
            } else {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                err.writeErr(
                        "Analizador Semantico - El operador '&&' solo puede recibir valores de tipo logico. Linea: "
                                + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 36) {
            if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"));
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                /* GCI */
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("lugar_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("posTS_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
            } else if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("entero") ||
                    (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion") &&
                            pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("entero"))) {
                if (pilaAux.peek().getAtributos().get("tipo").equals("error")) {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                } else {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_logico");
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                    /* GCI */
                    String t = "t" + temp;
                    pilaAux.get(pilaAux.size() - 3).setAtributo("lugar_" + t);
                    int posTS = this.listaTS.anadirTemporal(t, "logico");
                    pilaAux.get(pilaAux.size() - 3).setAtributo("posTS_" + posTS);
                    String arg1 = pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar");
                    String arg2 = pilaAux.peek().getAtributos().get("lugar1");
                    String nueva_etiq = "etiq" + etiquetas;
                    etiquetas++;
                    String nueva_etiq2 = "etiq" + etiquetas;
                    etiquetas++;
                    cuartetos.add(new Cuarteto("if<", arg1, arg2, nueva_etiq));
                    cuartetos.add(new Cuarteto(":=", "0", "", t));
                    cuartetos.add(new Cuarteto("goto", "", "", nueva_etiq2));
                    cuartetos.add(new Cuarteto(":", nueva_etiq, "", ""));
                    cuartetos.add(new Cuarteto(":=", "1", "", t));
                    cuartetos.add(new Cuarteto(":", nueva_etiq2, "", ""));
                    String R_posTS = pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS");
                    String W_posTS = pilaAux.peek().getAtributos().get("posTSlugar1");
                    String T_posTS = "" + posTS;
                    obtenerCO("if<", R_posTS, W_posTS, nueva_etiq);
                    obtenerCO(":=", "0", "", T_posTS);
                    obtenerCO("goto", "", "", nueva_etiq2);
                    obtenerCO(":", nueva_etiq, "", "");
                    obtenerCO(":=", "1", "", T_posTS);
                    obtenerCO(":", nueva_etiq2, "", "");
                    int elems = Integer.parseInt(pilaAux.peek().getAtributos().get("elems"));
                    for (int i = 2; i <= elems; i++) {
                        nueva_etiq = "etiq" + etiquetas;
                        etiquetas++;
                        nueva_etiq2 = "etiq" + etiquetas;
                        etiquetas++;
                        arg2 = pilaAux.peek().getAtributos().get("lugar" + i);
                        cuartetos.add(new Cuarteto("if<", t, arg2, nueva_etiq));
                        cuartetos.add(new Cuarteto(":=", "0", "", t));
                        cuartetos.add(new Cuarteto("goto", "", "", nueva_etiq2));
                        cuartetos.add(new Cuarteto(":", nueva_etiq, "", ""));
                        cuartetos.add(new Cuarteto(":=", "1", "", t));
                        cuartetos.add(new Cuarteto(":", nueva_etiq2, "", ""));
                        W_posTS = pilaAux.peek().getAtributos().get("posTSlugar" + i);
                        obtenerCO("if<", T_posTS, W_posTS, nueva_etiq);
                        obtenerCO(":=", "0", "", T_posTS);
                        obtenerCO("goto", "", "", nueva_etiq2);
                        obtenerCO(":", nueva_etiq, "", "");
                        obtenerCO(":=", "1", "", T_posTS);
                        obtenerCO(":", nueva_etiq2, "", "");
                    }
                    temp++;
                }
            } else {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - El operador '<' solo puede recibir valores de tipo entero. Linea: "
                        + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 38) {
            if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("entero")
                    || (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion")
                            && pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("entero"))) {
                if (pilaAux.peek().getAtributos().get("tipo").equals("error")) {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                } else {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_entero");
                    /* GCI */
                    if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("lugar1_" + (pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar")));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo(
                                        "posTSlugar1_" + (pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")));
                        pilaAux.get(pilaAux.size() - 4).setAtributo("elems_1");
                    } else {
                        int elems = 1 + Integer.parseInt(pilaAux.peek().getAtributos().get("elems"));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("lugar1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo(
                                        "posTSlugar1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                        for (int i = 2; i <= elems; i++) {
                            pilaAux.get(pilaAux.size() - 4).setAtributo(
                                    "lugar" + i + "_" + pilaAux.peek().getAtributos().get("lugar" + (i - 1)));
                            pilaAux.get(pilaAux.size() - 4).setAtributo(
                                    "posTSlugar" + i + "_" + pilaAux.peek().getAtributos().get("posTSlugar" + (i - 1)));
                        }
                        pilaAux.get(pilaAux.size() - 4).setAtributo("elems_" + elems);
                    }
                }
            } else {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - El operador '<' solo puede recibir valores de tipo entero. Linea: "
                        + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 39) {
            if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"));
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                /* GCI */
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("lugar_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                pilaAux.get(pilaAux.size() - 3)
                        .setAtributo("posTS_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
            } else if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("entero") ||
                    (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion") &&
                            pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("entero"))) {
                if (pilaAux.peek().getAtributos().get("tipo").equals("error")) {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                } else {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_entero");
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet"));
                    /* GCI */
                    String t = "t" + temp;
                    pilaAux.get(pilaAux.size() - 3).setAtributo("lugar_" + t);
                    int posTS = this.listaTS.anadirTemporal(t, "entero");
                    pilaAux.get(pilaAux.size() - 3).setAtributo("posTS_" + posTS);
                    String arg1 = pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar");
                    String arg2 = pilaAux.peek().getAtributos().get("lugar1");
                    cuartetos.add(new Cuarteto("+", arg1, arg2, t));
                    String V_posTS = pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS");
                    String R_posTS = pilaAux.peek().getAtributos().get("posTSlugar1");
                    String T_posTS = "" + posTS;
                    obtenerCO("+", V_posTS, R_posTS, T_posTS);
                    int elems = Integer.parseInt(pilaAux.peek().getAtributos().get("elems"));
                    for (int i = 2; i <= elems; i++) {
                        arg2 = pilaAux.peek().getAtributos().get("lugar" + i);
                        cuartetos.add(new Cuarteto("+", t, arg2, t));
                        R_posTS = pilaAux.peek().getAtributos().get("posTSlugar" + i);
                        obtenerCO("+", T_posTS, R_posTS, T_posTS);
                    }
                    temp++;
                }
            } else {
                pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - El operador '+' solo puede recibir valores de tipo entero. Linea: "
                        + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 41) {
            if (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("entero")
                    || (pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("funcion")
                            && pilaAux.get(pilaAux.size() - 2).getAtributos().get("valorRet").equals("entero"))) {
                if (pilaAux.peek().getAtributos().get("tipo").equals("error")) {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                } else {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_entero");
                    /* GCI */
                    if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("lugar1_" + (pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar")));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo(
                                        "posTSlugar1_" + (pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")));
                        pilaAux.get(pilaAux.size() - 4).setAtributo("elems_1");
                    } else {
                        int elems = 1 + Integer.parseInt(pilaAux.peek().getAtributos().get("elems"));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo("lugar1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
                        pilaAux.get(pilaAux.size() - 4)
                                .setAtributo(
                                        "posTSlugar1_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                        for (int i = 2; i <= elems; i++) {
                            pilaAux.get(pilaAux.size() - 4).setAtributo(
                                    "lugar" + i + "_" + pilaAux.peek().getAtributos().get("lugar" + (i - 1)));
                            pilaAux.get(pilaAux.size() - 4).setAtributo(
                                    "posTSlugar" + i + "_" + pilaAux.peek().getAtributos().get("posTSlugar" + (i - 1)));
                        }
                        pilaAux.get(pilaAux.size() - 4).setAtributo("elems_" + elems);
                    }
                }
            } else {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                err.writeErr("Analizador Semantico - El operador '+' solo puede recibir valores de tipo entero. Linea: "
                        + aL.linea);
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 42) {
            String tipo = listaTS
                    .getTipoID(Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")));
            if (tipo.equals("funcion")) {
                if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                    String cabecera = listaTS
                            .getCabecera(Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")));
                    err.writeErr("Analizador Semantico - La funcion " + cabecera
                            + " no puede ser utilizada como una variable. Linea: " + aL.linea);
                } else {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_funcion");
                    String valorRet = listaTS
                            .getValorRet(Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")));
                    pilaAux.get(pilaAux.size() - 3).setAtributo("valorRet_" + valorRet);
                    /* GCI */
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("lugar_" + pilaAux.peek().getAtributos().get("lugar"));
                    pilaAux.get(pilaAux.size() - 3)
                            .setAtributo("posTS_" + pilaAux.peek().getAtributos().get("posTS"));
                }
            } else {
                if (!pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_error");
                    String nombre = listaTS
                            .getNombreID(Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS")));
                    err.writeErr("Analizador Semantico - La variable " + nombre
                            + " no puede ser utilizada como una funcion. Linea: " + aL.linea);
                } else {
                    pilaAux.get(pilaAux.size() - 3).setAtributo("tipo_" + tipo);
                    pilaAux.get(pilaAux.size() - 3).setAtributo("valorRet_false");
                    /* GCI */
                    int posTS = Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
                    String nombre = listaTS.getNombreID(posTS);
                    pilaAux.get(pilaAux.size() - 3).setAtributo("lugar_" + nombre);
                    pilaAux.get(pilaAux.size() - 3).setAtributo("posTS_" + posTS);
                }
            }
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 43) {
            pilaAux.get(pilaAux.size() - 4)
                    .setAtributo("tipo_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"));
            pilaAux.get(pilaAux.size() - 4).setAtributo("valorRet_false");
            /* GCI */
            pilaAux.get(pilaAux.size() - 4)
                    .setAtributo("lugar_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar"));
            pilaAux.get(pilaAux.size() - 4)
                    .setAtributo("posTS_" + pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS"));
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 44) {
            if (listaTS.getTipoID(Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")))
                    .equals("funcion")) {
                if (listaTS.compararNumParams(
                        Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")),
                        pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"))) {
                    if (listaTS.compararParams(
                            Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")),
                            pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"))) {
                        pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_funcion");
                        /* GCI */
                        String t = "t" + temp;
                        pilaAux.get(pilaAux.size() - 4).setAtributo("lugar_" + t);
                        int posTS = this.listaTS.anadirTemporal(t,
                                listaTS.getValorRet(
                                        Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS"))));
                        int posTS_etiq = Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS"));
                        pilaAux.get(pilaAux.size() - 4).setAtributo("posTSetiq_" + posTS_etiq);
                        pilaAux.get(pilaAux.size() - 4).setAtributo("posTS_" + posTS);
                        String etiq = listaTS.getEtiq(posTS_etiq);
                        if (!pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo").equals("vacio")) {
                            int longi = Integer.parseInt(pilaAux.get(pilaAux.size() - 2).getAtributos().get("long"));
                            int newPos = 1; // Empezamos en 1 debido a EM
                            for (int i = 1; i <= longi; i++) {
                                String arg = pilaAux.get(pilaAux.size() - 2).getAtributos().get("param" + i);
                                cuartetos.add(new Cuarteto("param", "", "", arg));
                                String P_posTS = pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTSparam" + i);
                                obtenerCO("param", Integer.toString(newPos), "", P_posTS);
                                newPos = newPos + this.listaTS.getDespParam(Integer.parseInt(P_posTS));
                            }
                        }
                        cuartetos.add(new Cuarteto("call", etiq, "", t));
                        String T_posTS = "" + posTS;
                        String et_posTS = "" + posTS_etiq;
                        obtenerCO("call", et_posTS, "", T_posTS);
                        temp++;
                    } else {
                        pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                        String cabecera = listaTS
                                .getCabecera(
                                        Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")));
                        err.writeErr("Analizador Semantico - La funcion " + cabecera
                                + " no es aplicable para la cabecera ("
                                + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo") + ")." + " Linea: "
                                + aL.linea);
                    }
                } else {
                    pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
                    String cabecera = listaTS
                            .getCabecera(Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")));
                    err.writeErr("Analizador Semantico - La funcion " + cabecera + " no es aplicable para la cabecera ("
                            + pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo") + ")." + " Linea: "
                            + aL.linea);
                }
            } else {
                pilaAux.get(pilaAux.size() - 4).setAtributo("tipo_error");
            }
            pilaAux.pop();
            pilaAux.pop();
            pilaAux.pop();
        } else if (regla == 46) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_entero");
            pilaAux.get(pilaAux.size() - 2).setAtributo("valorRet_false");
            /* GCI */
            String t = "t" + temp;
            pilaAux.get(pilaAux.size() - 2).setAtributo("lugar_" + t);
            int posTS = this.listaTS.anadirTemporal(t, "entero");
            pilaAux.get(pilaAux.size() - 2).setAtributo("posTS_" + posTS);
            String valor = tokenActual.getLexema();
            cuartetos.add(new Cuarteto(":=", valor, "", t));
            String T_posTS = "" + posTS;
            obtenerCO(":=", valor, "", T_posTS);
            temp++;
            pilaAux.pop();
        } else if (regla == 47) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_cadena");
            pilaAux.get(pilaAux.size() - 2).setAtributo("valorRet_false");
            /* GCI */
            String t = "t" + temp;
            pilaAux.get(pilaAux.size() - 2).setAtributo("lugar_" + t);
            int posTS = this.listaTS.anadirTemporal(t, "cadena");
            this.listaTS.anadirTemporalCadena(posTS); // Indicamos que es un temporal que guarda la dirección a un DATA
                                                      // cadena
            pilaAux.get(pilaAux.size() - 2).setAtributo("posTS_" + posTS);
            String valor = tokenActual.getLexema();
            cuartetos.add(new Cuarteto(":=c", valor, "", t));
            String T_posTS = "" + posTS;
            obtenerCO(":=c", valor, "", T_posTS);
            temp++;
            pilaAux.pop();
        } else if (regla == 48) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_logico");
            pilaAux.get(pilaAux.size() - 2).setAtributo("valorRet_false");
            /* GCI */
            String t = "t" + temp;
            pilaAux.get(pilaAux.size() - 2).setAtributo("lugar_" + t);
            cuartetos.add(new Cuarteto(":=", "1", "", t));
            int posTS = this.listaTS.anadirTemporal(t, "logico");
            pilaAux.get(pilaAux.size() - 2).setAtributo("posTS_" + posTS);
            String T_posTS = "" + posTS;
            obtenerCO(":=", "1", "", T_posTS);
            temp++;
            pilaAux.pop();
        } else if (regla == 49) {
            pilaAux.get(pilaAux.size() - 2).setAtributo("tipo_logico");
            pilaAux.get(pilaAux.size() - 2).setAtributo("valorRet_false");
            /* GCI */
            String t = "t" + temp;
            pilaAux.get(pilaAux.size() - 2).setAtributo("lugar_" + t);
            cuartetos.add(new Cuarteto(":=", "0", "", t));
            int posTS = this.listaTS.anadirTemporal(t, "logico");
            pilaAux.get(pilaAux.size() - 2).setAtributo("posTS_" + posTS);
            String T_posTS = "" + posTS;
            obtenerCO(":=", "0", "", T_posTS);
            temp++;
            pilaAux.pop();
        } else if (regla == 50 || regla == 51) {
            listaTS.zonaDeclaracion = true;
        } else if (regla == 52) {
            listaTS.crearTabla();
        } else if (regla == 53) {
            listaTS.zonaDeclaracion = false;
            listaTS.anadirDatosFun(Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")),
                    pilaAux.get(pilaAux.size() - 3).getAtributos().get("tipo"),
                    pilaAux.peek().getAtributos().get("tipo"));
            listaTS.setRA_actual(Integer.parseInt(pilaAux.get(pilaAux.size() - 4).getAtributos().get("posTS")));
        } else if (regla == 54 || regla == 55) {
            listaTS.anadirTipoYDespl(Integer.parseInt(pilaAux.peek().getAtributos().get("posTS")),
                    pilaAux.get(pilaAux.size() - 2).getAtributos().get("tipo"));
        } else if (regla == 56 || regla == 57) {
            // Atributos heredados(se accede a la pila normal):
            pila.peek().setAtributo("return_false");
            pila.get(pila.size() - 2).setAtributo("return_false");
            pila.peek().setAtributo("valorRet_false");
            pila.get(pila.size() - 2).setAtributo("valorRet_false");
        } else if (regla == 58) {
            // Atributos heredados(se accede a la pila normal):
            pila.peek().setAtributo("return_" + pilaAux.get(pilaAux.size() - 5).getAtributos().get("return"));
            pila.peek().setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 5).getAtributos().get("valorRet"));
            String nueva_etiq = "etiq" + etiquetas;
            pila.peek().setAtributo("despues_" + nueva_etiq);
            String lugar = pilaAux.get(pilaAux.size() - 2).getAtributos().get("lugar");
            cuartetos.add(new Cuarteto("if=", lugar, "0", nueva_etiq));
            String posTS = pilaAux.get(pilaAux.size() - 2).getAtributos().get("posTS");
            obtenerCO("if=", posTS, "0", nueva_etiq);
            etiquetas++;
        } else if (regla == 59) {
            // Atributos heredados(se accede a la pila normal):
            pila.peek().setAtributo("return_" + pilaAux.peek().getAtributos().get("return"));
            pila.peek().setAtributo("valorRet_" + pilaAux.peek().getAtributos().get("valorRet"));
        } else if (regla == 60) {
            // Atributos heredados(se accede a la pila normal):
            pila.peek().setAtributo("return_" + pilaAux.get(pilaAux.size() - 6).getAtributos().get("return"));
            pila.peek().setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 6).getAtributos().get("valorRet"));
            String despues = pilaAux.get(pilaAux.size() - 6).getAtributos().get("despues");
            String E_posTS = pilaAux.get(pilaAux.size() - 3).getAtributos().get("posTS");
            String arg = this.listaTS.getNombreID(Integer.parseInt(E_posTS));
            cuartetos.add(new Cuarteto("if=", arg, "0", despues));
            obtenerCO("if=", E_posTS, "0", despues);
        } else if (regla == 61) {
            // Atributos heredados(se accede a la pila normal):
            pila.peek().setAtributo("return_" + pilaAux.peek().getAtributos().get("return"));
            pila.get(pila.size() - 2).setAtributo("return_" + pilaAux.peek().getAtributos().get("return"));
            pila.peek().setAtributo("valorRet_" + pilaAux.peek().getAtributos().get("valorRet"));
            pila.get(pila.size() - 2).setAtributo("valorRet_" + pilaAux.peek().getAtributos().get("valorRet"));
        } else if (regla == 62) {
            // Atributos heredados(se accede a la pila normal):
            pila.peek().setAtributo("return_true");
            pila.peek().setAtributo("valorRet_" + pilaAux.get(pilaAux.size() - 5).getAtributos().get("tipo"));
            /* GCI */
            String etiq = listaTS
                    .getEtiq(Integer.parseInt(pilaAux.get(pilaAux.size() - 6).getAtributos().get("posTS")));
            cuartetos.add(new Cuarteto(":", etiq, "", ""));
            ensamblador.changeEnsambladorFun();
            obtenerCO(":", etiq, "", "");
        } else if (regla == 63) {
            if (pilaAux.peek().getAtributos().get("tipo").equals("vacio")) {
                pilaAux.peek().setAtributo("return_false");
                pilaAux.peek().setAtributo("valorRet_false");
            }
            /* GCI */
            if (!pilaAux.get(pilaAux.size() - 6).getAtributos().get("tipo").equals("vacio")) { // ????
                listaTS.anadirTemporal("VD", pilaAux.get(pilaAux.size() - 6).getAtributos().get("tipo"));
            } else {
                cuartetos.add(new Cuarteto("return", "", "", ""));
                obtenerCO("return", "", "", "");
            }
            ensamblador.changeEnsambladorMain();
            int idPos = Integer.parseInt(pilaAux.get(pilaAux.size() - 7).getAtributos().get("posTS"));
            String etiq = listaTS.getEtiq(idPos);
            int tam = this.listaTS.getTAM_RA_actual() + 1;
            tamRA_fun.put(etiq, tam);
        } else if (regla == 64 || regla == 65) {
            // Atributos heredados(se accede a la pila normal):
            pila.peek().setAtributo("posTS_" + pilaAux.peek().getAtributos().get("posTS"));
        } else if (regla == 66) {
            String nueva_etiq = "etiq" + etiquetas;
            etiquetas++;
            pilaAux.get(pilaAux.size() - 3).setAtributo("inicio_" + nueva_etiq);
            String nueva_etiq2 = "etiq" + etiquetas;
            etiquetas++;
            pilaAux.get(pilaAux.size() - 3).setAtributo("despues_" + nueva_etiq2);

            cuartetos.add(new Cuarteto(":", nueva_etiq, "", ""));
            obtenerCO(":", nueva_etiq, "", "");
        }
    }

    private String transformar(String simbolo) {
        if (simbolo.equals("{")) {
            return "KEYIZQ";
        } else if (simbolo.equals("}")) {
            return "KEYDCH";
        } else if (simbolo.equals("%=")) {
            return "ASIGREST";
        } else if (simbolo.equals("=")) {
            return "ASIG";
        } else if (simbolo.equals("(")) {
            return "PARIZQ";
        } else if (simbolo.equals(")")) {
            return "PARDCH";
        } else if (simbolo.equals("eof")) {
            return "EOF";
        } else if (simbolo.equals(";")) {
            return "PYC";
        } else if (simbolo.equals(",")) {
            return "COMA";
        } else if (simbolo.equals("&&")) {
            return "CONJUN";
        } else if (simbolo.equals("entero")) {
            return "ENT";
        } else if (simbolo.equals("cadena")) {
            return "CAD";
        } else if (simbolo.equals("+")) {
            return "SUM";
        } else if (simbolo.equals("<")) {
            return "MENORQUE";
        } else {
            return simbolo.toUpperCase();
        }
    }

    private String transformarAIcono(String token) {
        if (token.equals("KEYIZQ")) {
            return "{";
        } else if (token.equals("KEYDCH")) {
            return "}";
        } else if (token.equals("ASIGREST")) {
            return "%=";
        } else if (token.equals("ASIG")) {
            return "=";
        } else if (token.equals("PARIZQ")) {
            return "(";
        } else if (token.equals("PARDCH")) {
            return ")";
        } else if (token.equals("EOF")) {
            return "eof";
        } else if (token.equals("PYC")) {
            return ";";
        } else if (token.equals("COMA")) {
            return ",";
        } else if (token.equals("CONJUN")) {
            return "&&";
        } else if (token.equals("ENT")) {
            return "entero";
        } else if (token.equals("CAD")) {
            return "cadena";
        } else if (token.equals("SUM")) {
            return "+";
        } else if (token.equals("MENORQUE")) {
            return "<";
        } else {
            return token.toUpperCase();
        }
    }

    private void expansor() {
        imprimirPilas();

        // Sacamos el elemento de la pila principal:
        ElementoPila top = pila.pop();
        // Identificador del terminal/no terminal o regla semantica
        String cima = top.getKey();
        // Transformamos el identificador para poder compararlo con el ID del token:
        String cimaMayus = transformar(cima);

        // Comprobamos si es una accion semántica:
        if (cimaMayus != "SEM") {
            // Metemos el elemento extraido en la pila auxiliar:
            pilaAux.add(top);
            /*
             * Comparamos si el token que envía el A.Léxico es igual al elemento extraído de
             * la pila:
             */
            if (tokenActual.getId().equals(cimaMayus)) {
                if (cimaMayus.equals("ID")) {
                    /* Si el token es un ID se anade su posición en la TS(posTS) */
                    pilaAux.peek().setAtributo("posTS_" + tokenActual.getLexema());
                }
                if (pila.peek().getKey().equals("SEM")) {
                    ElementoPila top2 = pila.pop();
                    String attr2 = top2.getAtributos().get("regla");
                    Prod_Sem(attr2);
                }
                if (!tokenActual.getId().equals("EOF")) {
                    getNextToken();
                }
            } else if (cimaMayus == "P") {
                Prod_P();
            } else if (cimaMayus == "B") {
                Prod_B();
            } else if (cimaMayus == "T") {
                Prod_T();
            } else if (cimaMayus == "S") {
                Prod_S();
            } else if (cimaMayus == "N") {
                Prod_N();
            } else if (cimaMayus == "X") {
                Prod_X();
            } else if (cimaMayus == "C") {
                Prod_C();
            } else if (cimaMayus == "L") {
                Prod_L();
            } else if (cimaMayus == "Q") {
                Prod_Q();
            } else if (cimaMayus == "F") {
                Prod_F();
            } else if (cimaMayus == "H") {
                Prod_H();
            } else if (cimaMayus == "A") {
                Prod_A();
            } else if (cimaMayus == "K") {
                Prod_K();
            } else if (cimaMayus == "E") {
                Prod_E();
            } else if (cimaMayus == "E2") {
                Prod_E2();
            } else if (cimaMayus == "W") {
                Prod_W();
            } else if (cimaMayus == "W2") {
                Prod_W2();
            } else if (cimaMayus == "R") {
                Prod_R();
            } else if (cimaMayus == "R2") {
                Prod_R2();
            } else if (cimaMayus == "V") {
                Prod_V();
            } else if (cimaMayus == "Z") {
                Prod_Z();
            } else if (cimaMayus.equals("ERROR")) {
                System.out.println("***ERROR***");
                imprimirParse();
                imprimirCuartetos();
                imprimirCO_final();
                cerrarArchivos();
            } else if (cimaMayus.equals("$")) {
                System.out.println("Finalizado correctamente.");
            } else {
                pila.push(new ElementoPila("ERROR", ""));
                err.writeErr(
                        "Analizador Sintactico - Token '" + transformarAIcono(tokenActual.getId())
                                + "' no esperado. Linea: " + aL.linea);
            }
        } else {
            Prod_Sem(top.getAtributos().get("regla"));
        }

        /* Si no ha finalizado ni ha habido errores, continuar la ejecución: */
        if (!cima.equals("$") && !cima.equals("ERROR")) {
            expansor();
        } else if (cima.equals("$")) {
            imprimirParse();
            imprimirCuartetos();
            imprimirCO_final();
            cerrarArchivos();
        }
    }

    public void inicializador() {
        pila.push(new ElementoPila("$", ""));
        pila.push(new ElementoPila("P", ""));

        /* Inicializar Tabla de Símbolos: */
        this.listaTS.crearTabla();

        // Pedimos token al Analizador Lexico:
        aL.leerCaracter();
        getNextToken();
        expansor();

        /* Eliminar Tabla De Símbolos: */
        this.listaTS.borrarTabla();
    }

    /* Pedir siguiente token al léxico: */
    public void getNextToken() {
        try {
            if (aL.fileReader.available() > 0) {
                tokenActual = aL.getToken();
                if (tokenActual == null) {
                    getNextToken();
                }
            } else {
                tokenActual = aL.createToken("EOF", "");
                aL.imprimirTokens();
            }
        } catch (Exception e) {
        }
    }

    /* Seguimiento del valor de las pilas en cada iteracción por consola: */
    public void imprimirPilas() {
        System.out.print("Token actual: " + tokenActual.getId() + " - Lexema: " + tokenActual.getLexema() + "\n");
        System.out.print("\tPila: ");
        for (int i = 0; i < pila.size(); i++) {
            System.out.print(pila.get(i).getKey() + " ");
        }
        System.out.println();
        System.out.print("\tPila Auxiliar: ");
        for (int j = 0; j < pilaAux.size(); j++) {
            System.out.print(pilaAux.get(j).getKey() + "[");
            for (Map.Entry<String, String> k : pilaAux.get(j).getAtributos().entrySet()) {
                System.out.print(k.getKey() + ": " + k.getValue());
            }
            System.out.print("]");
        }
        System.out.println();
        System.out.println();
    }

    /* Cerrar tokens.txt y errores.txt: */
    public void cerrarArchivos() {
        try {
            err.close();
            aL.fileReader.close();
        } catch (Exception e) {
        }
    }

    /* Imprimir y cerrar parse.txt: */
    public void imprimirParse() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("Ficheros/parse.txt");
            pw = new PrintWriter(fichero);
            pw.println(parse);
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

    /* GCI */

    /* Imprimir y cerrar cuartetos.txt */
    public void imprimirCuartetos() {
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("Ficheros/cuartetos.txt");
            pw = new PrintWriter(fichero);
            for (int i = 0; i < cuartetos.size(); i++) {
                pw.println(cuartetos.get(i));
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

    /* Generador de código objeto */
    public void obtenerCO(String op, String arg1, String arg2, String res) {
        /* SWITCH - ENSAMBLADOR */
        String[] dircArg1;
        String[] dircArg2;
        String[] dircRes;
        String dArg1;
        String dArg2;
        String dRes;
        String etiq;
        int id;
        switch (op) {
            case "goto":
                ensamblador.writeEnsamblador("BR /" + res);
                break;
            case ":":
                ensamblador.writeEnsamblador(arg1 + ":");
                break;
            case "if=":
                id = Integer.parseInt(arg1);
                dircArg1 = this.listaTS.buscarLugarTS(id).split("-");
                dArg2 = "#" + arg2;
                if (dircArg1[0].equals("0")) {
                    dArg1 = ".IY";
                } else {
                    dArg1 = ".IX";
                }
                ensamblador.writeEnsamblador("ADD " + dArg1 + ", #" + dircArg1[1]);
                ensamblador.writeEnsamblador("CMP [.A], " + dArg2);
                ensamblador.writeEnsamblador("BZ /" + res);
                break;
            case "if<":
                id = Integer.parseInt(arg1);
                dircArg1 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(arg2);
                dircArg2 = this.listaTS.buscarLugarTS(id).split("-");
                if (dircArg1[0].equals("0")) {
                    dArg1 = ".IY";
                } else {
                    dArg1 = ".IX";
                }
                if (dircArg2[0].equals("0")) {
                    dArg2 = ".IY";
                } else {
                    dArg2 = ".IX";
                }
                ensamblador.writeEnsamblador("ADD " + dArg1 + ", #" + dircArg1[1]);
                ensamblador.writeEnsamblador("MOVE [.A], .R0");
                ensamblador.writeEnsamblador("ADD " + dArg2 + ", #" + dircArg2[1]);
                ensamblador.writeEnsamblador("CMP .R0, [.A]");
                ensamblador.writeEnsamblador("BN /" + res);
                break;
            case "+":
                id = Integer.parseInt(arg1);
                dircArg1 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(arg2);
                dircArg2 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(res);
                dircRes = this.listaTS.buscarLugarTS(id).split("-");
                if (dircArg1[0].equals("0")) {
                    dArg1 = ".IY";
                } else {
                    dArg1 = ".IX";
                }
                if (dircArg2[0].equals("0")) {
                    dArg2 = ".IY";
                } else {
                    dArg2 = ".IX";
                }
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                ensamblador.writeEnsamblador("ADD " + dArg1 + ", #" + dircArg1[1]);
                ensamblador.writeEnsamblador("MOVE [.A], .R0");
                ensamblador.writeEnsamblador("ADD " + dArg2 + ", #" + dircArg2[1]);
                ensamblador.writeEnsamblador("ADD [.A], .R0");
                ensamblador.writeEnsamblador("MOVE .A, .R1");
                ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                ensamblador.writeEnsamblador("MOVE .R1, [.A]");
                break;
            case "&&":
                id = Integer.parseInt(arg1);
                dircArg1 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(arg2);
                dircArg2 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(res);
                dircRes = this.listaTS.buscarLugarTS(id).split("-");
                if (dircArg1[0].equals("0")) {
                    dArg1 = ".IY";
                } else {
                    dArg1 = ".IX";
                }
                if (dircArg2[0].equals("0")) {
                    dArg2 = ".IY";
                } else {
                    dArg2 = ".IX";
                }
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                ensamblador.writeEnsamblador("MOVE .A, .R1");
                ensamblador.writeEnsamblador("ADD " + dArg1 + ", #" + dircArg1[1]);
                ensamblador.writeEnsamblador("MOVE [.A], .R0");
                ensamblador.writeEnsamblador("ADD " + dArg2 + ", #" + dircArg2[1]);
                ensamblador.writeEnsamblador("AND [.A], .R0");
                ensamblador.writeEnsamblador("MOVE .A, [.R1]");
                break;
            case "%":
                id = Integer.parseInt(arg1);
                dircArg1 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(arg2);
                dircArg2 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(res);
                dircRes = this.listaTS.buscarLugarTS(id).split("-");
                if (dircArg1[0].equals("0")) {
                    dArg1 = ".IY";
                } else {
                    dArg1 = ".IX";
                }
                if (dircArg2[0].equals("0")) {
                    dArg2 = ".IY";
                } else {
                    dArg2 = ".IX";
                }
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                ensamblador.writeEnsamblador("ADD " + dArg1 + ", #" + dircArg1[1]);
                ensamblador.writeEnsamblador("MOVE [.A], .R0");
                ensamblador.writeEnsamblador("ADD " + dArg2 + ", #" + dircArg2[1]);
                ensamblador.writeEnsamblador("MOD .R0, [.A]");
                ensamblador.writeEnsamblador("MOVE .A, .R1");
                ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                ensamblador.writeEnsamblador("MOVE .R1, [.A]");
                break;
            case ":=":
                id = Integer.parseInt(res);
                dircRes = this.listaTS.buscarLugarTS(id).split("-");
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                ensamblador.writeEnsamblador("MOVE #" + arg1 + ", [.A]");
                break;
            case ":=c":
                id = Integer.parseInt(res);
                dircRes = this.listaTS.buscarLugarTS(id).split("-");
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                lista_cadenas.put("cad" + cadenas, arg1);
                ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                ensamblador.writeEnsamblador("MOVE #cad" + cadenas + ", [.A]");
                cadenas++;
                break;
            case "=":
                id = Integer.parseInt(arg1);
                dircArg1 = this.listaTS.buscarLugarTS(id).split("-");
                id = Integer.parseInt(res);
                dircRes = this.listaTS.buscarLugarTS(id).split("-");
                if (dircArg1[0].equals("0")) {
                    dArg1 = ".IY";
                } else {
                    dArg1 = ".IX";
                }
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                if (listaTS.getTipoID(id).equals("cadena")) {
                    copia_cadena(arg1, res);
                } else {
                    ensamblador.writeEnsamblador("ADD " + dArg1 + ", #" + dircArg1[1]);
                    ensamblador.writeEnsamblador("MOVE [.A], .R0");
                    ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                    ensamblador.writeEnsamblador("MOVE .R0, [.A]");
                }
                break;
            case "print":
                id = Integer.parseInt(res);
                dircRes = listaTS.buscarLugarTS(id).split("-");
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                if (listaTS.getTipoID(id).equals("entero")) {
                    ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                    ensamblador.writeEnsamblador("MOVE .A, .R1");
                    ensamblador.writeEnsamblador("WRINT [.R1]");
                } else if (listaTS.getTipoID(id).equals("cadena")) {
                    if (listaTS.esTemporalCadena(id)) {
                        ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                        ensamblador.writeEnsamblador("MOVE [.A], .R1");
                        ensamblador.writeEnsamblador("WRSTR [.R1]");
                    } else {
                        ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                        ensamblador.writeEnsamblador("MOVE .A, .R1");
                        ensamblador.writeEnsamblador("WRSTR [.R1]");
                    }
                }
                break;
            case "input":
                id = Integer.parseInt(res);
                dircRes = listaTS.buscarLugarTS(id).split("-");
                String tipo = listaTS.getTipoID(id);
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                if (tipo.equals("entero")) {
                    ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                    ensamblador.writeEnsamblador("MOVE .A, .R1");
                    ensamblador.writeEnsamblador("ININT[.R1]");
                } else if (tipo.equals("cadena")) {
                    if (listaTS.esTemporalCadena(id)) {
                        ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                        ensamblador.writeEnsamblador("MOVE [.A], .R1");
                        ensamblador.writeEnsamblador("INSTR [.R1]");
                    } else {
                        ensamblador.writeEnsamblador("ADD " + dRes + ", #" + dircRes[1]);
                        ensamblador.writeEnsamblador("MOVE .A, .R1");
                        ensamblador.writeEnsamblador("INSTR [.R1]");
                    }
                }
                break;
            case "param":
                id = Integer.parseInt(res);
                dircRes = this.listaTS.buscarLugarTS(id).split("-");
                if (dircRes[0].equals("0")) {
                    dRes = ".IY";
                } else {
                    dRes = ".IX";
                }
                if (!this.listaTS.getRA_Actual().equals("main")) {
                    ensamblador.writeEnsamblador("ADD #TAM_RA_" + this.listaTS.getRA_Actual() + ", .IX");
                } else {
                    ensamblador.writeEnsamblador("ADD #0, .IX");
                }
                // .R0 apunta al parámetro en el nuevo RA
                ensamblador.writeEnsamblador("ADD #" + arg1 + ", .A");
                ensamblador.writeEnsamblador("MOVE .A, .R0");
                if (listaTS.getTipoID(id).equals("cadena")) {
                    if (listaTS.esTemporalCadena(id)) {
                        ensamblador.writeEnsamblador("ADD #" + dircRes[1] + ", " + dRes);
                        ensamblador.writeEnsamblador("MOVE [.A], .A");
                    } else {
                        ensamblador.writeEnsamblador("ADD #" + dircRes[1] + ", " + dRes);
                    }
                    ensamblador.writeEnsamblador("MOVE .A, .R7");
                    ensamblador.writeEnsamblador("MOVE .R0, .R6");
                    String etiq_bucle = "etiq" + etiquetas;
                    etiquetas++;
                    String etiq_fin = "etiq" + etiquetas;
                    etiquetas++;
                    ensamblador.writeEnsamblador(etiq_bucle + ":");
                    ensamblador.writeEnsamblador("CMP [.R7], #0");
                    ensamblador.writeEnsamblador("BZ /" + etiq_fin);
                    ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
                    ensamblador.writeEnsamblador("INC .R6");
                    ensamblador.writeEnsamblador("INC .R7");
                    ensamblador.writeEnsamblador("BR /" + etiq_bucle);
                    ensamblador.writeEnsamblador(etiq_fin + ":");
                    ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
                } else {
                    ensamblador.writeEnsamblador("ADD #" + dircRes[1] + ", " + dRes);
                    ensamblador.writeEnsamblador("MOVE [.A], [.R0]");
                }
                break;
            case "call":
                id = Integer.parseInt(arg1);
                etiq = listaTS.getEtiq(id);
                int despVD = listaTS.getDespRetorno(id);
                if (!this.listaTS.getRA_Actual().equals("main")) {
                    ensamblador.writeEnsamblador("ADD #TAM_RA_" + this.listaTS.getRA_Actual() + ", .IX");
                    ensamblador.writeEnsamblador("MOVE #dir_ret" + ret + ", [.A]");
                    ensamblador.writeEnsamblador("ADD #TAM_RA_" + this.listaTS.getRA_Actual() + ", .IX");
                    ensamblador.writeEnsamblador("MOVE .A, .IX");
                } else {
                    ensamblador.writeEnsamblador(
                            "MOVE #dir_ret" + ret + ", #0[.IX]");
                }
                ensamblador.writeEnsamblador("BR /" + etiq);
                ensamblador.writeEnsamblador("dir_ret" + ret + ":");
                ret++;
                if (!res.isEmpty()) {
                    int idRes = Integer.parseInt(res);
                    dircRes = this.listaTS.buscarLugarTS(idRes).split("-");
                    this.listaTS.getTipoID(id);
                    ensamblador.writeEnsamblador("SUB #TAM_RA_" + etiq + ", #" + despVD);
                    ensamblador.writeEnsamblador("ADD .A, .IX");
                    // ensamblador.writeEnsamblador("MOVE [.A], .R9"); // .R9 apunta a VD
                    ensamblador.writeEnsamblador("MOVE .A, .R9"); // .R9 apunta a dirección VD
                    // Reseteamos puntero .IX:
                    if (!this.listaTS.getRA_Actual().equals("main")) {
                        ensamblador.writeEnsamblador("SUB .IX, #TAM_RA_" + this.listaTS.getRA_Actual());
                        ensamblador.writeEnsamblador("MOVE .A, .IX");
                    }
                    // VD -> temporal
                    if (dircRes[0].equals("0")) {
                        dRes = ".IY";
                    } else {
                        dRes = ".IX";
                    }
                    ensamblador.writeEnsamblador("ADD #" + dircRes[1] + ", " + dRes);
                    if (this.listaTS.getTipoID(idRes).equals("cadena")) {
                        if (listaTS.esTemporalCadena(id)) {
                            ensamblador.writeEnsamblador("MOVE [.A], .A");
                        }
                        ensamblador.writeEnsamblador("MOVE .A, .R6");
                        ensamblador.writeEnsamblador("MOVE .R9, .R7");
                        String etiq_bucle = "etiq" + etiquetas;
                        etiquetas++;
                        String etiq_fin = "etiq" + etiquetas;
                        etiquetas++;
                        ensamblador.writeEnsamblador(etiq_bucle + ":");
                        ensamblador.writeEnsamblador("CMP [.R7], #0");
                        ensamblador.writeEnsamblador("BZ /" + etiq_fin);
                        ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
                        ensamblador.writeEnsamblador("INC .R6");
                        ensamblador.writeEnsamblador("INC .R7");
                        ensamblador.writeEnsamblador("BR /" + etiq_bucle);
                        ensamblador.writeEnsamblador(etiq_fin + ":");
                        ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
                    } else {
                        ensamblador.writeEnsamblador("MOVE [.R9], [.A]");
                    }
                } else {
                    // Reseteamos puntero .IX:
                    if (!this.listaTS.getRA_Actual().equals("main")) {
                        ensamblador.writeEnsamblador("SUB .IX, #TAM_RA_" + this.listaTS.getRA_Actual());
                        ensamblador.writeEnsamblador("MOVE .A, .IX");
                    }
                }
                break;
            case "return":
                if (!arg1.isEmpty()) {
                    id = Integer.parseInt(arg1);
                    dircArg1 = this.listaTS.buscarLugarTS(id).split("-");
                    if (dircArg1[0].equals("0")) {
                        dArg1 = ".IY";
                    } else {
                        dArg1 = ".IX";
                    }
                    tipo = this.listaTS.getTipoID(id);
                    int desp = 1;
                    if (tipo.equals("cadena")) {
                        desp = 65;
                        ensamblador.writeEnsamblador("SUB #TAM_RA_" + this.listaTS.getRA_Actual() + ", #" + desp);
                        ensamblador.writeEnsamblador("ADD .A, .IX");
                        ensamblador.writeEnsamblador("MOVE .A, .R9"); // R9 apunta a VD
                        ensamblador.writeEnsamblador("MOVE .R9, .R6");
                        ensamblador.writeEnsamblador("ADD #" + dircArg1[1] + ", " + dArg1);
                        ensamblador.writeEnsamblador("MOVE .A, .R7");
                        String etiq_bucle = "etiq" + etiquetas;
                        etiquetas++;
                        String etiq_fin = "etiq" + etiquetas;
                        etiquetas++;
                        ensamblador.writeEnsamblador(etiq_bucle + ":");
                        ensamblador.writeEnsamblador("CMP [.R7], #0");
                        ensamblador.writeEnsamblador("BZ /" + etiq_fin);
                        ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
                        ensamblador.writeEnsamblador("INC .R6");
                        ensamblador.writeEnsamblador("INC .R7");
                        ensamblador.writeEnsamblador("BR /" + etiq_bucle);
                        ensamblador.writeEnsamblador(etiq_fin + ":");
                        ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
                    } else {
                        ensamblador.writeEnsamblador("SUB #TAM_RA_" + this.listaTS.getRA_Actual() + ", #" + desp);
                        ensamblador.writeEnsamblador("ADD .A, .IX");
                        ensamblador.writeEnsamblador("MOVE .A, .R9"); // R9 apunta a VD
                        ensamblador.writeEnsamblador("ADD #" + dircArg1[1] + ", " + dArg1);
                        ensamblador.writeEnsamblador("MOVE [.A], [.R9]");
                    }
                }
                ensamblador.writeEnsamblador("BR [.IX]");
                break;
            default:
                ensamblador.writeEnsamblador("Error: cuarteto no identificado");
                break;
        }
    }

    public void copia_cadena(String arg1, String res) {
        String etiq_bucle = "etiq" + etiquetas;
        etiquetas++;
        String etiq_fin = "etiq" + etiquetas;
        etiquetas++;
        /* OP1 */
        int idArg1 = Integer.parseInt(arg1);
        String[] dircArg1 = this.listaTS.buscarLugarTS(idArg1).split("-");
        String aux1;
        if (dircArg1[0].equals("0")) {
            aux1 = ".IY";
        } else {
            aux1 = ".IX";
        }
        if (this.listaTS.esTemporalCadena(idArg1)) {
            ensamblador.writeEnsamblador("ADD " + aux1 + ", #" + dircArg1[1]);
            ensamblador.writeEnsamblador("MOVE [.A]" + ", .R7");

        } else {
            ensamblador.writeEnsamblador("ADD " + aux1 + ", #" + dircArg1[1]);
            ensamblador.writeEnsamblador("MOVE .A" + ", .R7");
        }
        /* OP2 */
        int idRes = Integer.parseInt(res);
        String[] dircRes = this.listaTS.buscarLugarTS(idRes).split("-");
        String aux2;
        if (dircRes[0].equals("0")) {
            aux2 = ".IY";
        } else {
            aux2 = ".IX";
        }
        if (this.listaTS.esTemporalCadena(idRes)) {
            ensamblador.writeEnsamblador("ADD " + aux2 + ", #" + dircRes[1]);
            ensamblador.writeEnsamblador("MOVE [.A]" + ", .R7");
        } else {
            ensamblador.writeEnsamblador("ADD " + aux2 + ", #" + dircRes[1]);
            ensamblador.writeEnsamblador("MOVE .A" + ", .R6");
        }
        ensamblador.writeEnsamblador(etiq_bucle + ":");
        ensamblador.writeEnsamblador("CMP [.R7], #0");
        ensamblador.writeEnsamblador("BZ /" + etiq_fin);
        ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
        ensamblador.writeEnsamblador("INC .R6");
        ensamblador.writeEnsamblador("INC .R7");
        ensamblador.writeEnsamblador("BR /" + etiq_bucle);
        ensamblador.writeEnsamblador(etiq_fin + ":");
        ensamblador.writeEnsamblador("MOVE [.R7], [.R6]");
    }

    public void imprimirCO_final() {
        ensamblador.close();
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter("Ficheros/ensamblador.txt");
            pw = new PrintWriter(fichero);
            pw.println("ORG 0");
            pw.println("MOVE #PILA, .IX");
            pw.println("MOVE .IX, .SP");
            pw.println("MOVE #DE, .IY");
            pw.println("BR /main");
            // Código de funciones:
            FileReader fileReader = new FileReader("Ficheros/ensambladorFun.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linea;
            while ((linea = bufferedReader.readLine()) != null) {
                pw.println(linea);
            }
            fileReader.close();
            bufferedReader.close();
            pw.println("main: NOP");
            // Código de main:
            FileReader fileReader2 = new FileReader("Ficheros/ensambladorMain.txt");
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
            String linea2;
            while ((linea2 = bufferedReader2.readLine()) != null) {
                pw.println(linea2);
            }
            fileReader2.close();
            bufferedReader2.close();
            pw.println("HALT");
            pw.println("TAM_RA_main: EQU " + listaTS.getTAM_DE());
            for (Map.Entry<String, Integer> entry : tamRA_fun.entrySet()) {
                pw.println("TAM_RA_" + entry.getKey() + ": EQU " + entry.getValue());
            }
            for (Map.Entry<String, String> entry : lista_cadenas.entrySet()) {
                pw.println(entry.getKey() + ":");
                pw.println("DATA " + entry.getValue());
            }
            pw.println("DE: RES " + listaTS.getTAM_DE());
            pw.println("PILA: NOP");
            pw.println("END");
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
