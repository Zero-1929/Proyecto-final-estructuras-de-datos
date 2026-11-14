package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Nodo de una lista doblemente enlazada.
 *
 * @param <T> tipo de dato almacenado
 */
public class NodoDoble<T> {

    /**
     * Atributos
     */
    private T dato;
    private NodoDoble<T> anterior;
    private NodoDoble<T> siguiente;

    /**
     * Crea un nodo con su dato.
     *
     * @param dato dato almacenado
     */
    public NodoDoble(T dato) {
        this.dato = dato;
        this.anterior = null;
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
     * Obtiene el nodo anterior.
     *
     * @return nodo anterior o null
     */
    public NodoDoble<T> getAnterior() {
        return anterior;
    }

    /**
     * Cambia el nodo anterior.
     *
     * @param anterior nuevo nodo anterior
     */
    public void setAnterior(NodoDoble<T> anterior) {
        this.anterior = anterior;
    }

    /**
     * Obtiene el nodo siguiente.
     *
     * @return nodo siguiente o null
     */
    public NodoDoble<T> getSiguiente() {
        return siguiente;
    }

    /**
     * Cambia el nodo siguiente.
     *
     * @param siguiente nuevo nodo siguiente
     */
    public void setSiguiente(NodoDoble<T> siguiente) {
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
