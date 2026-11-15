package uniquindio.edu.co.proyectofinal.persistencia;

import uniquindio.edu.co.proyectofinal.excepciones.FechasInvalidasException;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.CanalReserva;
import uniquindio.edu.co.proyectofinal.modelo.Empleado;
import uniquindio.edu.co.proyectofinal.modelo.EstadoApartamento;
import uniquindio.edu.co.proyectofinal.modelo.EstadoReserva;
import uniquindio.edu.co.proyectofinal.modelo.EstadoSolicitud;
import uniquindio.edu.co.proyectofinal.modelo.Estancia;
import uniquindio.edu.co.proyectofinal.modelo.Folio;
import uniquindio.edu.co.proyectofinal.modelo.Huesped;
import uniquindio.edu.co.proyectofinal.modelo.Movimiento;
import uniquindio.edu.co.proyectofinal.modelo.Reserva;
import uniquindio.edu.co.proyectofinal.modelo.SolicitudExterna;
import uniquindio.edu.co.proyectofinal.modelo.Temporada;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;
import uniquindio.edu.co.proyectofinal.servicios.GeneradorIdentificadores;
import uniquindio.edu.co.proyectofinal.servicios.ServicioApartamento;
import uniquindio.edu.co.proyectofinal.servicios.ServicioCotizacion;
import uniquindio.edu.co.proyectofinal.servicios.ServicioEmpleados;
import uniquindio.edu.co.proyectofinal.servicios.ServicioExterno;
import uniquindio.edu.co.proyectofinal.servicios.ServicioHuesped;
import uniquindio.edu.co.proyectofinal.servicios.ServicioPreparacion;
import uniquindio.edu.co.proyectofinal.servicios.ServicioPreparacion.TareaPreparacion;
import uniquindio.edu.co.proyectofinal.servicios.ServicioReserva;
import uniquindio.edu.co.proyectofinal.servicios.ServicioTemporada;
import uniquindio.edu.co.proyectofinal.servicios.ServicioTurnos;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Convierte entre el sistema en memoria y el snapshot que se guarda en disco.
 *
 * La clase reconstruye las estructuras propias a partir de arreglos simples.
 * No serializa directamente listas, arboles, pilas ni colas porque sus nodos
 * contienen referencias y ciclos que no son adecuados para JSON.
 */
public final class GestorPersistencia {

    private GestorPersistencia() {
    }

