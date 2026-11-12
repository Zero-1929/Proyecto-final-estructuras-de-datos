package uniquindio.edu.co.proyectofinal.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * Conjunto generico implementado mediante un arreglo dinamico.
 *
 * No permite elementos duplicados ni nulos.
 */
public class Conjunto<T> implements Iterable<T> {

    private static final int CAPACIDAD_INICIAL = 10;

    private Object[] elementos;
    private int tamanio;
    private int modificaciones;

    /**
     * Constructor de un conjunto vacio.
     */
    public Conjunto() {
        this.elementos = new Object[CAPACIDAD_INICIAL];
        this.tamanio = 0;
        this.modificaciones = 0;
    }

    /**
     * Agrega un elemento si no existe.
     *
     * @return true si fue agregado, false si ya existia.
     */
    public boolean agregar(T elemento) {

        if (elemento == null) {
            throw new IllegalArgumentException(
                    "El conjunto no permite elementos nulos."
            );
        }

        if (contiene(elemento)) {
            return false;
        }

        asegurarCapacidad();

        elementos[tamanio] = elemento;
        tamanio++;
        modificaciones++;

        return true;
    }

    /**
     * Elimina un elemento del conjunto.
     *
     * @return true si fue eliminado.
     */
    public boolean eliminar(T elemento) {

        int indice = buscarIndice(elemento);

        if (indice == -1) {
            return false;
        }

        for (int i = indice; i < tamanio - 1; i++) {
            elementos[i] = elementos[i + 1];
        }

        tamanio--;
        elementos[tamanio] = null;
        modificaciones++;

        return true;
    }

    /**
     * Comprueba si un elemento pertenece al conjunto.
     */
    public boolean contiene(T elemento) {
        return buscarIndice(elemento) != -1;
    }

    /**
     * Busca el indice de un elemento utilizando equals().
     */
    private int buscarIndice(T elemento) {

        if (elemento == null) {
            return -1;
        }

        for (int i = 0; i < tamanio; i++) {

            if (elementos[i].equals(elemento)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Devuelve la cantidad de elementos.
     */
    public int tamanio() {
        return tamanio;
    }

    /**
     * Comprueba si el conjunto esta vacio.
     */
    public boolean estaVacio() {
        return tamanio == 0;
    }

    /**
     * Elimina todos los elementos.
     */
    public void vaciar() {

        for (int i = 0; i < tamanio; i++) {
            elementos[i] = null;
        }

        tamanio = 0;
        modificaciones++;
    }

    // -------------------- OPERACIONES DE CONJUNTOS --------------------

    /**
     * Union: elementos de ambos conjuntos sin duplicados.
     */
    public Conjunto<T> union(Conjunto<T> otro) {

        validarConjunto(otro);

        Conjunto<T> resultado = new Conjunto<>();

        for (T elemento : this) {
            resultado.agregar(elemento);
        }

        for (T elemento : otro) {
            resultado.agregar(elemento);
        }

        return resultado;
    }

    /**
     * Interseccion: elementos presentes en ambos conjuntos.
     */
    public Conjunto<T> interseccion(Conjunto<T> otro) {

        validarConjunto(otro);

        Conjunto<T> resultado = new Conjunto<>();

        for (T elemento : this) {

            if (otro.contiene(elemento)) {
                resultado.agregar(elemento);
            }
        }

        return resultado;
    }

    /**
     * Diferencia: elementos de este conjunto
     * que no pertenecen al otro.
     */
    public Conjunto<T> diferencia(Conjunto<T> otro) {

        validarConjunto(otro);

        Conjunto<T> resultado = new Conjunto<>();

        for (T elemento : this) {

            if (!otro.contiene(elemento)) {
                resultado.agregar(elemento);
            }
        }

        return resultado;
    }

    /**
     * Comprueba si todos los elementos de este conjunto
     * pertenecen al otro.
     */
    public boolean esSubconjuntoDe(Conjunto<T> otro) {

        validarConjunto(otro);

        for (T elemento : this) {

            if (!otro.contiene(elemento)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Comprueba si dos conjuntos contienen
     * exactamente los mismos elementos.
     */
    public boolean mismosElementos(Conjunto<T> otro) {

        if (otro == null) {
            return false;
        }

        return tamanio == otro.tamanio
                && esSubconjuntoDe(otro);
    }

    private void validarConjunto(Conjunto<T> otro) {

        if (otro == null) {
            throw new IllegalArgumentException(
                    "El conjunto recibido no puede ser nulo."
            );
        }
    }

    // -------------------- ARREGLO DINAMICO --------------------

    private void asegurarCapacidad() {

        if (tamanio < elementos.length) {
            return;
        }

        Object[] nuevo = new Object[elementos.length * 2];

        for (int i = 0; i < tamanio; i++) {
            nuevo[i] = elementos[i];
        }

        elementos = nuevo;
    }

    @SuppressWarnings("unchecked")
    private T obtenerElemento(int indice) {
        return (T) elementos[indice];
    }

    // -------------------- ITERABLE --------------------

    /**
     * Permite recorrer el conjunto utilizando for-each.
     */
    @Override
    public Iterator<T> iterator() {
        return new IteradorConjunto();
    }

    private class IteradorConjunto implements Iterator<T> {

        private int indiceActual = 0;
        private final int modificacionesEsperadas = modificaciones;

        @Override
        public boolean hasNext() {
            verificarModificaciones();
            return indiceActual < tamanio;
        }

        @Override
        public T next() {

            verificarModificaciones();

            if (!hasNext()) {
                throw new NoSuchElementException(
                        "No existen mas elementos."
                );
            }

            return obtenerElemento(indiceActual++);
        }

        private void verificarModificaciones() {

            if (modificacionesEsperadas != modificaciones) {
                throw new ConcurrentModificationException(
                        "El conjunto fue modificado durante el recorrido."
                );
            }
        }
    }
}
