package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.Grafo;
import uniquindio.edu.co.proyectofinal.estructuras.ResultadoRecorrido;
import uniquindio.edu.co.proyectofinal.estructuras.ResultadoRuta;

/**
 * Servicio encargado de administrar el mapa del alojamiento
 * y consultar las rutas entre sus diferentes ubicaciones.
 *
 * Utiliza el Grafo<T> desarrollado por el equipo.
 *
 * Los vértices representan apartamentos y puntos de servicio.
 * Las aristas representan pasillos con un peso asociado.
 *
 * @param <T> tipo de dato que representa cada ubicación.
 */
public class ServicioRutas<T> {

    // Grafo que representa el mapa del alojamiento.
    private final Grafo<T> mapa;

    /**
     * Constructor del servicio.
     * Inicializa un mapa vacío.
     */
    public ServicioRutas() {
        this.mapa = new Grafo<>();
    }

    /**
     * Registra una nueva ubicación en el mapa.
     *
     * No permite ubicaciones nulas ni repetidas.
     *
     * @param ubicacion ubicación que se desea registrar.
     */
    public void registrarUbicacion(T ubicacion) {

        if (ubicacion == null) {
            throw new IllegalArgumentException(
                    "La ubicación no puede ser nula."
            );
        }

        mapa.agregarVertice(ubicacion);
    }

    /**
     * Conecta dos ubicaciones mediante un pasillo.
     *
     * La conexión es bidireccional porque el grafo
     * implementado por el equipo es no dirigido.
     *
     * @param origen ubicación inicial.
     * @param destino ubicación final.
     * @param peso distancia o tiempo del recorrido.
     */
    public void conectarUbicaciones(
            T origen,
            T destino,
            double peso) {

        validarUbicacion(origen);
        validarUbicacion(destino);

        if (!Double.isFinite(peso) || peso < 0) {
            throw new IllegalArgumentException(
                    "El peso debe ser un número finito no negativo."
            );
        }

        if (!mapa.existeVertice(origen)) {
            throw new IllegalArgumentException(
                    "La ubicación de origen no está registrada."
            );
        }

        if (!mapa.existeVertice(destino)) {
            throw new IllegalArgumentException(
                    "La ubicación de destino no está registrada."
            );
        }

        mapa.conectar(origen, destino, peso);
    }

    /**
     * Elimina la conexión entre dos ubicaciones.
     *
     * @param origen primera ubicación.
     * @param destino segunda ubicación.
     * @return true si se eliminó la conexión.
     */
    public boolean eliminarConexion(T origen, T destino) {

        validarUbicacion(origen);
        validarUbicacion(destino);

        return mapa.eliminarArista(origen, destino);
    }

    /**
     * Consulta la ruta más corta entre dos ubicaciones
     * mediante el algoritmo de Dijkstra.
     *
     * @param origen ubicación de partida.
     * @param destino ubicación de llegada.
     * @return resultado con la ruta y el peso total.
     */
    public ResultadoRuta<T> consultarRutaMasCorta(
            T origen,
            T destino) {

        validarUbicacion(origen);
        validarUbicacion(destino);

        if (!mapa.existeVertice(origen)) {
            throw new IllegalArgumentException(
                    "La ubicación de origen no está registrada."
            );
        }

        if (!mapa.existeVertice(destino)) {
            throw new IllegalArgumentException(
                    "La ubicación de destino no está registrada."
            );
        }

        return mapa.dijkstra(origen, destino);
    }

    /**
     * Realiza un recorrido en anchura (BFS).
     *
     * Permite analizar las ubicaciones alcanzables
     * comenzando desde una ubicación determinada.
     *
     * @param inicio ubicación inicial.
     * @return resultado del recorrido BFS.
     */
    public ResultadoRecorrido<T> consultarRecorridoBFS(T inicio) {

        validarUbicacion(inicio);

        if (!mapa.existeVertice(inicio)) {
            throw new IllegalArgumentException(
                    "La ubicación inicial no está registrada."
            );
        }

        return mapa.recorridoBFS(inicio);
    }

    /**
     * Realiza un recorrido en profundidad (DFS).
     *
     * Utiliza el recorrido recursivo implementado
     * en la estructura Grafo<T>.
     *
     * @param inicio ubicación inicial.
     * @return resultado del recorrido DFS.
     */
    public ResultadoRecorrido<T> consultarRecorridoDFS(T inicio) {

        validarUbicacion(inicio);

        if (!mapa.existeVertice(inicio)) {
            throw new IllegalArgumentException(
                    "La ubicación inicial no está registrada."
            );
        }

        return mapa.recorridoDFS(inicio);
    }

    /**
     * Comprueba si una ubicación está registrada.
     *
     * @param ubicacion ubicación que se desea consultar.
     * @return true si existe en el mapa.
     */
    public boolean existeUbicacion(T ubicacion) {

        if (ubicacion == null) {
            return false;
        }

        return mapa.existeVertice(ubicacion);
    }

    /**
     * Comprueba si dos ubicaciones tienen una conexión directa.
     *
     * No comprueba si existe una ruta indirecta.
     *
     * @param origen primera ubicación.
     * @param destino segunda ubicación.
     * @return true si existe una arista directa.
     */
    public boolean estanConectadas(T origen, T destino) {

        validarUbicacion(origen);
        validarUbicacion(destino);

        return mapa.estanConectados(origen, destino);
    }

    /**
     * Consulta el peso de la conexión directa
     * entre dos ubicaciones.
     *
     * @param origen primera ubicación.
     * @param destino segunda ubicación.
     * @return peso de la conexión.
     */
    public double consultarPesoConexion(T origen, T destino) {

        validarUbicacion(origen);
        validarUbicacion(destino);

        return mapa.obtenerPeso(origen, destino);
    }

    /**
     * Obtiene la cantidad de ubicaciones registradas.
     *
     * @return número de vértices del mapa.
     */
    public int obtenerCantidadUbicaciones() {
        return mapa.getCantidadVertices();
    }

    /**
     * Obtiene la cantidad de conexiones del mapa.
     *
     * Cada conexión no dirigida se cuenta una sola vez.
     *
     * @return número de aristas.
     */
    public int obtenerCantidadConexiones() {
        return mapa.getCantidadAristas();
    }

    /**
     * Comprueba si el mapa no contiene ubicaciones.
     *
     * @return true si el grafo está vacío.
     */
    public boolean mapaVacio() {
        return mapa.estaVacio();
    }

    /**
     * Obtiene el grafo administrado por el servicio.
     *
     * Se utiliza cuando otro componente necesita recorrer
     * o visualizar los vértices del mapa.
     *
     * @return grafo del alojamiento.
     */
    public Grafo<T> obtenerMapa() {
        return mapa;
    }

    /**
     * Valida que una ubicación no sea nula.
     *
     * @param ubicacion ubicación que se desea validar.
     */
    private void validarUbicacion(T ubicacion) {

        if (ubicacion == null) {
            throw new IllegalArgumentException(
                    "La ubicación no puede ser nula."
            );
        }
    }
}

