package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Almacena el resultado de un recorrido realizado sobre un grafo.
 *
 * @param <T> tipo de dato de los vértices
 */
public class ResultadoRecorrido<T> {

    private final Object[] elementos;
    private final int cantidad;

    /**
     * Construye el resultado de un recorrido.
     *
     * @param elementos vértices visitados
     * @param cantidad cantidad de vértices visitados
     */
    public ResultadoRecorrido(Object[] elementos, int cantidad) {

        if (elementos == null || cantidad < 0
                || cantidad > elementos.length) {
            throw new IllegalArgumentException(
                    "Los datos del recorrido no son válidos");
        }

        this.elementos = new Object[cantidad];
        this.cantidad = cantidad;

        for (int i = 0; i < cantidad; i++) {
            this.elementos[i] = elementos[i];
        }
    }

    /**
     * Obtiene la cantidad de vértices visitados.
     *
     * @return cantidad de vértices
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Obtiene un vértice según su posición.
     *
     * @param indice posición del vértice
     * @return vértice correspondiente
     */
    @SuppressWarnings("unchecked")
    public T get(int indice) {

        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera del recorrido");
        }

        return (T) elementos[indice];
    }

    /**
     * Genera una representación textual del recorrido.
     *
     * @return recorrido de vértices
     */
    @Override
    public String toString() {

        StringBuilder texto = new StringBuilder();

        for (int i = 0; i < cantidad; i++) {

            texto.append(elementos[i]);

            if (i < cantidad - 1) {
                texto.append(" -> ");
            }
        }

        return texto.toString();
    }
}
