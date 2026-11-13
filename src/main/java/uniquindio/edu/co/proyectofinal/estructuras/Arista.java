package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Representa una arista o conexión entre dos vértices.
 *
 * Almacena el nodo de destino, el peso de la conexión
 * y la siguiente arista de la lista de adyacencia.
 *
 * @param <T> tipo de dato de los nodos del grafo
 */
public class Arista<T> {

    private NodoGrafo<T> destino;
    private double peso;
    private Arista<T> siguiente;

    /**
     * Crea una arista con su destino y peso.
     *
     * @param destino nodo al que conduce la conexión
     * @param peso distancia o costo de la conexión
     */
    public Arista(NodoGrafo<T> destino, double peso) {

        if (destino == null) {
            throw new IllegalArgumentException(
                    "El nodo destino no puede ser nulo");
        }

        validarPeso(peso);

        this.destino = destino;
        this.peso = peso;
        this.siguiente = null;
    }

    /**
     * Valida que el peso sea finito y no negativo.
     *
     * @param peso valor que se desea validar
     */
    private void validarPeso(double peso) {

        if (!Double.isFinite(peso) || peso < 0) {
            throw new IllegalArgumentException(
                    "El peso debe ser un número finito no negativo");
        }
    }

    public NodoGrafo<T> getDestino() {
        return destino;
    }

    public void setDestino(NodoGrafo<T> destino) {

        if (destino == null) {
            throw new IllegalArgumentException(
                    "El nodo destino no puede ser nulo");
        }

        this.destino = destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        validarPeso(peso);
        this.peso = peso;
    }

    public Arista<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Arista<T> siguiente) {
        this.siguiente = siguiente;
    }
}

