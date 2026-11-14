package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.*;
import java.time.LocalDate;

/** Consultas de ocupación por noches. Entrada inclusiva y salida exclusiva. */
public class ServicioDisponibilidad {
    private final ServicioApartamento servicioApartamento;
    private final ListaDoble<Reserva> reservas;

    public ServicioDisponibilidad(ServicioApartamento servicioApartamento,
                                  ListaDoble<Reserva> reservas) {
        if (servicioApartamento == null || reservas == null)
            throw new IllegalArgumentException("Los servicios y las reservas son obligatorios.");
        this.servicioApartamento = servicioApartamento;
        this.reservas = reservas;
    }

    /** Indica si las fechas están libres, sin considerar preparación o capacidad. */
    public boolean fechasLibres(String codigoApartamento, Estancia estancia) {
        return fechasLibres(codigoApartamento, estancia, null);
    }

    /** Permite excluir una reserva al evaluar modificaciones futuras. */
    public boolean fechasLibres(String codigoApartamento, Estancia estancia, String codigoExcluir) {
        if (codigoApartamento == null || codigoApartamento.isBlank() || estancia == null)
            throw new IllegalArgumentException("Apartamento y estancia son obligatorios.");
        if (servicioApartamento.buscarPorCodigo(codigoApartamento) == null) return false;
        for (Reserva reserva : reservas) {
            if (codigoExcluir != null && reserva.getCodigo().equals(codigoExcluir)) continue;
            if (ocupaCalendario(reserva.getEstado())
                    && reserva.getApartamento().getCodigo().equalsIgnoreCase(codigoApartamento.trim())
                    && reserva.getEstancia().seSolapaCon(estancia)) return false;
        }
        return true;
    }

    /** Incluye FINALIZADA para conservar el histórico real de ocupación. */
    private boolean ocupaCalendario(EstadoReserva estado) {
        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO
                || estado == EstadoReserva.FINALIZADA;
    }

    /** Disponibilidad para reservar: fechas, capacidad y ausencia de baja operativa. */
    public boolean estaDisponible(String codigoApartamento, Estancia estancia, int personas) {
        if (estancia == null || personas <= 0)
            throw new IllegalArgumentException("Estancia y cantidad de personas válidas son obligatorias.");
        Apartamento apartamento = servicioApartamento.buscarPorCodigo(codigoApartamento);
        return apartamento != null
                && apartamento.getEstado() != EstadoApartamento.FUERA_DE_SERVICIO
                && apartamento.tieneCapacidadPara(personas)
                && fechasLibres(codigoApartamento, estancia);
    }

    public ListaDoble<Apartamento> buscarDisponibles(Estancia estancia, int personas) {
        if (estancia == null || personas <= 0)
            throw new IllegalArgumentException("Estancia y cantidad de personas válidas son obligatorias.");
        ListaDoble<Apartamento> resultado = new ListaDoble<>();
        for (Apartamento apartamento : servicioApartamento.listarApartamentos()) {
            if (estaDisponible(apartamento.getCodigo(), estancia, personas))
                resultado.agregarFinal(apartamento);
        }
        return resultado;
    }

    public ListaDoble<Reserva> reservasEnFecha(String codigoApartamento, LocalDate noche) {
        if (codigoApartamento == null || codigoApartamento.isBlank() || noche == null)
            throw new IllegalArgumentException("Apartamento y fecha son obligatorios.");
        ListaDoble<Reserva> resultado = new ListaDoble<>();
        for (Reserva reserva : reservas) {
            if (reserva.getApartamento().getCodigo().equalsIgnoreCase(codigoApartamento.trim())
                    && ocupaCalendario(reserva.getEstado())
                    && reserva.getEstancia().contieneNoche(noche)) resultado.agregarFinal(reserva);
        }
        return resultado;
    }
}
