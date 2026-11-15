package uniquindio.edu.co.proyectofinal.persistencia;

import uniquindio.edu.co.proyectofinal.modelo.CanalReserva;
import uniquindio.edu.co.proyectofinal.modelo.EstadoApartamento;
import uniquindio.edu.co.proyectofinal.modelo.EstadoReserva;
import uniquindio.edu.co.proyectofinal.modelo.EstadoSolicitud;
import uniquindio.edu.co.proyectofinal.modelo.RolEmpleado;
import uniquindio.edu.co.proyectofinal.modelo.TipoMovimiento;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;

/**
 * Snapshot de los datos que se guardan en disco.
 *
 * Se usan arreglos y tipos simples para evitar serializar directamente las
 * estructuras enlazadas del proyecto. Las fechas y los montos se almacenan
 * como texto en formato ISO.
 *
 * @param version version del formato del archivo
 * @param apartamentos apartamentos registrados
 * @param huespedes huespedes registrados
 * @param empleados empleados registrados
 * @param temporadas temporadas registradas
 * @param temporadaPorDefecto temporada aplicada por defecto
 * @param reservas reservas registradas
 * @param folios folios con sus movimientos
 * @param solicitudesPendientes solicitudes externas pendientes
 * @param solicitudesHistorial solicitudes externas procesadas o rechazadas
 * @param tareasPreparacion tareas pendientes de preparacion
 * @param recepcionistas documentos de los recepcionistas en orden de turno
 * @param recepcionistaActual documento del recepcionista con el turno actual
 * @param siguienteGrupo siguiente consecutivo para grupos
 * @param siguienteReserva siguiente consecutivo para reservas
 * @param siguienteFolio siguiente consecutivo para folios
 * @param siguienteIdFolioExterno siguiente folio del canal externo
 * @param siguienteNumeroReservaExterno siguiente reserva del canal externo
 */
