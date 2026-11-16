
package uniquindio.edu.co.proyectofinal.modelo;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Representa una solicitud recibida por el canal externo.
 *
 * Puede almacenar los ocupantes y sus fechas de nacimiento
 * cuando la plataforma suministre esa información.
 *
 * Si no se suministran los datos personales, conserva
 * el número de personas sin inventar edades.
 */
public class SolicitudExterna {

    private final String idSolicitud;
    private final String nombreSolicitante;
    private final String documento;
    private final String telefono;
    private final String correo;
    private final String codigoApartamento;

    private final LocalDate fechaEntrada;
    private final LocalDate fechaSalida;
    private final int numeroPersonas;
    private final LocalDateTime fechaRecepcion;

    private final ListaDoble<Ocupante> ocupantes;

    private EstadoSolicitud estado;
    private String motivoRechazo;

    /**
     * Constructor compatible con solicitudes antiguas.
     * No contiene información individual de los ocupantes.
     */
    public SolicitudExterna(
            String idSolicitud,
            String nombreSolicitante,
            String documento,
            String telefono,
            String correo,
            String codigoApartamento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            int numeroPersonas) {

        this(
                idSolicitud,
                nombreSolicitante,
                documento,
                telefono,
                correo,
                codigoApartamento,
                fechaEntrada,
                fechaSalida,
                numeroPersonas,
                LocalDateTime.now(),
                new ListaDoble<>()
        );
    }

    /**
     * Constructor compatible con solicitudes que ya tienen
     * una fecha de recepción definida.
     */
    public SolicitudExterna(
            String idSolicitud,
            String nombreSolicitante,
            String documento,
            String telefono,
            String correo,
            String codigoApartamento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            int numeroPersonas,
            LocalDateTime fechaRecepcion) {

        this(
                idSolicitud,
                nombreSolicitante,
                documento,
                telefono,
                correo,
                codigoApartamento,
                fechaEntrada,
                fechaSalida,
                numeroPersonas,
                fechaRecepcion,
                new ListaDoble<>()
        );
    }

    /**
     * Constructor completo con ocupantes.
     */
    public SolicitudExterna(
            String idSolicitud,
            String nombreSolicitante,
            String documento,
            String telefono,
            String correo,
            String codigoApartamento,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            int numeroPersonas,
            LocalDateTime fechaRecepcion,
            ListaDoble<Ocupante> ocupantes) {

        this.idSolicitud = validarTexto(idSolicitud, "id de solicitud");
        this.nombreSolicitante =
                validarTexto(nombreSolicitante, "nombre del solicitante");
        this.documento = validarTexto(documento, "documento");
        this.telefono = validarTexto(telefono, "teléfono");
        this.correo = validarTexto(correo, "correo");
        this.codigoApartamento =
                validarTexto(codigoApartamento, "código de apartamento");

        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException(
                    "Las fechas de entrada y salida no pueden ser nulas."
            );
        }

        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException(
                    "La fecha de salida debe ser posterior a la entrada."
            );
        }

        if (numeroPersonas <= 0) {
            throw new IllegalArgumentException(
                    "El número de personas debe ser mayor que cero."
            );
        }

        if (fechaRecepcion == null) {
            throw new IllegalArgumentException(
                    "La fecha de recepción no puede ser nula."
            );
        }

        if (ocupantes == null) {
            throw new IllegalArgumentException(
                    "La lista de ocupantes no puede ser nula."
            );
        }

        if (!ocupantes.estaVacia()
                && ocupantes.tamano() != numeroPersonas) {

            throw new IllegalArgumentException(
                    "La cantidad de ocupantes debe coincidir "
                            + "con el número de personas de la solicitud."
            );
        }

        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.numeroPersonas = numeroPersonas;
        this.fechaRecepcion = fechaRecepcion;

        this.ocupantes = new ListaDoble<>();

        for (Ocupante ocupante : ocupantes) {

            if (ocupante == null) {
                throw new IllegalArgumentException(
                        "La lista no puede contener ocupantes nulos."
                );
            }

            if (ocupante.getFechaNacimiento() == null) {
                throw new IllegalArgumentException(
                        "Todos los ocupantes deben tener fecha de nacimiento."
                );
            }

            this.ocupantes.agregarFinal(ocupante);
        }

        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCodigoApartamento() {
        return codigoApartamento;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    /**
     * Devuelve una copia de los ocupantes.
     */
    public ListaDoble<Ocupante> getOcupantes() {

        ListaDoble<Ocupante> copia = new ListaDoble<>();

        for (Ocupante ocupante : ocupantes) {
            copia.agregarFinal(ocupante);
        }

        return copia;
    }

    /**
     * Indica si la plataforma suministró información individual
     * de todos los ocupantes.
     */
    public boolean tieneDatosCompletosOcupantes() {
        return ocupantes.tamano() == numeroPersonas;
    }

    public long noches() {
        return ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    }

    public void marcarProcesada() {

        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se puede procesar una solicitud pendiente."
            );
        }

        estado = EstadoSolicitud.PROCESADA;
        motivoRechazo = null;
    }

    public void marcarRechazada(String motivo) {

        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se puede rechazar una solicitud pendiente."
            );
        }

        motivoRechazo = validarTexto(motivo, "motivo de rechazo");
        estado = EstadoSolicitud.RECHAZADA;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SolicitudExterna otra)) {
            return false;
        }

        return idSolicitud.equals(otra.idSolicitud);
    }

    @Override
    public int hashCode() {
        return idSolicitud.hashCode();
    }

    private static String validarTexto(String valor, String campo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede ser nulo o vacío."
            );
        }

        return valor.trim();
    }
}
