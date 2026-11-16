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
     * Cancela la reserva y deja el folio saldado, sin borrar movimientos previos.
     * AJUSTE_CARGO representa la salida de dinero (devolución), mientras
     * AJUSTE_ABONO anula el cargo pendiente de una estancia cancelada.
     */
    public BigDecimal cancelarYRegistrarDevolucion(String codigo, BigDecimal porcentaje,
                                                   int idMovimientoDevolucion) {
        Reserva reserva = exigirCancelable(codigo);
        BigDecimal monto = previsualizarDevolucion(codigo, porcentaje);
        Folio folio = reserva.getFolio();

        // Saldo que quedaría tras devolver el dinero: debe regularizarse a cero.
        BigDecimal ajuste = folio.calcularSaldo().add(monto);
        boolean registrarDevolucion = monto.signum() > 0;
        boolean registrarRegularizacion = ajuste.signum() != 0;
        if (registrarDevolucion || registrarRegularizacion) {
            if (idMovimientoDevolucion <= 0)
                throw new IllegalArgumentException("ID de movimiento inválido");
        }
        int maxId = 0;
        for (Movimiento movimiento : folio.getMovimientos()) {
            if (movimiento.getId() == idMovimientoDevolucion)
                throw new IllegalArgumentException("ID de movimiento duplicado en el folio");
            maxId = Math.max(maxId, movimiento.getId());
        }
        int idRegularizacion = Math.max(maxId, idMovimientoDevolucion) + 1;
        if (registrarRegularizacion && idRegularizacion <= 0)
            throw new IllegalStateException("No hay identificadores de movimiento disponibles");

        reserva.cancelar();
        LocalDateTime ahora = LocalDateTime.now();
        if (registrarRegularizacion) {
            folio.agregarMovimiento(new Movimiento(
                    registrarDevolucion ? idRegularizacion : idMovimientoDevolucion,
                    "Regularización de cargos por cancelación de reserva " + codigo,
                    ajuste.signum() > 0 ? TipoMovimiento.AJUSTE_ABONO : TipoMovimiento.AJUSTE_CARGO,
                    ajuste.abs(), ahora));
        }
        if (registrarDevolucion) {
            folio.agregarMovimiento(new Movimiento(
                    idMovimientoDevolucion,
                    "Devolución de pagos por cancelación de reserva " + codigo,
                    TipoMovimiento.AJUSTE_CARGO, monto, ahora));
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
