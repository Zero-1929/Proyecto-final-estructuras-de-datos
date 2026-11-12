package uniquindio.edu.co.proyectofinal.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista circular simplemente enlazada y genérica.
 *
 * Permite almacenar elementos y recorrerlos circularmente.
 * Se utilizará para administrar la rotación de turnos
 * de los recepcionistas del SGA.
 *
 * @param <T> tipo de elementos almacenados
 */
public class ListaCircular<T> implements Iterable<T> {

    private NodoCircular<T> ultimo;
    private NodoCircular<T> actual;
    private int tamano;

    /**
     * Crea una lista circular vacía.
     */
    public ListaCircular() {
        ultimo = null;
        actual = null;
        tamano = 0;
    }

    /**
     * Agrega un elemento al final de la lista circular.
     *
     * @param dato elemento que se desea agregar
     */
    public void agregar(T dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "No se pueden agregar elementos nulos");
        }

        NodoCircular<T> nuevo = new NodoCircular<>(dato);

        if (estaVacia()) {
            nuevo.setSiguiente(nuevo);
            ultimo = nuevo;
            actual = nuevo;
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }

        tamano++;
    }

    /**
     * Elimina la primera aparición de un elemento.
     *
     * Si el elemento eliminado tiene el turno actual,
     * el turno pasa al siguiente elemento.
     *
     * @param dato elemento que se desea eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminar(T dato) {

        if (estaVacia() || dato == null) {
            return false;
        }

        NodoCircular<T> anterior = ultimo;
        NodoCircular<T> nodo = ultimo.getSiguiente();

        for (int i = 0; i < tamano; i++) {

            if (nodo.getDato().equals(dato)) {

                if (tamano == 1) {
                    ultimo = null;
                    actual = null;
                } else {
                    anterior.setSiguiente(nodo.getSiguiente());

                    if (nodo == ultimo) {
                        ultimo = anterior;
                    }

                    if (nodo == actual) {
                        actual = nodo.getSiguiente();
                    }
                }

                tamano--;
                return true;
            }

            anterior = nodo;
            nodo = nodo.getSiguiente();
        }

        return false;
    }

    /**
     * Consulta el elemento que tiene el turno actual.
     *
     * @return elemento del turno actual
     * @throws IllegalStateException si la lista está vacía
     */
    public T obtenerActual() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No hay elementos en la lista circular");
        }

        return actual.getDato();
    }

    /**
     * Avanza el turno al siguiente elemento.
     *
     * Al llegar al último elemento, vuelve al primero.
     *
     * @return elemento que queda con el turno actual
     * @throws IllegalStateException si la lista está vacía
     */
    public T avanzarTurno() {

        if (estaVacia()) {
            throw new IllegalStateException(
                    "No se puede avanzar el turno: lista vacía");
        }

        actual = actual.getSiguiente();

        return actual.getDato();
    }

    /**
     * Indica si un elemento está contenido en la lista.
     *
     * @param dato elemento que se desea buscar
     * @return true si se encuentra
     */
    public boolean contiene(T dato) {

        if (estaVacia() || dato == null) {
            return false;
        }

        NodoCircular<T> nodo = ultimo.getSiguiente();

        for (int i = 0; i < tamano; i++) {

            if (nodo.getDato().equals(dato)) {
                return true;
            }

            nodo = nodo.getSiguiente();
        }

        return false;
    }

    /**
     * Indica si la lista está vacía.
     *
     * @return true si no tiene elementos
     */
    public boolean estaVacia() {
        return tamano == 0;
    }

    /**
     * Obtiene la cantidad de elementos.
     *
     * @return número de elementos
     */
    public int getTamano() {
        return tamano;
    }

    /**
     * Elimina todos los elementos de la lista.
     */
    public void limpiar() {
        ultimo = null;
        actual = null;
        tamano = 0;
    }

    /**
     * Crea un iterador que recorre cada elemento una sola vez.
     *
     * @return iterador de la lista circular
     */
    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {

            private NodoCircular<T> nodoActual =
                    estaVacia() ? null : ultimo.getSiguiente();

            private int restantes = tamano;

            @Override
            public boolean hasNext() {
                return restantes > 0;
            }

            @Override
            public T next() {

                if (!hasNext()) {
                    throw new NoSuchElementException(
                            "No existen más elementos en la lista");
                }

                T dato = nodoActual.getDato();

                nodoActual = nodoActual.getSiguiente();
                restantes--;

                return dato;
            }
        };
    }

    /**
     * Representa los elementos de la lista circular.
     *
     * @return representación textual de la lista
     */
    @Override
    public String toString() {

        StringBuilder texto = new StringBuilder("Inicio -> ");

        NodoCircular<T> nodo = estaVacia()
                ? null : ultimo.getSiguiente();

        for (int i = 0; i < tamano; i++) {

            texto.append(nodo.getDato());

            if (i < tamano - 1) {
                texto.append(" -> ");
            }

            nodo = nodo.getSiguiente();
        }

        texto.append(" -> (vuelve al inicio)");

        return texto.toString();
    }
}

