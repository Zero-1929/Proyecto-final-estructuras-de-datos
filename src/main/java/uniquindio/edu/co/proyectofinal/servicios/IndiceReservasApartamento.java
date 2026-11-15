package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Estancia;
import uniquindio.edu.co.proyectofinal.modelo.EstadoReserva;
import uniquindio.edu.co.proyectofinal.modelo.Reserva;

import java.time.LocalDate;

/**
 * Arbol binario por fecha de entrada, separado por apartamento.
 * La fecha maxima de salida en cada subarbol permite podar busquedas.
 * La salida es exclusiva. Las reservas canceladas/no-show no bloquean.
 */
public final class IndiceReservasApartamento {
    private Nodo raiz;

    private static final class Nodo {
        final LocalDate entrada;
        LocalDate maximaSalida;
        final ListaDoble<Reserva> reservas = new ListaDoble<>();
        Nodo izquierdo, derecho;

        Nodo(Reserva reserva) {
            entrada = reserva.getEstancia().getFechaEntrada();
            maximaSalida = reserva.getEstancia().getFechaSalida();
            reservas.agregarFinal(reserva);
        }
    }

    public void insertar(Reserva reserva) {
        if (reserva == null) throw new IllegalArgumentException("Reserva requerida");
        raiz = insertarRecursivo(raiz, reserva);
    }

    private Nodo insertarRecursivo(Nodo nodo, Reserva reserva) {
        if (nodo == null) return new Nodo(reserva);
        LocalDate entrada = reserva.getEstancia().getFechaEntrada();
        int cmp = entrada.compareTo(nodo.entrada);
        if (cmp < 0) nodo.izquierdo = insertarRecursivo(nodo.izquierdo, reserva);
        else if (cmp > 0) nodo.derecho = insertarRecursivo(nodo.derecho, reserva);
        else nodo.reservas.agregarFinal(reserva);
        LocalDate salida = reserva.getEstancia().getFechaSalida();
        if (salida.isAfter(nodo.maximaSalida)) nodo.maximaSalida = salida;
        return nodo;
    }

    public boolean haySolapamiento(Estancia estancia, String codigoExcluir) {
        if (estancia == null) throw new IllegalArgumentException("Estancia requerida");
        return haySolapamiento(raiz, estancia, codigoExcluir);
    }

    private boolean haySolapamiento(Nodo nodo, Estancia estancia, String codigoExcluir) {
        if (nodo == null || !nodo.maximaSalida.isAfter(estancia.getFechaEntrada()))
            return false;
        if (haySolapamiento(nodo.izquierdo, estancia, codigoExcluir)) return true;
        if (nodo.entrada.isBefore(estancia.getFechaSalida())) {
            for (Reserva reserva : nodo.reservas) {
                if ((codigoExcluir == null || !reserva.getCodigo().equalsIgnoreCase(codigoExcluir))
                        && bloquea(reserva.getEstado())
                        && reserva.getEstancia().seSolapaCon(estancia)) return true;
            }
            return haySolapamiento(nodo.derecho, estancia, codigoExcluir);
        }
        return false;
    }

    public ListaDoble<Reserva> reservasEnNoche(LocalDate noche) {
        if (noche == null) throw new IllegalArgumentException("Noche requerida");
        ListaDoble<Reserva> resultado = new ListaDoble<>();
        recolectar(raiz, noche, resultado);
        return resultado;
    }

    private void recolectar(Nodo nodo, LocalDate noche, ListaDoble<Reserva> resultado) {
        if (nodo == null || !nodo.maximaSalida.isAfter(noche)) return;
        recolectar(nodo.izquierdo, noche, resultado);
        if (!nodo.entrada.isAfter(noche)) {
            for (Reserva reserva : nodo.reservas)
                if (bloquea(reserva.getEstado()) && reserva.getEstancia().contieneNoche(noche))
                    resultado.agregarFinal(reserva);
            recolectar(nodo.derecho, noche, resultado);
        }
    }

    private boolean bloquea(EstadoReserva estado) {
        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO
                || estado == EstadoReserva.FINALIZADA;
    }
}
