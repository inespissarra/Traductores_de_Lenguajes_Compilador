package Analizadores;

public class Token {

    public String id;
    public String lexema;

    public Token(String id, String lexema) {
        this.id = id;
        this.lexema = lexema;
    }

    public String getId() {
        return id;
    }

    public String getLexema() {
        return lexema;
    }
    
}
