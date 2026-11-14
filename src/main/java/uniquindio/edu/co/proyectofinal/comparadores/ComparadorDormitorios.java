package uniquindio.edu.co.proyectofinal.comparadores;

import java.util.Comparator;

import uniquindio.edu.co.proyectofinal.modelo.Apartamento;

/**
 * Compara apartamentos por numero de dormitorios.
 *
 * El orden natural es ascendente: primero el apartamento con menos
 * dormitorios. Si hay empate se compara la capacidad y, finalmente,
 * el codigo del apartamento.
 */
public class ComparadorDormitorios implements Comparator<Apartamento> {

    /**
     * Atributo que indica si el orden se invierte.
     */
    private final boolean descendente;

    /**
     * Crea el comparador en orden ascendente.
     */
    public ComparadorDormitorios() {
        this(false);
    }

    /**
     * Crea el comparador indicando el sentido del orden.
     *
     * @param descendente true para ordenar de mayor a menor
     */
    public ComparadorDormitorios(boolean descendente) {
        this.descendente = descendente;
    }

    /**
     * Compara dos apartamentos.
     *
     * @param primero primer apartamento
     * @param segundo segundo apartamento
     * @return valor negativo, cero o positivo segun el orden
     */
    @Override
    public int compare(Apartamento primero, Apartamento segundo) {

        if (primero == null || segundo == null) {
            throw new IllegalArgumentException(
                    "Los apartamentos a comparar no pueden ser nulos."
            );
        }

        int comparacion = Integer.compare(
                primero.getNumeroDormitorios(),
                segundo.getNumeroDormitorios()
        );

        if (comparacion == 0) {
            comparacion = Integer.compare(
                    primero.getCapacidad(),
                    segundo.getCapacidad()
            );
        }

        if (comparacion == 0) {
            comparacion = primero.getCodigo().compareTo(segundo.getCodigo());
        }

        return descendente ? -comparacion : comparacion;
    }
}
