/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author Raquel
 */
public class IndicadorDeErrores {

    void mostrar(int i) {
        if (i != 0) {
            System.err.print("ERROR: ");
        }
        switch (i) {
            case 0:
                System.out.println("Compilacion exitosa");
                break;
            case 1:
                 System.err.println("No se pudo generar el ejecutable");
                 break;
            default:
                System.err.println("");

        }
        System.exit(0);
    }

    void mostrar(int i, String message) {

        System.err.print("ERROR: ");
        switch (i) {
            case 1:
                System.err.println("El archivo no existe (" + message + ")");
                break;
            default:
                System.err.println(message);

        }
        System.exit(0);
    }

    void mostrar(int i, String message, int fila, int columna) {
        fila++;
        columna++;

        System.err.print("ERROR: ");
        switch (i) {
            case 2:
                System.err.println("Se esperaba un punto y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 3:
                System.err.println("Se esperaba un identificador y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 4:
                System.err.println("Se esperaba un punto y coma, y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 5:
                System.err.println("Se esperaba un igual y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 6:
                System.err.println("Se esperaba un numero y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 7:
                System.err.println("Se esperaba una asignacion y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 8:
                System.err.println("Se esperaba un end y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 9:
                System.err.println("Se esperaba un then y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 10:
                System.err.println("Se esperaba un do y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 11:
                System.err.println("Se esperaba un ( y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 12:
                System.err.println("Se esperaba un ) y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 13:
                System.err.println("Se esperaba un operador relacional y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 14:
                System.err.println("Se esperaba un numero, un identificador o un ( y se recibio \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 15:
                System.err.println("Identificador duplicado \"" + message + "\" en " + fila + ":" + columna);
                break;
            case 16:
                System.err.println("Identificador no declarado\"" + message + "\" en " + fila + ":" + columna);
                break;
            case 17:
                System.err.println("El identificador \"" + message + "\" no fue declarado como variable, en " + fila + ":" + columna);
                break;
            case 18:
                System.err.println("El identificador \"" + message + "\" no fue declarado como procedimiento, en " + fila + ":" + columna);
                break;
            case 19:
                System.err.println("El identificador \"" + message + "\" no fue declarado como constante o varaible, en " + fila + ":" + columna);
                break;
            case 20:
                System.err.println("Constante \"" + message + "\" fuera de rango, en " + fila + ":" + columna);
                break;
            default:
                System.err.println(message);

        }
        System.exit(0);
    }

}
