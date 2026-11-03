package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

public class Empleado extends PersonaIdentificada implements Comparable<Empleado> {

    private RolEmpleado rol;

    public Empleado(String nombre, LocalDate fechaNacimiento, String documento,
                    String telefono, String correo, RolEmpleado rol) {
        super(nombre, fechaNacimiento, documento, telefono, correo);
        setRol(rol);
    }

    public RolEmpleado getRol() {
        return rol;
    }

    public void setRol(RolEmpleado rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.rol = rol;
    }

    public boolean esRecepcionista() {
        return rol == RolEmpleado.RECEPCIONISTA;
    }

    public boolean esAdministrador() {
        return rol == RolEmpleado.ADMINISTRADOR;
    }

    @Override
    public int compareTo(Empleado otro) {
        return getDocumento().compareTo(otro.getDocumento());
    }

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

    @Override
    public int hashCode() {
        return getDocumento().hashCode();
    }
}
