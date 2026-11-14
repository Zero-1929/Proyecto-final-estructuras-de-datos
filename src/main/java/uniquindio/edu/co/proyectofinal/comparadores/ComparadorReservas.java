package uniquindio.edu.co.proyectofinal.comparadores;

import java.util.Comparator;

import uniquindio.edu.co.proyectofinal.modelo.Reserva;

/**
 * Compara reservas por fecha de entrada.
 *
 * Si dos reservas tienen la misma fecha de entrada se compara la fecha
 * de salida y, por ultimo, el codigo de la reserva.
 */
public class ComparadorReservas implements Comparator<Reserva> {

    /**
     * Atributo que indica si el orden se invierte.
     */
    private final boolean descendente;

    /**
     * Crea el comparador en orden ascendente.
     */
    public ComparadorReservas() {
        this(false);
    }

    /**
     * Crea el comparador indicando el sentido del orden.
     *
     * @param descendente true para ordenar de mayor a menor
     */
    public ComparadorReservas(boolean descendente) {
        this.descendente = descendente;
    }

    /**
     * Compara dos reservas.
     *
     * @param primera primera reserva
     * @param segunda segunda reserva
     * @return valor negativo, cero o positivo segun el orden
     */
    @Override
    public int compare(Reserva primera, Reserva segunda) {

        if (primera == null || segunda == null) {
            throw new IllegalArgumentException(
                    "Las reservas a comparar no pueden ser nulas."
            );
        }

        int comparacion = primera.getEstancia().getFechaEntrada()
                .compareTo(segunda.getEstancia().getFechaEntrada());

        if (comparacion == 0) {
            comparacion = primera.getEstancia().getFechaSalida()
                    .compareTo(segunda.getEstancia().getFechaSalida());
        }

        if (comparacion == 0) {
            comparacion = primera.getCodigo().compareTo(segunda.getCodigo());
        }

        return descendente ? -comparacion : comparacion;
    }
}
