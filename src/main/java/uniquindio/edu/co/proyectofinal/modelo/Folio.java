
package uniquindio.edu.co.proyectofinal.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el folio o cuenta economica de una reserva
 * dentro del Sistema de Gestion de Alojamiento (SGA).
 *
 * Permite registrar los movimientos economicos,
 * consultar los cargos, los pagos y calcular
 * el saldo pendiente de la cuenta.
 *
 * @author Integrante 3
 */
public class Folio {

    // Identificador unico del folio.
    private final int id;

    // Lista de movimientos economicos registrados.
    private final List<Movimiento> movimientos;

    /**
     * Constructor de la clase Folio.
     *
     * @param id identificador unico del folio.
     */
    public Folio(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero."
            );
        }

        this.id = id;
        this.movimientos = new ArrayList<>();
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
     * Obtiene los movimientos registrados en el folio.
     *
     * @return lista de movimientos economicos.
     */
    public List<Movimiento> getMovimientos() {
        return new ArrayList<>(movimientos);
    }

    /**
     * Registra un nuevo movimiento en el folio.
     *
     * @param movimiento movimiento economico que se desea registrar.
     */
    public void agregarMovimiento(Movimiento movimiento) {

        if (movimiento == null) {
            throw new IllegalArgumentException(
                    "El movimiento no puede ser nulo."
            );
        }

        movimientos.add(movimiento);
    }

    /**
     * Calcula el total de los cargos registrados.
     *
     * Incluye los cargos por alojamiento,
     * servicios adicionales y ajustes de cargo.
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
     * @return true si no tiene movimientos, false en caso contrario.
     */
    public boolean estaVacio() {
        return movimientos.isEmpty();
    }

    /**
     * Comprueba si el folio se encuentra completamente saldado.
     *
     * @return true si el saldo es cero, false en caso contrario.
     */
    public boolean estaSaldado() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Comprueba si el folio tiene un saldo pendiente por pagar.
     *
     * @return true si el saldo es positivo, false en caso contrario.
     */
    public boolean tieneSaldoPendiente() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Comprueba si el folio tiene un saldo a favor.
     *
     * @return true si el saldo es negativo, false en caso contrario.
     */
    public boolean tieneSaldoAFavor() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Obtiene la cantidad de movimientos registrados.
     *
     * @return numero de movimientos.
     */
    public int cantidadMovimientos() {
        return movimientos.size();
    }
}