    /**
     * Crea el snapshot de todos los datos persistibles del sistema.
     *
     * @param sistema sistema que se desea guardar
     * @return snapshot listo para serializar
     */
    public static DatosSGA exportar(SistemaSGA sistema) {

        if (sistema == null) {
            throw new IllegalArgumentException(
                    "El sistema no puede ser nulo."
            );
        }

        ServicioApartamento servicioApartamento =
                sistema.getServicioApartamento();
        ServicioHuesped servicioHuesped = sistema.getServicioHuesped();
        ServicioEmpleados servicioEmpleados =
                sistema.getServicioEmpleados();
        ServicioTemporada servicioTemporada =
                sistema.getServicioTemporada();
        ServicioReserva servicioReserva = sistema.getServicioReserva();
        ServicioPreparacion servicioPreparacion =
                sistema.getServicioPreparacion();
        ServicioTurnos servicioTurnos = sistema.getServicioTurnos();
        ServicioExterno servicioExterno = sistema.getServicioExterno();
        GeneradorIdentificadores generador =
                sistema.getGeneradorIdentificadores();

        ListaDoble<Apartamento> apartamentos = new ListaDoble<>();

        for (Apartamento apartamento
                : servicioApartamento.listarApartamentos()) {
            agregarApartamentoSinDuplicar(apartamentos, apartamento);
        }

        for (Apartamento apartamento
                : servicioExterno.listarApartamentos()) {
            agregarApartamentoSinDuplicar(apartamentos, apartamento);
        }

        ListaDoble<Huesped> huespedes = new ListaDoble<>();

        for (Huesped huesped : servicioHuesped.listarHuespedes()) {
            agregarHuespedSinDuplicar(huespedes, huesped);
        }

        for (Huesped huesped : servicioExterno.listarHuespedes()) {
            agregarHuespedSinDuplicar(huespedes, huesped);
        }

        ListaDoble<Reserva> reservas = new ListaDoble<>();

        for (Reserva reserva : servicioReserva.listarReservas()) {
            agregarReservaSinDuplicar(reservas, reserva);
        }

        for (Reserva reserva : servicioExterno.listarReservas()) {
            agregarReservaSinDuplicar(reservas, reserva);
        }

        for (Reserva reserva : reservas) {
            agregarApartamentoSinDuplicar(
                    apartamentos,
                    reserva.getApartamento()
            );
            agregarHuespedSinDuplicar(huespedes, reserva.getHuesped());
        }

        ListaDoble<Folio> folios = new ListaDoble<>();

        for (Reserva reserva : reservas) {
            agregarFolioSinDuplicar(folios, reserva.getFolio());
        }

        String recepcionistaActual = null;

        if (!servicioTurnos.estaVacia()) {
            recepcionistaActual = servicioTurnos
                    .consultarTurnoActual()
                    .getDocumento();
        }

        return new DatosSGA(
                DatosSGA.VERSION_ACTUAL,
                convertir(
                        apartamentos,
                        DatosSGA.ApartamentoDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        huespedes,
                        DatosSGA.HuespedDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        servicioEmpleados.listarEmpleados(),
                        DatosSGA.EmpleadoDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        servicioTemporada.obtenerTemporadas(),
                        DatosSGA.TemporadaDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                servicioTemporada.getTemporadaPorDefecto(),
                convertir(
                        reservas,
                        DatosSGA.ReservaDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        folios,
                        DatosSGA.FolioDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        servicioExterno.listarSolicitudesPendientes(),
                        DatosSGA.SolicitudDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        servicioExterno.listarHistorial(),
                        DatosSGA.SolicitudDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        servicioPreparacion.listarPendientes(),
                        DatosSGA.TareaDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                convertir(
                        servicioTurnos.listarRecepcionistas(),
                        String[]::new,
                        Empleado::getDocumento
                ),
                recepcionistaActual,
                generador.getSiguienteGrupo(),
                generador.getSiguienteReserva(),
                generador.getSiguienteFolio(),
                servicioExterno.getSiguienteIdFolio(),
                servicioExterno.getSiguienteNumeroReserva()
        );
    }

    /**
     * Reconstruye el sistema a partir de un snapshot.
     *
     * @param datos snapshot leido desde disco
     * @return sistema listo para usar
     * @throws IOException si los datos no son consistentes
     */
    public static SistemaSGA importar(DatosSGA datos) throws IOException {

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos no pueden ser nulos."
            );
        }

        try {
            return importarInterno(datos);
        } catch (IOException e) {
            throw e;
        } catch (FechasInvalidasException e) {
            throw new IOException(
                    "No se pudieron restaurar las temporadas.",
                    e
            );
        } catch (RuntimeException e) {
            throw new IOException(
                    "Los datos guardados no son validos.",
                    e
            );
        }
    }

