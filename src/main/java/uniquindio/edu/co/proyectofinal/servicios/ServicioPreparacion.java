package uniquindio.edu.co.proyectofinal.servicios;

import java.time.LocalDateTime;

import uniquindio.edu.co.proyectofinal.comparadores.ComparadorDormitorios;
import uniquindio.edu.co.proyectofinal.estructuras.ColaPrioridad;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.EstadoApartamento;

/**
 * Administra la cola de prioridad de preparacion de apartamentos.
 *
 * Tienen mayor prioridad los valores mas altos de prioridad. Si dos tareas
 * tienen la misma prioridad se atiende primero el apartamento con menos
 * dormitorios y, si aun hay empate, la solicitud mas antigua.
 */
public class ServicioPreparacion {

    /**
     * Comparador usado para desempatar por cantidad de dormitorios.
     */
    private static final ComparadorDormitorios COMPARADOR_DORMITORIOS =
            new ComparadorDormitorios();

    /**
     * Cola de apartamentos pendientes de preparacion.
     */
    private final ColaPrioridad<TareaPreparacion> colaPreparacion;

    /**
     * Contador usado para conservar el orden de llegada.
     */
    private long siguienteOrden;

    /**
     * Crea el servicio sin tareas pendientes.
     */
    public ServicioPreparacion() {

        this.siguienteOrden = 1;

        this.colaPreparacion = new ColaPrioridad<>(
                (primera, segunda) -> {

                    int comparacion = Integer.compare(
                            segunda.getPrioridad(),
                            primera.getPrioridad()
                    );

                    if (comparacion == 0) {
                        comparacion = COMPARADOR_DORMITORIOS.compare(
                                primera.getApartamento(),
                                segunda.getApartamento()
                        );
                    }

                    if (comparacion == 0) {
                        comparacion = Long.compare(
                                primera.getOrden(),
                                segunda.getOrden()
                        );
                    }

                    return comparacion;
                }
        );
    }

