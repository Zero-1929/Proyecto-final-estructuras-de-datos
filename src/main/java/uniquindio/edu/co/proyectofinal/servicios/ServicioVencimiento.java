package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ColaPrioridad;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.EstadoReserva;
import uniquindio.edu.co.proyectofinal.modelo.Reserva;

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Servicio encargado de controlar el vencimiento
 * de las reservas pendientes.
 *
 * Una reserva pendiente vence después de 24 horas
 * desde su fecha de creación.
 *
 * Utiliza una cola de prioridad propia para procesar
 * primero las reservas con vencimiento más cercano.
 */
public class ServicioVencimiento {

    private static final int HORAS_VENCIMIENTO = 24;

    private final ColaPrioridad<Reserva> colaVencimientos;

    /**
     * Inicializa la cola de prioridad ordenando
     * las reservas por su fecha de vencimiento.
     */
    public ServicioVencimiento() {

        Comparator<Reserva> comparador =
                (reserva1, reserva2) -> {

                    LocalDateTime vencimiento1 =
                            calcularVencimiento(reserva1);

                    LocalDateTime vencimiento2 =
                            calcularVencimiento(reserva2);

                    int resultado =
                            vencimiento1.compareTo(vencimiento2);

                    if (resultado != 0) {
                        return resultado;
                    }

                    return reserva1.getCodigo()
                            .compareTo(reserva2.getCodigo());
                };

        colaVencimientos = new ColaPrioridad<>(comparador);
    }

    /**
     * Calcula la fecha y hora en la que vence una reserva.
     */
    public LocalDateTime calcularVencimiento(Reserva reserva) {

        validarReserva(reserva);

        return reserva.getFechaCreacion()
                .plusHours(HORAS_VENCIMIENTO);
    }

    /**
     * Registra una reserva pendiente en la cola.
     *
     * Evita registrar dos veces la misma reserva.
     */
    public void registrarReserva(Reserva reserva) {

        validarReserva(reserva);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalArgumentException(
                    "Solo se pueden registrar reservas pendientes."
            );
        }

        if (!colaVencimientos.contiene(reserva)) {
            colaVencimientos.encolar(reserva);
        }
    }

    /**
     * Registra las reservas pendientes de una lista.
     *
     * Se utiliza para cargar las reservas que ya
     * existen en el sistema.
     */
    public void registrarReservas(
            ListaDoble<Reserva> reservas) {

        if (reservas == null) {
            throw new IllegalArgumentException(
                    "La lista de reservas no puede ser nula."
            );
        }

        for (Reserva reserva : reservas) {

            if (reserva != null
                    && reserva.getEstado() == EstadoReserva.PENDIENTE) {

                registrarReserva(reserva);
            }
        }
    }

    /**
     * Comprueba si una reserva pendiente ha vencido.
     *
     * El vencimiento se cumple al llegar exactamente
     * a las 24 horas desde su creación.
     */
    public boolean estaVencida(
            Reserva reserva,
            LocalDateTime fechaActual) {

        validarReserva(reserva);
        validarFecha(fechaActual);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            return false;
        }

        LocalDateTime vencimiento =
                calcularVencimiento(reserva);

        return !fechaActual.isBefore(vencimiento);
    }

    /**
     * Procesa las reservas de la cola.
     *
     * Cancela las reservas pendientes que ya vencieron.
     * Las reservas confirmadas o canceladas previamente
     * se retiran de la cola sin modificar su estado.
     *
     * @return cantidad de reservas canceladas por vencimiento.
     */
    public int procesarVencimientos(
            LocalDateTime fechaActual) {

        validarFecha(fechaActual);

        int cantidadCanceladas = 0;

        while (!colaVencimientos.estaVacia()) {

            Reserva reserva = colaVencimientos.frente();

            // Retirar reservas que ya no están pendientes.
            if (reserva.getEstado() != EstadoReserva.PENDIENTE) {

                colaVencimientos.desencolar();
                continue;
            }

            // La reserva más próxima todavía no ha vencido.
            // Las demás tienen vencimientos iguales o posteriores.
            if (!estaVencida(reserva, fechaActual)) {
                break;
            }

            // Retirar la reserva de la cola.
            colaVencimientos.desencolar();

            // Cancelar la reserva vencida.
            reserva.cancelar();

            cantidadCanceladas++;
        }

        return cantidadCanceladas;
    }

    /**
     * Ejecuta el procesamiento utilizando
     * la fecha y hora actual del sistema.
     */
    public int procesarVencimientos() {

        return procesarVencimientos(LocalDateTime.now());
    }

    /**
     * Consulta la próxima reserva pendiente
     * que se encuentra en la cola.
     *
     * Descarta primero las reservas cuyo estado
     * ya cambió a confirmado o cancelado.
     */
    public Reserva consultarProximaReserva() {

        while (!colaVencimientos.estaVacia()) {

            Reserva reserva = colaVencimientos.frente();

            if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
                return reserva;
            }

            colaVencimientos.desencolar();
        }

        return null;
    }

    /**
     * Consulta la fecha de vencimiento
     * de la próxima reserva pendiente.
     */
    public LocalDateTime consultarProximoVencimiento() {

        Reserva reserva = consultarProximaReserva();

        if (reserva == null) {
            return null;
        }

        return calcularVencimiento(reserva);
    }

    /**
     * Indica cuántas reservas permanecen almacenadas
     * en la cola, incluyendo entradas cuyo estado
     * todavía no se haya depurado.
     */
    public int cantidadEnCola() {

        return colaVencimientos.tamanio();
    }

    /**
     * Comprueba si la cola está vacía.
     */
    public boolean estaVacia() {

        return colaVencimientos.estaVacia();
    }

    /**
     * Limpia la cola de vencimientos.
     *
     * No modifica el estado de las reservas.
     */
    public void limpiarCola() {

        colaVencimientos.vaciar();
    }

    /**
     * Valida que la reserva no sea nula.
     */
    private void validarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula."
            );
        }
    }

    /**
     * Valida la fecha utilizada para procesar
     * los vencimientos.
     */
    private void validarFecha(LocalDateTime fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha actual no puede ser nula."
            );
        }
    }
}

