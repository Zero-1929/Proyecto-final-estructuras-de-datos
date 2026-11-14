package uniquindio.edu.co.proyectofinal.servicios;

import java.time.LocalDate;

import uniquindio.edu.co.proyectofinal.comparadores.ComparadorReservas;
import uniquindio.edu.co.proyectofinal.estructuras.Cola;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.CanalReserva;
import uniquindio.edu.co.proyectofinal.modelo.EstadoApartamento;
import uniquindio.edu.co.proyectofinal.modelo.EstadoReserva;
import uniquindio.edu.co.proyectofinal.modelo.Estancia;
import uniquindio.edu.co.proyectofinal.modelo.Folio;
import uniquindio.edu.co.proyectofinal.modelo.Huesped;
import uniquindio.edu.co.proyectofinal.modelo.Reserva;
import uniquindio.edu.co.proyectofinal.modelo.SolicitudExterna;

/**
 * Procesa las solicitudes que llegan por el canal externo.
 *
 * Las solicitudes se atienden en orden FIFO. Antes de crear una reserva
 * se comprueba que el apartamento exista, este operativo, tenga capacidad
 * y no tenga otra reserva activa que se solape con las fechas solicitadas.
 */
public class ServicioExterno {

    /**
     * Fecha de nacimiento usada cuando la solicitud no trae ese dato.
     */
    private static final LocalDate FECHA_NACIMIENTO_PROVISIONAL =
            LocalDate.of(1990, 1, 1);

    /**
     * Solicitudes pendientes de procesar.
     */
    private final Cola<SolicitudExterna> solicitudesPendientes;

    /**
     * Solicitudes procesadas o rechazadas.
     */
    private final ListaDoble<SolicitudExterna> historial;

    /**
     * Apartamentos conocidos por el servicio.
     */
    private final ListaDoble<Apartamento> apartamentos;

    /**
     * Reservas registradas en el sistema.
     */
    private final ListaDoble<Reserva> reservas;

    /**
     * Huespedes registrados en el sistema.
     */
    private final ListaDoble<Huesped> huespedes;

    /**
     * Comparador usado para ordenar las reservas por fecha de entrada.
     */
    private final ComparadorReservas comparadorReservas;

    /**
     * Fecha de nacimiento provisional para nuevos huespedes externos.
     */
    private final LocalDate fechaNacimientoProvisional;

    /**
     * Siguiente identificador de folio.
     */
    private int siguienteIdFolio;

    /**
     * Siguiente numero consecutivo de reserva externa.
     */
    private int siguienteNumeroReserva;

    /**
     * Crea el servicio con la fecha de nacimiento provisional por defecto.
     */
    public ServicioExterno() {
        this(FECHA_NACIMIENTO_PROVISIONAL);
    }

    /**
     * Crea el servicio indicando la fecha de nacimiento provisional.
     *
     * @param fechaNacimientoProvisional fecha usada para nuevos huespedes
     */
    public ServicioExterno(LocalDate fechaNacimientoProvisional) {

        if (fechaNacimientoProvisional == null) {
            throw new IllegalArgumentException(
                    "La fecha de nacimiento provisional no puede ser nula."
            );
        }

        this.solicitudesPendientes = new Cola<>();
        this.historial = new ListaDoble<>();
        this.apartamentos = new ListaDoble<>();
        this.reservas = new ListaDoble<>();
        this.huespedes = new ListaDoble<>();
        this.comparadorReservas = new ComparadorReservas();
        this.fechaNacimientoProvisional = fechaNacimientoProvisional;
        this.siguienteIdFolio = 1;
        this.siguienteNumeroReserva = 1;
    }

