package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Representa un nodo o vértice dentro del grafo.
 *
 * Almacena un dato genérico, sus aristas de conexión
 * y una referencia al siguiente nodo del grafo.
 *
 * @param <T> tipo de dato almacenado en el nodo
 */
public class NodoGrafo<T> {

    private T dato;
    private Arista<T> primeraArista;
    private NodoGrafo<T> siguiente;

    /**
     * Crea un nodo del grafo.
     *
     * @param dato información que representa el nodo
     */
    public NodoGrafo(T dato) {
        this.dato = dato;
        this.primeraArista = null;
        this.siguiente = null;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public Arista<T> getPrimeraArista() {
        return primeraArista;
    }

    public void setPrimeraArista(Arista<T> primeraArista) {
        this.primeraArista = primeraArista;
    }

    public NodoGrafo<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoGrafo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