    private static SistemaSGA importarInterno(DatosSGA datos)
            throws IOException, FechasInvalidasException {

        if (datos.version() > DatosSGA.VERSION_ACTUAL) {
            throw new IOException(
                    "Version de datos no soportada: " + datos.version()
            );
        }

        ServicioApartamento servicioApartamento = new ServicioApartamento();

        for (DatosSGA.ApartamentoDatos dato : datos.apartamentos()) {
            Apartamento apartamento = crearApartamento(dato);

            if (!servicioApartamento.registrarApartamento(apartamento)) {
                throw new IOException(
                        "Apartamento duplicado: " + dato.codigo()
                );
            }
        }

        ServicioHuesped servicioHuesped = new ServicioHuesped();

        for (DatosSGA.HuespedDatos dato : datos.huespedes()) {
            Huesped huesped = new Huesped(
                    dato.nombre(),
                    fecha(
                            dato.fechaNacimiento(),
                            "nacimiento de " + dato.documento()
                    ),
                    dato.documento(),
                    dato.telefono(),
                    dato.correo(),
                    dato.rutaFoto()
            );

            for (String codigoFavorito : dato.favoritos()) {
                if (codigoFavorito != null && !codigoFavorito.isBlank()) {
                    huesped.agregarFavorito(codigoFavorito);
                }
            }

            if (!servicioHuesped.registrarHuesped(huesped)) {
                throw new IOException(
                        "Huesped duplicado: " + dato.documento()
                );
            }
        }

        ServicioEmpleados servicioEmpleados = new ServicioEmpleados();

        for (DatosSGA.EmpleadoDatos dato : datos.empleados()) {
            Empleado empleado = new Empleado(
                    dato.nombre(),
                    fecha(
                            dato.fechaNacimiento(),
                            "nacimiento de " + dato.documento()
                    ),
                    dato.documento(),
                    dato.telefono(),
                    dato.correo(),
                    dato.rol()
            );

            servicioEmpleados.registrarEmpleado(empleado);
        }

        ServicioTemporada servicioTemporada = new ServicioTemporada(
                datos.temporadaPorDefecto()
        );
        ServicioCotizacion servicioCotizacion = new ServicioCotizacion();

        for (DatosSGA.TemporadaDatos dato : datos.temporadas()) {
            Temporada temporada = new Temporada(
                    dato.tipo(),
                    fecha(dato.fechaInicio(), "inicio de temporada"),
                    fecha(dato.fechaFin(), "fin de temporada")
            );

            servicioTemporada.registrarTemporada(temporada);

            if (temporada.getTipo() != TipoTemporada.BAJA
                    && !servicioCotizacion.registrarTemporada(temporada)) {
                throw new IOException(
                        "Temporada solapada en cotizacion: "
                                + dato.fechaInicio()
                );
            }
        }

        ListaDoble<Folio> folios = new ListaDoble<>();

        for (DatosSGA.FolioDatos dato : datos.folios()) {
            Folio folio = new Folio(dato.id());
            ListaDoble<Movimiento> movimientos = new ListaDoble<>();

            for (DatosSGA.MovimientoDatos movimientoDato
                    : dato.movimientos()) {

                movimientos.agregarFinal(new Movimiento(
                        movimientoDato.id(),
                        movimientoDato.descripcion(),
                        movimientoDato.tipo(),
                        monto(
                                movimientoDato.monto(),
                                "movimiento " + movimientoDato.id()
                        ),
                        fechaHora(
                                movimientoDato.fecha(),
                                "movimiento " + movimientoDato.id()
                        )
                ));
            }

            ListaDoble<Movimiento> deshacibles = new ListaDoble<>();

            for (int id : dato.idsOperacionesDeshacibles()) {
                Movimiento movimiento = buscarMovimiento(movimientos, id);

                if (movimiento == null) {
                    throw new IOException(
                            "Movimiento deshacible no encontrado: " + id
                    );
                }

                deshacibles.agregarFinal(movimiento);
            }

            folio.restaurarMovimientos(movimientos, deshacibles);
            folios.agregarFinal(folio);
        }

        ListaDoble<Reserva> reservas = new ListaDoble<>();
        ListaDoble<Reserva> reservasExternas = new ListaDoble<>();

        for (DatosSGA.ReservaDatos dato : datos.reservas()) {
            Huesped huesped = servicioHuesped.buscarPorDocumento(
                    dato.documentoHuesped()
            );

            if (huesped == null) {
                throw new IOException(
                        "Huesped no encontrado: " + dato.documentoHuesped()
                );
            }

            Apartamento apartamento = servicioApartamento.buscarPorCodigo(
                    dato.codigoApartamento()
            );

            if (apartamento == null) {
                throw new IOException(
                        "Apartamento no encontrado: "
                                + dato.codigoApartamento()
                );
            }

            Folio folio = buscarFolio(folios, dato.folioId());

            if (folio == null) {
                throw new IOException(
                        "Folio no encontrado: " + dato.folioId()
                );
            }

            Estancia estancia = new Estancia(
                    fecha(
                            dato.fechaEntrada(),
                            "entrada de " + dato.codigo()
                    ),
                    fecha(
                            dato.fechaSalida(),
                            "salida de " + dato.codigo()
                    )
            );

            Reserva reserva = new Reserva(
                    dato.codigo(),
                    huesped,
                    apartamento,
                    estancia,
                    dato.canalReserva(),
                    folio,
                    fechaHora(
                            dato.fechaCreacion(),
                            "creacion de " + dato.codigo()
                    ),
                    horaOpcional(
                            dato.horaLlegadaEstimada(),
                            "llegada de " + dato.codigo()
                    )
            );

            aplicarEstado(reserva, dato.estado());

            reservas.agregarFinal(reserva);
        }

        ServicioReserva servicioReserva = new ServicioReserva(
                servicioApartamento,
                servicioCotizacion,
                reservas
        );

        ServicioExterno servicioExterno = new ServicioExterno(
                servicioReserva, servicioApartamento, servicioHuesped);

        for (DatosSGA.SolicitudDatos dato
                : datos.solicitudesPendientes()) {
            servicioExterno.recibirSolicitud(crearSolicitud(dato));
        }

        for (DatosSGA.SolicitudDatos dato
                : datos.solicitudesHistorial()) {
            servicioExterno.restaurarSolicitudHistorial(
                    crearSolicitud(dato)
            );
        }

        servicioExterno.restaurarContadores(
                datos.siguienteIdFolioExterno(),
                datos.siguienteNumeroReservaExterno()
        );

        ServicioPreparacion servicioPreparacion =
                new ServicioPreparacion();

        for (DatosSGA.TareaDatos dato : datos.tareasPreparacion()) {
            Apartamento apartamento = servicioApartamento.buscarPorCodigo(
                    dato.codigoApartamento()
            );

            if (apartamento == null) {
                throw new IOException(
                        "Apartamento de preparacion no encontrado: "
                                + dato.codigoApartamento()
                );
            }

            servicioPreparacion.restaurarTarea(
                    apartamento,
                    dato.prioridad(),
                    dato.orden(),
                    fechaHora(
                            dato.fechaSolicitud(),
                            "tarea de preparacion"
                    )
            );
        }

        ServicioTurnos servicioTurnos = new ServicioTurnos();
        ListaDoble<Empleado> recepcionistas = new ListaDoble<>();

        for (String documento : datos.recepcionistas()) {
            Empleado empleado = servicioEmpleados.buscarPorDocumento(
                    documento
            );

            if (empleado == null) {
                throw new IOException(
                        "Recepcionista no encontrado: " + documento
                );
            }

            recepcionistas.agregarFinal(empleado);
        }

        servicioTurnos.restaurarTurnos(
                recepcionistas,
                datos.recepcionistaActual()
        );

        GeneradorIdentificadores generador =
                new GeneradorIdentificadores(
                        datos.siguienteGrupo(),
                        datos.siguienteReserva(),
                        datos.siguienteFolio()
                );

        return new SistemaSGA(
                servicioApartamento,
                servicioHuesped,
                servicioEmpleados,
                servicioTemporada,
                servicioCotizacion,
                servicioReserva,
                servicioPreparacion,
                servicioTurnos,
                servicioExterno,
                generador
        );
    }

