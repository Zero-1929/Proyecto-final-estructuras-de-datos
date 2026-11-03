package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;
import java.time.Period;

public abstract class Persona {

    private String nombre;
    private LocalDate fechaNacimiento;

    protected Persona(String nombre, LocalDate fechaNacimiento) {
        setNombre(nombre);
        setFechaNacimiento(fechaNacimiento);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
        this.fechaNacimiento = fechaNacimiento;
    }

    public int edad(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        return Period.between(fechaNacimiento, fecha).getYears();
    }
}