    /**
     * Registra un apartamento disponible para el canal externo.
     *
     * @param apartamento apartamento que se registra
     */
    public void registrarApartamento(Apartamento apartamento) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo."
            );
        }

        if (buscarApartamento(apartamento.getCodigo()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un apartamento con el codigo "
                            + apartamento.getCodigo() + "."
            );
        }

        apartamentos.agregarFinal(apartamento);
    }

    /**
     * Registra un huesped conocido por el sistema.
     *
     * @param huesped huesped que se registra
     */
    public void registrarHuesped(Huesped huesped) {

        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huesped no puede ser nulo."
            );
        }

        if (huespedes.contiene(huesped)) {
            throw new IllegalArgumentException(
                    "Ya existe un huesped con el documento "
                            + huesped.getDocumento() + "."
            );
        }

        huespedes.agregarFinal(huesped);
    }

    /**
     * Registra una reserva existente para controlar la disponibilidad.
     *
     * @param reserva reserva que se registra
     */
    public void registrarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula."
            );
        }

        if (reservas.contiene(reserva)) {
            throw new IllegalArgumentException(
                    "Ya existe una reserva con el codigo "
                            + reserva.getCodigo() + "."
            );
        }

        reservas.agregarFinal(reserva);
    }

    /**
     * Recibe una solicitud externa y la agrega a la cola FIFO.
     *
     * @param solicitud solicitud recibida
     */
    public void recibirSolicitud(SolicitudExterna solicitud) {

        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "La solicitud no puede ser nula."
            );
        }

        solicitudesPendientes.encolar(solicitud);
    }

    /**
     * Procesa la primera solicitud de la cola.
     *
     * @return reserva creada o null si la solicitud fue rechazada
     */
    public Reserva procesarSiguiente() {

        if (solicitudesPendientes.estaVacia()) {
            return null;
        }

        SolicitudExterna solicitud = solicitudesPendientes.desencolar();

        Apartamento apartamento = buscarApartamento(
                solicitud.getCodigoApartamento()
        );

        if (apartamento == null) {
            rechazar(solicitud, "El apartamento solicitado no existe.");
            return null;
        }

        if (apartamento.getEstado() == EstadoApartamento.FUERA_DE_SERVICIO) {
            rechazar(solicitud, "El apartamento esta fuera de servicio.");
            return null;
        }

        if (!apartamento.tieneCapacidadPara(solicitud.getNumeroPersonas())) {
            rechazar(
                    solicitud,
                    "La cantidad de personas supera la capacidad del apartamento."
            );
            return null;
        }

        Estancia estancia;

        try {
            estancia = new Estancia(
                    solicitud.getFechaEntrada(),
                    solicitud.getFechaSalida()
            );
        } catch (IllegalArgumentException excepcion) {
            rechazar(solicitud, excepcion.getMessage());
            return null;
        }

        if (haySolapamiento(apartamento, estancia)) {
            rechazar(
                    solicitud,
                    "El apartamento ya tiene una reserva activa en esas fechas."
            );
            return null;
        }

        Huesped titular = buscarHuesped(solicitud.getDocumento());

        if (titular == null) {
            titular = crearHuespedProvisional(solicitud);
            huespedes.agregarFinal(titular);
        }

        Folio folio = new Folio(siguienteIdFolio++);

        Reserva reserva = new Reserva(
                generarCodigoReserva(),
                titular,
                apartamento,
                estancia,
                CanalReserva.PLATAFORMA_EXTERNA,
                folio
        );

        reservas.agregarFinal(reserva);
        solicitud.marcarProcesada();
        historial.agregarFinal(solicitud);

        return reserva;
    }

    /**
     * Procesa todas las solicitudes pendientes.
     *
     * @return reservas creadas
     */
    public ListaDoble<Reserva> procesarTodas() {

        ListaDoble<Reserva> creadas = new ListaDoble<>();

        while (!solicitudesPendientes.estaVacia()) {

            Reserva reserva = procesarSiguiente();

            if (reserva != null) {
                creadas.agregarFinal(reserva);
            }
        }

        return creadas;
    }

    /**
     * Rechaza manualmente la primera solicitud de la cola.
     *
     * @param motivo motivo del rechazo
     * @return true si habia una solicitud pendiente
     */
    public boolean rechazarSiguiente(String motivo) {

        if (solicitudesPendientes.estaVacia()) {
            return false;
        }

        SolicitudExterna solicitud = solicitudesPendientes.desencolar();
        rechazar(solicitud, motivo);
        return true;
    }

    /**
     * Consulta la primera solicitud sin retirarla.
     *
     * @return solicitud pendiente o null si no hay
     */
    public SolicitudExterna verSiguienteSolicitud() {

        if (solicitudesPendientes.estaVacia()) {
            return null;
        }

        return solicitudesPendientes.consultarFrente();
    }

    /**
     * Obtiene la cantidad de solicitudes pendientes.
     *
     * @return cantidad de solicitudes
     */
    public int cantidadPendientes() {
        return solicitudesPendientes.tamano();
    }

    /**
     * Indica si no hay solicitudes pendientes.
     *
     * @return true si la cola esta vacia
     */
    public boolean estaVacia() {
        return solicitudesPendientes.estaVacia();
    }

    /**
     * Devuelve una copia del historial de solicitudes.
     *
     * @return solicitudes procesadas o rechazadas
     */
    public ListaDoble<SolicitudExterna> listarHistorial() {

        ListaDoble<SolicitudExterna> copia = new ListaDoble<>();

        for (SolicitudExterna solicitud : historial) {
            copia.agregarFinal(solicitud);
        }

        return copia;
    }

    /**
     * Devuelve una copia de las reservas registradas.
     *
     * @return reservas registradas
     */
    public ListaDoble<Reserva> listarReservas() {

        ListaDoble<Reserva> copia = new ListaDoble<>();

        for (Reserva reserva : reservas) {
            copia.agregarFinal(reserva);
        }

        return copia;
    }

    /**
     * Lista las reservas ordenadas por fecha de entrada.
     *
     * @return reservas ordenadas
     */
    public ListaDoble<Reserva> listarReservasPorFechaEntrada() {

        ListaDoble<Reserva> ordenadas = new ListaDoble<>();

        for (Reserva reserva : reservas) {

            int indice = 0;

            while (indice < ordenadas.tamano()
                    && comparadorReservas.compare(
                    ordenadas.obtener(indice),
                    reserva
            ) <= 0) {
                indice++;
            }

            ordenadas.insertar(indice, reserva);
        }

        return ordenadas;
    }

    /**
     * Busca un apartamento por su codigo.
     *
     * @param codigo codigo del apartamento
     * @return apartamento encontrado o null
     */
    public Apartamento buscarApartamento(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        String codigoBuscado = codigo.trim();

        for (Apartamento apartamento : apartamentos) {
            if (apartamento.getCodigo().equals(codigoBuscado)) {
                return apartamento;
            }
        }

        return null;
    }

    /**
     * Busca un huesped por su documento.
     *
     * @param documento documento del huesped
     * @return huesped encontrado o null
     */
    public Huesped buscarHuesped(String documento) {

        if (documento == null || documento.isBlank()) {
            return null;
        }

        String documentoBuscado = documento.trim();

        for (Huesped huesped : huespedes) {
            if (huesped.getDocumento().equals(documentoBuscado)) {
                return huesped;
            }
        }

        return null;
    }

    /**
     * Busca una reserva por su codigo.
     *
     * @param codigo codigo de la reserva
     * @return reserva encontrada o null
     */
    public Reserva buscarReserva(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        String codigoBuscado = codigo.trim();

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo().equals(codigoBuscado)) {
                return reserva;
            }
        }

        return null;
    }

    /**
     * Comprueba si una estancia se solapa con una reserva activa.
     *
     * @param apartamento apartamento consultado
     * @param estancia estancia propuesta
     * @return true si hay solapamiento
     */
    private boolean haySolapamiento(
            Apartamento apartamento,
            Estancia estancia
    ) {

        for (Reserva reserva : reservas) {

            boolean mismoApartamento = reserva.getApartamento()
                    .getCodigo()
                    .equals(apartamento.getCodigo());

            if (mismoApartamento
                    && esReservaActiva(reserva.getEstado())
                    && reserva.getEstancia().seSolapaCon(estancia)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Indica si una reserva ocupa las fechas del apartamento.
     *
     * @param estado estado de la reserva
     * @return true si la reserva esta activa
     */
    private boolean esReservaActiva(EstadoReserva estado) {
        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO;
    }

    /**
     * Crea un huesped provisional con los datos de la solicitud.
     *
     * @param solicitud solicitud externa
     * @return huesped creado
     */
    private Huesped crearHuespedProvisional(SolicitudExterna solicitud) {

        return new Huesped(
                solicitud.getNombreSolicitante(),
                fechaNacimientoProvisional,
                solicitud.getDocumento(),
                solicitud.getTelefono(),
                solicitud.getCorreo()
        );
    }

    /**
     * Genera el codigo consecutivo de una reserva externa.
     *
     * @return codigo de la reserva
     */
    private String generarCodigoReserva() {
        return String.format("EXT-%04d", siguienteNumeroReserva++);
    }

    /**
     * Marca una solicitud como rechazada y la guarda en el historial.
     *
     * @param solicitud solicitud rechazada
     * @param motivo motivo del rechazo
     */
    private void rechazar(SolicitudExterna solicitud, String motivo) {

        String motivoFinal = motivo;

        if (motivoFinal == null || motivoFinal.isBlank()) {
            motivoFinal = "Solicitud rechazada por el sistema.";
        }

        solicitud.marcarRechazada(motivoFinal);
        historial.agregarFinal(solicitud);
    }
}