    private static Apartamento crearApartamento(
            DatosSGA.ApartamentoDatos dato
    ) throws IOException {

        Apartamento apartamento = new Apartamento(
                dato.codigo(),
                dato.nombre(),
                dato.bloque(),
                dato.piso(),
                dato.numeroDormitorios(),
                dato.capacidad(),
                dato.caracteristicas()
        );

        configurarTarifa(
                apartamento,
                TipoTemporada.BAJA,
                dato.tarifaBaja()
        );
        configurarTarifa(
                apartamento,
                TipoTemporada.MEDIA,
                dato.tarifaMedia()
        );
        configurarTarifa(
                apartamento,
                TipoTemporada.ALTA,
                dato.tarifaAlta()
        );

        if (dato.estado() == EstadoApartamento.FUERA_DE_SERVICIO) {
            String motivo = dato.motivoFueraServicio();

            if (motivo == null || motivo.isBlank()) {
                motivo = "Sin motivo registrado";
            }

            apartamento.marcarFueraDeServicio(motivo);
        } else {
            apartamento.setEstado(
                    dato.estado() == null
                            ? EstadoApartamento.PREPARADO
                            : dato.estado()
            );
        }

        return apartamento;
    }

    private static void configurarTarifa(
            Apartamento apartamento,
            TipoTemporada tipo,
            String valor
    ) throws IOException {

        if (valor == null || valor.isBlank()) {
            return;
        }

        try {
            apartamento.configurarTarifa(tipo, new BigDecimal(valor));
        } catch (NumberFormatException e) {
            throw new IOException(
                    "Tarifa invalida para " + apartamento.getCodigo(),
                    e
            );
        }
    }

