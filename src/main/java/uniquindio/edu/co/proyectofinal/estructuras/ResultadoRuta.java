package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Almacena el resultado de una ruta calculada en un grafo.
 *
 * @param <T> tipo de dato de los vértices
 */
public class ResultadoRuta<T> {

    private final Object[] ruta;
    private final int cantidad;
    private final double distanciaTotal;

    /**
     * Construye el resultado de una ruta.
     *
     * @param ruta vértices que conforman la ruta
     * @param cantidad cantidad de vértices
     * @param distanciaTotal peso total de la ruta
     */
    public ResultadoRuta(
            Object[] ruta,
            int cantidad,
            double distanciaTotal) {

        if (ruta == null || cantidad < 0
                || cantidad > ruta.length
                || Double.isNaN(distanciaTotal)
                || distanciaTotal < 0) {
            throw new IllegalArgumentException(
                    "Los datos de la ruta no son válidos");
        }

        this.ruta = new Object[cantidad];

        for (int i = 0; i < cantidad; i++) {
            this.ruta[i] = ruta[i];
        }

        this.cantidad = cantidad;
        this.distanciaTotal = distanciaTotal;
    }

    /**
     * Obtiene la cantidad de vértices de la ruta.
     *
     * @return cantidad de vértices
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Obtiene un vértice de la ruta.
     *
     * @param indice posición del vértice
     * @return vértice correspondiente
     */
    @SuppressWarnings("unchecked")
    public T getNodo(int indice) {

        if (indice < 0 || indice >= cantidad) {
            throw new IndexOutOfBoundsException(
                    "Índice fuera de la ruta");
        }

        return (T) ruta[indice];
    }

    /**
     * Obtiene el peso total de la ruta.
     *
     * @return distancia total
     */
    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    /**
     * Comprueba si existe una ruta.
     *
     * @return true si se encontró una ruta
     */
    public boolean existeRuta() {
        return cantidad > 0;
    }

    /**
     * Genera una representación textual de la ruta.
     *
     * @return ruta y distancia total
     */
    @Override
    public String toString() {

        if (!existeRuta()) {
            return "No existe una ruta entre los vértices";
        }

        StringBuilder texto = new StringBuilder();

        for (int i = 0; i < cantidad; i++) {

            texto.append(ruta[i]);

            if (i < cantidad - 1) {
                texto.append(" -> ");
            }
        }

        texto.append(" | Distancia total: ")
                .append(distanciaTotal);

        return texto.toString();
    }
}

