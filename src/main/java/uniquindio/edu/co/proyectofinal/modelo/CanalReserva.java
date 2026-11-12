package uniquindio.edu.co.proyectofinal.modelo;

public enum CanalReserva {

    /**
     * Reserva realizada directamente en la recepcion
     * del establecimiento.
     */
    RECEPCION,

    /**
     * Reserva realizada mediante la pagina web
     * oficial del establecimiento.
     */
    WEB,

    /**
     * Reserva recibida a traves de una plataforma
     * externa de reservas.
     */
    PLATAFORMA_EXTERNA,

    /**
     * Reserva realizada mediante una llamada telefonica.
     */
    TELEFONO
}
