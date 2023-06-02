package Analizadores;

public class Cuarteto {

    private String op;
    private String p1;
    private String p2;
    private String res;

    public Cuarteto(String op, String p1, String p2, String res) {
        this.op = op;
        this.p1 = p1;
        this.p2 = p2;
        this.res = res;
    }

    public String toString() {
        String cuarteto = "(";
        cuarteto += getOp() + ", " + getP1() + ", " + getP2() + ", " + getRes() + ")";
        return cuarteto;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP1() {
        return p1;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public String getP2() {
        return p2;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getRes() {
        return res;
    }
}
