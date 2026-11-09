package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Árbol binario de búsqueda genérico.
 * Los elementos repetidos se ubican en el subárbol derecho.
 *
 * @param <T> tipo de dato, debe ser comparable
 */
public class ABB<T extends Comparable<? super T>> {

    /**
     * Atributos
     */
    private NodoArbol<T> raiz;
    private int tamano;

    /**
     * Crea un árbol vacío.
     */
    public ABB() {
        raiz = null;
        tamano = 0;
    }

    /**
     * Inserta un elemento en el árbol.
     *
     * @param dato elemento a insertar
     */
    public void insertar(T dato) {
        validarDato(dato);
        if (raiz == null) {
            raiz = new NodoArbol<>(dato);
            tamano++;
            return;
        }
        NodoArbol<T> actual = raiz;
        while (true) {
            int comparacion = dato.compareTo(actual.getDato());
            if (comparacion < 0) {
                if (actual.getIzquierdo() == null) {
                    actual.setIzquierdo(new NodoArbol<>(dato));
                    tamano++;
                    return;
                }
                actual = actual.getIzquierdo();
            } else {
                if (actual.getDerecho() == null) {
                    actual.setDerecho(new NodoArbol<>(dato));
                    tamano++;
                    return;
                }
                actual = actual.getDerecho();
            }
        }
    }

