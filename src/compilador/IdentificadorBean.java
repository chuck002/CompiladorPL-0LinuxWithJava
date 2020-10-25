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
public class IdentificadorBean {

    private String nombre;
    private Terminal tipo;
    private int valor;

    public IdentificadorBean(String nombre, Terminal tipo, int valor) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    public IdentificadorBean() {
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the tipo
     */
    public Terminal getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(Terminal tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the valor
     */
    public int getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(int valor) {
        this.valor = valor;
    }

}
