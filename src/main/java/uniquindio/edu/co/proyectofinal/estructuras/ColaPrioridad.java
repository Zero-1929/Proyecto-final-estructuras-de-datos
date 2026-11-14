package uniquindio.edu.co.proyectofinal.estructuras;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

/**
 * Cola de prioridad generica
 */
public class ColaPrioridad<T> implements Iterable<T> {

    private static final int CAPACIDAD_INICIAL = 10;

    private Object[] elementos;
    private int tamanio;
    private final Comparator<? super T> comparador;
    private int modificaciones;

    /**
     * Constructor que recibe el criterio de prioridad.
     */
    public ColaPrioridad(Comparator<? super T> comparador) {

        if (comparador == null) {
            throw new IllegalArgumentException(
                    "El comparador no puede ser nulo."
            );
        }

        this.comparador = comparador;
        this.elementos = new Object[CAPACIDAD_INICIAL];
        this.tamanio = 0;
        this.modificaciones = 0;
    }

    /**
     * Inserta un elemento respetando el orden del heap.
     */
    public void encolar(T elemento) {

        if (elemento == null) {
            throw new IllegalArgumentException(
                    "No se permiten elementos nulos."
            );
        }

        asegurarCapacidad();

        elementos[tamanio] = elemento;
        subir(tamanio);

        tamanio++;
        modificaciones++;
    }

    /**
     * Extrae y devuelve el elemento de mayor prioridad.
     */
    public T desencolar() {

        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La cola de prioridad esta vacia."
            );
        }

        T primero = obtenerElemento(0);

        tamanio--;

        if (tamanio > 0) {
            elementos[0] = elementos[tamanio];
        }

        elementos[tamanio] = null;

        if (tamanio > 0) {
            bajar(0);
        }

        modificaciones++;

        return primero;
    }

    /**
     * Consulta el elemento de mayor prioridad
     * sin retirarlo de la cola.
     */
    public T frente() {

        if (estaVacia()) {
            throw new NoSuchElementException(
                    "La cola de prioridad esta vacia."
            );
        }

        return obtenerElemento(0);
    }

    /**
     * Comprueba si la cola no tiene elementos.
     */
    public boolean estaVacia() {
        return tamanio == 0;
    }

    /**
     * Devuelve la cantidad de elementos.
     */
    public int tamanio() {
        return tamanio;
    }

    /**
     * Comprueba si un elemento esta almacenado.
     */
    public boolean contiene(T elemento) {

        for (int i = 0; i < tamanio; i++) {

            T actual = obtenerElemento(i);

            if (actual.equals(elemento)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Elimina todos los elementos de la cola.
     */
    public void vaciar() {

        for (int i = 0; i < tamanio; i++) {
            elementos[i] = null;
        }

        tamanio = 0;
        modificaciones++;
    }

    // -------------------- HEAP --------------------

    /**
     * Sube un elemento mientras tenga mayor prioridad
     * que su padre.
     */
    private void subir(int indice) {

        while (indice > 0) {

            int padre = (indice - 1) / 2;

            if (comparar(indice, padre) >= 0) {
                break;
            }

            intercambiar(indice, padre);
            indice = padre;
        }
    }

    /**
     * Baja un elemento mientras alguno de sus hijos
     * tenga mayor prioridad.
     */
    private void bajar(int indice) {

        while (true) {

            int izquierdo = 2 * indice + 1;
            int derecho = 2 * indice + 2;
            int menor = indice;

            if (izquierdo < tamanio
                    && comparar(izquierdo, menor) < 0) {
                menor = izquierdo;
            }

            if (derecho < tamanio
                    && comparar(derecho, menor) < 0) {
                menor = derecho;
            }

            if (menor == indice) {
                break;
            }

            intercambiar(indice, menor);
            indice = menor;
        }
    }

    private int comparar(int indiceA, int indiceB) {
        return comparador.compare(
                obtenerElemento(indiceA),
                obtenerElemento(indiceB)
        );
    }

    private void intercambiar(int i, int j) {

        Object temporal = elementos[i];
        elementos[i] = elementos[j];
        elementos[j] = temporal;
    }

    /**
     * Duplica la capacidad del arreglo cuando se llena.
     */
    private void asegurarCapacidad() {

        if (tamanio < elementos.length) {
            return;
        }

        Object[] nuevo = new Object[elementos.length * 2];

        for (int i = 0; i < tamanio; i++) {
            nuevo[i] = elementos[i];
        }

        elementos = nuevo;
    }

    @SuppressWarnings("unchecked")
    private T obtenerElemento(int indice) {
        return (T) elementos[indice];
    }

    // -------------------- ITERABLE --------------------

    /**
     * Devuelve un iterador propio.
     *
     * Recorre el arreglo interno del heap sin modificarlo.
     * No garantiza recorrer los elementos en orden de prioridad.
     */
    @Override
    public Iterator<T> iterator() {
        return new IteradorColaPrioridad();
    }

    private class IteradorColaPrioridad implements Iterator<T> {

        private int indiceActual = 0;
        private final int modificacionesEsperadas = modificaciones;

        @Override
        public boolean hasNext() {
            verificarModificaciones();
            return indiceActual < tamanio;
        }

        @Override
        public T next() {

            verificarModificaciones();

            if (!hasNext()) {
                throw new NoSuchElementException(
                        "No hay mas elementos."
                );
            }

            return obtenerElemento(indiceActual++);
        }

        private void verificarModificaciones() {

            if (modificacionesEsperadas != modificaciones) {
                throw new ConcurrentModificationException(
                        "La cola fue modificada durante el recorrido."
                );
            }
        }
    }
}

