package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.modelo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/** Cancelación con política de devolución explícita; nunca borra movimientos. */
public final class ServicioCancelacionDevolucion {
    private final ServicioReserva reservas;

    public ServicioCancelacionDevolucion(ServicioReserva reservas) {
        if (reservas == null) throw new IllegalArgumentException("Servicio de reservas requerido");
        this.reservas = reservas;
    }

    /** Calcula la devolución sobre los pagos netos, no sobre el precio cotizado. */
    public BigDecimal previsualizarDevolucion(String codigo, BigDecimal porcentaje) {
        Reserva reserva = exigirCancelable(codigo);
        validarPorcentaje(porcentaje);
        BigDecimal pagado = reserva.getFolio().calcularTotalPagos();
        if (pagado.signum() < 0) pagado = BigDecimal.ZERO;
        return pagado.multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Registra la devolución como AJUSTE_CARGO, compensando el pago previo.
     * Esto registra contablemente una salida de dinero; la transferencia bancaria
     * o entrega física debe realizarse por fuera de este servicio.
     * Si la devolución es cero, no crea un movimiento ficticio.
     */
    public BigDecimal cancelarYRegistrarDevolucion(String codigo, BigDecimal porcentaje,
                                                   int idMovimientoDevolucion) {
        Reserva reserva = exigirCancelable(codigo);
        BigDecimal monto = previsualizarDevolucion(codigo, porcentaje);
        if (monto.signum() > 0) {
            if (idMovimientoDevolucion <= 0)
                throw new IllegalArgumentException("ID de movimiento inválido");
            for (Movimiento movimiento : reserva.getFolio().getMovimientos())
                if (movimiento.getId() == idMovimientoDevolucion)
                    throw new IllegalArgumentException("ID de movimiento duplicado en el folio");
        }
        // Primero cambia estado, después registra movimiento ya validado.
        reserva.cancelar();
        if (monto.signum() > 0) {
            reserva.getFolio().agregarMovimiento(new Movimiento(
                    idMovimientoDevolucion, "Devolución por cancelación de reserva " + codigo,
                    TipoMovimiento.AJUSTE_CARGO, monto, LocalDateTime.now()));
        }
        return monto;
    }

    private Reserva exigirCancelable(String codigo) {
        Reserva reserva = reservas.buscarPorCodigo(codigo);
        if (reserva == null) throw new IllegalArgumentException("Reserva no encontrada: " + codigo);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE
                && reserva.getEstado() != EstadoReserva.CONFIRMADA)
            throw new IllegalStateException("Solo se pueden cancelar reservas pendientes o confirmadas");
        return reserva;
    }

    private void validarPorcentaje(BigDecimal porcentaje) {
        if (porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) < 0
                || porcentaje.compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Porcentaje de devolución debe estar entre 0 y 100");
    }
}