    private static SolicitudExterna crearSolicitud(
            DatosSGA.SolicitudDatos dato
    ) throws IOException {

        SolicitudExterna solicitud = new SolicitudExterna(
                dato.idSolicitud(),
                dato.nombreSolicitante(),
                dato.documento(),
                dato.telefono(),
                dato.correo(),
                dato.codigoApartamento(),
                fecha(
                        dato.fechaEntrada(),
                        "entrada de " + dato.idSolicitud()
                ),
                fecha(
                        dato.fechaSalida(),
                        "salida de " + dato.idSolicitud()
                ),
                dato.numeroPersonas(),
                fechaHora(
                        dato.fechaRecepcion(),
                        "recepcion de " + dato.idSolicitud()
                )
        );

        if (dato.estado() == EstadoSolicitud.PROCESADA) {
            solicitud.marcarProcesada();
        } else if (dato.estado() == EstadoSolicitud.RECHAZADA) {
            String motivo = dato.motivoRechazo();

            if (motivo == null || motivo.isBlank()) {
                motivo = "Sin motivo registrado";
            }

            solicitud.marcarRechazada(motivo);
        }

        return solicitud;
    }

    private static void aplicarEstado(
            Reserva reserva,
            EstadoReserva estado
    ) {

        if (estado == null) {
            return;
        }

        switch (estado) {
            case PENDIENTE -> {
            }
            case CONFIRMADA -> reserva.confirmar();
            case EN_CURSO -> {
                reserva.confirmar();
                reserva.iniciarEstancia();
            }
            case FINALIZADA -> {
                reserva.confirmar();
                reserva.iniciarEstancia();
                reserva.finalizar();
            }
            case CANCELADA -> reserva.cancelar();
            case NO_PRESENTADO -> {
                reserva.confirmar();
                reserva.marcarNoShow();
            }
        }
    }

    private static DatosSGA.ApartamentoDatos aDatos(
            Apartamento apartamento
    ) {

        return new DatosSGA.ApartamentoDatos(
                apartamento.getCodigo(),
                apartamento.getNombre(),
                apartamento.getBloque(),
                apartamento.getPiso(),
                apartamento.getNumeroDormitorios(),
                apartamento.getCapacidad(),
                apartamento.getCaracteristicas(),
                apartamento.getEstado(),
                apartamento.getMotivoFueraServicio(),
                texto(apartamento.getTarifaBaja()),
                texto(apartamento.getTarifaMedia()),
                texto(apartamento.getTarifaAlta())
        );
    }

