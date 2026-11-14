package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.CanalReserva;
import uniquindio.edu.co.proyectofinal.modelo.EstadoReserva;
import uniquindio.edu.co.proyectofinal.modelo.EstadoSolicitud;
import uniquindio.edu.co.proyectofinal.modelo.Movimiento;
import uniquindio.edu.co.proyectofinal.modelo.Reserva;
import uniquindio.edu.co.proyectofinal.modelo.SolicitudExterna;
import uniquindio.edu.co.proyectofinal.modelo.Temporada;
import uniquindio.edu.co.proyectofinal.modelo.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Servicio encargado de generar los reportes estadísticos
 * del Sistema de Gestión de Alojamiento.
 *
 * Permite consultar la ocupación de apartamentos,
 * los ingresos por periodo, las reservas por canal,
 * los conflictos del canal externo y los tiempos
 * promedio de preparación.
 */
public class ServicioReportes {

    /**
     * Calcula el porcentaje de ocupación de un apartamento
     * durante un periodo determinado.
     *
     * La fecha de entrada se incluye y la fecha de salida
     * se excluye, porque la noche de salida no se ocupa.
     *
     * @param apartamento apartamento que se desea consultar.
     * @param reservas reservas registradas.
     * @param fechaInicio inicio del periodo.
     * @param fechaFin fecha de salida del periodo.
     * @return porcentaje de ocupación entre 0 y 100.
     */
    public double calcularOcupacionApartamento(
            Apartamento apartamento,
            Reserva[] reservas,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        validarApartamento(apartamento);
        validarReservas(reservas);
        validarPeriodo(fechaInicio, fechaFin);

        long nochesTotales =
                java.time.temporal.ChronoUnit.DAYS.between(
                        fechaInicio, fechaFin);

        if (nochesTotales == 0) {
            return 0.0;
        }

        long nochesOcupadas = 0;

        for (LocalDate noche = fechaInicio;
             noche.isBefore(fechaFin);
             noche = noche.plusDays(1)) {

            if (estaOcupado(apartamento, reservas, noche)) {
                nochesOcupadas++;
            }
        }

        return (nochesOcupadas * 100.0) / nochesTotales;
    }

    /**
     * Calcula el porcentaje de ocupación de un apartamento
     * durante una temporada determinada.
     *
     * Las fechas de inicio y fin de Temporada se consideran
     * incluidas, de acuerdo con su método contieneFecha().
     *
     * @param apartamento apartamento que se desea consultar.
     * @param reservas reservas registradas.
     * @param temporada temporada que se desea analizar.
     * @return porcentaje de ocupación.
     */
    public double calcularOcupacionPorTemporada(
            Apartamento apartamento,
            Reserva[] reservas,
            Temporada temporada) {

        validarApartamento(apartamento);
        validarReservas(reservas);

        if (temporada == null) {
            throw new IllegalArgumentException(
                    "La temporada no puede ser nula.");
        }

        long nochesTotales = 0;
        long nochesOcupadas = 0;

        for (LocalDate noche = temporada.getFechaInicio();
             !noche.isAfter(temporada.getFechaFin());
             noche = noche.plusDays(1)) {

            nochesTotales++;

            if (estaOcupado(apartamento, reservas, noche)) {
                nochesOcupadas++;
            }
        }

        if (nochesTotales == 0) {
            return 0.0;
        }

        return (nochesOcupadas * 100.0) / nochesTotales;
    }

