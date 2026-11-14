package uniquindio.edu.co.proyectofinal.excepciones;

/**
 * Excepcion que se lanza cuando una reserva se solapa
 * con otra reserva activa del mismo apartamento.
 */
public class ReservaSolapadaException extends RuntimeException {

    /**
     * Constructor con mensaje predeterminado.
     */
    public ReservaSolapadaException() {
        super("El apartamento ya tiene una reserva activa en las fechas solicitadas.");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param mensaje descripcion del error.
     */
    public ReservaSolapadaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa original.
     *
     * @param mensaje descripcion del error.
     * @param causa excepcion que origino el error.
     */
    public ReservaSolapadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}