
package uniquindio.edu.co.proyectofinal.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un movimiento economico realizado
 * dentro del folio de una reserva en el SGA.
 *
 * Un movimiento puede corresponder a un pago,
 * un cargo por alojamiento, un cargo adicional
 * o un ajuste que aumenta o disminuye el saldo.
 *
 *
 */
public class Movimiento {

    // Identificador unico del movimiento.
    private final int id;

    // Descripcion del movimiento economico.
    private String descripcion;

    // Tipo de movimiento registrado.
    private TipoMovimiento tipo;

    // Valor monetario del movimiento.
    private BigDecimal monto;

    // Fecha y hora en que se registra el movimiento.
    private final LocalDateTime fecha;

    /**
     * Constructor de la clase Movimiento.
     *
     * @param id identificador del movimiento.
     * @param descripcion descripcion del movimiento.
     * @param tipo tipo de movimiento economico.
     * @param monto valor monetario del movimiento.
     * @param fecha fecha y hora del registro.
     */
    public Movimiento(int id, String descripcion,
                      TipoMovimiento tipo, BigDecimal monto,
                      LocalDateTime fecha) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero."
            );
        }

        this.id = id;
        setDescripcion(descripcion);
        setTipo(tipo);
        setMonto(monto);

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha no puede ser nula."
            );
        }

        this.fecha = fecha;
    }

    /**
     * Obtiene el identificador del movimiento.
     *
     * @return identificador del movimiento.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene la descripcion del movimiento.
     *
     * @return descripcion del movimiento.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Modifica la descripcion del movimiento.
     *
     * @param descripcion nueva descripcion.
     */
    public void setDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripcion no puede estar vacia."
            );
        }

        this.descripcion = descripcion.trim();
    }

    /**
     * Obtiene el tipo de movimiento.
     *
     * @return tipo de movimiento.
     */
    public TipoMovimiento getTipo() {
        return tipo;
    }

    /**
     * Modifica el tipo de movimiento.
     *
     * @param tipo nuevo tipo de movimiento.
     */
    public void setTipo(TipoMovimiento tipo) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de movimiento no puede ser nulo."
            );
        }

        this.tipo = tipo;
    }

    /**
     * Obtiene el monto del movimiento.
     *
     * @return monto registrado.
     */
    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * Modifica el monto del movimiento.
     *
     * @param monto nuevo monto.
     */
    public void setMonto(BigDecimal monto) {

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero."
            );
        }

        this.monto = monto;
    }

    /**
     * Obtiene la fecha y hora del movimiento.
     *
     * @return fecha y hora del registro.
     */
    public LocalDateTime getFecha() {
        return fecha;
    }
}
