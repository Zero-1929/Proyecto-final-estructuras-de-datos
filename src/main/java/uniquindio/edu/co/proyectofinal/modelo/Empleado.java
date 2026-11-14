package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

/**
 * Representa un empleado del alojamiento.
 * El orden natural se establece por documento.
 */
public class Empleado extends PersonaIdentificada implements Comparable<Empleado> {

    /**
     * Rol que desempeña el empleado.
     */
    private RolEmpleado rol;

    /**
     * Crea un empleado con su rol.
     *
     * @param nombre nombre completo
     * @param fechaNacimiento fecha de nacimiento
     * @param documento documento de identidad
     * @param telefono teléfono de contacto
     * @param correo correo electrónico
     * @param rol rol del empleado
     */
    public Empleado(String nombre, LocalDate fechaNacimiento, String documento,
                    String telefono, String correo, RolEmpleado rol) {
        super(nombre, fechaNacimiento, documento, telefono, correo);
        setRol(rol);
    }

    /**
     * Obtiene el rol del empleado.
     *
     * @return rol del empleado
     */
    public RolEmpleado getRol() {
        return rol;
    }

    /**
     * Cambia el rol del empleado.
     *
     * @param rol nuevo rol
     */
    public void setRol(RolEmpleado rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.rol = rol;
    }

    /**
     * Indica si el empleado es recepcionista.
     *
     * @return true si su rol es recepcionista
     */
    public boolean esRecepcionista() {
        return rol == RolEmpleado.RECEPCIONISTA;
    }

    /**
     * Indica si el empleado es administrador.
     *
     * @return true si su rol es administrador
     */
    public boolean esAdministrador() {
        return rol == RolEmpleado.ADMINISTRADOR;
    }

    /**
     * Compara empleados por documento.
     *
     * @param otro empleado con el que se compara
     * @return resultado de la comparación de documentos
     */
    @Override
    public int compareTo(Empleado otro) {
        return getDocumento().compareTo(otro.getDocumento());
    }

    /**
     * Compara dos empleados por documento.
     *
     * @param obj objeto a comparar
     * @return true si tienen el mismo documento
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Empleado otro)) {
            return false;
        }
        return getDocumento().equals(otro.getDocumento());
    }

    /**
     * Genera el código hash a partir del documento.
     *
     * @return código hash del documento
     */
    @Override
    public int hashCode() {
        return getDocumento().hashCode();
    }
}
