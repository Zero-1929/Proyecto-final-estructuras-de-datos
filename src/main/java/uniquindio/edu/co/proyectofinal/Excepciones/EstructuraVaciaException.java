package uniquindio.edu.co.proyectofinal.Excepciones;

/**
 * Excepción personalizada que representa un intento de realizar
 * una operación sobre una estructura de datos que se encuentra vacía.
 *
 * Ejemplos:
 * - Desencolar cuando una cola está vacía.
 * - Desapilar cuando una pila está vacía.
 * - Consultar un elemento cuando no existen elementos.
 */
public class EstructuraVaciaException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo.
     *
     * @param mensaje descripción del error ocurrido.
     */
    public EstructuraVaciaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye la excepción con un mensaje y su causa original.
     *
     * @param mensaje descripción del error ocurrido.
     * @param causa excepción que originó el problema.
     */
    public EstructuraVaciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

