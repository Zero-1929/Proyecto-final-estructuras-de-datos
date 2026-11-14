package uniquindio.edu.co.proyectofinal.servicios;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Folio;
import uniquindio.edu.co.proyectofinal.modelo.Movimiento;
import uniquindio.edu.co.proyectofinal.modelo.Ocupante;
import uniquindio.edu.co.proyectofinal.modelo.Reserva;
import uniquindio.edu.co.proyectofinal.modelo.TipoMovimiento;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;

/**
 * Calcula y registra los cargos de alojamiento de una reserva.
 *
 * El cobro se realiza por persona y por noche. Los menores de seis anos
 * ocupan cupo pero no pagan. La noche de salida no se cobra porque el
 * recorrido de la estancia solo incluye las noches efectivas.
 */
public class ServicioFacturacion {

    /**
     * Servicio utilizado para conocer la temporada de cada noche.
     */
    private final ServicioTemporada servicioTemporada;

    /**
     * Crea el servicio con un administrador de temporadas nuevo.
     */
    public ServicioFacturacion() {
        this(new ServicioTemporada());
    }

    /**
     * Crea el servicio con un administrador de temporadas existente.
     *
     * @param servicioTemporada servicio de temporadas
     */
    public ServicioFacturacion(ServicioTemporada servicioTemporada) {

        if (servicioTemporada == null) {
            throw new IllegalArgumentException(
                    "El servicio de temporadas no puede ser nulo."
            );
        }

        this.servicioTemporada = servicioTemporada;
    }

    /**
     * Obtiene el servicio de temporadas utilizado.
     *
     * @return servicio de temporadas
     */
    public ServicioTemporada getServicioTemporada() {
        return servicioTemporada;
    }

    /**
     * Calcula el cargo de alojamiento cuando solo ocupa el titular.
     *
     * @param reserva reserva consultada
     * @return valor total del alojamiento
     */
    public BigDecimal calcularCargoAlojamiento(Reserva reserva) {
        return calcularCargoAlojamiento(reserva, new ListaDoble<>());
    }

    /**
     * Calcula el cargo de alojamiento de una reserva.
     *
     * La lista de acompanantes no debe incluir al titular, porque el
     * titular siempre se cuenta como una persona que paga.
     *
     * @param reserva reserva consultada
     * @param acompanantes ocupantes adicionales al titular
     * @return valor total del alojamiento
     */
    public BigDecimal calcularCargoAlojamiento(
            Reserva reserva,
            ListaDoble<Ocupante> acompanantes
    ) {

        validarReservaYCapacidad(reserva, acompanantes);

        BigDecimal total = BigDecimal.ZERO;

        for (LocalDate noche : reserva.getEstancia()) {
            total = total.add(
                    calcularCargoNocheInterno(reserva, noche, acompanantes)
            );
        }

        return total;
    }

    /**
     * Calcula el cargo de una noche especifica.
     *
     * @param reserva reserva consultada
     * @param noche noche que se desea cobrar
     * @param acompanantes ocupantes adicionales al titular
     * @return valor de la noche
     */
    public BigDecimal calcularCargoNoche(
            Reserva reserva,
            LocalDate noche,
            ListaDoble<Ocupante> acompanantes
    ) {

        validarReservaYCapacidad(reserva, acompanantes);

        if (noche == null) {
            throw new IllegalArgumentException(
                    "La noche no puede ser nula."
            );
        }

        if (!reserva.getEstancia().contieneNoche(noche)) {
            throw new IllegalArgumentException(
                    "La fecha indicada no pertenece a la estancia de la reserva."
            );
        }

        return calcularCargoNocheInterno(reserva, noche, acompanantes);
    }

    /**
     * Registra un cargo de alojamiento cuando solo ocupa el titular.
     *
     * @param reserva reserva que se va a facturar
     * @return valor total registrado
     */
    public BigDecimal registrarCargoAlojamiento(Reserva reserva) {
        return registrarCargoAlojamiento(reserva, new ListaDoble<>());
    }

    /**
     * Registra en el folio un movimiento por cada noche de alojamiento.
     *
     * @param reserva reserva que se va a facturar
     * @param acompanantes ocupantes adicionales al titular
     * @return valor total registrado
     */
    public BigDecimal registrarCargoAlojamiento(
            Reserva reserva,
            ListaDoble<Ocupante> acompanantes
    ) {

        validarReservaYCapacidad(reserva, acompanantes);

        Folio folio = reserva.getFolio();
        BigDecimal total = BigDecimal.ZERO;

        for (LocalDate noche : reserva.getEstancia()) {

            BigDecimal monto = calcularCargoNocheInterno(
                    reserva,
                    noche,
                    acompanantes
            );

            total = total.add(monto);

            if (monto.compareTo(BigDecimal.ZERO) > 0) {
                TipoTemporada tipo = servicioTemporada
                        .obtenerTipoTemporada(noche);

                String descripcion = "Alojamiento " + noche
                        + " (" + tipo + ")";

                folio.agregarMovimiento(
                        crearMovimiento(
                                folio,
                                descripcion,
                                TipoMovimiento.CARGO_ALOJAMIENTO,
                                monto
                        )
                );
            }
        }

        return total;
    }

    /**
     * Registra un pago en el folio de la reserva.
     *
     * @param reserva reserva asociada
     * @param monto valor del pago
     * @param descripcion descripcion del pago
     */
    public void registrarPago(
            Reserva reserva,
            BigDecimal monto,
            String descripcion
    ) {

        validarReserva(reserva);
        validarMonto(monto);
        validarDescripcion(descripcion);

        Folio folio = reserva.getFolio();

        folio.agregarMovimiento(
                crearMovimiento(
                        folio,
                        descripcion,
                        TipoMovimiento.PAGO,
                        monto
                )
        );
    }

