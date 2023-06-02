package TablaDeSimbolos;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ListaTS {

    /* Tablas de Simbolos: */
    Map<Integer, TablaDeSimbolos> listaTS;
    int posTablaActual;

    /* Desplazamiento de Tablas de Simbolos: */
    LinkedHashMap<Integer, Integer> despTablas;
    
    /* Variables especiales: */
    public boolean zonaDeclaracion = false;
    int posID = 0;
    String RA_actual = "";

    /* Archivos de texto(Tabla de simbolos): */
    FileWriter fileTS;
    PrintWriter pwTS;
    ArrayList<TablaDeSimbolos> imprimirTS;

    public ListaTS(String pathTS) {
        this.listaTS = new HashMap<Integer, TablaDeSimbolos>();
        this.posTablaActual = -1;
        despTablas = new LinkedHashMap<Integer, Integer>();
        try {
            fileTS = new FileWriter(pathTS);
            pwTS = new PrintWriter(fileTS);
        } catch (Exception e) {
        }
        imprimirTS = new ArrayList<TablaDeSimbolos>();
    }

    public void crearTabla() {
        TablaDeSimbolos nuevaTS = new TablaDeSimbolos();
        posTablaActual++;
        if(posTablaActual == 0) {
            RA_actual = "main";
        }
        this.listaTS.put(posTablaActual, nuevaTS);
        this.despTablas.put(posTablaActual, 0);
        imprimirTS.add(nuevaTS);
    }

    public void borrarTabla() {
        this.listaTS.remove(posTablaActual);
        this.despTablas.remove(posTablaActual);
        posTablaActual--;
        RA_actual = "main";
        if (posTablaActual == -1) {
            writeTS();
        }
    }

    public void writeTS() {
        for (int i = 0; i < imprimirTS.size(); i++) {
            pwTS.println("#" + i + ":");
            Map<String, ParametrosTS> writeTS = imprimirTS.get(i).getTabla();
            for (Map.Entry<String, ParametrosTS> ts : writeTS.entrySet()) {
                pwTS.print(ts.getValue().imprimirAtributos());
            }
        }
        if (posTablaActual == -1) {
            try {
                fileTS.close();
            } catch (Exception e) {
            }
        }
    }

    public int anadirLexema(String lexema) {
        int pos = -1;
        // ZonaDeclaracion: true
        if (zonaDeclaracion) {
            TablaDeSimbolos tsActual = this.listaTS.get(posTablaActual);
            if (tsActual.buscarLexema(lexema) != null) {
                return tsActual.buscarLexema(lexema).getIndex();
            } else {
                ParametrosTS params = new ParametrosTS(lexema, posID);
                tsActual.getTabla().put(lexema, params);
            }
            return posID++;
            // ZonaDeclaracion: false
        } else {
            boolean encontrado = false;
            for (int i = posTablaActual; i >= 0; i--) {
                TablaDeSimbolos tsActual = this.listaTS.get(i);
                if (tsActual.buscarLexema(lexema) != null) {
                    encontrado = true;
                    pos = tsActual.getTabla().get(lexema).getIndex();
                    break;
                }
            }
            // Si la variable no existe, se crea como global y entera:
            if (!encontrado) {
                ParametrosTS params = new ParametrosTS(lexema, posID);
                params.setTipo("entero");
                params.setDesp(despTablas.get(0));
                int nuevoDesp = despTablas.get(0) + 1;
                despTablas.put(0, nuevoDesp);
                this.listaTS.get(0).getTabla().put(lexema, params);
                return posID++;
            }
        }
        return pos;
    }

    public void anadirTipoYDespl(int id, String tipo) {
        ParametrosTS fila = listaTS.get(posTablaActual).buscarID(id);
        fila.setTipo(tipo);
        fila.setDesp(despTablas.get(posTablaActual));
        if (tipo.equals("entero")) {
            int nuevoDesp = despTablas.get(posTablaActual) + 1;
            despTablas.put(posTablaActual, nuevoDesp);
        } else if (tipo.equals("cadena")) {
            int nuevoDesp = despTablas.get(posTablaActual) + 65;
            despTablas.put(posTablaActual, nuevoDesp);
        } else if (tipo.equals("logico")) {
            int nuevoDesp = despTablas.get(posTablaActual) + 1;
            despTablas.put(posTablaActual, nuevoDesp);
        }
    }

    public void anadirDatosFun(int id, String tipo, String params) {
        ParametrosTS fila = listaTS.get(0).buscarID(id);
        fila.setTipo("funcion");
        fila.setRetorno(tipo);
        String[] param = params.split(",");
        if (!param[0].equals("vacio")) {
            for (int i = 0; i < param.length; i++) {
                fila.getParams().add(param[i]);
            }
            fila.setNumParams(param.length);
        }
        fila.setEtiqueta("Et" + fila.lexema);
    }

    public String getTipoDecl(int id) {
        return this.listaTS.get(posTablaActual).buscarID(id).getTipo();
    }

    public String getTipoID(int id) {
        ParametrosTS fila = null;
        for (int i = posTablaActual; i >= 0; i--) {
            TablaDeSimbolos tsActual = this.listaTS.get(i);
            if ((fila = tsActual.buscarID(id)) != null) {
                return fila.getTipo();
            }
        }
        return "error";
    }

    public String getValorRet(int id) {
        ParametrosTS fila = null;
        fila = this.listaTS.get(0).buscarID(id);
        if (fila.getRetorno() != null) {
            return fila.getRetorno();
        } else {
            return "error";
        }
    }

    public boolean compararNumParams(int id, String params) {
        String[] param = params.split(",");
        int numParams = param.length;
        ArrayList<String> idParams = listaTS.get(0).buscarID(id).getParams();
        if(param[0].equals("vacio")) {
           numParams--;
        }
        return idParams.size() == numParams;
    }

    public boolean compararParams(int id, String params) {
        String[] param = params.split(",");
        boolean iguales = true;
        ArrayList<String> idParams = listaTS.get(0).buscarID(id).getParams();
        for (int i = 0; i < idParams.size(); i++) {
            if (!idParams.get(i).equals(param[i])) {
                iguales = false;
            }
        }
        return iguales;
    }

    public String getCabecera(int id) {
        return listaTS.get(0).buscarID(id).imprimirCabecera();
    }

    public String getEtiq(int id) {
        return listaTS.get(0).buscarID(id).getEtiqueta();
    }

    public String getNombreID(int id) {
        String resultado = "";
        for (int i = posTablaActual; i >= 0; i--) {
            TablaDeSimbolos tsActual = this.listaTS.get(i);
            if (tsActual.buscarID(id) != null) {
                resultado += tsActual.buscarID(id).getLexema();
                break;
            }
        }
        return resultado;
    }

     /* GCI */

     public int anadirTemporal(String temporal, String tipo) {
        TablaDeSimbolos tsActual = this.listaTS.get(posTablaActual);
        ParametrosTS params = new ParametrosTS(temporal, posID);
        params.setTipo(tipo);
        int desp = despTablas.get(posTablaActual);
        params.setDesp(desp);
        if (tipo.equals("entero")) {
            int nuevoDesp = despTablas.get(posTablaActual) + 1;
            despTablas.put(posTablaActual, nuevoDesp);
        } else if (tipo.equals("cadena")) {
            int nuevoDesp = despTablas.get(posTablaActual) + 65;
            despTablas.put(posTablaActual, nuevoDesp);
        } else if (tipo.equals("logico")) {
            int nuevoDesp = despTablas.get(posTablaActual) + 1;
            despTablas.put(posTablaActual, nuevoDesp);
        }
        tsActual.getTabla().put(temporal, params);
        return posID++;
    }

    public String buscarLugarTS(int id) {
        String res = "";
        for (int i = posTablaActual; i >= 0; i--) {
            TablaDeSimbolos tsActual = this.listaTS.get(i);
            if (tsActual.buscarID(id) != null) {
                // 0 para globales, 1 para locales
                // Al Desplazamiento le sumamos + 1 debido a EM(dr)
                if (i == 0) {
                    res = "0-" + tsActual.buscarID(id).getDesp();
                } else {
                    res = "1-" + (tsActual.buscarID(id).getDesp()+1);
                }
                break;
            }
        }
        return res;
    }

    public void anadirTemporalCadena(int id) {
        for (int i = posTablaActual; i >= 0; i--) {
            TablaDeSimbolos tsActual = this.listaTS.get(i);
            if (tsActual.buscarID(id) != null) {
                tsActual.buscarID(id).setTCadena(true);
                break;
            }
        }
    }

    public boolean esTemporalCadena(int id) {
        boolean res = false;
        for (int i = posTablaActual; i >= 0; i--) {
            TablaDeSimbolos tsActual = this.listaTS.get(i);
            if (tsActual.buscarID(id) != null) {
                if(tsActual.buscarID(id).getTCadena()) {
                    res = true;
                }
                break;
            }
        }
        return res;
    }

    public int getDespParam(int id) {
        int res = 0;
        for (int i = posTablaActual; i >= 0; i--) {
            TablaDeSimbolos tsActual = this.listaTS.get(i);
            if (tsActual.buscarID(id) != null) {
                String tipo = tsActual.buscarID(id).getTipo();
                if (tipo.equals("entero")) {
                    res = res + 1;
                } else if (tipo.equals("cadena")) {
                    res = res + 65;
                } else if (tipo.equals("logico")) {
                    res = res + 1;
                }
                break;
            }
        }
        return res;
    }

    public int getDespRetorno(int idFun) {
        int res = 0;
        String tipo = getValorRet(idFun);
        if (tipo.equals("entero")) {
            res = res + 1;
        } else if (tipo.equals("cadena")) {
            res = res + 65;
        } else if (tipo.equals("logico")) {
            res = res + 1;
        }
        return res;
    }

    public int getTAM_RA_actual() {
        return this.despTablas.get(posTablaActual);
    }

    public int getTAM_DE() {
        return this.despTablas.get(0);
    }

    public void setRA_actual(int id) {
        RA_actual = listaTS.get(0).buscarID(id).getEtiqueta();
        System.out.println("RA actual: " + RA_actual);
    }

    public String getRA_Actual() {
        return RA_actual;
    }
}
