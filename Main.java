import Analizadores.AnalizadorSintactico;
import Analizadores.Errores;
import TablaDeSimbolos.ListaTS;

public class Main {

    public static void main(String[] args) {
        String path = "Ficheros/ej2.txt"; /* En el archivo prueba.txt se vuelca el código fuente de Javascript-PDL */
        String pathTS = "Ficheros/tablaSimbolos.txt";
        String pathErr = "Ficheros/errores.txt";
        
        AnalizadorSintactico aS = new AnalizadorSintactico(path, new ListaTS(pathTS), new Errores(pathErr));
        aS.inicializador();
    }
}