    /**
     * Comprueba si un apartamento tiene una reserva
     * que ocupe una noche determinada.
     *
     * Se excluyen las reservas canceladas y no presentadas.
     */
    private boolean estaOcupado(
            Apartamento apartamento,
            Reserva[] reservas,
            LocalDate noche) {

        for (Reserva reserva : reservas) {

            if (reserva == null) {
                continue;
            }

            boolean mismoApartamento =
                    reserva.getApartamento().getCodigo()
                            .equals(apartamento.getCodigo());

            boolean estadoValido =
                    reserva.getEstado() == EstadoReserva.PENDIENTE
                            || reserva.getEstado() == EstadoReserva.CONFIRMADA
                            || reserva.getEstado() == EstadoReserva.EN_CURSO
                            || reserva.getEstado() == EstadoReserva.FINALIZADA;

            if (mismoApartamento
                    && estadoValido
                    && reserva.getEstancia().contieneNoche(noche)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Calcula los ingresos netos registrados en los folios
     * durante un periodo determinado.
     *
     * Los pagos aumentan los ingresos.
     * Los ajustes de cargo disminuyen los ingresos cuando
     * representan la reversión de un pago.
     *
     * @param reservas reservas registradas.
     * @param fechaInicio inicio del periodo.
     * @param fechaFin fin del periodo.
     * @return valor neto de los pagos del periodo.
     */
    public BigDecimal calcularIngresosPorPeriodo(
            Reserva[] reservas,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        validarReservas(reservas);
        validarPeriodo(fechaInicio, fechaFin);

        BigDecimal ingresos = BigDecimal.ZERO;

        for (Reserva reserva : reservas) {

            if (reserva == null) {
                continue;
            }

            for (Movimiento movimiento :
                    reserva.getFolio().getMovimientos()) {

                LocalDateTime fechaMovimiento = movimiento.getFecha();

                boolean dentroDelPeriodo =
                        !fechaMovimiento.isBefore(fechaInicio)
                                && !fechaMovimiento.isAfter(fechaFin);

                if (!dentroDelPeriodo) {
                    continue;
                }

                if (movimiento.getTipo() == TipoMovimiento.PAGO) {

                    ingresos = ingresos.add(movimiento.getMonto());

                } else if (
                        movimiento.getTipo() == TipoMovimiento.AJUSTE_CARGO) {

                    ingresos = ingresos.subtract(movimiento.getMonto());
                }
            }
        }

        return ingresos;
    }

    /**
     * Cuenta las reservas realizadas por un canal específico.
     *
     * Se incluyen todos los estados de reserva:
     * pendientes, confirmadas, en curso, finalizadas,
     * canceladas y no presentadas.
     *
     * @param reservas reservas registradas.
     * @param canal canal que se desea consultar.
     * @return cantidad de reservas del canal.
     */
    public int contarReservasPorCanal(
            Reserva[] reservas,
            CanalReserva canal) {

        validarReservas(reservas);

        if (canal == null) {
            throw new IllegalArgumentException(
                    "El canal no puede ser nulo.");
        }

        int cantidad = 0;

        for (Reserva reserva : reservas) {

            if (reserva != null
                    && reserva.getCanalReserva() == canal) {

                cantidad++;
            }
        }

        return cantidad;
    }

    /**
     * Cuenta las solicitudes externas rechazadas.
     *
     * Según EstadoSolicitud, una solicitud RECHAZADA
     * corresponde a una solicitud que chocó con una
     * reserva vigente.
     *
     * @param solicitudes solicitudes externas procesadas.
     * @return cantidad de conflictos del canal externo.
     */
    public int contarConflictosDeCanal(
            SolicitudExterna[] solicitudes) {

        if (solicitudes == null) {
            throw new IllegalArgumentException(
                    "El arreglo de solicitudes no puede ser nulo.");
        }

        int conflictos = 0;

        for (SolicitudExterna solicitud : solicitudes) {

            if (solicitud != null
                    && solicitud.getEstado() == EstadoSolicitud.RECHAZADA) {

                conflictos++;
            }
        }

        return conflictos;
    }

    /**
     * Calcula el tiempo promedio de preparación
     * utilizando los tiempos registrados en minutos.
     *
     * Los tiempos deben corresponder a preparaciones
     * que ya hayan finalizado.
     *
     * @param tiemposMinutos arreglo con los tiempos registrados.
     * @return promedio de minutos de preparación.
     */
    public double calcularTiempoPromedioPreparacion(
            long[] tiemposMinutos) {

        if (tiemposMinutos == null) {
            throw new IllegalArgumentException(
                    "El arreglo de tiempos no puede ser nulo.");
        }

        if (tiemposMinutos.length == 0) {
            return 0.0;
        }

        long suma = 0;

        for (long tiempo : tiemposMinutos) {

            if (tiempo < 0) {
                throw new IllegalArgumentException(
                        "Los tiempos de preparación no pueden ser negativos.");
            }

            suma = Math.addExact(suma, tiempo);
        }

        return (double) suma / tiemposMinutos.length;
    }

    /**
     * Valida que el apartamento exista.
     */
    private void validarApartamento(Apartamento apartamento) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo.");
        }
    }

    /**
     * Valida que el arreglo de reservas exista.
     */
    private void validarReservas(Reserva[] reservas) {

        if (reservas == null) {
            throw new IllegalArgumentException(
                    "El arreglo de reservas no puede ser nulo.");
        }
    }

    /**
     * Valida un periodo de fechas.
     */
    private void validarPeriodo(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException(
                    "Las fechas del periodo no pueden ser nulas.");
        }

        if (!fechaFin.isAfter(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha final debe ser posterior a la inicial.");
        }
    }

    /**
     * Valida un periodo de fecha y hora.
     */
    private void validarPeriodo(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException(
                    "Las fechas del periodo no pueden ser nulas.");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la inicial.");
        }
    }
}
