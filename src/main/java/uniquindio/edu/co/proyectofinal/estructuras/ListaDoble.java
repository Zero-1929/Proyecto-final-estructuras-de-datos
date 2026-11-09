package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Lista doblemente enlazada genérica.
 * Permite recorrer sus elementos en ambos sentidos.
 *
 * @param <T> tipo de dato almacenado
 */
public class ListaDoble<T> {

    /**
     * Atributos
     */
    private NodoDoble<T> inicio;
    private NodoDoble<T> fin;
    private int tamano;

    /**
     * Crea una lista vacía.
     */
    public ListaDoble() {
        inicio = null;
        fin = null;
        tamano = 0;
    }

    /**
     * Agrega un elemento al inicio de la lista.
     *
     * @param dato elemento a agregar
     */
    public void agregarInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            nuevo.setSiguiente(inicio);
            inicio.setAnterior(nuevo);
            inicio = nuevo;
        }
        tamano++;
    }

    /**
     * Agrega un elemento al final de la lista.
     *
     * @param dato elemento a agregar
     */
    public void agregarFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            nuevo.setAnterior(fin);
            fin = nuevo;
        }
        tamano++;
    }

    /**
     * Inserta un elemento en una posición determinada.
     *
     * @param indice posición donde se inserta
     * @param dato elemento a insertar
     */
    public void insertar(int indice, T dato) {
        if (indice < 0 || indice > tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        if (indice == 0) {
            agregarInicio(dato);
            return;
        }
        if (indice == tamano) {
            agregarFinal(dato);
            return;
        }
        NodoDoble<T> actual = nodoEn(indice);
        NodoDoble<T> anterior = actual.getAnterior();
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        anterior.setSiguiente(nuevo);
        nuevo.setAnterior(anterior);
        nuevo.setSiguiente(actual);
        actual.setAnterior(nuevo);
        tamano++;
    }

    /**
     * Elimina y retorna el primer elemento.
     *
     * @return dato eliminado
     */
    public T eliminarInicio() {
        if (estaVacia()) {
            throw new IllegalStateException("La lista está vacía");
        }
        T dato = inicio.getDato();
        if (inicio == fin) {
            inicio = null;
            fin = null;
        } else {
            inicio = inicio.getSiguiente();
            inicio.setAnterior(null);
        }
        tamano--;
        return dato;
    }

    /**
     * Elimina y retorna el último elemento.
     *
     * @return dato eliminado
     */
    public T eliminarFinal() {
        if (estaVacia()) {
            throw new IllegalStateException("La lista está vacía");
        }
        T dato = fin.getDato();
        if (inicio == fin) {
            inicio = null;
            fin = null;
        } else {
            fin = fin.getAnterior();
            fin.setSiguiente(null);
        }
        tamano--;
        return dato;
    }

    /**
     * Elimina y retorna el elemento de una posición.
     *
     * @param indice posición del elemento
     * @return dato eliminado
     */
    public T eliminar(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        if (indice == 0) {
            return eliminarInicio();
        }
        if (indice == tamano - 1) {
            return eliminarFinal();
        }
        NodoDoble<T> actual = nodoEn(indice);
        NodoDoble<T> anterior = actual.getAnterior();
        NodoDoble<T> siguiente = actual.getSiguiente();
        anterior.setSiguiente(siguiente);
        siguiente.setAnterior(anterior);
        actual.setAnterior(null);
        actual.setSiguiente(null);
        tamano--;
        return actual.getDato();
    }

    /**
     * Elimina la primera aparición de un elemento.
     *
     * @param dato elemento a eliminar
     * @return true si se eliminó
     */
    public boolean eliminar(T dato) {
        NodoDoble<T> actual = inicio;
        while (actual != null) {
            if (sonIguales(actual.getDato(), dato)) {
                if (actual == inicio) {
                    eliminarInicio();
                } else if (actual == fin) {
                    eliminarFinal();
                } else {
                    actual.getAnterior().setSiguiente(actual.getSiguiente());
                    actual.getSiguiente().setAnterior(actual.getAnterior());
                    actual.setAnterior(null);
                    actual.setSiguiente(null);
                    tamano--;
                }
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Obtiene el elemento de una posición.
     *
     * @param indice posición del elemento
     * @return dato almacenado
     */
    public T obtener(int indice) {
        return nodoEn(indice).getDato();
    }

    /**
     * Cambia el elemento de una posición.
     *
     * @param indice posición del elemento
     * @param dato nuevo dato
     */
    public void actualizar(int indice, T dato) {
        nodoEn(indice).setDato(dato);
    }

    /**
     * Busca la primera posición de un elemento.
     *
     * @param dato elemento a buscar
     * @return índice o -1 si no existe
     */
    public int indiceDe(T dato) {
        NodoDoble<T> actual = inicio;
        int indice = 0;
        while (actual != null) {
            if (sonIguales(actual.getDato(), dato)) {
                return indice;
            }
            actual = actual.getSiguiente();
            indice++;
        }
        return -1;
    }

    /**
     * Indica si un elemento está en la lista.
     *
     * @param dato elemento a buscar
     * @return true si existe
     */
    public boolean contiene(T dato) {
        return indiceDe(dato) != -1;
    }

    /**
     * Obtiene el primer elemento sin eliminarlo.
     *
     * @return primer dato o null si la lista está vacía
     */
    public T getPrimero() {
        return inicio == null ? null : inicio.getDato();
    }

    /**
     * Obtiene el último elemento sin eliminarlo.
     *
     * @return último dato o null si la lista está vacía
     */
    public T getUltimo() {
        return fin == null ? null : fin.getDato();
    }

    /**
     * Obtiene la cantidad de elementos.
     *
     * @return tamaño de la lista
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Indica si la lista está vacía.
     *
     * @return true si no tiene elementos
     */
    public boolean estaVacia() {
        return inicio == null;
    }

    /**
     * Elimina todos los elementos de la lista.
     */
    public void limpiar() {
        inicio = null;
        fin = null;
        tamano = 0;
    }

    /**
     * Obtiene el nodo de una posición.
     *
     * @param indice posición del nodo
     * @return nodo encontrado
     */
    private NodoDoble<T> nodoEn(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        NodoDoble<T> actual;
        if (indice < tamano / 2) {
            actual = inicio;
            for (int i = 0; i < indice; i++) {
                actual = actual.getSiguiente();
            }
        } else {
            actual = fin;
            for (int i = tamano - 1; i > indice; i--) {
                actual = actual.getAnterior();
            }
        }
        return actual;
    }

    /**
     * Compara dos datos teniendo en cuenta valores nulos.
     *
     * @param primero primer dato
     * @param segundo segundo dato
     * @return true si son iguales
     */
    private boolean sonIguales(T primero, T segundo) {
        return primero == null ? segundo == null : primero.equals(segundo);
    }

    /**
     * Genera una representación textual de la lista.
     *
     * @return elementos entre corchetes
     */
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        NodoDoble<T> actual = inicio;
        while (actual != null) {
            texto.append(actual.getDato());
            if (actual.getSiguiente() != null) {
                texto.append(", ");
            }
            actual = actual.getSiguiente();
        }
        return texto.append("]").toString();
    }
}
