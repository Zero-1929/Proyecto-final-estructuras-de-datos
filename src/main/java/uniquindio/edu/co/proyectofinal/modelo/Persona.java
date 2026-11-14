package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;
import java.time.Period;

/**
 * Representa una persona con los datos comunes del sistema.
 * Es la clase base para huéspedes, empleados y ocupantes.
 */
public abstract class Persona {

    /**
     * Atributos
     */
    private String nombre;
    private LocalDate fechaNacimiento;

    /**
     * Crea una persona con su nombre y fecha de nacimiento.
     *
     * @param nombre nombre completo de la persona
     * @param fechaNacimiento fecha de nacimiento
     */
    protected Persona(String nombre, LocalDate fechaNacimiento) {
        setNombre(nombre);
        setFechaNacimiento(fechaNacimiento);
    }

    /**
     * Obtiene el nombre.
     *
     * @return nombre de la persona
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }

    /**
     * Obtiene la fecha de nacimiento.
     *
     * @return fecha de nacimiento
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Cambia la fecha de nacimiento.
     *
     * @param fechaNacimiento nueva fecha de nacimiento
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Calcula la edad de la persona en una fecha determinada.
     *
     * @param fecha fecha de referencia
     * @return edad en años
     */
    public int edad(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        return Period.between(fechaNacimiento, fecha).getYears();
    }
}
