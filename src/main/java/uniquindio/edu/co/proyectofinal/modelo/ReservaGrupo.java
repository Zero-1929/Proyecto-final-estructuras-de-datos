
package uniquindio.edu.co.proyectofinal.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un conjunto de reservas individuales
 * asociadas a un mismo grupo.
 *
 * Permite administrar las reservas que pertenecen
 * al grupo y consultar su informacion.
 *
 * @author Integrante 3
 */
public class ReservaGrupo {

    // Codigo unico que identifica el grupo.
    private final String codigoGrupo;

    // Huesped responsable del grupo.
    private final Huesped responsable;

    // Reservas individuales que pertenecen al grupo.
    private final List<Reserva> reservas;

    /**
     * Constructor de la clase ReservaGrupo.
     *
     * @param codigoGrupo codigo identificador del grupo.
     * @param responsable huesped responsable del grupo.
     */
    public ReservaGrupo(String codigoGrupo, Huesped responsable) {

        if (codigoGrupo == null || codigoGrupo.isBlank()) {
            throw new IllegalArgumentException(
                    "El codigo del grupo no puede estar vacio."
            );
        }

        if (responsable == null) {
            throw new IllegalArgumentException(
                    "El responsable del grupo no puede ser nulo."
            );
        }

        this.codigoGrupo = codigoGrupo.trim();
        this.responsable = responsable;
        this.reservas = new ArrayList<>();
    }

    /**
     * Obtiene el codigo identificador del grupo.
     *
     * @return codigo del grupo.
     */
    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    /**
     * Obtiene el huesped responsable del grupo.
     *
     * @return huesped responsable.
     */
    public Huesped getResponsable() {
        return responsable;
    }

    /**
     * Obtiene una copia de las reservas del grupo.
     *
     * Se devuelve una copia para evitar que otras clases
     * modifiquen directamente la lista interna.
     *
     * @return lista de reservas del grupo.
     */
    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    /**
     * Agrega una reserva al grupo.
     *
     * No permite agregar reservas nulas ni reservas
     * que ya se encuentren registradas en el grupo.
     *
     * @param reserva reserva que se desea agregar.
     */
    public void agregarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula."
            );
        }

        if (reservas.contains(reserva)) {
            throw new IllegalArgumentException(
                    "La reserva ya pertenece a este grupo."
            );
        }

        reservas.add(reserva);
    }

    /**
     * Elimina una reserva del grupo utilizando su codigo.
     *
     * @param codigoReserva codigo de la reserva que se desea eliminar.
     * @return true si la reserva fue eliminada.
     */
    public boolean eliminarReserva(String codigoReserva) {

        if (codigoReserva == null || codigoReserva.isBlank()) {
            throw new IllegalArgumentException(
                    "El codigo de la reserva no puede estar vacio."
            );
        }

        return reservas.removeIf(
                reserva -> reserva.getCodigo().equals(codigoReserva)
        );
    }

    /**
     * Busca una reserva dentro del grupo por su codigo.
     *
     * @param codigoReserva codigo de la reserva que se desea buscar.
     * @return la reserva encontrada o null si no existe.
     */
    public Reserva buscarReserva(String codigoReserva) {

        if (codigoReserva == null || codigoReserva.isBlank()) {
            throw new IllegalArgumentException(
                    "El codigo de la reserva no puede estar vacio."
            );
        }

        for (Reserva reserva : reservas) {

            if (reserva.getCodigo().equals(codigoReserva)) {
                return reserva;
            }
        }

        return null;
    }

    /**
     * Consulta la cantidad de reservas que tiene el grupo.
     *
     * @return cantidad de reservas.
     */
    public int cantidadReservas() {
        return reservas.size();
    }

    /**
     * Comprueba si el grupo tiene reservas registradas.
     *
     * @return true si no hay reservas registradas.
     */
    public boolean estaVacio() {
        return reservas.isEmpty();
    }

    /**
     * Compara dos grupos por su codigo identificador.
     *
     * @param obj objeto que se desea comparar.
     * @return true si ambos grupos tienen el mismo codigo.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ReservaGrupo otra)) {
            return false;
        }

        return codigoGrupo.equals(otra.codigoGrupo);
    }

    /**
     * Genera el codigo hash del grupo.
     *
     * @return codigo hash del grupo.
     */
    @Override
    public int hashCode() {
        return codigoGrupo.hashCode();
    }

    /**
     * Representacion textual del grupo.
     *
     * @return informacion principal del grupo.
     */
    @Override
    public String toString() {

        return "Grupo: " + codigoGrupo
                + " | Responsable: " + responsable.getNombre()
                + " | Cantidad de reservas: " + reservas.size();
    }
}