    /**
     * Solicita la preparacion de un apartamento.
     *
     * @param apartamento apartamento que se va a preparar
     * @param prioridad prioridad de la tarea; un valor mayor es mas urgente
     * @return tarea creada
     */
    public TareaPreparacion solicitarPreparacion(
            Apartamento apartamento,
            int prioridad
    ) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo."
            );
        }

        EstadoApartamento estado = apartamento.getEstado();

        if (estado == EstadoApartamento.FUERA_DE_SERVICIO) {
            throw new IllegalStateException(
                    "El apartamento esta fuera de servicio."
            );
        }

        if (estado == EstadoApartamento.OCUPADO) {
            throw new IllegalStateException(
                    "No se puede preparar un apartamento ocupado."
            );
        }

        if (estado == EstadoApartamento.EN_PREPARACION) {
            throw new IllegalStateException(
                    "El apartamento ya se esta preparando."
            );
        }

        if (estaEnCola(apartamento)) {
            throw new IllegalStateException(
                    "El apartamento ya tiene una preparacion pendiente."
            );
        }

        apartamento.setEstado(EstadoApartamento.PENDIENTE_PREPARACION);

        TareaPreparacion tarea = new TareaPreparacion(
                apartamento,
                prioridad,
                siguienteOrden++,
                LocalDateTime.now()
        );

        colaPreparacion.encolar(tarea);
        return tarea;
    }

    /**
     * Solicita la preparacion con prioridad cero.
     *
     * @param apartamento apartamento que se va a preparar
     * @return tarea creada
     */
    public TareaPreparacion solicitarPreparacion(Apartamento apartamento) {
        return solicitarPreparacion(apartamento, 0);
    }

    /**
     * Inicia la preparacion de la tarea de mayor prioridad.
     *
     * @return tarea que se comenzo a preparar
     */
    public TareaPreparacion iniciarSiguientePreparacion() {

        if (colaPreparacion.estaVacia()) {
            throw new IllegalStateException(
                    "No hay preparaciones pendientes."
            );
        }

        TareaPreparacion tarea = colaPreparacion.desencolar();
        tarea.getApartamento().setEstado(EstadoApartamento.EN_PREPARACION);

        return tarea;
    }

    /**
     * Finaliza la preparacion de un apartamento.
     *
     * @param apartamento apartamento que termino su preparacion
     */
    public void finalizarPreparacion(Apartamento apartamento) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo."
            );
        }

        if (apartamento.getEstado() != EstadoApartamento.EN_PREPARACION) {
            throw new IllegalStateException(
                    "El apartamento no esta en preparacion."
            );
        }

        apartamento.setEstado(EstadoApartamento.PREPARADO);
    }

    /**
     * Cancela una preparacion pendiente.
     *
     * @param apartamento apartamento cuya preparacion se cancela
     * @return true si la tarea estaba en la cola
     */
    public boolean cancelarPreparacion(Apartamento apartamento) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo."
            );
        }

        boolean eliminada = eliminarDeCola(apartamento);

        if (eliminada
                && apartamento.getEstado()
                == EstadoApartamento.PENDIENTE_PREPARACION) {

            apartamento.setEstado(EstadoApartamento.PREPARADO);
        }

        return eliminada;
    }

    /**
     * Marca un apartamento fuera de servicio y lo retira de la cola.
     *
     * @param apartamento apartamento afectado
     * @param motivo motivo por el cual queda fuera de servicio
     */
    public void marcarFueraDeServicio(
            Apartamento apartamento,
            String motivo
    ) {

        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo."
            );
        }

        eliminarDeCola(apartamento);
        apartamento.marcarFueraDeServicio(motivo);
    }

    /**
     * Consulta la siguiente tarea sin retirarla de la cola.
     *
     * @return siguiente tarea o null si no hay pendientes
     */
    public TareaPreparacion verSiguientePreparacion() {

        if (colaPreparacion.estaVacia()) {
            return null;
        }

        return colaPreparacion.frente();
    }

    /**
     * Obtiene la cantidad de preparaciones pendientes.
     *
     * @return cantidad de tareas
     */
    public int cantidadPendientes() {
        return colaPreparacion.tamanio();
    }

    /**
     * Indica si no hay preparaciones pendientes.
     *
     * @return true si la cola esta vacia
     */
    public boolean estaVacia() {
        return colaPreparacion.estaVacia();
    }

    /**
     * Lista las tareas pendientes en orden de atencion.
     *
     * @return lista ordenada por prioridad
     */
    public ListaDoble<TareaPreparacion> listarPendientes() {

        ListaDoble<TareaPreparacion> ordenadas = new ListaDoble<>();
        ListaDoble<TareaPreparacion> temporal = new ListaDoble<>();

        while (!colaPreparacion.estaVacia()) {

            TareaPreparacion tarea = colaPreparacion.desencolar();
            ordenadas.agregarFinal(tarea);
            temporal.agregarFinal(tarea);
        }

        for (TareaPreparacion tarea : temporal) {
            colaPreparacion.encolar(tarea);
        }

        return ordenadas;
    }

    /**
     * Elimina todas las tareas pendientes y deja los apartamentos preparados.
     */
    public void limpiar() {

        while (!colaPreparacion.estaVacia()) {
            TareaPreparacion tarea = colaPreparacion.desencolar();
            tarea.getApartamento().setEstado(EstadoApartamento.PREPARADO);
        }
    }

    /**
     * Comprueba si un apartamento tiene una tarea pendiente.
     *
     * @param apartamento apartamento consultado
     * @return true si esta en la cola
     */
    private boolean estaEnCola(Apartamento apartamento) {

        for (TareaPreparacion tarea : colaPreparacion) {
            if (mismoApartamento(tarea.getApartamento(), apartamento)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Elimina una tarea de la cola conservando el orden de las demas.
     *
     * @param apartamento apartamento cuya tarea se elimina
     * @return true si la tarea fue encontrada
     */
    private boolean eliminarDeCola(Apartamento apartamento) {

        boolean eliminada = false;
        ListaDoble<TareaPreparacion> restantes = new ListaDoble<>();

        while (!colaPreparacion.estaVacia()) {

            TareaPreparacion tarea = colaPreparacion.desencolar();

            if (mismoApartamento(tarea.getApartamento(), apartamento)) {
                eliminada = true;
            } else {
                restantes.agregarFinal(tarea);
            }
        }

        for (TareaPreparacion tarea : restantes) {
            colaPreparacion.encolar(tarea);
        }

        return eliminada;
    }

    /**
     * Compara dos apartamentos por su codigo.
     *
     * @param primero primer apartamento
     * @param segundo segundo apartamento
     * @return true si representan el mismo apartamento
     */
    private boolean mismoApartamento(
            Apartamento primero,
            Apartamento segundo
    ) {
        return primero.getCodigo().equals(segundo.getCodigo());
    }

    /**
     * Tarea de preparacion almacenada en la cola de prioridad.
     */
    public static class TareaPreparacion {

        private final Apartamento apartamento;
        private final int prioridad;
        private final long orden;
        private final LocalDateTime fechaSolicitud;

        /**
         * Crea una tarea de preparacion.
         *
         * @param apartamento apartamento que se prepara
         * @param prioridad prioridad de la tarea
         * @param orden orden de llegada
         * @param fechaSolicitud fecha y hora de la solicitud
         */
        private TareaPreparacion(
                Apartamento apartamento,
                int prioridad,
                long orden,
                LocalDateTime fechaSolicitud
        ) {
            this.apartamento = apartamento;
            this.prioridad = prioridad;
            this.orden = orden;
            this.fechaSolicitud = fechaSolicitud;
        }

        /**
         * Obtiene el apartamento asociado.
         *
         * @return apartamento de la tarea
         */
        public Apartamento getApartamento() {
            return apartamento;
        }

        /**
         * Obtiene la prioridad de la tarea.
         *
         * @return prioridad asignada
         */
        public int getPrioridad() {
            return prioridad;
        }

        /**
         * Obtiene el orden de llegada.
         *
         * @return orden de la solicitud
         */
        public long getOrden() {
            return orden;
        }

        /**
         * Obtiene la fecha de la solicitud.
         *
         * @return fecha y hora de la solicitud
         */
        public LocalDateTime getFechaSolicitud() {
            return fechaSolicitud;
        }

        /**
         * Devuelve una representacion textual de la tarea.
         *
         * @return descripcion de la tarea
         */
        @Override
        public String toString() {
            return "TareaPreparacion{"
                    + "apartamento=" + apartamento.getCodigo()
                    + ", prioridad=" + prioridad
                    + ", orden=" + orden
                    + '}';
        }
    }
}