public record DatosSGA(
        int version,
        ApartamentoDatos[] apartamentos,
        HuespedDatos[] huespedes,
        EmpleadoDatos[] empleados,
        TemporadaDatos[] temporadas,
        TipoTemporada temporadaPorDefecto,
        ReservaDatos[] reservas,
        FolioDatos[] folios,
        SolicitudDatos[] solicitudesPendientes,
        SolicitudDatos[] solicitudesHistorial,
        TareaDatos[] tareasPreparacion,
        String[] recepcionistas,
        String recepcionistaActual,
        int siguienteGrupo,
        int siguienteReserva,
        int siguienteFolio,
        int siguienteIdFolioExterno,
        int siguienteNumeroReservaExterno
) {

    /**
     * Version actual del formato.
     */
    public static final int VERSION_ACTUAL = 1;

    /**
     * Normaliza los arreglos y valores obligatorios del snapshot.
     */
    public DatosSGA {
        version = version <= 0 ? VERSION_ACTUAL : version;
        apartamentos = apartamentos == null
                ? new ApartamentoDatos[0] : apartamentos;
        huespedes = huespedes == null
                ? new HuespedDatos[0] : huespedes;
        empleados = empleados == null
                ? new EmpleadoDatos[0] : empleados;
        temporadas = temporadas == null
                ? new TemporadaDatos[0] : temporadas;
        temporadaPorDefecto = temporadaPorDefecto == null
                ? TipoTemporada.BAJA : temporadaPorDefecto;
        reservas = reservas == null
                ? new ReservaDatos[0] : reservas;
        folios = folios == null
                ? new FolioDatos[0] : folios;
        solicitudesPendientes = solicitudesPendientes == null
                ? new SolicitudDatos[0] : solicitudesPendientes;
        solicitudesHistorial = solicitudesHistorial == null
                ? new SolicitudDatos[0] : solicitudesHistorial;
        tareasPreparacion = tareasPreparacion == null
                ? new TareaDatos[0] : tareasPreparacion;
        recepcionistas = recepcionistas == null
                ? new String[0] : recepcionistas;
        siguienteGrupo = siguienteGrupo <= 0 ? 1 : siguienteGrupo;
        siguienteReserva = siguienteReserva <= 0 ? 1 : siguienteReserva;
        siguienteFolio = siguienteFolio <= 0 ? 1 : siguienteFolio;
        siguienteIdFolioExterno = siguienteIdFolioExterno <= 0
                ? 1 : siguienteIdFolioExterno;
        siguienteNumeroReservaExterno =
                siguienteNumeroReservaExterno <= 0
                        ? 1 : siguienteNumeroReservaExterno;
    }

    /**
     * Crea un snapshot vacio.
     *
     * @return snapshot sin datos
     */
    public static DatosSGA vacio() {
        return new DatosSGA(
                VERSION_ACTUAL,
                new ApartamentoDatos[0],
                new HuespedDatos[0],
                new EmpleadoDatos[0],
                new TemporadaDatos[0],
                TipoTemporada.BAJA,
                new ReservaDatos[0],
                new FolioDatos[0],
                new SolicitudDatos[0],
                new SolicitudDatos[0],
                new TareaDatos[0],
                new String[0],
                null,
                1,
                1,
                1,
                1,
                1
        );
    }

    /**
     * Datos persistibles de un apartamento.
     */
    public record ApartamentoDatos(
            String codigo,
            String nombre,
            String bloque,
            int piso,
            int numeroDormitorios,
            int capacidad,
            String caracteristicas,
            EstadoApartamento estado,
            String motivoFueraServicio,
            String tarifaBaja,
            String tarifaMedia,
            String tarifaAlta
    ) {
    }

    /**
     * Datos persistibles de un huesped.
     */
    public record HuespedDatos(
            String nombre,
            String fechaNacimiento,
            String documento,
            String telefono,
            String correo,
            String rutaFoto,
            String[] favoritos
    ) {
        public HuespedDatos {
            favoritos = favoritos == null ? new String[0] : favoritos;
        }
    }

    /**
     * Datos persistibles de un empleado.
     */
    public record EmpleadoDatos(
            String nombre,
            String fechaNacimiento,
            String documento,
            String telefono,
            String correo,
            RolEmpleado rol
    ) {
    }

    /**
     * Datos persistibles de una temporada.
     */
    public record TemporadaDatos(
            TipoTemporada tipo,
            String fechaInicio,
            String fechaFin
    ) {
    }

    /**
     * Datos persistibles de una reserva.
     */
    public record ReservaDatos(
            String codigo,
            String documentoHuesped,
            String codigoApartamento,
            String fechaEntrada,
            String fechaSalida,
            EstadoReserva estado,
            CanalReserva canalReserva,
            String fechaCreacion,
            String horaLlegadaEstimada,
            int folioId
    ) {
    }

    /**
     * Datos persistibles de un folio.
     *
     * Los identificadores de las operaciones deshacibles se guardan en orden
     * desde la cima hasta el fondo de la pila.
     */
    public record FolioDatos(
            int id,
            MovimientoDatos[] movimientos,
            int[] idsOperacionesDeshacibles
    ) {

        /**
         * Normaliza los arreglos del folio.
         */
        public FolioDatos {
            movimientos = movimientos == null
                    ? new MovimientoDatos[0] : movimientos;
            idsOperacionesDeshacibles = idsOperacionesDeshacibles == null
                    ? new int[0] : idsOperacionesDeshacibles;
        }
    }

    /**
     * Datos persistibles de un movimiento.
     */
    public record MovimientoDatos(
            int id,
            String descripcion,
            TipoMovimiento tipo,
            String monto,
            String fecha
    ) {
    }

    /**
     * Datos persistibles de una solicitud externa.
     */
    public record SolicitudDatos(
            String idSolicitud,
            String nombreSolicitante,
            String documento,
            String telefono,
            String correo,
            String codigoApartamento,
            String fechaEntrada,
            String fechaSalida,
            int numeroPersonas,
            String fechaRecepcion,
            EstadoSolicitud estado,
            String motivoRechazo
    ) {
    }

    /**
     * Datos persistibles de una tarea de preparacion.
     */
    public record TareaDatos(
            String codigoApartamento,
            int prioridad,
            long orden,
            String fechaSolicitud
    ) {
    }
}
