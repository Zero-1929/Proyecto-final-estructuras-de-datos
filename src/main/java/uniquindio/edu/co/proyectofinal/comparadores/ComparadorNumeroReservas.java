package uniquindio.edu.co.proyectofinal.comparadores;

import java.util.Comparator;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.Reserva;

/** Ordena apartamentos por cantidad historica de reservas, de mayor a menor. */
public final class ComparadorNumeroReservas implements Comparator<Apartamento> {
    private final ListaDoble<Reserva> reservas;
    public ComparadorNumeroReservas(ListaDoble<Reserva> reservas) {
        if (reservas == null) throw new IllegalArgumentException("Reservas requeridas");
        this.reservas = reservas;
    }
    private int contar(String codigo) {
        int cantidad = 0;
        for (Reserva r : reservas)
            if (r.getApartamento().getCodigo().equalsIgnoreCase(codigo)) cantidad++;
        return cantidad;
    }
    @Override public int compare(Apartamento a, Apartamento b) {
        if (a == null || b == null) throw new IllegalArgumentException("Apartamento nulo");
        int diferencia = Integer.compare(contar(b.getCodigo()), contar(a.getCodigo()));
        return diferencia != 0 ? diferencia : a.getCodigo().compareTo(b.getCodigo());
    }
}
