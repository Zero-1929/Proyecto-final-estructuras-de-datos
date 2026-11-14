package uniquindio.edu.co.proyectofinal.excepciones;

/**
 * Excepción que representa un intento de realizar una operación
 * que no está permitida por el estado actual de un objeto.
 *
 * Se utiliza, por ejemplo, cuando se intenta realizar una
 * transición de estado no válida en una reserva o apartamento.
 */
public class EstadoInvalidoException extends IllegalStateException {

    /**
     * Construye una excepción con un mensaje descriptivo.
     *
     * @param mensaje explicación del estado inválido.
     */
    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una excepción con un mensaje y una causa original.
     *
     * @param mensaje explicación del estado inválido.
     * @param causa excepción que originó el problema.
     */
    public EstadoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

