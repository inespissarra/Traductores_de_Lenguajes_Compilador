package TablaDeSimbolos;

import java.util.ArrayList;

public class ParametrosTS {

    public int index;
    public String lexema;
    public String tipo;
    public int desp;

    /* Funciones: */
    public String retorno;
    public int numParams;
    public ArrayList<String> params;
    public String etiqueta;
    public boolean tCadena;

    public ParametrosTS(String lexema, int index) {
        this.index = index;
        this.lexema = lexema;
        this.tipo = null;
        this.desp = -1;
        this.retorno = null;
        this.numParams = 0;
        this.params = new ArrayList<String>();
        this.etiqueta = null;
        this.tCadena = false;
    }

    public int getIndex() {
        return this.index;
    }

    public String getLexema() {
        return this.lexema;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setDesp(int desp) {
        this.desp = desp;
    }

    public Integer getDesp() {
        return desp;
    }

    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }

    public String getRetorno() {
        return retorno;
    }

    public void setNumParams(int numParams) {
        this.numParams = numParams;
    }

    public int getNumParams() {
        return numParams;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setTCadena(boolean tCadena) {
        this.tCadena = tCadena;
    }

    public boolean getTCadena() {
        return tCadena;
    }

    public String imprimirAtributos() {
        String resultado = "*'" + this.lexema + "'\n";
        if (this.tipo != null) {
            resultado += "+Tipo:'" + this.tipo + "'\n";
        }
        if (this.desp != -1) {
            resultado += "+Despl:'" + this.desp + "'\n";
        }
        if (this.numParams != 0) {
            resultado += "+numParam:'" + this.numParams + "'\n";
        }
        if (!this.params.isEmpty()) {
            for (int i = 0; i < this.params.size(); i++) {
                resultado += "+TipoParam" + (i + 1) + ":'" + this.params.get(i) + "'\n";
            }
        }
        if (this.retorno != null) {
            if (!this.retorno.equals("vacio"))
                resultado += "+TipoRetorno:'" + this.retorno + "'\n";
        }
        if (this.etiqueta != null) {
            resultado += "+EtiqFuncion:'" + this.etiqueta + "'\n";
        }

        return resultado;
    }

    public String imprimirCabecera() {
        String resultado = this.lexema + "(";
        if (!this.params.isEmpty()) {
            for (int i = 0; i < this.params.size(); i++) {
                if (i != this.params.size() - 1) {
                    resultado += this.params.get(i) + ",";
                } else {
                    resultado += this.params.get(i);
                }
            }
        }
        return resultado + ")";
    }
}
