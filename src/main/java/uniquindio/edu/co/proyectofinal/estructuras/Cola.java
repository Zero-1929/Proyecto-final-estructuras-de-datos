package uniquindio.edu.co.proyectofinal.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cola genérica implementada mediante nodos enlazados.
 * Sigue el principio FIFO: primero en entrar, primero en salir.
 *
 * @param <T> tipo de dato almacenado en la cola
 */
public class Cola<T> implements Iterable<T> {

    private NodoCola<T> frente;
    private NodoCola<T> fin;
    private int tamano;

    /**
     * Crea una cola vacía.
     */
    public Cola() {
        frente = null;
        fin = null;
        tamano = 0;
    }

    /**
     * Agrega un elemento al final de la cola.
     *
     * @param dato elemento que se desea agregar
     */
    public void encolar(T dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden agregar elementos nulos a la cola");
        }

        NodoCola<T> nuevo = new NodoCola<>(dato);

        if (estaVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }

        tamano++;
    }

    /**
     * Retira y devuelve el primer elemento de la cola.
     *
     * @return elemento retirado
     * @throws IllegalStateException si la cola está vacía
     */
    public T desencolar() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede desencolar porque la cola está vacía");
        }

        T dato = frente.getDato();
        frente = frente.getSiguiente();

        if (frente == null) {
            fin = null;
        }

        tamano--;

        return dato;
    }

    /**
     * Consulta el primer elemento sin retirarlo.
     *
     * @return elemento ubicado al frente
     * @throws IllegalStateException si la cola está vacía
     */
    public T consultarFrente() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede consultar una cola vacía");
        }

        return frente.getDato();
    }

    /**
     * Indica si la cola está vacía.
     *
     * @return true si no contiene elementos
     */
    public boolean estaVacia() {
        return frente == null;
    }

    /**
     * Obtiene la cantidad de elementos de la cola.
     *
     * @return número de elementos
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Elimina todos los elementos de la cola.
     */
    public void limpiar() {
        frente = null;
        fin = null;
        tamano = 0;
    }

    /**
     * Comprueba si un elemento se encuentra en la cola.
     *
     * @param dato elemento que se desea buscar
     * @return true si el elemento existe
     */
    public boolean contiene(T dato) {

        NodoCola<T> actual = frente;

        while (actual != null) {

            if (actual.getDato().equals(dato)) {
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    /**
     * Permite recorrer los elementos desde el frente hasta el final.
     *
     * @return iterador de la cola
     */
    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {

            private NodoCola<T> actual = frente;

            /**
             * Comprueba si existe otro elemento.
             *
             * @return true si hay otro elemento
             */
            @Override
            public boolean hasNext() {
                return actual != null;
            }

            /**
             * Obtiene el siguiente elemento.
             *
             * @return dato del siguiente nodo
             */
            @Override
            public T next() {

                if (!hasNext()) {
                    throw new NoSuchElementException(
                            "No existen más elementos en la cola");
                }

                T dato = actual.getDato();
                actual = actual.getSiguiente();

                return dato;
            }
        };
    }

    /**
     * Genera una representación textual de la cola.
     *
     * @return elementos desde el frente hasta el final
     */
    @Override
    public String toString() {

        StringBuilder texto = new StringBuilder("Frente -> ");

        NodoCola<T> actual = frente;

        while (actual != null) {

            texto.append(actual.getDato());

            if (actual.getSiguiente() != null) {
                texto.append(" -> ");
            }

            actual = actual.getSiguiente();
        }

        return texto.append(" -> Fin").toString();
    }
}

