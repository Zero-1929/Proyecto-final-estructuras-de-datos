package uniquindio.edu.co.proyectofinal.servicios;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.Estancia;
import uniquindio.edu.co.proyectofinal.modelo.Ocupante;
import uniquindio.edu.co.proyectofinal.modelo.Temporada;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;

/**
 * Servicio encargado de calcular las cotizaciones del SGA.
 *
 * El alojamiento cobra por persona y por noche.
 * Los menores de seis años ocupan cupo, pero no pagan.
 *
 * La temporada BAJA es la temporada base.
 */
public class ServicioCotizacion {

    private static final BigDecimal PORCENTAJE_ANTICIPO =
            new BigDecimal("0.30");

    private final ListaDoble<Temporada> temporadas;

    /**
     * Crea el servicio sin temporadas especiales.
     * Todas las fechas corresponden inicialmente a BAJA.
     */
    public ServicioCotizacion() {
        this.temporadas = new ListaDoble<>();
    }

    /**
     * Crea el servicio con temporadas existentes.
     *
     * Se copian los períodos para evitar modificar
     * directamente la lista recibida.
     */
    public ServicioCotizacion(ListaDoble<Temporada> temporadas) {
        if (temporadas == null) {
            throw new IllegalArgumentException(
                    "La lista de temporadas no puede ser nula.");
        }

        this.temporadas = new ListaDoble<>();

        for (Temporada temporada : temporadas) {
            registrarTemporada(temporada);
        }
    }

    /**
     * Registra una temporada especial.
     *
     * BAJA es la temporada base, por lo que no
     * necesita registrarse como período especial.
     *
     * @return true si se registró correctamente.
     */
    public boolean registrarTemporada(Temporada temporada) {
        if (temporada == null) {
            throw new IllegalArgumentException(
                    "La temporada no puede ser nula.");
        }

        if (temporada.getTipo() == TipoTemporada.BAJA) {
            throw new IllegalArgumentException(
                    "BAJA es la temporada base y no requiere un período.");
        }

        for (Temporada existente : temporadas) {
            if (existente.seSolapaCon(temporada)) {
                return false;
            }
        }

        temporadas.agregarFinal(temporada);
        return true;
    }

    /**
     * Obtiene el tipo de temporada correspondiente
     * a una fecha.
     */
    public TipoTemporada obtenerTemporada(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha no puede ser nula.");
        }

        for (Temporada temporada : temporadas) {
            if (temporada.contieneFecha(fecha)) {
                return temporada.getTipo();
            }
        }