    private static DatosSGA.HuespedDatos aDatos(Huesped huesped) {
        return new DatosSGA.HuespedDatos(
                huesped.getNombre(),
                huesped.getFechaNacimiento().toString(),
                huesped.getDocumento(),
                huesped.getTelefono(),
                huesped.getCorreo(),
                huesped.getRutaFoto(),
                codigosFavoritos(huesped)
        );
    }

    private static String[] codigosFavoritos(Huesped huesped) {
        int cantidad = 0;
        for (String codigo : huesped.getFavoritos()) cantidad++;
        String[] codigos = new String[cantidad];
        int indice = 0;
        for (String codigo : huesped.getFavoritos()) codigos[indice++] = codigo;
        return codigos;
    }

    private static DatosSGA.EmpleadoDatos aDatos(Empleado empleado) {
        return new DatosSGA.EmpleadoDatos(
                empleado.getNombre(),
                empleado.getFechaNacimiento().toString(),
                empleado.getDocumento(),
                empleado.getTelefono(),
                empleado.getCorreo(),
                empleado.getRol()
        );
    }

    private static DatosSGA.TemporadaDatos aDatos(Temporada temporada) {
        return new DatosSGA.TemporadaDatos(
                temporada.getTipo(),
                temporada.getFechaInicio().toString(),
                temporada.getFechaFin().toString()
        );
    }

    private static DatosSGA.ReservaDatos aDatos(Reserva reserva) {
        return new DatosSGA.ReservaDatos(
                reserva.getCodigo(),
                reserva.getHuesped().getDocumento(),
                reserva.getApartamento().getCodigo(),
                reserva.getEstancia().getFechaEntrada().toString(),
                reserva.getEstancia().getFechaSalida().toString(),
                reserva.getEstado(),
                reserva.getCanalReserva(),
                reserva.getFechaCreacion().toString(),
                reserva.getHoraLlegadaEstimada() == null
                        ? null
                        : reserva.getHoraLlegadaEstimada().toString(),
                reserva.getFolio().getId()
        );
    }

    private static DatosSGA.FolioDatos aDatos(Folio folio) {
        ListaDoble<Movimiento> deshacibles =
                folio.getOperacionesDeshacibles();
        int[] ids = new int[deshacibles.tamano()];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = deshacibles.obtener(i).getId();
        }

