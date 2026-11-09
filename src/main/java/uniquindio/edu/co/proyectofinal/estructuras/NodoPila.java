package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Nodo de una pila enlazada.
 *
 * @param <T> tipo de dato almacenado
 */
public class NodoPila<T> {

    /**
     * Atributos
     */
    private T dato;
    private NodoPila<T> siguiente;

    /**
     * Crea un nodo con su dato.
     *
     * @param dato dato almacenado
     */
    public NodoPila(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Obtiene el dato.
     *
     * @return dato almacenado
     */
    public T getDato() {
        return dato;
    }

    /**
     * Cambia el dato.
     *
     * @param dato nuevo dato
     */
    public void setDato(T dato) {
        this.dato = dato;
    }

    /**
     * Obtiene el nodo siguiente.
     *
     * @return nodo siguiente o null
     */
    public NodoPila<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Cambia el nodo siguiente.
     *
     * @param siguiente nuevo nodo siguiente
     */
    public void setSiguiente(NodoPila<T> siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Genera una representación textual del nodo.
     *
     * @return dato del nodo como texto
     */
    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
