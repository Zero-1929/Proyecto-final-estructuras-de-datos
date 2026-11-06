package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Representa una solicitud de reserva recibida por el canal externo.
 * Se procesa en orden de llegada.
 */
public class SolicitudExterna {

    /**
     * Atributos
     */
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
    private EstadoSolicitud estado;
    private String motivoRechazo;

    /**
     * Crea una solicitud con la fecha de recepción actual.
     *
     * @param idSolicitud identificador de la solicitud
     * @param nombreSolicitante nombre de quien solicita
     * @param documento documento del solicitante
     * @param telefono teléfono de contacto
     * @param correo correo electrónico
     * @param codigoApartamento código del apartamento solicitado
     * @param fechaEntrada fecha de entrada
     * @param fechaSalida fecha de salida
     * @param numeroPersonas cantidad de personas
     */
    public SolicitudExterna(String idSolicitud, String nombreSolicitante, String documento,
                            String telefono, String correo, String codigoApartamento,
                            LocalDate fechaEntrada, LocalDate fechaSalida, int numeroPersonas) {
        this(idSolicitud, nombreSolicitante, documento, telefono, correo, codigoApartamento,
                fechaEntrada, fechaSalida, numeroPersonas, LocalDateTime.now());
    }

    /**
     * Crea una solicitud con todos sus datos.
     *
     * @param idSolicitud identificador de la solicitud
     * @param nombreSolicitante nombre de quien solicita
     * @param documento documento del solicitante
     * @param telefono teléfono de contacto
     * @param correo correo electrónico
     * @param codigoApartamento código del apartamento solicitado
     * @param fechaEntrada fecha de entrada
     * @param fechaSalida fecha de salida
     * @param numeroPersonas cantidad de personas
     * @param fechaRecepcion fecha y hora de recepción
     */
    public SolicitudExterna(String idSolicitud, String nombreSolicitante, String documento,
                            String telefono, String correo, String codigoApartamento,
                            LocalDate fechaEntrada, LocalDate fechaSalida, int numeroPersonas,
                            LocalDateTime fechaRecepcion) {
        this.idSolicitud = validarTexto(idSolicitud, "id de solicitud");
        this.nombreSolicitante = validarTexto(nombreSolicitante, "nombre del solicitante");
        this.documento = validarTexto(documento, "documento");
        this.telefono = validarTexto(telefono, "teléfono");
        this.correo = validarTexto(correo, "correo");
        this.codigoApartamento = validarTexto(codigoApartamento, "código de apartamento");
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException("Las fechas de entrada y salida no pueden ser nulas");
        }
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada");
        }
        if (numeroPersonas <= 0) {
            throw new IllegalArgumentException("El número de personas debe ser mayor que cero");
        }
        if (fechaRecepcion == null) {
            throw new IllegalArgumentException("La fecha de recepción no puede ser nula");
        }
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.numeroPersonas = numeroPersonas;
        this.fechaRecepcion = fechaRecepcion;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    /**
     * Obtiene el identificador de la solicitud.
     *
     * @return id de la solicitud
     */
    public String getIdSolicitud() {
        return idSolicitud;
    }

    /**
     * Obtiene el nombre del solicitante.
     *
     * @return nombre del solicitante
     */
    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    /**
     * Obtiene el documento del solicitante.
     *
     * @return documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Obtiene el teléfono del solicitante.
     *
     * @return teléfono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Obtiene el correo del solicitante.
     *
     * @return correo electrónico
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Obtiene el código del apartamento solicitado.
     *
     * @return código del apartamento
     */
    public String getCodigoApartamento() {
        return codigoApartamento;
    }

    /**
     * Obtiene la fecha de entrada.
     *
     * @return fecha de entrada
     */
    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    /**
     * Obtiene la fecha de salida.
     *
     * @return fecha de salida
     */
    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    /**
     * Obtiene la cantidad de personas.
     *
     * @return número de personas
     */
    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    /**
     * Obtiene la fecha y hora de recepción.
     *
     * @return fecha de recepción
     */
    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    /**
     * Obtiene el estado de la solicitud.
     *
     * @return estado actual
     */
    public EstadoSolicitud getEstado() {
        return estado;
    }

    /**
     * Obtiene el motivo de rechazo.
     *
     * @return motivo de rechazo o null si no fue rechazada
     */
    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    /**
     * Calcula la cantidad de noches solicitadas.
     *
     * @return número de noches
     */
    public long noches() {
        return ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    }

    /**
     * Marca la solicitud como procesada.
     */
    public void marcarProcesada() {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se puede procesar una solicitud pendiente");
        }
        this.estado = EstadoSolicitud.PROCESADA;
        this.motivoRechazo = null;
    }

    /**
     * Marca la solicitud como rechazada.
     *
     * @param motivo motivo del rechazo
     */
    public void marcarRechazada(String motivo) {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se puede rechazar una solicitud pendiente");
        }
        this.motivoRechazo = validarTexto(motivo, "motivo de rechazo");
        this.estado = EstadoSolicitud.RECHAZADA;
    }

    /**
     * Compara dos solicitudes por identificador.
     *
     * @param obj objeto a comparar
     * @return true si tienen el mismo identificador
     */
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

    /**
     * Genera el código hash a partir del identificador.
     *
     * @return código hash del identificador
     */
    @Override
    public int hashCode() {
        return idSolicitud.hashCode();
    }

    /**
     * Valida que un texto no sea nulo ni vacío.
     *
     * @param valor texto a validar
     * @param campo nombre del campo para el mensaje
     * @return texto sin espacios externos
     */
    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser nulo o vacío");
        }
        return valor.trim();
    }
}