        return TipoTemporada.BAJA;
    }

    /**
     * Cuenta los ocupantes que deben pagar
     * en una noche determinada.
     */
    public int contarPersonasQuePagan(
            ListaDoble<Ocupante> ocupantes,
            LocalDate noche) {

        validarOcupantes(ocupantes);

        if (noche == null) {
            throw new IllegalArgumentException(
                    "La fecha de la noche no puede ser nula.");
        }

        int cantidad = 0;

        for (Ocupante ocupante : ocupantes) {
            if (ocupante.getFechaNacimiento().isAfter(noche)) {
                throw new IllegalArgumentException(
                        "Un ocupante no puede haber nacido después "
                                + "de la noche cotizada.");
            }

            if (!ocupante.esMenorDeSeis(noche)) {
                cantidad++;
            }
        }

        return cantidad;
    }

    /**
     * Calcula el valor de una noche.
     *
     * Fórmula:
     * tarifa por persona * personas que pagan.
     */
    public BigDecimal calcularValorNoche(
            Apartamento apartamento,
            LocalDate noche,
            ListaDoble<Ocupante> ocupantes) {

        validarApartamento(apartamento);
        validarOcupantes(ocupantes);
        validarCapacidad(apartamento, ocupantes);

        TipoTemporada tipo = obtenerTemporada(noche);

        BigDecimal tarifa = apartamento.obtenerTarifa(tipo);

        int personasQuePagan =
                contarPersonasQuePagan(ocupantes, noche);

        return tarifa.multiply(
                BigDecimal.valueOf(personasQuePagan));
    }

    /**
     * Calcula el valor total de una estancia.
     *
     * Recorre las noches utilizando el Iterator
     * implementado por Estancia.
     */
    public BigDecimal calcularTotal(
            Apartamento apartamento,
            Estancia estancia,
            ListaDoble<Ocupante> ocupantes) {

        validarCotizacion(apartamento, estancia, ocupantes);

        BigDecimal total = BigDecimal.ZERO;

        for (LocalDate noche : estancia) {
            BigDecimal valorNoche =
                    calcularValorNoche(apartamento, noche, ocupantes);

            total = total.add(valorNoche);
        }

        return total;
    }

    /**
     * Genera el desglose de la cotización
     * para cada noche de la estancia.
     */
    public ListaDoble<DetalleNoche> generarDesglose(
            Apartamento apartamento,
            Estancia estancia,
            ListaDoble<Ocupante> ocupantes) {

        validarCotizacion(apartamento, estancia, ocupantes);

        ListaDoble<DetalleNoche> desglose = new ListaDoble<>();

        for (LocalDate noche : estancia) {

            TipoTemporada temporada = obtenerTemporada(noche);

            BigDecimal tarifa =
                    apartamento.obtenerTarifa(temporada);

            int personasQuePagan =
                    contarPersonasQuePagan(ocupantes, noche);

            BigDecimal subtotal = tarifa.multiply(
                    BigDecimal.valueOf(personasQuePagan));

            DetalleNoche detalle = new DetalleNoche(
                    noche,
                    temporada,
                    ocupantes.tamano(),
                    personasQuePagan,
                    tarifa,
                    subtotal
            );

            desglose.agregarFinal(detalle);
        }

        return desglose;
    }

    /**
     * Calcula el anticipo del 30 % sobre el total.
     *
     * El resultado se redondea a dos decimales.
     */
    public BigDecimal calcularAnticipo(BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El total no puede ser nulo ni negativo.");
        }

        return total.multiply(PORCENTAJE_ANTICIPO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula directamente el anticipo de una estancia.
     */
    public BigDecimal calcularAnticipo(
            Apartamento apartamento,
            Estancia estancia,
            ListaDoble<Ocupante> ocupantes) {

        BigDecimal total =
                calcularTotal(apartamento, estancia, ocupantes);

        return calcularAnticipo(total);
    }

    /**
     * Calcula el saldo restante después del anticipo.
     */
    public BigDecimal calcularSaldoRestante(BigDecimal total) {
        BigDecimal anticipo = calcularAnticipo(total);

        return total.subtract(anticipo);
    }

    /**
     * Devuelve los períodos especiales registrados.
     */
    public ListaDoble<Temporada> listarTemporadas() {
        ListaDoble<Temporada> resultado = new ListaDoble<>();

        for (Temporada temporada : temporadas) {
            resultado.agregarFinal(temporada);
        }

        return resultado;
    }

    /**
     * Valida los datos necesarios para cotizar.
     */
    private void validarCotizacion(
            Apartamento apartamento,
            Estancia estancia,
            ListaDoble<Ocupante> ocupantes) {

        validarApartamento(apartamento);
        validarOcupantes(ocupantes);
        validarCapacidad(apartamento, ocupantes);

        if (estancia == null) {
            throw new IllegalArgumentException(
                    "La estancia no puede ser nula.");
        }
    }

    /**
     * Valida el apartamento.
     */
    private void validarApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo.");
        }
    }

    /**
     * Valida que exista al menos un ocupante
     * y que no haya ocupantes nulos.
     */
    private void validarOcupantes(ListaDoble<Ocupante> ocupantes) {
        if (ocupantes == null || ocupantes.estaVacia()) {
            throw new IllegalArgumentException(
                    "Debe existir al menos un ocupante.");
        }

        for (Ocupante ocupante : ocupantes) {
            if (ocupante == null) {
                throw new IllegalArgumentException(
                        "La lista no puede contener ocupantes nulos.");
            }
        }
    }

    /**
     * Comprueba que el grupo no supere la capacidad.
     *
     * Los menores de seis años también ocupan cupo.
     */
    private void validarCapacidad(
            Apartamento apartamento,
            ListaDoble<Ocupante> ocupantes) {

        if (!apartamento.tieneCapacidadPara(ocupantes.tamano())) {
            throw new IllegalArgumentException(
                    "La cantidad de ocupantes supera "
                            + "la capacidad del apartamento.");
        }
    }

    /**
     * Representa el desglose de una noche cotizada.
     *
     * Record disponible en Java 25.
     */
    public record DetalleNoche(
            LocalDate fecha,
            TipoTemporada temporada,
            int totalOcupantes,
            int personasQuePagan,
            BigDecimal tarifaPorPersona,
            BigDecimal subtotal
    ) {
    }
}
