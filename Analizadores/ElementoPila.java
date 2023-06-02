package Analizadores;

import java.util.HashMap;
import java.util.Map;

public class ElementoPila {

    public String key;
    public Map<String, String> atributos;

    public ElementoPila(String key, String atributo) {
        this.key = key;
        this.atributos = new HashMap<String, String>();
        String[] attr = atributo.split("_");
        if (attr.length == 2) {
            this.atributos.put(attr[0], attr[1]);
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAtributo(String atributo) {
        String[] attr = atributo.split("_");
        this.atributos.put(attr[0], attr[1]);
    }

    public String getKey() {
        return this.key;
    }

    public Map<String, String> getAtributos() {
        return this.atributos;
    }
}
