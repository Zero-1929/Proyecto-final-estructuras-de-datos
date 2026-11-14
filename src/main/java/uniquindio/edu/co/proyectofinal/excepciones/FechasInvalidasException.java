package uniquindio.edu.co.proyectofinal.excepciones;

/**
 * Excepcion controlada para indicar que un rango de fechas no es valido.
 *
 * Se utiliza cuando la fecha de entrada no es anterior a la de salida,
 * cuando falta alguna de las fechas o cuando dos temporadas se solapan.
 */
public class FechasInvalidasException extends Exception {

    /**
     * Crea la excepcion con un mensaje descriptivo.
     *
     * @param mensaje detalle del error
     */
    public FechasInvalidasException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepcion con un mensaje y la causa original.
     *
     * @param mensaje detalle del error
     * @param causa excepcion que origino el problema
     */
    public FechasInvalidasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
