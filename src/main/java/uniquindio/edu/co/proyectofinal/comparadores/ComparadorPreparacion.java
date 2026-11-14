package uniquindio.edu.co.proyectofinal.comparadores;

import uniquindio.edu.co.proyectofinal.modelo.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

/**
 * Compara las reservas según la prioridad de preparación
 * de sus respectivos apartamentos.
 *
 * Criterios:
 * 1. Reservas con entrada programada para hoy.
 * 2. Menor hora estimada de llegada.
 * 3. Reservas sin hora estimada después de las que sí tienen.
 * 4. Código de reserva como criterio de desempate.
 */
public class ComparadorPreparacion implements Comparator<Reserva> {

    @Override
    public int compare(Reserva reserva1, Reserva reserva2) {

        if (reserva1 == null || reserva2 == null) {
            throw new IllegalArgumentException(
                    "Las reservas no pueden ser nulas."
            );
        }

        LocalDate hoy = LocalDate.now();

        boolean entradaHoy1 = reserva1.getEstancia()
                .getFechaEntrada().equals(hoy);

        boolean entradaHoy2 = reserva2.getEstancia()
                .getFechaEntrada().equals(hoy);

        // Las reservas con entrada hoy tienen prioridad.
        if (entradaHoy1 && !entradaHoy2) {
            return -1;
        }

        if (!entradaHoy1 && entradaHoy2) {
            return 1;
        }

        // Si ambas reservas tienen entrada hoy,
        // se comparan sus horas estimadas de llegada.
        if (entradaHoy1) {

            LocalTime hora1 = reserva1.getHoraLlegadaEstimada();
            LocalTime hora2 = reserva2.getHoraLlegadaEstimada();

            // Las reservas sin hora quedan después.
            if (hora1 == null && hora2 != null) {
                return 1;
            }

            if (hora1 != null && hora2 == null) {
                return -1;
            }

            if (hora1 != null && hora2 != null) {

                int resultadoHora = hora1.compareTo(hora2);

                if (resultadoHora != 0) {
                    return resultadoHora;
                }
            }
        }

        // Desempate mediante el código de reserva.
        return reserva1.getCodigo().compareTo(
                reserva2.getCodigo()
        );
    }
}

