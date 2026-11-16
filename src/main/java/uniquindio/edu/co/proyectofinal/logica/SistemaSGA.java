package uniquindio.edu.co.proyectofinal.logica;

import uniquindio.edu.co.proyectofinal.excepciones.FechasInvalidasException;
import uniquindio.edu.co.proyectofinal.modelo.Temporada;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;
import uniquindio.edu.co.proyectofinal.persistencia.GestorPersistencia;
import uniquindio.edu.co.proyectofinal.persistencia.RepositorioJSON;
import uniquindio.edu.co.proyectofinal.servicios.GeneradorIdentificadores;
import uniquindio.edu.co.proyectofinal.servicios.ServicioApartamento;
import uniquindio.edu.co.proyectofinal.servicios.ServicioCotizacion;
import uniquindio.edu.co.proyectofinal.servicios.ServicioEmpleados;
import uniquindio.edu.co.proyectofinal.servicios.ServicioExterno;
import uniquindio.edu.co.proyectofinal.servicios.ServicioHuesped;
import uniquindio.edu.co.proyectofinal.servicios.ServicioPreparacion;
import uniquindio.edu.co.proyectofinal.servicios.ServicioReserva;
import uniquindio.edu.co.proyectofinal.servicios.ServicioTemporada;
import uniquindio.edu.co.proyectofinal.servicios.ServicioTurnos;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * Reune los servicios principales del SGA y coordina su persistencia.
 *
 * La clase no contiene reglas de negocio nuevas: solo conserva las instancias
 * de los servicios que comparten la aplicacion y delega el guardado y la carga
 * en la capa de persistencia.
 */
public class SistemaSGA {

    private final ServicioApartamento servicioApartamento;
    private final ServicioHuesped servicioHuesped;
    private final ServicioEmpleados servicioEmpleados;
    private final ServicioTemporada servicioTemporada;
    private final ServicioCotizacion servicioCotizacion;
    private final ServicioReserva servicioReserva;
    private final ServicioPreparacion servicioPreparacion;
    private final ServicioTurnos servicioTurnos;
    private final ServicioExterno servicioExterno;
    private final GeneradorIdentificadores generadorIdentificadores;

    /**
     * Crea el sistema con los servicios ya construidos.
     *
     * @param servicioApartamento servicio de apartamentos
     * @param servicioHuesped servicio de huespedes
     * @param servicioEmpleados servicio de empleados
     * @param servicioTemporada servicio de temporadas
     * @param servicioCotizacion servicio de cotizacion
     * @param servicioReserva servicio de reservas
     * @param servicioPreparacion servicio de preparacion
     * @param servicioTurnos servicio de turnos
     * @param servicioExterno servicio del canal externo
     * @param generadorIdentificadores generador de consecutivos
     */
    public SistemaSGA(
            ServicioApartamento servicioApartamento,
            ServicioHuesped servicioHuesped,
            ServicioEmpleados servicioEmpleados,
            ServicioTemporada servicioTemporada,
            ServicioCotizacion servicioCotizacion,
            ServicioReserva servicioReserva,
            ServicioPreparacion servicioPreparacion,
            ServicioTurnos servicioTurnos,
            ServicioExterno servicioExterno,
            GeneradorIdentificadores generadorIdentificadores
    ) {
        if (servicioApartamento == null
                || servicioHuesped == null
                || servicioEmpleados == null
                || servicioTemporada == null
                || servicioCotizacion == null
                || servicioReserva == null
                || servicioPreparacion == null
                || servicioTurnos == null
                || servicioExterno == null
                || generadorIdentificadores == null) {
            throw new IllegalArgumentException(
                    "Los servicios del sistema no pueden ser nulos."
            );
        }

        this.servicioApartamento = servicioApartamento;
        this.servicioHuesped = servicioHuesped;
        this.servicioEmpleados = servicioEmpleados;
        this.servicioTemporada = servicioTemporada;
        this.servicioCotizacion = servicioCotizacion;
        this.servicioReserva = servicioReserva;
        this.servicioPreparacion = servicioPreparacion;
        this.servicioPreparacion.configurarReservas(servicioReserva.getRegistroCompartido());
        this.servicioReserva.configurarPreparacion(servicioPreparacion);
        // Recupera tareas faltantes al crear o cargar el sistema, sin duplicarlas.
        this.servicioPreparacion.sincronizarPendientes(servicioApartamento.listarApartamentos());
        this.servicioTurnos = servicioTurnos;
        this.servicioExterno = servicioExterno;
        this.generadorIdentificadores = generadorIdentificadores;
    }

