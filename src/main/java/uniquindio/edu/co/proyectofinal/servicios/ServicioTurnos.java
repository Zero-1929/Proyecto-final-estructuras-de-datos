package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ListaCircular;
import uniquindio.edu.co.proyectofinal.modelo.Empleado;

/**
 * Administra la rotación de turnos de los recepcionistas
 * del Sistema de Gestión de Alojamiento.
 *
 * Utiliza una lista circular para garantizar que,
 * después del último recepcionista, el turno vuelva
 * automáticamente al primero.
 */
public class ServicioTurnos {

    private final ListaCircular<Empleado> recepcionistas;

    /**
     * Inicializa el servicio con una lista circular vacía.
     */
    public ServicioTurnos() {
        recepcionistas = new ListaCircular<>();
    }

    /**
     * Registra un recepcionista en la rotación de turnos.
     *
     * Solo permite empleados cuyo rol sea RECEPCIONISTA.
     *
     * @param empleado empleado que se desea registrar.
     */
    public void registrarRecepcionista(Empleado empleado) {

        validarEmpleado(empleado);

        if (!empleado.esRecepcionista()) {
            throw new IllegalArgumentException(
                    "El empleado no tiene el rol de recepcionista."
            );
        }

        if (recepcionistas.contiene(empleado)) {
            throw new IllegalArgumentException(
                    "El recepcionista ya está registrado."
            );
        }

        recepcionistas.agregar(empleado);
    }

    /**
     * Elimina un recepcionista de la rotación.
     *
     * Si el empleado eliminado tenía el turno actual,
     * la lista circular asigna el turno al siguiente.
     *
     * @param empleado empleado que se desea retirar.
     * @return true si se eliminó correctamente.
     */
    public boolean retirarRecepcionista(Empleado empleado) {

        validarEmpleado(empleado);

        return recepcionistas.eliminar(empleado);
    }

    /**
     * Consulta el recepcionista que tiene el turno actual.
     *
     * @return empleado que tiene el turno.
     */
    public Empleado consultarTurnoActual() {

        if (recepcionistas.estaVacia()) {
            throw new IllegalStateException(
                    "No hay recepcionistas registrados."
            );
        }

        return recepcionistas.obtenerActual();
    }

    /**
     * Avanza el turno al siguiente recepcionista.
     *
     * Cuando llega al último, vuelve automáticamente
     * al primero gracias a la estructura circular.
     *
     * @return empleado que recibe el nuevo turno.
     */
    public Empleado avanzarTurno() {

        if (recepcionistas.estaVacia()) {
            throw new IllegalStateException(
                    "No hay recepcionistas disponibles para asignar turno."
            );
        }

        return recepcionistas.avanzarTurno();
    }

    /**
     * Consulta si un empleado está registrado
     * en la rotación de turnos.
     *
     * @param empleado empleado que se desea consultar.
     * @return true si está registrado.
     */
    public boolean estaRegistrado(Empleado empleado) {

        if (empleado == null) {
            return false;
        }

        return recepcionistas.contiene(empleado);
    }

    /**
     * Obtiene la cantidad de recepcionistas registrados.
     *
     * @return cantidad de recepcionistas.
     */
    public int cantidadRecepcionistas() {
        return recepcionistas.getTamano();
    }

    /**
     * Comprueba si existen recepcionistas registrados.
     *
     * @return true si la lista está vacía.
     */
    public boolean estaVacia() {
        return recepcionistas.estaVacia();
    }

    /**
     * Retira todos los recepcionistas de la rotación.
     */
    public void limpiarTurnos() {
        recepcionistas.limpiar();
    }

    /**
     * Obtiene una representación de los turnos registrados.
     *
     * @return representación textual de la lista circular.
     */
    public String consultarRotacion() {
        return recepcionistas.toString();
    }

    /**
     * Valida que el empleado recibido no sea nulo.
     *
     * @param empleado empleado que se desea validar.
     */
    private void validarEmpleado(Empleado empleado) {

        if (empleado == null) {
            throw new IllegalArgumentException(
                    "El empleado no puede ser nulo."
            );
        }
    }
}

