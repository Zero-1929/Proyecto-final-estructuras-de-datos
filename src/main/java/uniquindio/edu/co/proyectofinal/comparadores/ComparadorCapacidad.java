package uniquindio.edu.co.proyectofinal.comparadores;

import uniquindio.edu.co.proyectofinal.modelo.Apartamento;

import java.util.Comparator;

/**
 * Compara apartamentos segun su capacidad.
 * Ordena de menor a mayor capacidad.
 */
public class ComparadorCapacidad implements Comparator<Apartamento> {

    /**
     * Compara la capacidad de dos apartamentos.
     */
    @Override
    public int compare(Apartamento a1, Apartamento a2) {

        if (a1 == null || a2 == null) {
            throw new IllegalArgumentException(
                    "Los apartamentos no pueden ser nulos."
            );
        }

        int resultado = Integer.compare(
                a1.getCapacidad(),
                a2.getCapacidad()
        );

        // Desempate por codigo del apartamento.
        if (resultado == 0) {
            return a1.getCodigo().compareTo(a2.getCodigo());
        }

        return resultado;
    }
}