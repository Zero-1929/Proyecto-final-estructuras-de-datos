package uniquindio.edu.co.proyectofinal.excepciones;

/**
 * Excepcion que se lanza cuando la cantidad de personas
 * supera la capacidad maxima de un apartamento.
 */
public class CapacidadExcedidaException extends RuntimeException {

    /**
     * Constructor con mensaje predeterminado.
     */
    public CapacidadExcedidaException() {
        super("La cantidad de ocupantes supera la capacidad del apartamento.");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param mensaje descripcion del error.
     */
    public CapacidadExcedidaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa original.
     *
     * @param mensaje descripcion del error.
     * @param causa excepcion que origino el error.
     */
    public CapacidadExcedidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
