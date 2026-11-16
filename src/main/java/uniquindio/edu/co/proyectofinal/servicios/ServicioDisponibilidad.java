package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.*;
import java.time.LocalDate;

/**
 * Disponibilidad mediante un arbol de intervalos por apartamento.
 * Conserva la lista central como fuente de verdad y reconstruye los indices
 * cuando cambia la cantidad de reservas (registro, carga de JSON, etc.).
 */
public class ServicioDisponibilidad {
    private final ServicioApartamento servicioApartamento;
    private final ListaDoble<Reserva> reservas;
    private final ListaDoble<IndicePorApartamento> indices = new ListaDoble<>();
    private int cantidadIndexada = -1;

    private static final class IndicePorApartamento {
        final String codigo;
        final IndiceReservasApartamento arbol = new IndiceReservasApartamento();
        IndicePorApartamento(String codigo) { this.codigo = codigo; }
    }

    public ServicioDisponibilidad(ServicioApartamento servicioApartamento,
                                  ListaDoble<Reserva> reservas) {
        if (servicioApartamento == null || reservas == null)
            throw new IllegalArgumentException("Los servicios y las reservas son obligatorios.");
        this.servicioApartamento = servicioApartamento;
        this.reservas = reservas;
    }

    /** Reconstruye el índice tras cambiar fechas o estados sin añadir reservas. */
    public void invalidarIndice() { cantidadIndexada = -1; }

    private void sincronizar() {
        if (cantidadIndexada == reservas.tamano()) return;
        indices.limpiar();
        for (Reserva reserva : reservas) {
            String codigo = reserva.getApartamento().getCodigo();
            IndicePorApartamento indice = buscarIndice(codigo);
            if (indice == null) {
                indice = new IndicePorApartamento(codigo);
                indices.agregarFinal(indice);
            }
            indice.arbol.insertar(reserva);
        }
        cantidadIndexada = reservas.tamano();
    }

    private IndicePorApartamento buscarIndice(String codigo) {
        for (IndicePorApartamento indice : indices)
            if (indice.codigo.equalsIgnoreCase(codigo)) return indice;
        return null;
    }

    public boolean fechasLibres(String codigoApartamento, Estancia estancia) {
        return fechasLibres(codigoApartamento, estancia, null);
    }

    public boolean fechasLibres(String codigoApartamento, Estancia estancia, String codigoExcluir) {
        if (codigoApartamento == null || codigoApartamento.isBlank() || estancia == null)
            throw new IllegalArgumentException("Apartamento y estancia son obligatorios.");
        if (servicioApartamento.buscarPorCodigo(codigoApartamento) == null) return false;
        sincronizar();
        IndicePorApartamento indice = buscarIndice(codigoApartamento.trim());
        return indice == null || !indice.arbol.haySolapamiento(estancia, codigoExcluir);
    }

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
        for (Apartamento apartamento : servicioApartamento.listarApartamentos())
            if (estaDisponible(apartamento.getCodigo(), estancia, personas))
                resultado.agregarFinal(apartamento);
        return resultado;
    }

    public ListaDoble<Reserva> reservasEnFecha(String codigoApartamento, LocalDate noche) {
        if (codigoApartamento == null || codigoApartamento.isBlank() || noche == null)
            throw new IllegalArgumentException("Apartamento y fecha son obligatorios.");
        sincronizar();
        IndicePorApartamento indice = buscarIndice(codigoApartamento.trim());
        return indice == null ? new ListaDoble<>() : indice.arbol.reservasEnNoche(noche);
    }
}
