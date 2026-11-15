package uniquindio.edu.co.proyectofinal.servicios;

import java.util.Comparator;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;

/** Ordenacion estable por insercion sobre una lista propia; sin java.util.List. */
public final class OrdenadorListaDoble {
    private OrdenadorListaDoble() {}
    public static <T> ListaDoble<T> ordenar(Iterable<T> origen, Comparator<? super T> criterio) {
        if (origen == null || criterio == null)
            throw new IllegalArgumentException("Origen y comparador requeridos");
        ListaDoble<T> resultado = new ListaDoble<>();
        for (T elemento : origen) {
            int posicion = 0;
            for (T existente : resultado) {
                if (criterio.compare(elemento, existente) < 0) break;
                posicion++;
            }
            resultado.insertar(posicion, elemento);
        }
        return resultado;
    }
}
