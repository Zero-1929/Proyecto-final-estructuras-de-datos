
package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Asigna apartamentos a grupos grandes mediante backtracking.
 *
 * El objetivo es alojar a todos los ocupantes utilizando
 * el menor número posible de apartamentos.
 */
public class ServicioAsignacionGrupal {

    private final GeneradorIdentificadores generador;

    private Apartamento[] mejorAsignacion;
    private int mejorCantidad;
    private int mejorCapacidadSobrante;

    public ServicioAsignacionGrupal(
            GeneradorIdentificadores generador) {

        if (generador == null) {
            throw new IllegalArgumentException(
                    "El generador de identificadores no puede ser nulo."
            );
        }

        this.generador = generador;
    }

    // --------------------------------------------------
    // BÚSQUEDA DE APARTAMENTOS
    // --------------------------------------------------

    /**
     * Busca la combinación de apartamentos que permita
     * alojar a todas las personas con el menor número
     * de apartamentos y menor capacidad sobrante.
     */
    public Apartamento[] buscarMejorAsignacion(
            int cantidadPersonas,
            Estancia estancia,
            Apartamento[] apartamentos,
            ListaDoble<Reserva> reservas) {

        validarParametrosBusqueda(
                cantidadPersonas,
                estancia,
                apartamentos,
                reservas
        );

        mejorAsignacion = new Apartamento[apartamentos.length];
        mejorCantidad = Integer.MAX_VALUE;
        mejorCapacidadSobrante = Integer.MAX_VALUE;

        Apartamento[] candidatos =
                new Apartamento[apartamentos.length];

        int cantidadCandidatos = 0;

        for (int i = 0; i < apartamentos.length; i++) {

            Apartamento apartamento = apartamentos[i];

            if (apartamento == null) {
                continue;
            }

            // Evitar evaluar dos veces el mismo apartamento.
            if (estaRepetido(apartamentos, i, apartamento)) {
                continue;
            }

            if (esCandidato(apartamento, estancia, reservas)) {
                candidatos[cantidadCandidatos++] = apartamento;
            }
        }

        Apartamento[] seleccion =
                new Apartamento[cantidadCandidatos];

        buscarCombinacion(
                0,
                0,
                0,
                cantidadPersonas,
                candidatos,
                cantidadCandidatos,
                seleccion
        );

        if (mejorCantidad == Integer.MAX_VALUE) {
            return new Apartamento[0];
        }

        Apartamento[] resultado = new Apartamento[mejorCantidad];

        System.arraycopy(
                mejorAsignacion,
                0,
                resultado,
                0,
                mejorCantidad
        );

        return resultado;
    }

