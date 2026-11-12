package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Representa un nodo de una cola enlazada.
 *
 * @param <T> tipo de dato almacenado en el nodo
 */
public class NodoCola<T> {

    private T dato;
    private NodoCola<T> siguiente;

    /**
     * Crea un nodo con el dato recibido.
     *
     * @param dato elemento que se almacenará
     */
    public NodoCola(T dato) {
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
    public NodoCola<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el siguiente nodo.
     *
     * @param siguiente nodo siguiente
     */
    public void setSiguiente(NodoCola<T> siguiente) {
        this.siguiente = siguiente;
    }
}