    /**
     * Crea un sistema vacio con todos sus servicios inicializados.
     *
     * @return sistema sin datos
     */
    public static SistemaSGA crearVacio() {

        ServicioApartamento apartamentos = new ServicioApartamento();
        ServicioHuesped huespedes = new ServicioHuesped();
        ServicioEmpleados empleados = new ServicioEmpleados();
        ServicioTemporada temporadas = new ServicioTemporada();
        ServicioCotizacion cotizacion = new ServicioCotizacion();
        ServicioReserva reservas = new ServicioReserva(
                apartamentos,
                cotizacion
        );
        ServicioPreparacion preparacion = new ServicioPreparacion();
        ServicioTurnos turnos = new ServicioTurnos();
        ServicioExterno externo = new ServicioExterno(reservas, apartamentos, huespedes);
        GeneradorIdentificadores generador =
                new GeneradorIdentificadores(1, 1, 1);

        return new SistemaSGA(
                apartamentos,
                huespedes,
                empleados,
                temporadas,
                cotizacion,
                reservas,
                preparacion,
                turnos,
                externo,
                generador
        );
    }

    /**
     * Carga un sistema desde un archivo JSON.
     *
     * @param ruta archivo de origen
     * @return sistema cargado
     * @throws IOException si el archivo no se puede leer
     */
    public static SistemaSGA cargar(Path ruta) throws IOException {
        return GestorPersistencia.importar(
                RepositorioJSON.cargar(ruta)
        );
    }

    /**
     * Guarda el sistema en un archivo JSON.
     *
     * @param ruta archivo de destino
     * @throws IOException si el archivo no se puede escribir
     */
    public void guardar(Path ruta) throws IOException {
        RepositorioJSON.guardar(
                GestorPersistencia.exportar(this),
                ruta
        );
    }

    /**
     * Guarda el sistema en la ruta usada por la aplicacion.
     *
     * @throws IOException si el archivo no se puede escribir
     */
    public void guardar() throws IOException {
        guardar(rutaPorDefecto());
    }

    /**
     * Ruta del archivo de datos usado por la aplicacion.
     *
     * El archivo se crea en el directorio de trabajo actual para que quede
     * dentro del repositorio del proyecto.
     *
     * @return ruta del archivo de datos
     */
    public static Path rutaPorDefecto() {
        String directorio = System.getProperty("user.dir");
        if (directorio == null || directorio.isBlank()) {
            return Path.of("datos-sga.txt").toAbsolutePath().normalize();
        }
        return Path.of(directorio, "datos-sga.txt")
                .toAbsolutePath()
                .normalize();
    }

    /**
     * Registra una temporada en los servicios de temporada y cotizacion.
     *
     * @param tipo tipo de temporada
     * @param fechaInicio fecha inicial inclusiva
     * @param fechaFin fecha final inclusiva
     * @return temporada registrada
     * @throws FechasInvalidasException si las fechas no son validas
     */
    public Temporada registrarTemporada(
            TipoTemporada tipo,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) throws FechasInvalidasException {

        Temporada temporada = servicioTemporada.registrarTemporada(
                tipo,
                fechaInicio,
                fechaFin
        );

        if (!servicioCotizacion.registrarTemporada(temporada)) {
            throw new IllegalStateException(
                    "La temporada se solapa con una temporada de cotizacion."
            );
        }

        return temporada;
    }

    public ServicioApartamento getServicioApartamento() {
        return servicioApartamento;
    }

    public ServicioHuesped getServicioHuesped() {
        return servicioHuesped;
    }

    public ServicioEmpleados getServicioEmpleados() {
        return servicioEmpleados;
    }

    public ServicioTemporada getServicioTemporada() {
        return servicioTemporada;
    }

    public ServicioCotizacion getServicioCotizacion() {
        return servicioCotizacion;
    }

    public ServicioReserva getServicioReserva() {
        return servicioReserva;
    }

    public ServicioPreparacion getServicioPreparacion() {
        return servicioPreparacion;
    }

    public ServicioTurnos getServicioTurnos() {
        return servicioTurnos;
    }

    public ServicioExterno getServicioExterno() {
        return servicioExterno;
    }

    public GeneradorIdentificadores getGeneradorIdentificadores() {
        return generadorIdentificadores;
    }
}
