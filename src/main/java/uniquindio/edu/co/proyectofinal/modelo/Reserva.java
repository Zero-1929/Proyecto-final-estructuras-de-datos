
package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representa una reserva individual dentro del Sistema de Gestión
 * de Alojamiento (SGA).
 *
 * Una reserva relaciona al huésped responsable, el apartamento
 * asignado, el periodo de estancia, el canal por el que se realizó
 * y el folio donde se registran los movimientos económicos.
 *
 * También permite controlar los cambios de estado de la reserva.
 *
 *
 */
public class Reserva {

    // Código único que identifica la reserva.
    private final String codigo;

    // Huésped responsable de la reserva.
    private final Huesped huesped;

    // Apartamento asignado a la reserva.
    private final Apartamento apartamento;

    // Periodo de alojamiento solicitado.
    private final Estancia estancia;

    // Estado actual de la reserva.
    private EstadoReserva estado;

    // Canal por el cual se realizó la reserva.
    private final CanalReserva canalReserva;

    // Fecha y hora en que se creó la reserva.
    private final LocalDateTime fechaCreacion;

    // Hora estimada de llegada del huésped.
    private LocalTime horaLlegadaEstimada;

    // Folio donde se registran los cargos y pagos.
    private final Folio folio;

    /**
     * Constructor principal de la reserva.
     *
     * La fecha de creación se establece automáticamente
     * con la fecha y hora actuales.
     *
     * @param codigo código único de la reserva.
     * @param huesped huésped responsable.
     * @param apartamento apartamento asignado.
     * @param estancia periodo de alojamiento.
     * @param canalReserva canal por el que se realizó la reserva.
     * @param folio folio asociado a la reserva.
     */
    public Reserva(String codigo, Huesped huesped,
                   Apartamento apartamento, Estancia estancia,
                   CanalReserva canalReserva, Folio folio) {

        this(codigo, huesped, apartamento, estancia,
                canalReserva, folio, LocalDateTime.now());
    }

    /**
     * Constructor que permite establecer la fecha de creación
     * de la reserva.
     *
     * @param codigo código único de la reserva.
     * @param huesped huésped responsable.
     * @param apartamento apartamento asignado.
     * @param estancia periodo de alojamiento.
     * @param canalReserva canal por el que se realizó la reserva.
     * @param folio folio asociado a la reserva.
     * @param fechaCreacion fecha y hora de creación.
     */
    public Reserva(String codigo, Huesped huesped,
                   Apartamento apartamento, Estancia estancia,
                   CanalReserva canalReserva, Folio folio,
                   LocalDateTime fechaCreacion,LocalTime horaLlegadaEstimada) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la reserva no puede estar vacío."
            );
        }

        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huésped no puede ser nulo."
            );
        }

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo."
            );
        }

        if (estancia == null) {
            throw new IllegalArgumentException(
                    "La estancia no puede ser nula."
            );
        }

        if (canalReserva == null) {
            throw new IllegalArgumentException(
                    "El canal de reserva no puede ser nulo."
            );
        }

        if (folio == null) {
            throw new IllegalArgumentException(
                    "El folio no puede ser nulo."
            );
        }

        if (fechaCreacion == null) {
            throw new IllegalArgumentException(
                    "La fecha de creación no puede ser nula."
            );
        }

        this.codigo = codigo.trim();
        this.huesped = huesped;
        this.apartamento = apartamento;
        this.estancia = estancia;
        this.canalReserva = canalReserva;
        this.folio = folio;
        this.fechaCreacion = fechaCreacion;
        this.horaLlegadaEstimada = horaLlegadaEstimada;

        // Toda reserva nueva inicia en estado pendiente.
        this.estado = EstadoReserva.PENDIENTE;
    }

    // -------------------- GETTERS --------------------

    public String getCodigo() {
        return codigo;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Apartamento getApartamento() {
        return apartamento;
    }

    public Estancia getEstancia() {
        return estancia;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public CanalReserva getCanalReserva() {
        return canalReserva;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalTime getHoraLlegadaEstimada() {
        return horaLlegadaEstimada;
    }

    public Folio getFolio() {
        return folio;
    }

    // -------------------- GESTIÓN DEL ESTADO --------------------

    /**
     * Confirma una reserva que se encuentre pendiente.
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
     * Inicia la estancia cuando el huésped realiza el check-in.
     */
    public void iniciarEstancia() {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se puede iniciar una reserva confirmada."
            );
        }

        estado = EstadoReserva.EN_CURSO;
    }

    /**
     * Finaliza la estancia cuando se realiza el check-out.
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
     * Marca una reserva confirmada cuando el huésped
     * no se presenta en la fecha de llegada.
     */
    public void marcarNoShow() {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como no presentado "
                            + "las reservas confirmadas."
            );
        }

        estado = EstadoReserva.NO_PRESENTADO;
    }

    /**
     * Actualiza la hora estimada de llegada del huésped.
     *
     * @param horaLlegadaEstimada hora prevista de llegada.
     */
    public void setHoraLlegadaEstimada(LocalTime horaLlegadaEstimada) {
        if (horaLlegadaEstimada == null) {
            throw new IllegalArgumentException(
                    "La hora estimada de llegada no puede ser nula."
            );
        }

        this.horaLlegadaEstimada = horaLlegadaEstimada;
    }

    // -------------------- CONSULTAS --------------------

    /**
     * Calcula el número de noches de alojamiento.
     *
     * @return cantidad de noches de la estancia.
     */
    public long calcularNumeroNoches() {
        return estancia.calcularNumeroNoches();
    }

    /**
     * Comprueba si la reserva está confirmada.
     *
     * @return true si está confirmada.
     */
    public boolean estaConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    /**
     * Comprueba si la reserva se encuentra en curso.
     *
     * @return true si está en curso.
     */
    public boolean estaEnCurso() {
        return estado == EstadoReserva.EN_CURSO;
    }

    /**
     * Comprueba si esta reserva y otra reserva utilizan
     * el mismo apartamento durante noches que se cruzan.
     *
     * Este método no verifica por sí solo la disponibilidad
     * completa del apartamento ni considera los estados
     * de las reservas.
     *
     * @param otra reserva que se desea comparar.
     * @return true si comparten apartamento y fechas.
     */
    public boolean seSolapaCon(Reserva otra) {

        if (otra == null) {
            return false;
        }

        boolean mismoApartamento =
                apartamento.getCodigo().equals(
                        otra.getApartamento().getCodigo()
                );

        return mismoApartamento
                && estancia.seSolapaCon(otra.getEstancia());
    }

    // -------------------- EQUALS Y HASHCODE --------------------

    /**
     * Compara dos reservas utilizando su código único.
     *
     * @param obj objeto que se desea comparar.
     * @return true si ambas reservas tienen el mismo código.
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
     * Genera el código hash de la reserva.
     *
     * @return código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    // -------------------- TO STRING --------------------

    /**
     * Genera una representación textual de la reserva.
     *
     * @return información principal de la reserva.
     */
    @Override
    public String toString() {

        return "Reserva {"
                + "codigo='" + codigo + '\''
                + ", huesped=" + huesped.getNombre()
                + ", apartamento=" + apartamento.getCodigo()
                + ", estancia=" + estancia
                + ", estado=" + estado
                + ", canalReserva=" + canalReserva
                + ", fechaCreacion=" + fechaCreacion
                + ", horaLlegadaEstimada=" + horaLlegadaEstimada
                + '}';
    }
}
