package uniquindio.edu.co.proyectofinal.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Representa un grafo genérico, no dirigido y ponderado.
 *
 * Utiliza listas de adyacencia implementadas mediante
 * nodos y aristas propias, sin utilizar colecciones
 * de Java para almacenar sus elementos.
 *
 * Permite representar el mapa del alojamiento y realizar
 * recorridos BFS, DFS y rutas más cortas con Dijkstra.
 *
 * @param <T> tipo de dato almacenado en los vértices
 */
public class Grafo<T> implements Iterable<T> {

    private NodoGrafo<T> primerNodo;
    private int cantidadVertices;
    private int cantidadAristas;

    /**
     * Crea un grafo vacío.
     */
    public Grafo() {
        primerNodo = null;
        cantidadVertices = 0;
        cantidadAristas = 0;
    }

    /**
     * Agrega un vértice al grafo.
     *
     * No permite datos nulos ni vértices repetidos.
     *
     * @param dato elemento que representará el vértice
     */
    public void agregarVertice(T dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "El vértice no puede ser nulo");
        }

        if (buscarNodo(dato) != null) {
            throw new IllegalArgumentException(
                    "El vértice ya existe en el grafo");
        }

        NodoGrafo<T> nuevo = new NodoGrafo<>(dato);

        if (primerNodo == null) {
            primerNodo = nuevo;
        } else {
            NodoGrafo<T> actual = primerNodo;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }

        cantidadVertices++;
    }

    /**
     * Busca un nodo a partir del dato almacenado.
     *
     * @param dato elemento que se desea buscar
     * @return nodo encontrado o null si no existe
     */
    private NodoGrafo<T> buscarNodo(T dato) {

        if (dato == null) {
            return null;
        }

        NodoGrafo<T> actual = primerNodo;

        while (actual != null) {

            if (actual.getDato().equals(dato)) {
                return actual;
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    /**
     * Comprueba si existe un vértice.
     *
     * @param dato elemento que se desea consultar
     * @return true si existe
     */
    public boolean existeVertice(T dato) {
        return buscarNodo(dato) != null;
    }

    /**
     * Conecta dos vértices mediante una arista no dirigida.
     *
     * La conexión queda disponible en ambos sentidos.
     * Si ya existe, actualiza el peso de la conexión.
     *
     * @param origen dato del vértice de origen
     * @param destino dato del vértice de destino
     * @param peso distancia o costo de la conexión
     */
    public void conectar(T origen, T destino, double peso) {

        if (!Double.isFinite(peso) || peso < 0) {
            throw new IllegalArgumentException(
                    "El peso debe ser un número finito no negativo");
        }

        if (origen == null || destino == null) {
            throw new IllegalArgumentException(
                    "Los vértices no pueden ser nulos");
        }

        if (origen.equals(destino)) {
            throw new IllegalArgumentException(
                    "No se permiten conexiones de un nodo consigo mismo");
        }

        NodoGrafo<T> nodoOrigen = buscarNodo(origen);
        NodoGrafo<T> nodoDestino = buscarNodo(destino);

        if (nodoOrigen == null || nodoDestino == null) {
            throw new IllegalArgumentException(
                    "Ambos vértices deben existir antes de conectarlos");
        }

        Arista<T> aristaExistente =
                buscarArista(nodoOrigen, nodoDestino);

        if (aristaExistente != null) {

            buscarArista(nodoDestino, nodoOrigen).setPeso(peso);
            aristaExistente.setPeso(peso);

            return;
        }

        insertarArista(nodoOrigen, nodoDestino, peso);
        insertarArista(nodoDestino, nodoOrigen, peso);

        cantidadAristas++;
    }

    /**
     * Inserta una arista en la lista de adyacencia de un nodo.
     *
     * @param origen nodo desde el que parte la conexión
     * @param destino nodo al que llega la conexión
     * @param peso peso de la conexión
     */
    private void insertarArista(
            NodoGrafo<T> origen,
            NodoGrafo<T> destino,
            double peso) {

        Arista<T> nueva = new Arista<>(destino, peso);

        if (origen.getPrimeraArista() == null) {
            origen.setPrimeraArista(nueva);
        } else {

            Arista<T> actual = origen.getPrimeraArista();

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nueva);
        }
    }

    /**
     * Busca una arista entre dos nodos.
     *
     * @param origen nodo de origen
     * @param destino nodo de destino
     * @return arista encontrada o null
     */
    private Arista<T> buscarArista(
            NodoGrafo<T> origen,
            NodoGrafo<T> destino) {

        Arista<T> actual = origen.getPrimeraArista();

        while (actual != null) {

            if (actual.getDestino() == destino) {
                return actual;
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    /**
     * Comprueba si dos vértices están conectados directamente.
     *
     * @param origen primer vértice
     * @param destino segundo vértice
     * @return true si existe una conexión
     */
    public boolean estanConectados(T origen, T destino) {

        NodoGrafo<T> nodoOrigen = buscarNodo(origen);
        NodoGrafo<T> nodoDestino = buscarNodo(destino);

        if (nodoOrigen == null || nodoDestino == null) {
            return false;
        }

        return buscarArista(nodoOrigen, nodoDestino) != null;
    }

    /**
     * Obtiene el peso de la conexión entre dos vértices.
     *
     * @param origen primer vértice
     * @param destino segundo vértice
     * @return peso de la conexión
     */
    public double obtenerPeso(T origen, T destino) {

        NodoGrafo<T> nodoOrigen = buscarNodo(origen);
        NodoGrafo<T> nodoDestino = buscarNodo(destino);

        if (nodoOrigen == null || nodoDestino == null) {
            throw new IllegalArgumentException(
                    "Uno o ambos vértices no existen");
        }

        Arista<T> arista = buscarArista(nodoOrigen, nodoDestino);

        if (arista == null) {
            throw new IllegalArgumentException(
                    "No existe una conexión entre los vértices");
        }

        return arista.getPeso();
    }

    /**
     * Elimina una conexión no dirigida entre dos vértices.
     *
     * @param origen primer vértice
     * @param destino segundo vértice
     * @return true si se eliminó la conexión
     */
    public boolean eliminarArista(T origen, T destino) {

        NodoGrafo<T> nodoOrigen = buscarNodo(origen);
        NodoGrafo<T> nodoDestino = buscarNodo(destino);

        if (nodoOrigen == null || nodoDestino == null) {
            return false;
        }

        if (buscarArista(nodoOrigen, nodoDestino) == null) {
            return false;
        }

        quitarArista(nodoOrigen, nodoDestino);
        quitarArista(nodoDestino, nodoOrigen);

        cantidadAristas--;

        return true;
    }

    /**
     * Quita una arista de la lista de adyacencia de un nodo.
     *
     * @param origen nodo de origen
     * @param destino nodo de destino
     * @return true si se eliminó
     */
    private boolean quitarArista(
            NodoGrafo<T> origen,
            NodoGrafo<T> destino) {

        Arista<T> actual = origen.getPrimeraArista();
        Arista<T> anterior = null;

        while (actual != null) {

            if (actual.getDestino() == destino) {

                if (anterior == null) {
                    origen.setPrimeraArista(actual.getSiguiente());
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }

                return true;
            }

            anterior = actual;
            actual = actual.getSiguiente();
        }

        return false;
    }

    /**
     * Realiza un recorrido en anchura (BFS).
     *
     * Visita primero los vecinos directos del vértice inicial.
     *
     * @param inicio vértice desde el que comienza el recorrido
     * @return resultado con los vértices visitados
     */
    public ResultadoRecorrido<T> recorridoBFS(T inicio) {

        NodoGrafo<T> nodoInicio = buscarNodo(inicio);

        if (nodoInicio == null) {
            throw new IllegalArgumentException(
                    "El vértice inicial no existe");
        }

        Object[] visitados = new Object[cantidadVertices];
        Object[] cola = new Object[cantidadVertices];

        int frente = 0;
        int fin = 0;
        int cantidadVisitados = 0;

        cola[fin++] = nodoInicio;

        while (frente < fin) {

            NodoGrafo<T> actual =
                    (NodoGrafo<T>) cola[frente++];

            visitados[cantidadVisitados++] = actual.getDato();

            Arista<T> arista = actual.getPrimeraArista();

            while (arista != null) {

                NodoGrafo<T> vecino = arista.getDestino();

                if (!contieneNodo(visitados,
                        cantidadVisitados, vecino)
                        && !contieneEnCola(cola,
                        frente, fin, vecino)) {

                    cola[fin++] = vecino;
                }

                arista = arista.getSiguiente();
            }
        }

        return new ResultadoRecorrido<>(
                visitados, cantidadVisitados);
    }

    /**
     * Comprueba si un vértice ya fue visitado.
     *
     * @param visitados arreglo de datos visitados
     * @param cantidad cantidad de datos visitados
     * @param nodo nodo que se desea comprobar
     * @return true si ya fue visitado
     */
    private boolean contieneNodo(
            Object[] visitados,
            int cantidad,
            NodoGrafo<T> nodo) {

        for (int i = 0; i < cantidad; i++) {

            if (visitados[i].equals(nodo.getDato())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Comprueba si un nodo ya está esperando en la cola de BFS.
     *
     * @param cola arreglo que representa la cola auxiliar
     * @param frente posición del frente
     * @param fin posición del final
     * @param nodo nodo que se desea comprobar
     * @return true si ya está en la cola
     */
    private boolean contieneEnCola(
            Object[] cola,
            int frente,
            int fin,
            NodoGrafo<T> nodo) {

        for (int i = frente; i < fin; i++) {

            NodoGrafo<T> nodoCola = (NodoGrafo<T>) cola[i];

            if (nodoCola == nodo) {
                return true;
            }
        }

        return false;
    }

    /**
     * Realiza un recorrido en profundidad (DFS) recursivo.
     *
     * @param inicio vértice desde el que comienza el recorrido
     * @return resultado con los vértices visitados
     */
    public ResultadoRecorrido<T> recorridoDFS(T inicio) {

        NodoGrafo<T> nodoInicio = buscarNodo(inicio);

        if (nodoInicio == null) {
            throw new IllegalArgumentException(
                    "El vértice inicial no existe");
        }

        Object[] visitados = new Object[cantidadVertices];
        int[] cantidad = {0};

        dfsRecursivo(nodoInicio, visitados, cantidad);

        return new ResultadoRecorrido<>(
                visitados, cantidad[0]);
    }

    /**
     * Método recursivo auxiliar para DFS.
     *
     * @param actual nodo que se está visitando
     * @param visitados arreglo de vértices visitados
     * @param cantidad cantidad de vértices visitados
     */
    private void dfsRecursivo(
            NodoGrafo<T> actual,
            Object[] visitados,
            int[] cantidad) {

        if (contieneNodo(visitados, cantidad[0], actual)) {
            return;
        }

        visitados[cantidad[0]++] = actual.getDato();

        Arista<T> arista = actual.getPrimeraArista();

        while (arista != null) {

            dfsRecursivo(
                    arista.getDestino(),
                    visitados,
                    cantidad);

            arista = arista.getSiguiente();
        }
    }

    /**
     * Calcula la ruta más corta entre dos vértices mediante Dijkstra.
     *
     * Los pesos de las aristas deben ser no negativos.
     *
     * @param origen vértice de inicio
     * @param destino vértice de destino
     * @return resultado con la ruta y su distancia total
     */
    public ResultadoRuta<T> dijkstra(T origen, T destino) {

        NodoGrafo<T> nodoOrigen = buscarNodo(origen);
        NodoGrafo<T> nodoDestino = buscarNodo(destino);

        if (nodoOrigen == null || nodoDestino == null) {
            throw new IllegalArgumentException(
                    "El origen y el destino deben existir");
        }

        NodoGrafo<T>[] nodos = obtenerNodos();

        int n = cantidadVertices;

        double[] distancias = new double[n];
        int[] anteriores = new int[n];
        boolean[] visitados = new boolean[n];

        for (int i = 0; i < n; i++) {
            distancias[i] = Double.POSITIVE_INFINITY;
            anteriores[i] = -1;
        }

        int indiceOrigen = buscarIndice(nodos, nodoOrigen);
        int indiceDestino = buscarIndice(nodos, nodoDestino);

        distancias[indiceOrigen] = 0;

        for (int i = 0; i < n; i++) {

            int actual = obtenerMenorDistancia(
                    distancias, visitados);

            if (actual == -1) {
                break;
            }

            if (actual == indiceDestino) {
                break;
            }

            visitados[actual] = true;

            Arista<T> arista =
                    nodos[actual].getPrimeraArista();

            while (arista != null) {

                int vecino = buscarIndice(
                        nodos, arista.getDestino());

                if (!visitados[vecino]) {

                    double nuevaDistancia =
                            distancias[actual] + arista.getPeso();

                    if (nuevaDistancia < distancias[vecino]) {
                        distancias[vecino] = nuevaDistancia;
                        anteriores[vecino] = actual;
                    }
                }

                arista = arista.getSiguiente();
            }
        }

        if (distancias[indiceDestino]
                == Double.POSITIVE_INFINITY) {

            return new ResultadoRuta<>(
                    new Object[0],
                    0,
                    Double.POSITIVE_INFINITY);
        }

        int longitud = 0;
        int posicion = indiceDestino;

        while (posicion != -1) {
            longitud++;
            posicion = anteriores[posicion];
        }

        Object[] ruta = new Object[longitud];

        posicion = indiceDestino;

        for (int i = longitud - 1; i >= 0; i--) {
            ruta[i] = nodos[posicion].getDato();
            posicion = anteriores[posicion];
        }

        return new ResultadoRuta<>(
                ruta,
                longitud,
                distancias[indiceDestino]);
    }

    /**
     * Convierte los vértices enlazados en un arreglo temporal.
     *
     * @return arreglo con los nodos del grafo
     */
    @SuppressWarnings("unchecked")
    private NodoGrafo<T>[] obtenerNodos() {

        NodoGrafo<T>[] nodos =
                (NodoGrafo<T>[]) new NodoGrafo[cantidadVertices];

        NodoGrafo<T> actual = primerNodo;
        int indice = 0;

        while (actual != null) {
            nodos[indice++] = actual;
            actual = actual.getSiguiente();
        }

        return nodos;
    }

    /**
     * Busca la posición de un nodo dentro del arreglo.
     *
     * @param nodos arreglo de nodos
     * @param buscado nodo que se desea localizar
     * @return posición del nodo
     */
    private int buscarIndice(
            NodoGrafo<T>[] nodos,
            NodoGrafo<T> buscado) {

        for (int i = 0; i < nodos.length; i++) {

            if (nodos[i] == buscado) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Encuentra el vértice no visitado con menor distancia.
     *
     * @param distancias distancias acumuladas
     * @param visitados arreglo de vértices visitados
     * @return índice del vértice de menor distancia
     */
    private int obtenerMenorDistancia(
            double[] distancias,
            boolean[] visitados) {

        double menor = Double.POSITIVE_INFINITY;
        int indiceMenor = -1;

        for (int i = 0; i < distancias.length; i++) {

            if (!visitados[i] && distancias[i] < menor) {
                menor = distancias[i];
                indiceMenor = i;
            }
        }

        return indiceMenor;
    }

    /**
     * Obtiene la cantidad de vértices.
     *
     * @return número de vértices
     */
    public int getCantidadVertices() {
        return cantidadVertices;
    }

    /**
     * Obtiene la cantidad de conexiones no dirigidas.
     *
     * @return número de aristas
     */
    public int getCantidadAristas() {
        return cantidadAristas;
    }

    /**
     * Indica si el grafo está vacío.
     *
     * @return true si no contiene vértices
     */
    public boolean estaVacio() {
        return primerNodo == null;
    }

    /**
     * Permite recorrer todos los vértices del grafo.
     *
     * @return iterador de vértices
     */
    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {

            private NodoGrafo<T> actual = primerNodo;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {

                if (!hasNext()) {
                    throw new NoSuchElementException(
                            "No existen más vértices");
                }

                T dato = actual.getDato();
                actual = actual.getSiguiente();

                return dato;
            }
        };
    }
}

