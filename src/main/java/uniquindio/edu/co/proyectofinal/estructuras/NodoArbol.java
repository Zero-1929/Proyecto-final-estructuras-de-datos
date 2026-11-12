package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Nodo de un árbol binario de búsqueda.
 *
 * @param <T> tipo de dato almacenado
 */
public class NodoArbol<T> {

    /**
     * Atributos
     */
    private T dato;
    private NodoArbol<T> izquierdo;
    private NodoArbol<T> derecho;

    /**
     * Crea un nodo con su dato.
     *
     * @param dato dato almacenado
     */
    public NodoArbol(T dato) {
        this.dato = dato;
        this.izquierdo = null;
        this.derecho = null;
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
     * Obtiene el hijo izquierdo.
     *
     * @return hijo izquierdo o null
     */
    public NodoArbol<T> getIzquierdo() {
        return izquierdo;
    }

    /**
     * Cambia el hijo izquierdo.
     *
     * @param izquierdo nuevo hijo izquierdo
     */
    public void setIzquierdo(NodoArbol<T> izquierdo) {
        this.izquierdo = izquierdo;
    }

    /**
     * Obtiene el hijo derecho.
     *
     * @return hijo derecho o null
     */
    public NodoArbol<T> getDerecho() {
        return derecho;
    }

    /**
     * Cambia el hijo derecho.
     *
     * @param derecho nuevo hijo derecho
     */
    public void setDerecho(NodoArbol<T> derecho) {
        this.derecho = derecho;
    }

    /**
     * Indica si el nodo no tiene hijos.
     *
     * @return true si es una hoja
     */
    public boolean esHoja() {
        return izquierdo == null && derecho == null;
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
