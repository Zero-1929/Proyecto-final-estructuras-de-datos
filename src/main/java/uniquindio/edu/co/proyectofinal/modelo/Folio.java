
package uniquindio.edu.co.proyectofinal.modelo;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.estructuras.Pila;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa el folio o cuenta económica de una reserva.
 *
 * Utiliza ListaDoble para conservar todos los movimientos
 * económicos y Pila para administrar las operaciones
 * que pueden deshacerse.
 *
 * Los movimientos originales nunca se eliminan.
 * Deshacer una operación genera un movimiento contrario.
 */
public class Folio {

    // Identificador único del folio.
    private final int id;

    // Historial completo e inalterable de movimientos.
    private final ListaDoble<Movimiento> movimientos;

    // Pila de operaciones que pueden deshacerse.
    private final Pila<Movimiento> historialOperaciones;

    /**
     * Constructor del folio.
     *
     * @param id identificador único.
     */
    public Folio(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero."
            );
        }

        this.id = id;
        this.movimientos = new ListaDoble<>();
        this.historialOperaciones = new Pila<>();
    }

    /**
     * Obtiene el identificador del folio.
     */
    public int getId() {
        return id;
    }

    /**
     * Devuelve una copia de la lista de movimientos.
     *
     * Se utiliza ListaDoble para evitar colecciones
     * de Java y proteger la lista interna.
     *
     * @return copia de los movimientos registrados.
     */
    public ListaDoble<Movimiento> getMovimientos() {

        ListaDoble<Movimiento> copia = new ListaDoble<>();

        for (Movimiento movimiento : movimientos) {
            copia.agregarFinal(movimiento);
        }

        return copia;
    }

    /**
     * Registra un movimiento en el folio y lo apila
     * en el historial de operaciones deshacibles.
     *
     * @param movimiento movimiento que se desea registrar.
     */
    public void agregarMovimiento(Movimiento movimiento) {

        if (movimiento == null) {
            throw new IllegalArgumentException(
                    "El movimiento no puede ser nulo."
            );
        }

        movimientos.agregarFinal(movimiento);
        historialOperaciones.apilar(movimiento);
    }

    /**
     * Calcula el total de cargos registrados.
     *
     * Incluye alojamiento, servicios adicionales
     * y ajustes que aumentan el saldo.
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
     * Calcula el total de pagos y abonos registrados.
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
     */
    public BigDecimal calcularSaldo() {
        return calcularTotalCargos().subtract(calcularTotalPagos());
    }

    /**
     * Comprueba si no existen movimientos.
     */
    public boolean estaVacio() {
        return movimientos.estaVacia();
    }

    /**
     * Comprueba si el folio está completamente saldado.
     */
    public boolean estaSaldado() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Comprueba si existe un saldo pendiente.
     */
    public boolean tieneSaldoPendiente() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Comprueba si existe un saldo a favor.
     */
    public boolean tieneSaldoAFavor() {
        return calcularSaldo().compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Obtiene la cantidad de movimientos registrados.
     */
    public int cantidadMovimientos() {
        return movimientos.tamano();
    }

    /**
     * Indica si existe una operación que pueda deshacerse.
     */
    public boolean puedeDeshacer() {
        return !historialOperaciones.estaVacia();
    }

    /**
     * Deshace la última operación.
     *
     * El movimiento original permanece en la lista.
     * Se retira de la pila y se registra un movimiento
     * contrario en el historial económico.
     *
     * @param nuevoId identificador del movimiento contrario.
     */
    public void deshacerUltimaOperacion(int nuevoId) {

        if (nuevoId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero."
            );
        }

        if (historialOperaciones.estaVacia()) {
            throw new IllegalStateException(
                    "No existen operaciones para deshacer."
            );
        }

        // Consulta la última operación sin retirarla.
        Movimiento original = historialOperaciones.cima();

        // Construye primero el movimiento contrario.
        Movimiento contrario =
                crearMovimientoContrario(original, nuevoId);

        // Retira de la pila la operación que se deshizo.
        historialOperaciones.desapilar();

        // Agrega el movimiento contrario al historial económico.
        // No se vuelve a apilar, evitando ciclos de reversión.
        movimientos.agregarFinal(contrario);
    }

    /**
     * Construye el movimiento contrario al original.
     *
     * Un pago se revierte mediante un ajuste de cargo.
     * Un cargo se revierte mediante un ajuste de abono.
     *
     * @param original movimiento que se desea revertir.
     * @param nuevoId identificador del nuevo movimiento.
     * @return movimiento contrario.
     */
    private Movimiento crearMovimientoContrario(
            Movimiento original,
            int nuevoId) {

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