    /**
     * Registra un cargo adicional en el folio de la reserva.
     *
     * @param reserva reserva asociada
     * @param monto valor del cargo
     * @param descripcion descripcion del cargo
     */
    public void registrarCargoAdicional(
            Reserva reserva,
            BigDecimal monto,
            String descripcion
    ) {

        validarReserva(reserva);
        validarMonto(monto);
        validarDescripcion(descripcion);

        Folio folio = reserva.getFolio();

        folio.agregarMovimiento(
                crearMovimiento(
                        folio,
                        descripcion,
                        TipoMovimiento.CARGO_ADICIONAL,
                        monto
                )
        );
    }

    /**
     * Deshace el ultimo movimiento del folio agregando el movimiento contrario.
     *
     * @param reserva reserva asociada
     */
    public void deshacerUltimoMovimiento(Reserva reserva) {

        validarReserva(reserva);

        Folio folio = reserva.getFolio();
        folio.deshacerUltimaOperacion(siguienteIdMovimiento(folio));
    }

    /**
     * Calcula el saldo actual del folio de la reserva.
     *
     * @param reserva reserva consultada
     * @return saldo del folio
     */
    public BigDecimal calcularSaldo(Reserva reserva) {

        validarReserva(reserva);
        return reserva.getFolio().calcularSaldo();
    }

    /**
     * Calcula el valor de una noche sin volver a validar la reserva.
     *
     * @param reserva reserva consultada
     * @param noche noche que se cobra
     * @param acompanantes ocupantes adicionales al titular
     * @return valor de la noche
     */
    private BigDecimal calcularCargoNocheInterno(
            Reserva reserva,
            LocalDate noche,
            ListaDoble<Ocupante> acompanantes
    ) {

        TipoTemporada tipo = servicioTemporada.obtenerTipoTemporada(noche);
        BigDecimal tarifa = reserva.getApartamento().obtenerTarifa(tipo);

        int personasQuePagan = 1 + contarPagantes(acompanantes, noche);

        return tarifa.multiply(BigDecimal.valueOf(personasQuePagan));
    }

    /**
     * Cuenta los acompanantes que no son menores de seis anos en una noche.
     *
     * @param acompanantes ocupantes adicionales al titular
     * @param noche noche consultada
     * @return cantidad de acompanantes que pagan
     */
    private int contarPagantes(
            ListaDoble<Ocupante> acompanantes,
            LocalDate noche
    ) {

        int cantidad = 0;

        for (Ocupante ocupante : acompanantes) {
            if (!ocupante.esMenorDeSeis(noche)) {
                cantidad++;
            }
        }

        return cantidad;
    }

    /**
     * Valida la reserva, los acompanantes y la capacidad del apartamento.
     *
     * @param reserva reserva consultada
     * @param acompanantes ocupantes adicionales al titular
     */
    private void validarReservaYCapacidad(
            Reserva reserva,
            ListaDoble<Ocupante> acompanantes
    ) {

        validarReserva(reserva);

        if (acompanantes == null) {
            throw new IllegalArgumentException(
                    "La lista de acompanantes no puede ser nula."
            );
        }

        int totalPersonas = 1 + acompanantes.tamano();

        if (!reserva.getApartamento().tieneCapacidadPara(totalPersonas)) {
            throw new IllegalArgumentException(
                    "La cantidad de personas supera la capacidad del apartamento."
            );
        }

        for (Ocupante ocupante : acompanantes) {
            if (ocupante == null) {
                throw new IllegalArgumentException(
                        "La lista de acompanantes no puede contener nulos."
                );
            }
        }
    }

    /**
     * Valida que la reserva tenga los datos necesarios para facturar.
     *
     * @param reserva reserva consultada
     */
    private void validarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula."
            );
        }

        if (reserva.getFolio() == null) {
            throw new IllegalArgumentException(
                    "La reserva debe tener un folio asociado."
            );
        }

        if (reserva.getApartamento() == null) {
            throw new IllegalArgumentException(
                    "La reserva debe tener un apartamento asociado."
            );
        }

        if (reserva.getEstancia() == null) {
            throw new IllegalArgumentException(
                    "La reserva debe tener una estancia asociada."
            );
        }
    }

    /**
     * Valida que un monto sea mayor que cero.
     *
     * @param monto valor consultado
     */
    private void validarMonto(BigDecimal monto) {

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero."
            );
        }
    }

    /**
     * Valida que una descripcion no este vacia.
     *
     * @param descripcion texto consultado
     */
    private void validarDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripcion no puede estar vacia."
            );
        }
    }

    /**
     * Crea un movimiento con el siguiente identificador del folio.
     *
     * @param folio folio asociado
     * @param descripcion descripcion del movimiento
     * @param tipo tipo de movimiento
     * @param monto valor del movimiento
     * @return movimiento creado
     */
    private Movimiento crearMovimiento(
            Folio folio,
            String descripcion,
            TipoMovimiento tipo,
            BigDecimal monto
    ) {

        return new Movimiento(
                siguienteIdMovimiento(folio),
                descripcion,
                tipo,
                monto,
                LocalDateTime.now()
        );
    }

    /**
     * Calcula el siguiente identificador de movimiento de un folio.
     *
     * @param folio folio consultado
     * @return maximo identificador existente mas uno
     */
    private int siguienteIdMovimiento(Folio folio) {

        int maximo = 0;

        for (Movimiento movimiento : folio.getMovimientos()) {
            if (movimiento.getId() > maximo) {
                maximo = movimiento.getId();
            }
        }

        return maximo + 1;
    }
}