    /**
     * Busca un elemento en el árbol.
     *
     * @param dato elemento a buscar
     * @return elemento encontrado o null si no existe
     */
    public T buscar(T dato) {
        validarDato(dato);
        NodoArbol<T> actual = raiz;
        while (actual != null) {
            int comparacion = dato.compareTo(actual.getDato());
            if (comparacion == 0) {
                return actual.getDato();
            }
            actual = comparacion < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    /**
     * Indica si un elemento está en el árbol.
     *
     * @param dato elemento a buscar
     * @return true si existe
     */
    public boolean contiene(T dato) {
        return buscar(dato) != null;
    }

    /**
     * Busca todas las apariciones de un elemento.
     *
     * @param dato elemento a buscar
     * @return lista con las apariciones encontradas
     */
    public ListaDoble<T> buscarTodos(T dato) {
        validarDato(dato);
        ListaDoble<T> encontrados = new ListaDoble<>();
        buscarTodos(raiz, dato, encontrados);
        return encontrados;
    }

    /**
     * Busca todas las apariciones de un elemento en un subárbol.
     *
     * @param nodo nodo actual
     * @param dato elemento a buscar
     * @param encontrados lista donde se acumulan los resultados
     */
    private void buscarTodos(NodoArbol<T> nodo, T dato, ListaDoble<T> encontrados) {
        if (nodo == null) {
            return;
        }
        int comparacion = dato.compareTo(nodo.getDato());
        if (comparacion < 0) {
            buscarTodos(nodo.getIzquierdo(), dato, encontrados);
        } else if (comparacion > 0) {
            buscarTodos(nodo.getDerecho(), dato, encontrados);
        } else {
            encontrados.agregarFinal(nodo.getDato());
            buscarTodos(nodo.getIzquierdo(), dato, encontrados);
            buscarTodos(nodo.getDerecho(), dato, encontrados);
        }
    }

    /**
     * Elimina una aparición de un elemento.
     *
     * @param dato elemento a eliminar
     * @return true si se eliminó
     */
    public boolean eliminar(T dato) {
        validarDato(dato);
        int tamanoAnterior = tamano;
        raiz = eliminar(raiz, dato);
        return tamano < tamanoAnterior;
    }

    /**
     * Elimina una aparición de un elemento en un subárbol.
     *
     * @param nodo nodo actual
     * @param dato elemento a eliminar
     * @return raíz del subárbol modificado
     */
    private NodoArbol<T> eliminar(NodoArbol<T> nodo, T dato) {
        if (nodo == null) {
            return null;
        }
        int comparacion = dato.compareTo(nodo.getDato());
        if (comparacion < 0) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), dato));
        } else if (comparacion > 0) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), dato));
        } else {
            if (nodo.getIzquierdo() == null) {
                tamano--;
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                tamano--;
                return nodo.getIzquierdo();
            }
            NodoArbol<T> sucesor = minimoNodo(nodo.getDerecho());
            nodo.setDato(sucesor.getDato());
            nodo.setDerecho(eliminarMinimo(nodo.getDerecho()));
        }
        return nodo;
    }

    /**
     * Elimina el nodo con el menor valor de un subárbol.
     *
     * @param nodo raíz del subárbol
     * @return raíz del subárbol modificado
     */
    private NodoArbol<T> eliminarMinimo(NodoArbol<T> nodo) {
        if (nodo.getIzquierdo() == null) {
            tamano--;
            return nodo.getDerecho();
        }
        nodo.setIzquierdo(eliminarMinimo(nodo.getIzquierdo()));
        return nodo;
    }

    /**
     * Obtiene el menor elemento del árbol.
     *
     * @return menor elemento
     */
    public T minimo() {
        if (raiz == null) {
            throw new IllegalStateException("El árbol está vacío");
        }
        return minimoNodo(raiz).getDato();
    }

    /**
     * Obtiene el mayor elemento del árbol.
     *
     * @return mayor elemento
     */
    public T maximo() {
        if (raiz == null) {
            throw new IllegalStateException("El árbol está vacío");
        }
        return maximoNodo(raiz).getDato();
    }

    /**
     * Obtiene el nodo con el menor valor de un subárbol.
     *
     * @param nodo raíz del subárbol
     * @return nodo con el menor valor
     */
    private NodoArbol<T> minimoNodo(NodoArbol<T> nodo) {
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo;
    }

    /**
     * Obtiene el nodo con el mayor valor de un subárbol.
     *
     * @param nodo raíz del subárbol
     * @return nodo con el mayor valor
     */
    private NodoArbol<T> maximoNodo(NodoArbol<T> nodo) {
        while (nodo.getDerecho() != null) {
            nodo = nodo.getDerecho();
        }
        return nodo;
    }

    /**
     * Calcula la altura del árbol.
     *
     * @return altura, cero si está vacío
     */
    public int altura() {
        return altura(raiz);
    }

    /**
     * Calcula la altura de un subárbol.
     *
     * @param nodo raíz del subárbol
     * @return altura del subárbol
     */
    private int altura(NodoArbol<T> nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

    /**
     * Obtiene la cantidad de elementos.
     *
     * @return tamaño del árbol
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Indica si el árbol está vacío.
     *
     * @return true si no tiene elementos
     */
    public boolean estaVacia() {
        return raiz == null;
    }

    /**
     * Elimina todos los elementos del árbol.
     */
    public void limpiar() {
        raiz = null;
        tamano = 0;
    }

    /**
     * Obtiene la raíz del árbol.
     *
     * @return nodo raíz o null si está vacío
     */
    public NodoArbol<T> getRaiz() {
        return raiz;
    }

    /**
     * Recorre el árbol en orden ascendente.
     *
     * @return lista con el recorrido inorden
     */
    public ListaDoble<T> inOrden() {
        ListaDoble<T> resultado = new ListaDoble<>();
        inOrden(raiz, resultado);
        return resultado;
    }

    /**
     * Acumula el recorrido inorden de un subárbol.
     *
     * @param nodo nodo actual
     * @param resultado lista donde se acumula el recorrido
     */
    private void inOrden(NodoArbol<T> nodo, ListaDoble<T> resultado) {
        if (nodo == null) {
            return;
        }
        inOrden(nodo.getIzquierdo(), resultado);
        resultado.agregarFinal(nodo.getDato());
        inOrden(nodo.getDerecho(), resultado);
    }

    /**
     * Recorre el árbol en preorden.
     *
     * @return lista con el recorrido preorden
     */
    public ListaDoble<T> preOrden() {
        ListaDoble<T> resultado = new ListaDoble<>();
        preOrden(raiz, resultado);
        return resultado;
    }

    /**
     * Acumula el recorrido preorden de un subárbol.
     *
     * @param nodo nodo actual
     * @param resultado lista donde se acumula el recorrido
     */
    private void preOrden(NodoArbol<T> nodo, ListaDoble<T> resultado) {
        if (nodo == null) {
            return;
        }
        resultado.agregarFinal(nodo.getDato());
        preOrden(nodo.getIzquierdo(), resultado);
        preOrden(nodo.getDerecho(), resultado);
    }

    /**
     * Recorre el árbol en postorden.
     *
     * @return lista con el recorrido postorden
     */
    public ListaDoble<T> postOrden() {
        ListaDoble<T> resultado = new ListaDoble<>();
        postOrden(raiz, resultado);
        return resultado;
    }

    /**
     * Acumula el recorrido postorden de un subárbol.
     *
     * @param nodo nodo actual
     * @param resultado lista donde se acumula el recorrido
     */
    private void postOrden(NodoArbol<T> nodo, ListaDoble<T> resultado) {
        if (nodo == null) {
            return;
        }
        postOrden(nodo.getIzquierdo(), resultado);
        postOrden(nodo.getDerecho(), resultado);
        resultado.agregarFinal(nodo.getDato());
    }

    /**
     * Recorre el árbol por niveles.
     *
     * @return lista con el recorrido por niveles
     */
    public ListaDoble<T> recorridoPorNiveles() {
        ListaDoble<T> resultado = new ListaDoble<>();
        ListaDoble<NodoArbol<T>> cola = new ListaDoble<>();
        if (raiz != null) {
            cola.agregarFinal(raiz);
        }
        while (!cola.estaVacia()) {
            NodoArbol<T> actual = cola.eliminarInicio();
            resultado.agregarFinal(actual.getDato());
            if (actual.getIzquierdo() != null) {
                cola.agregarFinal(actual.getIzquierdo());
            }
            if (actual.getDerecho() != null) {
                cola.agregarFinal(actual.getDerecho());
            }
        }
        return resultado;
    }

    /**
     * Valida que el dato no sea nulo.
     *
     * @param dato dato a validar
     */
    private void validarDato(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException("El dato no puede ser nulo");
        }
    }

    /**
     * Genera una representación textual del árbol.
     *
     * @return recorrido inorden como texto
     */
    @Override
    public String toString() {
        return inOrden().toString();
    }
}
