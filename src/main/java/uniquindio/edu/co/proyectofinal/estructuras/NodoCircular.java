package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Representa un nodo de una lista circular simplemente enlazada.
 *
 * @param <T> tipo de dato almacenado en el nodo
 */
public class NodoCircular<T> {

    private T dato;
    private NodoCircular<T> siguiente;

    /**
     * Crea un nodo con el dato recibido.
     *
     * @param dato elemento que se almacenará
     */
    public NodoCircular(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Obtiene el dato almacenado.
     *
     * @return dato del nodo
     */
    public T getDato() {
        return dato;
    }

    /**
     * Modifica el dato almacenado.
     *
     * @param dato nuevo dato
     */
    public void setDato(T dato) {
        this.dato = dato;
    }

    /**
     * Obtiene el siguiente nodo.
     *
     * @return siguiente nodo
     */
    public NodoCircular<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el siguiente nodo.
     *
     * @param siguiente nodo al que se enlazará
     */
    public void setSiguiente(NodoCircular<T> siguiente) {
        this.siguiente = siguiente;
    }
}

