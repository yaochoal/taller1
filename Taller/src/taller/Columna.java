/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taller;

/**
 *
 * @author Zylfrox
 */
public class Columna {
     String nombre;
     int columna;
     int fila;

    public Columna(String nombre, int columna, int fila) {
        this.nombre = nombre;
        this.columna = columna;
        this.fila = fila;
    }

    @Override
    public String toString() {
        return "Columna{" + "nombre=" + nombre + ", columna=" + columna + ", fila=" + fila + '}';
    }
    
}