    /**
     * Comprueba si el apartamento aparece anteriormente
     * en el arreglo.
     */
    private boolean estaRepetido(
            Apartamento[] apartamentos,
            int indiceActual,
            Apartamento apartamento) {

        for (int i = 0; i < indiceActual; i++) {

            if (apartamentos[i] != null
                    && apartamentos[i].getCodigo().equalsIgnoreCase(
                    apartamento.getCodigo())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Un apartamento solo es candidato cuando está preparado,
     * tiene capacidad y no presenta reservas que se solapen.
     *
     * No se seleccionan apartamentos ocupados o pendientes
     * de preparación, porque deben estar preparados antes
     * de recibir un nuevo grupo.
     */
    private boolean esCandidato(
            Apartamento apartamento,
            Estancia estancia,
            ListaDoble<Reserva> reservas) {

        if (apartamento.getEstado() != EstadoApartamento.PREPARADO) {
            return false;
        }

        for (Reserva reserva : reservas) {

            if (!reserva.getApartamento().getCodigo()
                    .equalsIgnoreCase(apartamento.getCodigo())) {
                continue;
            }

            if (esReservaQueBloqueaFechas(reserva)
                    && reserva.getEstancia().seSolapaCon(estancia)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Estados que ocupan las noches del calendario.
     */
    private boolean esReservaQueBloqueaFechas(Reserva reserva) {

        EstadoReserva estado = reserva.getEstado();

        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO
                || estado == EstadoReserva.FINALIZADA;
    }

    // --------------------------------------------------
    // BACKTRACKING
    // --------------------------------------------------

    /**
     * Explora recursivamente las combinaciones posibles.
     *
     * Criterios:
     * 1. Menor cantidad de apartamentos.
     * 2. Menor capacidad sobrante.
     */
    private void buscarCombinacion(
            int indice,
            int capacidadAcumulada,
            int cantidadSeleccionada,
            int personasNecesarias,
            Apartamento[] candidatos,
            int cantidadCandidatos,
            Apartamento[] seleccion) {

        if (capacidadAcumulada >= personasNecesarias) {

            int capacidadSobrante =
                    capacidadAcumulada - personasNecesarias;

            if (cantidadSeleccionada < mejorCantidad
                    || (cantidadSeleccionada == mejorCantidad
                    && capacidadSobrante < mejorCapacidadSobrante)) {

                mejorCantidad = cantidadSeleccionada;
                mejorCapacidadSobrante = capacidadSobrante;

                System.arraycopy(
                        seleccion,
                        0,
                        mejorAsignacion,
                        0,
                        cantidadSeleccionada
                );
            }

            return;
        }

        if (indice >= cantidadCandidatos) {
            return;
        }

        if (cantidadSeleccionada >= mejorCantidad) {
            return;
        }

        int capacidadPosible = capacidadAcumulada;

        for (int i = indice; i < cantidadCandidatos; i++) {
            capacidadPosible += candidatos[i].getCapacidad();
        }

        if (capacidadPosible < personasNecesarias) {
            return;
        }

        // Opción 1: seleccionar el apartamento actual.
        seleccion[cantidadSeleccionada] = candidatos[indice];

        buscarCombinacion(
                indice + 1,
                capacidadAcumulada + candidatos[indice].getCapacidad(),
                cantidadSeleccionada + 1,
                personasNecesarias,
                candidatos,
                cantidadCandidatos,
                seleccion
        );

        // Opción 2: no seleccionar el apartamento actual.
        seleccion[cantidadSeleccionada] = null;

        buscarCombinacion(
                indice + 1,
                capacidadAcumulada,
                cantidadSeleccionada,
                personasNecesarias,
                candidatos,
                cantidadCandidatos,
                seleccion
        );
    }

    // --------------------------------------------------
    // CREACIÓN DE RESERVAS GRUPALES
    // --------------------------------------------------

    /**
     * Crea un grupo de reservas individuales.
     *
     * Cada apartamento recibe:
     * - Una reserva independiente.
     * - Un folio independiente.
     * - Los ocupantes que le correspondan.
     *
     * Todas las reservas comparten el huésped responsable.
     * El registro se realiza de forma atómica en ServicioReserva.
     */
    public ReservaGrupo crearReservaGrupal(
            Huesped responsable,
            ListaDoble<Ocupante> ocupantes,
            Estancia estancia,
            Apartamento[] apartamentos,
            ServicioReserva servicioReserva,
            CanalReserva canal,
            LocalTime horaLlegada) {

        validarDatosGrupo(
                responsable,
                ocupantes,
                estancia,
                apartamentos,
                servicioReserva,
                canal,
                horaLlegada
        );

        validarTitular(responsable, ocupantes);

        int cantidadPersonas = ocupantes.tamano();

        // Obtener las reservas centrales actuales.
        ListaDoble<Reserva> reservasExistentes =
                servicioReserva.listarReservas();

        // Buscar la combinación óptima.
        Apartamento[] asignados = buscarMejorAsignacion(
                cantidadPersonas,
                estancia,
                apartamentos,
                reservasExistentes
        );

        if (asignados.length == 0) {
            throw new IllegalStateException(
                    "No hay apartamentos preparados y disponibles "
                            + "para alojar a todo el grupo."
            );
        }

        // Comprobar la capacidad total.
        int capacidadTotal = 0;

        for (Apartamento apartamento : asignados) {
            capacidadTotal += apartamento.getCapacidad();
        }

        if (capacidadTotal < cantidadPersonas) {
            throw new IllegalStateException(
                    "La capacidad de los apartamentos seleccionados "
                            + "no alcanza para todo el grupo."
            );
        }

        // Crear las reservas sin registrarlas todavía.
        ListaDoble<Reserva> nuevasReservas = new ListaDoble<>();

        int indiceOcupante = 0;

        for (Apartamento apartamento : asignados) {

            ListaDoble<Ocupante> ocupantesApartamento =
                    new ListaDoble<>();

            // Distribuir ocupantes hasta completar la capacidad.
            while (indiceOcupante < cantidadPersonas
                    && ocupantesApartamento.tamano()
                    < apartamento.getCapacidad()) {

                ocupantesApartamento.agregarFinal(
                        ocupantes.obtener(indiceOcupante)
                );

                indiceOcupante++;
            }

            if (ocupantesApartamento.estaVacia()) {
                continue;
            }

            Reserva reserva = new Reserva(
                    generador.generarCodigoReserva(),
                    responsable,
                    apartamento,
                    estancia,
                    canal,
                    new Folio(generador.generarIdFolio()),
                    LocalDateTime.now(),
                    horaLlegada,
                    ocupantesApartamento
            );

            nuevasReservas.agregarFinal(reserva);
        }

        // Verificar que todos los ocupantes fueron distribuidos.
        if (indiceOcupante != cantidadPersonas) {
            throw new IllegalStateException(
                    "No fue posible distribuir todos los ocupantes."
            );
        }

        // Registrar todas las reservas en una sola operación.
        boolean registradas =
                servicioReserva.registrarReservasAtomicas(nuevasReservas);

        if (!registradas) {
            throw new IllegalStateException(
                    "No se pudo registrar el grupo completo. "
                            + "Una o más reservas no cumplen las "
                            + "validaciones de disponibilidad."
            );
        }

        // Construir el grupo únicamente después del registro exitoso.
        ReservaGrupo grupo = new ReservaGrupo(
                generador.generarCodigoGrupo(),
                responsable
        );

        for (Reserva reserva : nuevasReservas) {
            grupo.agregarReserva(reserva);
        }

        return grupo;
    }

    // --------------------------------------------------
    // VALIDACIONES DEL GRUPO
    // --------------------------------------------------

    private void validarDatosGrupo(
            Huesped responsable,
            ListaDoble<Ocupante> ocupantes,
            Estancia estancia,
            Apartamento[] apartamentos,
            ServicioReserva servicioReserva,
            CanalReserva canal,
            LocalTime horaLlegada) {

        if (responsable == null
                || ocupantes == null
                || estancia == null
                || apartamentos == null
                || servicioReserva == null
                || canal == null
                || horaLlegada == null) {

            throw new IllegalArgumentException(
                    "Todos los datos de la asignación grupal son obligatorios."
            );
        }

        if (ocupantes.estaVacia()) {
            throw new IllegalArgumentException(
                    "El grupo debe tener al menos un ocupante."
            );
        }

        for (Ocupante ocupante : ocupantes) {

            if (ocupante == null) {
                throw new IllegalArgumentException(
                        "La lista no puede contener ocupantes nulos."
                );
            }

            if (ocupante.getFechaNacimiento() == null) {
                throw new IllegalArgumentException(
                        "Todos los ocupantes deben tener fecha de nacimiento."
                );
            }
        }

        if (apartamentos.length == 0) {
            throw new IllegalArgumentException(
                    "No hay apartamentos para realizar la asignación."
            );
        }
    }

    /**
     * Exige que el huésped responsable aparezca exactamente
     * una vez como ocupante vinculado a su perfil.
     */
    private void validarTitular(
            Huesped responsable,
            ListaDoble<Ocupante> ocupantes) {

        int coincidencias = 0;

        for (Ocupante ocupante : ocupantes) {

            if (ocupante.tienePerfil()
                    && ocupante.getPerfilVinculado().getDocumento()
                    .equalsIgnoreCase(responsable.getDocumento())) {

                coincidencias++;
            }
        }

        if (coincidencias != 1) {
            throw new IllegalArgumentException(
                    "El huésped responsable debe aparecer exactamente "
                            + "una vez entre los ocupantes, vinculado "
                            + "a su perfil."
            );
        }
    }

    private void validarParametrosBusqueda(
            int cantidadPersonas,
            Estancia estancia,
            Apartamento[] apartamentos,
            ListaDoble<Reserva> reservas) {

        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero."
            );
        }

        if (estancia == null
                || apartamentos == null
                || reservas == null) {

            throw new IllegalArgumentException(
                    "La estancia, los apartamentos y las reservas "
                            + "son obligatorios."
            );
        }

        if (apartamentos.length == 0) {
            throw new IllegalArgumentException(
                    "Debe existir al menos un apartamento."
            );
        }
    }
}
