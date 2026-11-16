package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Gestiona el ciclo de vida de reservas y comparte su lista con disponibilidad. */
public class ServicioReserva {
    private final ListaDoble<Reserva> reservas;
    private final ServicioApartamento apartamentos;
    private final ServicioDisponibilidad disponibilidad;
    private final ServicioCotizacion cotizacion;
    private ServicioPreparacion servicioPreparacion;

    public ServicioReserva(ServicioApartamento apartamentos, ServicioCotizacion cotizacion) {
        this(apartamentos, cotizacion, new ListaDoble<>());
    }

    public ServicioReserva(ServicioApartamento apartamentos, ServicioCotizacion cotizacion,
                           ListaDoble<Reserva> reservas) {
        if (apartamentos == null || cotizacion == null || reservas == null)
            throw new IllegalArgumentException("Las dependencias no pueden ser nulas.");
        this.apartamentos = apartamentos;
        this.cotizacion = cotizacion;
        this.reservas = reservas;
        this.disponibilidad = new ServicioDisponibilidad(apartamentos, reservas);
    }

    /** Conecta la cola compartida del sistema con el check-out. */
    public void configurarPreparacion(ServicioPreparacion servicioPreparacion) {
        if (servicioPreparacion == null)
            throw new IllegalArgumentException("El servicio de preparacion es obligatorio.");
        this.servicioPreparacion = servicioPreparacion;
    }

    public ServicioDisponibilidad getDisponibilidad() { return disponibilidad; }

    /** Registro compartido con el canal externo. */
    public ListaDoble<Reserva> getRegistroCompartido() { return reservas; }

    /** Registra una reserva ya construida, validando código, apartamento y fechas. */
    public boolean registrarReserva(Reserva reserva, int numeroOcupantes) {
        if (reserva == null || numeroOcupantes <= 0)
            throw new IllegalArgumentException("Reserva y número de ocupantes válidos son obligatorios.");
        if (buscarPorCodigo(reserva.getCodigo()) != null) return false;
        Apartamento apartamento = apartamentos.buscarPorCodigo(reserva.getApartamento().getCodigo());
        if (apartamento == null)
            throw new IllegalArgumentException("El apartamento no está registrado.");
        if (reserva.getEstado() != EstadoReserva.PENDIENTE)
            throw new IllegalArgumentException("La reserva nueva debe estar pendiente.");
        if (!disponibilidad.estaDisponible(apartamento.getCodigo(), reserva.getEstancia(), numeroOcupantes))
            return false;
        reservas.agregarFinal(reserva);
        return true;
    }

    public Reserva buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");
        for (Reserva reserva : reservas)
            if (reserva.getCodigo().equalsIgnoreCase(codigo.trim())) return reserva;
        return null;
    }

    public ListaDoble<Reserva> listarReservas() {
        ListaDoble<Reserva> copia = new ListaDoble<>();
        for (Reserva reserva : reservas) copia.agregarFinal(reserva);
        return copia;
    }

    public ListaDoble<Reserva> filtrarPorEstado(EstadoReserva estado) {
        if (estado == null) throw new IllegalArgumentException("El estado es obligatorio.");
        ListaDoble<Reserva> resultado = new ListaDoble<>();
        for (Reserva reserva : reservas)
            if (reserva.getEstado() == estado) resultado.agregarFinal(reserva);
        return resultado;
    }

    /** Para WEB exige pagos registrados >= 30% del valor cotizado. */
    public void confirmarReserva(String codigo, BigDecimal totalCotizado) {
        Reserva reserva = exigirReserva(codigo);
        if (reserva.getCanalReserva() == CanalReserva.WEB) {
            if (totalCotizado == null || totalCotizado.signum() < 0)
                throw new IllegalArgumentException("Para WEB se requiere un total cotizado válido.");
            BigDecimal anticipo = cotizacion.calcularAnticipo(totalCotizado);
            if (reserva.getFolio().calcularTotalPagos().compareTo(anticipo) < 0)
                throw new IllegalStateException("No se ha registrado el anticipo WEB del 30%.");
        }
        reserva.confirmar();
    }

    public void cancelarReserva(String codigo) { exigirReserva(codigo).cancelar(); }

    public void realizarCheckIn(String codigo) {
        Reserva reserva = exigirReserva(codigo);
        Apartamento apartamento = exigirApartamento(reserva);
        if (reserva.getEstado() != EstadoReserva.CONFIRMADA)
            throw new IllegalStateException("La reserva debe estar confirmada.");
        if (apartamento.getEstado() != EstadoApartamento.PREPARADO)
            throw new IllegalStateException("El apartamento debe estar PREPARADO para el check-in.");
        if (!LocalDate.now().equals(reserva.getEstancia().getFechaEntrada()))
            throw new IllegalStateException("El check-in solo se permite en la fecha de entrada.");
        reserva.iniciarEstancia();
        apartamento.setEstado(EstadoApartamento.OCUPADO);
    }

    public void realizarCheckOut(String codigo) {
        Reserva reserva = exigirReserva(codigo);
        if (reserva.getEstado() != EstadoReserva.EN_CURSO)
            throw new IllegalStateException("Para hacer check-out primero debes realizar el check-in. "
                + "La reserva debe estar EN_CURSO; estado actual: " + reserva.getEstado());
        Apartamento apartamento = exigirApartamento(reserva);
        if (servicioPreparacion == null)
            throw new IllegalStateException("El servicio de preparacion no esta configurado.");
        // Una salida anticipada es válida una vez realizado el check-in.
        // Liberar las noches futuras que ya no serán ocupadas.
        LocalDate hoy = LocalDate.now();
        LocalDate salidaEfectiva = hoy.isAfter(reserva.getEstancia().getFechaEntrada())
                ? hoy : reserva.getEstancia().getFechaEntrada().plusDays(1);
        if (salidaEfectiva.isBefore(reserva.getEstancia().getFechaSalida()))
            reserva.getEstancia().setFechaSalida(salidaEfectiva);
        reserva.finalizar();
        disponibilidad.invalidarIndice();
        apartamento.setEstado(EstadoApartamento.PENDIENTE_PREPARACION);
        // La tarea debe existir en la cola antes de guardar el sistema.
        servicioPreparacion.sincronizarPendientes(apartamentos.listarApartamentos());
    }

    public void marcarNoPresentado(String codigo) {
        Reserva reserva = exigirReserva(codigo);
        if (LocalDate.now().isBefore(reserva.getEstancia().getFechaEntrada()))
            throw new IllegalStateException("Todavía no ha llegado la fecha de entrada.");
        reserva.marcarNoShow();
    }

    public int contarReservas() { return reservas.tamano(); }

    private Reserva exigirReserva(String codigo) {
        Reserva reserva = buscarPorCodigo(codigo);
        if (reserva == null) throw new IllegalArgumentException("No existe la reserva: " + codigo);
        return reserva;
    }

    private Apartamento exigirApartamento(Reserva reserva) {
        Apartamento apartamento = apartamentos.buscarPorCodigo(reserva.getApartamento().getCodigo());
        if (apartamento == null) throw new IllegalStateException("El apartamento no está registrado.");
        return apartamento;
    }
}
