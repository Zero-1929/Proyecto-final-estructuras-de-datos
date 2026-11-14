package uniquindio.edu.co.proyectofinal.modelo;

/**
 * Estados posibles de una solicitud del canal externo.
 */
public enum EstadoSolicitud {

    /**
     * La solicitud está pendiente de procesamiento.
     */
    PENDIENTE,

    /**
     * La solicitud generó una reserva.
     */
    PROCESADA,

    /**
     * La solicitud chocó con una reserva vigente.
     */
    RECHAZADA
}
