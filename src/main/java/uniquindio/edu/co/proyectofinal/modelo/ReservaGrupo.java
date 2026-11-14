package uniquindio.edu.co.proyectofinal.modelo;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;

/**
 * Representa un conjunto de reservas individuales
 * asociadas a un mismo grupo.
 *
 * Utiliza ListaDoble para almacenar las reservas
 * sin depender de colecciones de Java.
 */
public class ReservaGrupo {

    private final String codigoGrupo;
    private final Huesped responsable;
    private final ListaDoble<Reserva> reservas;

    /**
     * Constructor de la clase.
     */
    public ReservaGrupo(String codigoGrupo, Huesped responsable) {

        if (codigoGrupo == null || codigoGrupo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del grupo no puede estar vacío."
            );
        }

        if (responsable == null) {
            throw new IllegalArgumentException(
                    "El responsable del grupo no puede ser nulo."
            );
        }

        this.codigoGrupo = codigoGrupo.trim();
        this.responsable = responsable;
        this.reservas = new ListaDoble<>();
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public Huesped getResponsable() {
        return responsable;
    }

    /**
     * Devuelve una copia de las reservas del grupo.
     */
    public ListaDoble<Reserva> getReservas() {

        ListaDoble<Reserva> copia = new ListaDoble<>();

        for (Reserva reserva : reservas) {
            copia.agregarFinal(reserva);
        }

        return copia;
    }

    /**
     * Agrega una reserva al grupo.
     */
    public void agregarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula."
            );
        }

        if (reservas.contiene(reserva)) {
            throw new IllegalArgumentException(
                    "La reserva ya pertenece a este grupo."
            );
        }

        reservas.agregarFinal(reserva);
    }

    /**
     * Elimina una reserva mediante su código.
     */
    public boolean eliminarReserva(String codigoReserva) {

        if (codigoReserva == null || codigoReserva.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la reserva no puede estar vacío."
            );
        }

        Reserva reserva = buscarReserva(codigoReserva);

        if (reserva == null) {
            return false;
        }

        return reservas.eliminar(reserva);
    }

    /**
     * Busca una reserva mediante su código.
     */
    public Reserva buscarReserva(String codigoReserva) {

        if (codigoReserva == null || codigoReserva.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de la reserva no puede estar vacío."
            );
        }

        for (Reserva reserva : reservas) {

            if (reserva.getCodigo().equals(codigoReserva)) {
                return reserva;
            }
        }

        return null;
    }

    public int cantidadReservas() {
        return reservas.tamano();
    }

    public boolean estaVacio() {
        return reservas.estaVacia();
    }

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

    @Override
    public int hashCode() {
        return codigoGrupo.hashCode();
    }

    @Override
    public String toString() {

        return "Grupo: " + codigoGrupo
                + " | Responsable: " + responsable.getNombre()
                + " | Cantidad de reservas: " + reservas.tamano();
    }
}
