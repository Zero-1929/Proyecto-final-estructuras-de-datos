package uniquindio.edu.co.proyectofinal.comparadores;

import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;

import java.util.Comparator;

/**
 * Compara apartamentos por su tarifa segun la temporada.
 * Ordena del precio menor al mayor.
 */
public class ComparadorPrecio implements Comparator<Apartamento> {

    private final TipoTemporada temporada;

    /**
     * Constructor que recibe la temporada a comparar.
     */
    public ComparadorPrecio(TipoTemporada temporada) {

        if (temporada == null) {
            throw new IllegalArgumentException(
                    "La temporada no puede ser nula."
            );
        }

        this.temporada = temporada;
    }

    /**
     * Compara las tarifas de dos apartamentos.
     */
    @Override
    public int compare(Apartamento a1, Apartamento a2) {

        if (a1 == null || a2 == null) {
            throw new IllegalArgumentException(
                    "Los apartamentos no pueden ser nulos."
            );
        }

        int resultado = a1.obtenerTarifa(temporada)
                .compareTo(a2.obtenerTarifa(temporada));

        // Desempate por codigo para mantener un orden estable.
        if (resultado == 0) {
            return a1.getCodigo().compareTo(a2.getCodigo());
        }

        return resultado;
    }
}