        return new DatosSGA.FolioDatos(
                folio.getId(),
                convertir(
                        folio.getMovimientos(),
                        DatosSGA.MovimientoDatos[]::new,
                        GestorPersistencia::aDatos
                ),
                ids
        );
    }

    private static DatosSGA.MovimientoDatos aDatos(Movimiento movimiento) {
        return new DatosSGA.MovimientoDatos(
                movimiento.getId(),
                movimiento.getDescripcion(),
                movimiento.getTipo(),
                movimiento.getMonto().toString(),
                movimiento.getFecha().toString()
        );
    }

    private static DatosSGA.SolicitudDatos aDatos(
            SolicitudExterna solicitud
    ) {

        return new DatosSGA.SolicitudDatos(
                solicitud.getIdSolicitud(),
                solicitud.getNombreSolicitante(),
                solicitud.getDocumento(),
                solicitud.getTelefono(),
                solicitud.getCorreo(),
                solicitud.getCodigoApartamento(),
                solicitud.getFechaEntrada().toString(),
                solicitud.getFechaSalida().toString(),
                solicitud.getNumeroPersonas(),
                solicitud.getFechaRecepcion().toString(),
                solicitud.getEstado(),
                solicitud.getMotivoRechazo()
        );
    }

    private static DatosSGA.TareaDatos aDatos(TareaPreparacion tarea) {
        return new DatosSGA.TareaDatos(
                tarea.getApartamento().getCodigo(),
                tarea.getPrioridad(),
                tarea.getOrden(),
                tarea.getFechaSolicitud().toString()
        );
    }

    private static <T, R> R[] convertir(
            ListaDoble<T> lista,
            IntFunction<R[]> creador,
            Function<T, R> funcion
    ) {

        R[] arreglo = creador.apply(lista.tamano());

        for (int i = 0; i < arreglo.length; i++) {
            arreglo[i] = funcion.apply(lista.obtener(i));
        }

        return arreglo;
    }

    private static void agregarApartamentoSinDuplicar(
            ListaDoble<Apartamento> apartamentos,
            Apartamento apartamento
    ) {

        if (apartamento == null) {
            return;
        }

        if (buscarApartamento(apartamentos, apartamento.getCodigo()) == null) {
            apartamentos.agregarFinal(apartamento);
        }
    }

    private static Apartamento buscarApartamento(
            ListaDoble<Apartamento> apartamentos,
            String codigo
    ) {

        for (Apartamento apartamento : apartamentos) {
            if (apartamento.getCodigo().equals(codigo)) {
                return apartamento;
            }
        }

        return null;
    }

    private static void agregarHuespedSinDuplicar(
            ListaDoble<Huesped> huespedes,
            Huesped huesped
    ) {

        if (huesped == null) {
            return;
        }

        if (buscarHuesped(huespedes, huesped.getDocumento()) == null) {
            huespedes.agregarFinal(huesped);
        }
    }

    private static Huesped buscarHuesped(
            ListaDoble<Huesped> huespedes,
            String documento
    ) {

        for (Huesped huesped : huespedes) {
            if (huesped.getDocumento().equals(documento)) {
                return huesped;
            }
        }

        return null;
    }

    private static void agregarReservaSinDuplicar(
            ListaDoble<Reserva> reservas,
            Reserva reserva
    ) {

        if (reserva == null) {
            return;
        }

        if (buscarReserva(reservas, reserva.getCodigo()) == null) {
            reservas.agregarFinal(reserva);
        }
    }

    private static void agregarFolioSinDuplicar(
            ListaDoble<Folio> folios,
            Folio folio
    ) {

        if (folio == null) {
            return;
        }

        if (buscarFolio(folios, folio.getId()) == null) {
            folios.agregarFinal(folio);
        }
    }

    private static Reserva buscarReserva(
            ListaDoble<Reserva> reservas,
            String codigo
    ) {

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo().equals(codigo)) {
                return reserva;
            }
        }

        return null;
    }

    private static Folio buscarFolio(
            ListaDoble<Folio> folios,
            int id
    ) {

        for (Folio folio : folios) {
            if (folio.getId() == id) {
                return folio;
            }
        }

        return null;
    }

    private static Movimiento buscarMovimiento(
            ListaDoble<Movimiento> movimientos,
            int id
    ) {

        for (Movimiento movimiento : movimientos) {
            if (movimiento.getId() == id) {
                return movimiento;
            }
        }

        return null;
    }

    private static String texto(BigDecimal valor) {
        return valor == null ? null : valor.toString();
    }

    private static LocalDate fecha(String valor, String campo)
            throws IOException {

        if (valor == null || valor.isBlank()) {
            throw new IOException("Falta la fecha: " + campo);
        }

        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            throw new IOException("Fecha invalida: " + campo, e);
        }
    }

    private static LocalDateTime fechaHora(String valor, String campo)
            throws IOException {

        if (valor == null || valor.isBlank()) {
            throw new IOException("Falta la fecha y hora: " + campo);
        }

        try {
            return LocalDateTime.parse(valor);
        } catch (DateTimeParseException e) {
            throw new IOException(
                    "Fecha y hora invalida: " + campo,
                    e
            );
        }
    }

    private static LocalTime horaOpcional(String valor, String campo)
            throws IOException {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            return LocalTime.parse(valor);
        } catch (DateTimeParseException e) {
            throw new IOException("Hora invalida: " + campo, e);
        }
    }

    private static BigDecimal monto(String valor, String campo)
            throws IOException {

        if (valor == null || valor.isBlank()) {
            throw new IOException("Falta el monto: " + campo);
        }

        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            throw new IOException("Monto invalido: " + campo, e);
        }
    }
}
