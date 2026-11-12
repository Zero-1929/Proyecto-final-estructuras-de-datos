package uniquindio.edu.co.proyectofinal.estructuras;

/**
 * Pila genérica implementada con nodos enlazados.
 * Sigue el principio LIFO (último en entrar, primero en salir).
 *
 * @param <T> tipo de dato almacenado
 */
public class Pila<T> {

    /**
     * Atributos
     */
    private NodoPila<T> cima;
    private int tamano;

    /**
     * Crea una pila vacía.
     */
    public Pila() {
        cima = null;
        tamano = 0;
    }

    /**
     * Apila un elemento en la cima.
     *
     * @param dato elemento a apilar
     */
    public void apilar(T dato) {
        NodoPila<T> nuevo = new NodoPila<>(dato);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamano++;
    }

    /**
     * Desapila y retorna el elemento de la cima.
     *
     * @return dato desapilado
     */
    public T desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía");
        }
        T dato = cima.getDato();
        cima = cima.getSiguiente();
        tamano--;
        return dato;
    }

    /**
     * Obtiene el elemento de la cima sin desapilarlo.
     *
     * @return dato en la cima
     */
    public T cima() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía");
        }
        return cima.getDato();
    }

    /**
     * Indica si la pila está vacía.
     *
     * @return true si no tiene elementos
     */
    public boolean estaVacia() {
        return cima == null;
    }

    /**
     * Obtiene la cantidad de elementos.
     *
     * @return tamaño de la pila
     */
    public int tamano() {
        return tamano;
    }

    /**
     * Elimina todos los elementos de la pila.
     */
    public void limpiar() {
        cima = null;
        tamano = 0;
    }

    /**
     * Indica si un elemento está en la pila.
     *
     * @param dato elemento a buscar
     * @return true si existe
     */
    public boolean contiene(T dato) {
        NodoPila<T> actual = cima;
        while (actual != null) {
            if (sonIguales(actual.getDato(), dato)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
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
     * Genera una representación textual de la pila.
     *
     * @return elementos desde la cima hasta el fondo
     */
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        NodoPila<T> actual = cima;
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
