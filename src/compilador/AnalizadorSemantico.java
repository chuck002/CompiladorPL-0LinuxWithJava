/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

public class AnalizadorSemantico {

    private IdentificadorBean[] tabla;

    public AnalizadorSemantico(IndicadorDeErrores indicadorDeErrores) {
        tabla = new IdentificadorBean[Constantes.MAXIDENT];
    }

    public IdentificadorBean buscar(String nombre, int desde, int hasta) {
        IdentificadorBean ident = null;
        int i = desde;
        while(ident == null && hasta <= i){
            if(tabla[i].getNombre().equalsIgnoreCase(nombre)){
                ident = tabla[i];
            }
            i--;
        }
        // TO DO: implementar algoritmo de busqueda (retrocediendo)
        return ident;
    }

    public void cargar(int pos, String nombre, Terminal tipo, int valor) {
        tabla[pos] = new IdentificadorBean(nombre, tipo, valor);
    }

    public void agregarValor(int i, int v) {
        tabla [i].setValor(v);
    }

    public IdentificadorBean[] getTabla() {
        return tabla;
    }
    

}
