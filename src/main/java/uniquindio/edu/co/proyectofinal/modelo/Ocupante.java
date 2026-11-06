package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

/**
 * Representa una persona que ocupa un cupo dentro de una reserva.
 * Puede estar vinculada a un huésped con perfil registrado.
 */
public class Ocupante extends Persona {

    /**
     * Perfil del huésped vinculado, si existe.
     */
    private Huesped perfilVinculado;

    /**
     * Crea un ocupante sin perfil vinculado.
     *
     * @param nombre nombre completo
     * @param fechaNacimiento fecha de nacimiento
     */
    public Ocupante(String nombre, LocalDate fechaNacimiento) {
        super(nombre, fechaNacimiento);
    }

    /**
     * Crea un ocupante con un perfil vinculado.
     *
     * @param nombre nombre completo
     * @param fechaNacimiento fecha de nacimiento
     * @param perfilVinculado perfil del huésped
     */
    public Ocupante(String nombre, LocalDate fechaNacimiento, Huesped perfilVinculado) {
        this(nombre, fechaNacimiento);
        setPerfilVinculado(perfilVinculado);
    }

    /**
     * Obtiene el perfil vinculado.
     *
     * @return perfil vinculado o null si no tiene
     */
    public Huesped getPerfilVinculado() {
        return perfilVinculado;
    }

    /**
     * Cambia el perfil vinculado.
     *
     * @param perfilVinculado perfil del huésped, puede ser null
     */
    public void setPerfilVinculado(Huesped perfilVinculado) {
        this.perfilVinculado = perfilVinculado;
    }

    /**
     * Indica si el ocupante tiene un perfil vinculado.
     *
     * @return true si tiene perfil
     */
    public boolean tienePerfil() {
        return perfilVinculado != null;
    }

    /**
     * Indica si el ocupante es menor de seis años en la fecha indicada.
     *
     * @param fechaEstancia fecha de referencia
     * @return true si es menor de seis años
     */
    public boolean esMenorDeSeis(LocalDate fechaEstancia) {
        return edad(fechaEstancia) < 6;
    }
}
