package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    private EstadoSolicitud estado;
    private String motivoRechazo;

    public SolicitudExterna(String idSolicitud, String nombreSolicitante, String documento,
                            String telefono, String correo, String codigoApartamento,
                            LocalDate fechaEntrada, LocalDate fechaSalida, int numeroPersonas) {
        this(idSolicitud, nombreSolicitante, documento, telefono, correo, codigoApartamento,
                fechaEntrada, fechaSalida, numeroPersonas, LocalDateTime.now());
    }

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

    public long noches() {
        return ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    }

    public void marcarProcesada() {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se puede procesar una solicitud pendiente");
        }
        this.estado = EstadoSolicitud.PROCESADA;
        this.motivoRechazo = null;
    }

    public void marcarRechazada(String motivo) {
        if (estado != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se puede rechazar una solicitud pendiente");
        }
        this.motivoRechazo = validarTexto(motivo, "motivo de rechazo");
        this.estado = EstadoSolicitud.RECHAZADA;
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
            throw new IllegalArgumentException("El campo " + campo + " no puede ser nulo o vacío");
        }
        return valor.trim();
    }
}
