package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.Conjunto;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.*;

/** Favoritos persistidos en Huesped e historial derivado de las reservas centrales. */
public final class ServicioPerfilHuesped {
    private final ServicioHuesped huespedes;
    private final ServicioApartamento apartamentos;
    private final ServicioReserva reservas;

    public ServicioPerfilHuesped(ServicioHuesped huespedes, ServicioApartamento apartamentos,
                                ServicioReserva reservas) {
        if (huespedes == null || apartamentos == null || reservas == null)
            throw new IllegalArgumentException("Dependencias obligatorias");
        this.huespedes = huespedes;
        this.apartamentos = apartamentos;
        this.reservas = reservas;
    }

    private Huesped exigirHuesped(String documento) {
        Huesped h = huespedes.buscarPorDocumento(documento);
        if (h == null) throw new IllegalArgumentException("Huésped no registrado: " + documento);
        return h;
    }

    public boolean agregarFavorito(String documento, String codigoApartamento) {
        Apartamento a = apartamentos.buscarPorCodigo(codigoApartamento);
        if (a == null) throw new IllegalArgumentException("Apartamento no registrado");
        return exigirHuesped(documento).agregarFavorito(a.getCodigo());
    }

    public boolean quitarFavorito(String documento, String codigoApartamento) {
        return exigirHuesped(documento).quitarFavorito(codigoApartamento);
    }

    public Conjunto<String> listarFavoritos(String documento) {
        return exigirHuesped(documento).getFavoritos();
    }

    /** Solo estancias efectivamente finalizadas, nunca cancelaciones ni no-shows. */
    public ListaDoble<Reserva> historialEstancias(String documento) {
        Huesped titular = exigirHuesped(documento);
        ListaDoble<Reserva> resultado = new ListaDoble<>();
        for (Reserva reserva : reservas.listarReservas())
            if (reserva.getHuesped().getDocumento().equals(titular.getDocumento())
                    && reserva.getEstado() == EstadoReserva.FINALIZADA)
                resultado.agregarFinal(reserva);
        return resultado;
    }

    /** Reservas del titular, incluyendo pendientes, canceladas y no-shows. */
    public ListaDoble<Reserva> historialReservas(String documento) {
        Huesped titular = exigirHuesped(documento);
        ListaDoble<Reserva> resultado = new ListaDoble<>();
        for (Reserva reserva : reservas.listarReservas())
            if (reserva.getHuesped().getDocumento().equals(titular.getDocumento()))
                resultado.agregarFinal(reserva);
        return resultado;
    }
}
