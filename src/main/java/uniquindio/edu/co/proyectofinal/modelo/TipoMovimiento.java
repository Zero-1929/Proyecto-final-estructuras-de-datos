package uniquindio.edu.co.proyectofinal.modelo;

/**
 * Representa los tipos de movimientos economicos
 * que pueden registrarse en el folio de un huesped.
 *
 * @author Integrante 3
 */
public enum TipoMovimiento {

    /** Pago realizado por el huesped. */
    PAGO,

    /** Cargo generado por el alojamiento. */
    CARGO_ALOJAMIENTO,

    /** Cargo generado por servicios adicionales. */
    CARGO_ADICIONAL,

    /** Ajuste que aumenta el saldo pendiente. */
    AJUSTE_CARGO,

    /** Ajuste que disminuye el saldo pendiente. */
    AJUSTE_ABONO
}
