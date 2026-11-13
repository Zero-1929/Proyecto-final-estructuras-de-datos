package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Estancia implements Iterable<LocalDate> {

    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;

    public Estancia(LocalDate fechaEntrada, LocalDate fechaSalida) {
        setFechas(fechaEntrada, fechaSalida);
    }

    // -------------------- GETTERS --------------------

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    // -------------------- SETTERS --------------------

    public void setFechaEntrada(LocalDate fechaEntrada) {
        if (fechaEntrada == null) {
            throw new IllegalArgumentException(
                    "La fecha de entrada no puede ser nula."
            );
        }

        if (fechaSalida != null && !fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "La fecha de entrada debe ser anterior a la salida."
            );
        }

        this.fechaEntrada = fechaEntrada;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        if (fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La fecha de salida no puede ser nula."
            );
        }

        if (fechaEntrada != null && !fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException(
                    "La fecha de salida debe ser posterior a la entrada."
            );
        }

        this.fechaSalida = fechaSalida;
    }

    public void setFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException(
                    "Las fechas no pueden ser nulas."
            );
        }

        if (!fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "La fecha de entrada debe ser anterior a la salida."
            );
        }

        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
    }

    // -------------------- REGLAS DE ESTANCIA --------------------

    /**
     * Calcula la cantidad de noches de la estancia.
     * La fecha de salida no se cuenta como noche.
     */
    public long calcularNumeroNoches() {
        return ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
    }

    /**
     * Comprueba si una fecha corresponde a una noche cobrable.
     */
    public boolean contieneNoche(LocalDate fecha) {
        if (fecha == null) {
            return false;
        }

        return !fecha.isBefore(fechaEntrada)
                && fecha.isBefore(fechaSalida);
    }

    /**
     * Comprueba si dos estancias comparten al menos una noche.
     */
    public boolean seSolapaCon(Estancia otra) {
        if (otra == null) {
            return false;
        }

        return fechaEntrada.isBefore(otra.fechaSalida)
                && otra.fechaEntrada.isBefore(fechaSalida);
    }

    // -------------------- ITERADOR --------------------

    /**
     * Permite recorrer las noches cobrables mediante for-each.
     * Incluye la fecha de entrada y excluye la fecha de salida.
     */
    @Override
    public Iterator<LocalDate> iterator() {

        return new Iterator<LocalDate>() {

            private LocalDate fechaActual = fechaEntrada;

            @Override
            public boolean hasNext() {
                return fechaActual.isBefore(fechaSalida);
            }

            @Override
            public LocalDate next() {
                if (!hasNext()) {
                    throw new NoSuchElementException(
                            "No existen más noches en la estancia."
                    );
                }

                LocalDate noche = fechaActual;
                fechaActual = fechaActual.plusDays(1);

                return noche;
            }
        };
    }

    // -------------------- TO STRING --------------------

    @Override
    public String toString() {
        return "Estancia {"
                + "fechaEntrada=" + fechaEntrada
                + ", fechaSalida=" + fechaSalida
                + ", numeroNoches=" + calcularNumeroNoches()
                + '}';
    }
}
