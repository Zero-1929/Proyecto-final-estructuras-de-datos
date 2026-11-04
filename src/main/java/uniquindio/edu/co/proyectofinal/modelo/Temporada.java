package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

public class Temporada {

    private TipoTemporada tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Temporada(TipoTemporada tipo, LocalDate fechaInicio, LocalDate fechaFin) {
        setTipo(tipo);
        setFechaInicio(fechaInicio);
        setFechaFin(fechaFin);
    }

    public TipoTemporada getTipo() {
        return tipo;
    }

    public void setTipo(TipoTemporada tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de temporada no puede ser nulo.");
        }

        this.tipo = tipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula.");
        }

        if (fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser posterior a la fecha final."
            );
        }

        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        if (fechaFin == null) {
            throw new IllegalArgumentException("La fecha final no puede ser nula.");
        }

        if (fechaInicio != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha final no puede ser anterior a la fecha de inicio."
            );
        }

        this.fechaFin = fechaFin;
    }

    /**
     * Comprueba si una fecha pertenece al periodo de esta temporada.
     * Tanto la fecha inicial como la final están incluidas.
     */
    public boolean contieneFecha(LocalDate fecha) {
        if (fecha == null) {
            return false;
        }

        return !fecha.isBefore(fechaInicio)
                && !fecha.isAfter(fechaFin);
    }

    /**
     * Comprueba si dos temporadas tienen fechas superpuestas.
     */
    public boolean seSolapaCon(Temporada otra) {
        if (otra == null) {
            return false;
        }

        return !fechaFin.isBefore(otra.fechaInicio)
                && !fechaInicio.isAfter(otra.fechaFin);
    }

    @Override
    public String toString() {
        return "Temporada: " + tipo
                + " | Desde: " + fechaInicio
                + " | Hasta: " + fechaFin;
    }
}