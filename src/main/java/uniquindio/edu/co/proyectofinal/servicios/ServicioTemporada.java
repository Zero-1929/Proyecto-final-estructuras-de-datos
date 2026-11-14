package uniquindio.edu.co.proyectofinal.servicios;

import java.time.LocalDate;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.excepciones.FechasInvalidasException;
import uniquindio.edu.co.proyectofinal.modelo.Temporada;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;

/**
 * Administra las temporadas de tarifas del alojamiento.
 *
 * Las temporadas no pueden solaparse. Cuando una fecha no pertenece a
 * ninguna temporada registrada se utiliza la temporada por defecto.
 */
public class ServicioTemporada {

    /**
     * Temporadas registradas.
     */
    private final ListaDoble<Temporada> temporadas;

    /**
     * Tipo de temporada aplicado cuando una fecha no tiene temporada.
     */
    private TipoTemporada temporadaPorDefecto;

    /**
     * Crea el servicio con la temporada BAJA como valor por defecto.
     */
    public ServicioTemporada() {
        this(TipoTemporada.BAJA);
    }

    /**
     * Crea el servicio indicando la temporada por defecto.
     *
     * @param temporadaPorDefecto tipo usado cuando no hay temporada
     */
    public ServicioTemporada(TipoTemporada temporadaPorDefecto) {

        if (temporadaPorDefecto == null) {
            throw new IllegalArgumentException(
                    "La temporada por defecto no puede ser nula."
            );
        }

        this.temporadas = new ListaDoble<>();
        this.temporadaPorDefecto = temporadaPorDefecto;
    }

    /**
     * Obtiene la temporada por defecto.
     *
     * @return tipo de temporada por defecto
     */
    public TipoTemporada getTemporadaPorDefecto() {
        return temporadaPorDefecto;
    }

    /**
     * Cambia la temporada por defecto.
     *
     * @param temporadaPorDefecto nuevo tipo por defecto
     */
    public void setTemporadaPorDefecto(TipoTemporada temporadaPorDefecto) {

        if (temporadaPorDefecto == null) {
            throw new IllegalArgumentException(
                    "La temporada por defecto no puede ser nula."
            );
        }

        this.temporadaPorDefecto = temporadaPorDefecto;
    }

    /**
     * Registra una temporada a partir de sus datos.
     *
     * @param tipo tipo de temporada
     * @param fechaInicio fecha inicial inclusiva
     * @param fechaFin fecha final inclusiva
     * @return temporada registrada
     * @throws FechasInvalidasException si las fechas no son validas o se solapan
     */
    public Temporada registrarTemporada(
            TipoTemporada tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) throws FechasInvalidasException {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de temporada no puede ser nulo."
            );
        }

        validarFechas(fechaInicio, fechaFin);

        Temporada nueva = new Temporada(tipo, fechaInicio, fechaFin);
        validarNoSolapamiento(nueva);

        temporadas.agregarFinal(nueva);
        return nueva;
    }

    /**
     * Registra una temporada ya construida.
     *
     * @param temporada temporada que se desea registrar
     * @return la misma temporada registrada
     * @throws FechasInvalidasException si las fechas no son validas o se solapan
     */
    public Temporada registrarTemporada(Temporada temporada)
            throws FechasInvalidasException {

        if (temporada == null) {
            throw new IllegalArgumentException(
                    "La temporada no puede ser nula."
            );
        }

        validarFechas(
                temporada.getFechaInicio(),
                temporada.getFechaFin()
        );
        validarNoSolapamiento(temporada);

        temporadas.agregarFinal(temporada);
        return temporada;
    }

    /**
     * Elimina una temporada registrada.
     *
     * @param temporada temporada que se desea eliminar
     * @return true si la temporada estaba registrada
     */
    public boolean eliminarTemporada(Temporada temporada) {

        if (temporada == null) {
            return false;
        }

        return temporadas.eliminar(temporada);
    }

    /**
     * Busca la temporada que contiene una fecha.
     *
     * @param fecha fecha consultada
     * @return temporada encontrada o null si no existe
     */
    public Temporada buscarTemporada(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha no puede ser nula."
            );
        }

        for (Temporada temporada : temporadas) {
            if (temporada.contieneFecha(fecha)) {
                return temporada;
            }
        }

        return null;
    }

    /**
     * Obtiene el tipo de temporada aplicable a una fecha.
     *
     * @param fecha fecha consultada
     * @return tipo de la temporada o el tipo por defecto
     */
    public TipoTemporada obtenerTipoTemporada(LocalDate fecha) {

        Temporada temporada = buscarTemporada(fecha);

        if (temporada == null) {
            return temporadaPorDefecto;
        }

        return temporada.getTipo();
    }

    /**
     * Devuelve una copia de las temporadas registradas.
     *
     * @return lista con las temporadas
     */
    public ListaDoble<Temporada> obtenerTemporadas() {

        ListaDoble<Temporada> copia = new ListaDoble<>();

        for (Temporada temporada : temporadas) {
            copia.agregarFinal(temporada);
        }

        return copia;
    }

    /**
     * Obtiene la cantidad de temporadas registradas.
     *
     * @return cantidad de temporadas
     */
    public int cantidadTemporadas() {
        return temporadas.tamano();
    }

    /**
     * Indica si no hay temporadas registradas.
     *
     * @return true si el servicio esta vacio
     */
    public boolean estaVacia() {
        return temporadas.estaVacia();
    }

    /**
     * Elimina todas las temporadas registradas.
     */
    public void limpiar() {
        temporadas.limpiar();
    }

    /**
     * Valida que un rango de fechas sea correcto.
     *
     * @param fechaEntrada fecha inicial
     * @param fechaSalida fecha final
     * @throws FechasInvalidasException si alguna fecha es nula o el rango es invalido
     */
    public static void validarFechas(
            LocalDate fechaEntrada,
            LocalDate fechaSalida
    ) throws FechasInvalidasException {

        if (fechaEntrada == null || fechaSalida == null) {
            throw new FechasInvalidasException(
                    "Las fechas de entrada y salida son obligatorias."
            );
        }

        if (!fechaEntrada.isBefore(fechaSalida)) {
            throw new FechasInvalidasException(
                    "La fecha de entrada debe ser anterior a la de salida."
            );
        }
    }

    /**
     * Comprueba que una temporada no se solape con las ya registradas.
     *
     * @param nueva temporada que se desea agregar
     * @throws FechasInvalidasException si hay solapamiento
     */
    private void validarNoSolapamiento(Temporada nueva)
            throws FechasInvalidasException {

        for (Temporada existente : temporadas) {
            if (existente.seSolapaCon(nueva)) {
                throw new FechasInvalidasException(
                        "La temporada se solapa con una temporada existente."
                );
            }
        }
    }
}
