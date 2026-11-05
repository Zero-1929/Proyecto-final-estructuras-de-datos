
package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDateTime;

/**
 * Representa una reserva realizada por un huesped
 * dentro del Sistema de Gestion de Alojamiento (SGA).
 *
 * Almacena la informacion principal de la reserva,
 * su estado, el canal de origen y el folio asociado
 * a sus movimientos economicos.
 *
 * @author Integrante 3
 */
public class Reserva {

    // Codigo unico que identifica la reserva.
    private final String codigo;

    // Huesped titular de la reserva.
    private final Huesped huesped;

    // Estado actual de la reserva.
    private EstadoReserva estado;

    // Canal mediante el cual se realizo la reserva.
    private final CanalReserva canalReserva;

    // Fecha y hora en que se creo la reserva.
    private final LocalDateTime fechaCreacion;

    // Folio asociado a la reserva.
    private final Folio folio;

    /**
     * Constructor de la clase Reserva.
     *
     * La reserva se crea inicialmente en estado PENDIENTE.
     *
     * @param codigo codigo unico de la reserva.
     * @param huesped huesped titular de la reserva.
     * @param canalReserva canal mediante el cual se realizo.
     * @param folio folio economico asociado a la reserva.
     */
    public Reserva(String codigo, Huesped huesped,
                   CanalReserva canalReserva, Folio folio) {

        this(codigo, huesped, canalReserva, folio, LocalDateTime.now());
    }

    /**
     * Constructor que permite establecer la fecha de creacion.
     *
     * @param codigo codigo unico de la reserva.
     * @param huesped huesped titular de la reserva.
     * @param canalReserva canal mediante el cual se realizo.
     * @param folio folio economico asociado a la reserva.
     * @param fechaCreacion fecha y hora de creacion.
     */
    public Reserva(String codigo, Huesped huesped,
                   CanalReserva canalReserva, Folio folio,
                   LocalDateTime fechaCreacion) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El codigo de la reserva no puede estar vacio."
            );
        }

        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huesped titular no puede ser nulo."
            );
        }

        if (canalReserva == null) {
            throw new IllegalArgumentException(
                    "El canal de reserva no puede ser nulo."
            );
        }

        if (folio == null) {
            throw new IllegalArgumentException(
                    "El folio asociado no puede ser nulo."
            );
        }

        if (fechaCreacion == null) {
            throw new IllegalArgumentException(
                    "La fecha de creacion no puede ser nula."
            );
        }

        this.codigo = codigo.trim();
        this.huesped = huesped;
        this.canalReserva = canalReserva;
        this.folio = folio;
        this.fechaCreacion = fechaCreacion;
        this.estado = EstadoReserva.PENDIENTE;
    }

    /**
     * Obtiene el codigo de la reserva.
     *
     * @return codigo de la reserva.
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Obtiene el huesped titular de la reserva.
     *
     * @return huesped titular.
     */
    public Huesped getHuesped() {
        return huesped;
    }

    /**
     * Obtiene el estado actual de la reserva.
     *
     * @return estado de la reserva.
     */
    public EstadoReserva getEstado() {
        return estado;
    }

    /**
     * Obtiene el canal mediante el cual se realizo la reserva.
     *
     * @return canal de reserva.
     */
    public CanalReserva getCanalReserva() {
        return canalReserva;
    }

    /**
     * Obtiene la fecha y hora de creacion de la reserva.
     *
     * @return fecha de creacion.
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Obtiene el folio asociado a la reserva.
     *
     * @return folio de la reserva.
     */
    public Folio getFolio() {
        return folio;
    }

    /**
     * Confirma una reserva que se encuentra pendiente.
     */
    public void confirmar() {

        if (estado != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar reservas pendientes."
            );
        }

        estado = EstadoReserva.CONFIRMADA;
    }

    /**
     * Inicia la estancia de una reserva confirmada.
     */
    public void iniciarEstancia() {

        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se pueden iniciar reservas confirmadas."
            );
        }

        estado = EstadoReserva.EN_CURSO;
    }

    /**
     * Finaliza una reserva que se encuentra en curso.
     */
    public void finalizar() {

        if (estado != EstadoReserva.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se pueden finalizar reservas en curso."
            );
        }

        estado = EstadoReserva.FINALIZADA;
    }

    /**
     * Cancela una reserva pendiente o confirmada.
     */
    public void cancelar() {

        if (estado != EstadoReserva.PENDIENTE
                && estado != EstadoReserva.CONFIRMADA) {

            throw new IllegalStateException(
                    "La reserva no se puede cancelar en su estado actual."
            );
        }

        estado = EstadoReserva.CANCELADA;
    }

    /**
     * Marca como no presentada una reserva confirmada.
     */
    public void marcarNoShow() {

        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como no-show reservas confirmadas."
            );
        }

        estado = EstadoReserva.NO_PRESENTADO;
    }

    /**
     * Comprueba si la reserva se encuentra confirmada.
     *
     * @return true si la reserva esta confirmada.
     */
    public boolean estaConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    /**
     * Comprueba si la reserva se encuentra en curso.
     *
     * @return true si la reserva esta en curso.
     */
    public boolean estaEnCurso() {
        return estado == EstadoReserva.EN_CURSO;
    }

    /**
     * Compara dos reservas por su codigo identificador.
     *
     * @param obj objeto que se desea comparar.
     * @return true si ambas reservas tienen el mismo codigo.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Reserva otra)) {
            return false;
        }

        return codigo.equals(otra.codigo);
    }

    /**
     * Genera el codigo hash de la reserva utilizando su codigo.
     *
     * @return codigo hash de la reserva.
     */
    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    /**
     * Representacion textual de la reserva.
     *
     * @return informacion principal de la reserva.
     */
    @Override
    public String toString() {

        return "Reserva: " + codigo
                + " | Huesped: " + huesped.getNombre()
                + " | Estado: " + estado
                + " | Canal: " + canalReserva
                + " | Fecha de creacion: " + fechaCreacion;
    }
}
