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

    public ServicioDisponibilidad getDisponibilidad() { return disponibilidad; }

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
            throw new IllegalStateException("La reserva debe estar en curso.");
        Apartamento apartamento = exigirApartamento(reserva);
        reserva.finalizar();
        apartamento.setEstado(EstadoApartamento.PENDIENTE_PREPARACION);
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
