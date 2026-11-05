package uniquindio.edu.co.proyectofinal.modelo;

public enum EstadoReserva {
    /**
     * La reserva fue creada, pero todavía no se ha confirmado.
     */
    PENDIENTE,

    /**
     * La reserva fue confirmada después de cumplir
     * las condiciones correspondientes.
     */
    CONFIRMADA,

    /**
     * El huésped realizó el check-in y se encuentra
     * disfrutando de su estadía.
     */
    EN_CURSO,

    /**
     * La estadía terminó correctamente y se realizó
     * el check-out.
     */
    FINALIZADA,

    /**
     * La reserva fue cancelada antes de su finalización.
     */
    CANCELADA,

    /**
     * El huésped no se presentó en la fecha de llegada
     * y la reserva fue marcada como no_Presentado.
     */
    NO_PRESENTADO
}
