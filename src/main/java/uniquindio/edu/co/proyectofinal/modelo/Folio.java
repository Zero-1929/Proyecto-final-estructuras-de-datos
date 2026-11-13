
package uniquindio.edu.co.proyectofinal.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el folio o cuenta económica de una reserva
 * dentro del Sistema de Gestión de Alojamiento (SGA).
 *
 * Permite registrar movimientos económicos, consultar
 * cargos, pagos y saldos, y deshacer la última operación
 * mediante el registro de un movimiento contrario.
 *
 * El historial de operaciones utiliza temporalmente
 * ArrayList para simular una pila. Posteriormente se
 * reemplazará por la estructura Pila<T> desarrollada
 * por el equipo.
 *
 *
 */
public class Folio {

    // Identificador único del folio.
    private final int id;

    // Lista de todos los movimientos económicos del folio.
    private final List<Movimiento> movimientos;

    // Historial temporal que simula una pila de operaciones.
    private final List<Movimiento> historialOperaciones;

    /**
     * Constructor de la clase Folio.
     *
     * @param id identificador único del folio.
     */
    public Folio(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero."
            );
        }

        this.id = id;
        this.movimientos = new ArrayList<>();
        this.historialOperaciones = new ArrayList<>();
    }

    /**
     * Obtiene el identificador del folio.
     *
     * @return identificador del folio.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene una copia de los movimientos registrados.
     *
     * Se devuelve una copia para evitar modificaciones
     * directas sobre la lista interna.
     *
     * @return lista de movimientos económicos.
     */
    public List<Movimiento> getMovimientos() {
        return new ArrayList<>(movimientos);
    }

    /**
     * Registra un nuevo movimiento económico en el folio.
     *
     * El movimiento se agrega tanto al registro económico
     * como al historial de operaciones para permitir
     * deshacerlo posteriormente.
     *
     * @param movimiento movimiento económico que se desea registrar.
     */
    public void agregarMovimiento(Movimiento movimiento) {

        if (movimiento == null) {
            throw new IllegalArgumentException(
                    "El movimiento no puede ser nulo."
            );
        }

        movimientos.add(movimiento);
        historialOperaciones.add(movimiento);
    }

    /**
     * Calcula el total de los cargos registrados.
     *
     * Incluye cargos por alojamiento, servicios adicionales
     * y ajustes que aumentan el saldo.
     *
     * @return valor total de los cargos.
     */
    public BigDecimal calcularTotalCargos() {

        BigDecimal total = BigDecimal.ZERO;

        for (Movimiento movimiento : movimientos) {

            TipoMovimiento tipo = movimiento.getTipo();

            if (tipo == TipoMovimiento.CARGO_ALOJAMIENTO
                    || tipo == TipoMovimiento.CARGO_ADICIONAL
                    || tipo == TipoMovimiento.AJUSTE_CARGO) {

                total = total.add(movimiento.getMonto());
            }
        }

        return total;
    }

    /**
     * Calcula el total de los pagos y abonos realizados.
     *
     * Incluye los pagos y los ajustes que disminuyen
     * el saldo pendiente.
     *
     * @return valor total de los pagos y abonos.
     */
    public BigDecimal calcularTotalPagos() {

        BigDecimal total = BigDecimal.ZERO;

        for (Movimiento movimiento : movimientos) {

            TipoMovimiento tipo = movimiento.getTipo();

            if (tipo == TipoMovimiento.PAGO
                    || tipo == TipoMovimiento.AJUSTE_ABONO) {

                total = total.add(movimiento.getMonto());
            }
        }

        return total;
    }

    /**
     * Calcula el saldo pendiente del folio.
     *
     * El saldo corresponde a la diferencia entre
     * el total de cargos y el total de pagos.
     *
     * @return saldo de la cuenta.
     */
    public BigDecimal calcularSaldo() {
        return calcularTotalCargos().subtract(calcularTotalPagos());
    }

    /**
     * Comprueba si el folio no tiene movimientos registrados.
     *
     * @return true si no tiene movimientos.
     */
    public boolean estaVacio() {
        return movimientos.isEmpty();
    }

    /**
     * Comprueba si el folio se encuentra completamente saldado.
     *
     * @return true si el saldo es cero.
     */
    public boolean estaSaldado() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Comprueba si el folio tiene un saldo pendiente por pagar.
     *
     * @return true si el saldo es positivo.
     */
    public boolean tieneSaldoPendiente() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Comprueba si el folio tiene un saldo a favor.
     *
     * @return true si el saldo es negativo.
     */
    public boolean tieneSaldoAFavor() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Obtiene la cantidad de movimientos registrados.
     *
     * @return número de movimientos.
     */
    public int cantidadMovimientos() {
        return movimientos.size();
    }

    /**
     * Comprueba si existe alguna operación que pueda deshacerse.
     *
     * @return true si el historial contiene operaciones.
     */
    public boolean puedeDeshacer() {
        return !historialOperaciones.isEmpty();
    }

    /**
     * Deshace la última operación registrada en el historial.
     *
     * No elimina ni modifica el movimiento original.
     * En su lugar, registra un movimiento contrario que
     * compensa su efecto económico.
     *
     * El movimiento contrario no se agrega nuevamente
     * al historial de operaciones, evitando ciclos
     * de reversiones.
     *
     * @param nuevoId identificador del movimiento contrario.
     */
    public void deshacerUltimaOperacion(int nuevoId) {

        if (nuevoId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero."
            );
        }

        if (historialOperaciones.isEmpty()) {
            throw new IllegalStateException(
                    "No existen operaciones para deshacer."
            );
        }

        // Consulta la última operación registrada.
        Movimiento original =
                historialOperaciones.get(historialOperaciones.size() - 1);

        // Crea el movimiento que revierte su efecto.
        Movimiento contrario = crearMovimientoContrario(original, nuevoId);

        // Retira la operación del historial, pero no del folio.
        historialOperaciones.remove(historialOperaciones.size() - 1);

        // Registra el movimiento contrario en el folio.
        movimientos.add(contrario);
    }

    /**
     * Crea un movimiento contrario al movimiento original.
     *
     * Los cargos se revierten mediante abonos y los pagos
     * se revierten mediante cargos.
     *
     * @param original movimiento que se desea revertir.
     * @param nuevoId identificador del movimiento contrario.
     * @return nuevo movimiento que compensa el original.
     */
    private Movimiento crearMovimientoContrario(
            Movimiento original, int nuevoId) {

        TipoMovimiento tipoContrario;

        switch (original.getTipo()) {

            case PAGO:
                tipoContrario = TipoMovimiento.AJUSTE_CARGO;
                break;

            case CARGO_ALOJAMIENTO:
            case CARGO_ADICIONAL:
            case AJUSTE_CARGO:
                tipoContrario = TipoMovimiento.AJUSTE_ABONO;
                break;

            case AJUSTE_ABONO:
                tipoContrario = TipoMovimiento.AJUSTE_CARGO;
                break;

            default:
                throw new IllegalStateException(
                        "No se puede revertir este tipo de movimiento."
                );
        }

        return new Movimiento(
                nuevoId,
                "Reversión del movimiento " + original.getId()
                        + ": " + original.getDescripcion(),
                tipoContrario,
                original.getMonto(),
                LocalDateTime.now()
        );
    }
}
