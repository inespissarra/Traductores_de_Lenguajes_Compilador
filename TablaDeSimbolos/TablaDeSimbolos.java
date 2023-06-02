package TablaDeSimbolos;

import java.util.LinkedHashMap;
import java.util.Map;

public class TablaDeSimbolos {

    LinkedHashMap<String, ParametrosTS> tablaSimbolos;

    public TablaDeSimbolos() {
        this.tablaSimbolos = new LinkedHashMap<String, ParametrosTS>();
    }

    public Map<String, ParametrosTS> getTabla() {
        return this.tablaSimbolos;
    }

    public ParametrosTS buscarLexema(String lexema) {
        return tablaSimbolos.get(lexema);
    }

    public ParametrosTS buscarEtiqueta(String etiqueta) {
        ParametrosTS fila = null;
        for(Map.Entry<String, ParametrosTS> e: tablaSimbolos.entrySet()) {
            if(etiqueta == e.getValue().getEtiqueta()) {
                fila = e.getValue();
            }
        }
        return fila;
    }

    public ParametrosTS buscarID(int id) {
        ParametrosTS fila = null;
        for(Map.Entry<String, ParametrosTS> e: tablaSimbolos.entrySet()) {
            if(id == e.getValue().getIndex()) {
                fila = e.getValue();
            }
        }
        return fila;
    }
}
