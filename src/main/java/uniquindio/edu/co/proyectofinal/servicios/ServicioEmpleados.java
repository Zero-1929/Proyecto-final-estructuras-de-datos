package uniquindio.edu.co.proyectofinal.servicios;

import java.time.LocalDate;

import uniquindio.edu.co.proyectofinal.estructuras.ABB;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Empleado;
import uniquindio.edu.co.proyectofinal.modelo.RolEmpleado;

/**
 * Administra los empleados del alojamiento.
 *
 * Los empleados se almacenan en un arbol binario de busqueda ordenado
 * por documento. No se permiten dos empleados con el mismo documento.
 */
public class ServicioEmpleados {

    /**
     * Empleados ordenados por documento.
     */
    private final ABB<Empleado> empleados;

    /**
     * Crea el servicio sin empleados registrados.
     */
    public ServicioEmpleados() {
        this.empleados = new ABB<>();
    }

    /**
     * Registra un empleado.
     *
     * @param empleado empleado que se desea registrar
     */
    public void registrarEmpleado(Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado no puede ser nulo."
            );
        }

        if (empleados.contiene(empleado)) {
            throw new IllegalArgumentException(
                    "Ya existe un empleado con el documento "
                            + empleado.getDocumento() + "."
            );
        }

        empleados.insertar(empleado);
    }

    /**
     * Crea y registra un empleado a partir de sus datos.
     *
     * @param nombre nombre completo
     * @param fechaNacimiento fecha de nacimiento
     * @param documento documento de identidad
     * @param telefono telefono de contacto
     * @param correo correo electronico
     * @param rol rol del empleado
     * @return empleado registrado
     */
    public Empleado registrarEmpleado(
            String nombre,
            LocalDate fechaNacimiento,
            String documento,
            String telefono,
            String correo,
            RolEmpleado rol
    ) {

        Empleado empleado = new Empleado(
                nombre,
                fechaNacimiento,
                documento,
                telefono,
                correo,
                rol
        );

        registrarEmpleado(empleado);
        return empleado;
    }

    /**
     * Busca un empleado por su documento.
     *
     * @param documento documento consultado
     * @return empleado encontrado o null si no existe
     */
    public Empleado buscarPorDocumento(String documento) {

        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento no puede estar vacio."
            );
        }

        String documentoBuscado = documento.trim();

        for (Empleado empleado : empleados) {
            if (empleado.getDocumento().equals(documentoBuscado)) {
                return empleado;
            }
        }

        return null;
    }

    /**
     * Elimina un empleado por su documento.
     *
     * @param documento documento del empleado
     * @return true si el empleado fue eliminado
     */
    public boolean eliminarEmpleado(String documento) {

        Empleado empleado = buscarPorDocumento(documento);

        if (empleado == null) {
            return false;
        }

        return empleados.eliminar(empleado);
    }

    /**
     * Cambia el rol de un empleado.
     *
     * @param documento documento del empleado
     * @param nuevoRol nuevo rol
     * @return true si el empleado fue encontrado
     */
    public boolean cambiarRol(String documento, RolEmpleado nuevoRol) {

        if (nuevoRol == null) {
            throw new IllegalArgumentException(
                    "El nuevo rol no puede ser nulo."
            );
        }

        Empleado empleado = buscarPorDocumento(documento);

        if (empleado == null) {
            return false;
        }

        empleado.setRol(nuevoRol);
        return true;
    }

    /**
     * Lista todos los empleados ordenados por documento.
     *
     * @return lista de empleados
     */
    public ListaDoble<Empleado> listarEmpleados() {

        ListaDoble<Empleado> lista = new ListaDoble<>();

        for (Empleado empleado : empleados) {
            lista.agregarFinal(empleado);
        }

        return lista;
    }

    /**
     * Lista los empleados que tienen un rol determinado.
     *
     * @param rol rol consultado
     * @return lista de empleados con ese rol
     */
    public ListaDoble<Empleado> listarPorRol(RolEmpleado rol) {

        if (rol == null) {
            throw new IllegalArgumentException(
                    "El rol no puede ser nulo."
            );
        }

        ListaDoble<Empleado> lista = new ListaDoble<>();

        for (Empleado empleado : empleados) {
            if (empleado.getRol() == rol) {
                lista.agregarFinal(empleado);
            }
        }

        return lista;
    }

    /**
     * Obtiene la cantidad de empleados registrados.
     *
     * @return cantidad de empleados
     */
    public int cantidadEmpleados() {
        return empleados.tamano();
    }

    /**
     * Indica si no hay empleados registrados.
     *
     * @return true si el servicio esta vacio
     */
    public boolean estaVacio() {
        return empleados.estaVacia();
    }

    /**
     * Elimina todos los empleados registrados.
     */
    public void limpiar() {
        empleados.limpiar();
    }
}
