package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Servicio encargado de asignar apartamentos
 * para solicitudes de alojamiento grupal.
 *
 * Utiliza backtracking para encontrar la combinación
 * que permita alojar al grupo con el menor número
 * de apartamentos posible.
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
     * Busca la mejor combinación de apartamentos
     * para alojar la cantidad de personas solicitada.
     *
     * @param cantidadPersonas personas que se alojarán.
     * @param estancia periodo solicitado.
     * @param apartamentos apartamentos disponibles en el sistema.
     * @param reservas reservas registradas en el sistema.
     * @return arreglo con los apartamentos seleccionados.
     */
    public Apartamento[] buscarMejorAsignacion(
            int cantidadPersonas,
            Estancia estancia,
            Apartamento[] apartamentos,
            ListaDoble<Reserva> reservas) {

        validarParametros(
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

        // Filtrar apartamentos que pueden recibir
        // una reserva durante las fechas solicitadas.
        for (Apartamento apartamento : apartamentos) {

            if (apartamento != null
                    && esCandidato(apartamento, estancia, reservas)) {

                candidatos[cantidadCandidatos] = apartamento;
                cantidadCandidatos++;
            }
        }

        Apartamento[] seleccion =
                new Apartamento[cantidadCandidatos];

        // Ejecutar el algoritmo de backtracking.
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
     * Determina si un apartamento puede ser candidato.
     *
     * No permite apartamentos fuera de servicio,
     * pendientes de preparación o en preparación.
     *
     * Los apartamentos ocupados solo se consideran
     * cuando existe una reserva en curso que termina
     * antes de la nueva fecha de entrada.
     */
    private boolean esCandidato(
            Apartamento apartamento,
            Estancia estancia,
            ListaDoble<Reserva> reservas) {

        EstadoApartamento estado = apartamento.getEstado();

        if (estado == EstadoApartamento.FUERA_DE_SERVICIO
                || estado == EstadoApartamento.EN_PREPARACION
                || estado == EstadoApartamento.PENDIENTE_PREPARACION) {
            return false;
        }

        boolean tieneReservaEnCurso = false;

        for (Reserva reserva : reservas) {

            if (!reserva.getApartamento().getCodigo()
                    .equals(apartamento.getCodigo())) {
                continue;
            }

            if (reserva.getEstado() == EstadoReserva.EN_CURSO) {
                tieneReservaEnCurso = true;
            }

            // Las reservas pendientes, confirmadas y en curso
            // bloquean las noches que se cruzan.
            if (esReservaBloqueante(reserva)
                    && estancia.seSolapaCon(reserva.getEstancia())) {
                return false;
            }
        }

        // Si está ocupado actualmente, debe existir
        // una reserva en curso que explique ese estado.
        if (estado == EstadoApartamento.OCUPADO
                && !tieneReservaEnCurso) {
            return false;
        }

        return estado == EstadoApartamento.PREPARADO
                || estado == EstadoApartamento.OCUPADO;
    }

    /**
     * Comprueba si el estado de una reserva bloquea
     * la disponibilidad del apartamento.
     */
    private boolean esReservaBloqueante(Reserva reserva) {

        EstadoReserva estado = reserva.getEstado();

        return estado == EstadoReserva.PENDIENTE
                || estado == EstadoReserva.CONFIRMADA
                || estado == EstadoReserva.EN_CURSO;
    }

    // --------------------------------------------------
    // BACKTRACKING
    // --------------------------------------------------

    /**
     * Busca recursivamente la combinación óptima.
     *
     * Primero minimiza la cantidad de apartamentos.
     * Si existen varias combinaciones con la misma
     * cantidad, minimiza la capacidad sobrante.
     */
    private void buscarCombinacion(
            int indice,
            int capacidadAcumulada,
            int cantidadSeleccionada,
            int personasNecesarias,
            Apartamento[] candidatos,
            int cantidadCandidatos,
            Apartamento[] seleccion) {

        // Si se alcanza la capacidad requerida,
        // se evalúa la solución encontrada.
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

        // No quedan candidatos para evaluar.
        if (indice >= cantidadCandidatos) {
            return;
        }

        // Poda: no continuar si ya se seleccionaron
        // tantos apartamentos como la mejor solución.
        if (cantidadSeleccionada >= mejorCantidad) {
            return;
        }

        // Calcular la capacidad que todavía podría alcanzarse.
        int capacidadPosible = capacidadAcumulada;

        for (int i = indice; i < cantidadCandidatos; i++) {
            capacidadPosible += candidatos[i].getCapacidad();
        }

        // Si no alcanza, se descarta esta rama.
        if (capacidadPosible < personasNecesarias) {
            return;
        }

        // OPCIÓN 1: incluir el apartamento actual.
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

        // OPCIÓN 2: no incluir el apartamento actual.
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
     * Busca apartamentos y crea una reserva individual
     * por cada apartamento seleccionado.
     *
     * Todas las reservas pertenecen al mismo huésped
     * responsable y quedan agrupadas en ReservaGrupo.
     *
     * También se agregan al registro general recibido.
     *
     * @param responsable huésped responsable del grupo.
     * @param cantidadPersonas cantidad total de personas.
     * @param estancia fechas de entrada y salida.
     * @param apartamentos apartamentos del alojamiento.
     * @param reservasExistentes registro general de reservas.
     * @param canal canal por el que se realiza la reserva.
     * @param horaLlegada hora estimada de llegada.
     * @return grupo con sus reservas individuales.
     */
    public ReservaGrupo crearReservaGrupal(
            Huesped responsable,
            int cantidadPersonas,
            Estancia estancia,
            Apartamento[] apartamentos,
            ListaDoble<Reserva> reservasExistentes,
            CanalReserva canal,
            LocalTime horaLlegada) {

        if (responsable == null
                || canal == null
                || horaLlegada == null) {
            throw new IllegalArgumentException(
                    "El responsable, el canal y la hora de llegada son obligatorios."
            );
        }

        validarParametros(
                cantidadPersonas,
                estancia,
                apartamentos,
                reservasExistentes
        );

        // Buscar la mejor combinación.
        Apartamento[] asignados = buscarMejorAsignacion(
                cantidadPersonas,
                estancia,
                apartamentos,
                reservasExistentes
        );

        if (asignados.length == 0) {
            throw new IllegalStateException(
                    "No existe capacidad disponible para alojar al grupo."
            );
        }

        // Crear el grupo.
        ReservaGrupo grupo = new ReservaGrupo(
                generador.generarCodigoGrupo(),
                responsable
        );

        // Preparar las reservas individuales.
        ListaDoble<Reserva> nuevasReservas = new ListaDoble<>();

        for (Apartamento apartamento : asignados) {

            Reserva reserva = new Reserva(
                    generador.generarCodigoReserva(),
                    responsable,
                    apartamento,
                    estancia,
                    canal,
                    new Folio(generador.generarIdFolio()),
                    LocalDateTime.now(),
                    horaLlegada
            );

            nuevasReservas.agregarFinal(reserva);
            grupo.agregarReserva(reserva);
        }

        // Registrar todas las reservas en la estructura general.
        for (Reserva reserva : nuevasReservas) {
            reservasExistentes.agregarFinal(reserva);
        }

        return grupo;
    }

    // --------------------------------------------------
    // VALIDACIONES
    // --------------------------------------------------

    private void validarParametros(
            int cantidadPersonas,
            Estancia estancia,
            Apartamento[] apartamentos,
            ListaDoble<Reserva> reservas) {

        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero."
            );
        }

        if (estancia == null) {
            throw new IllegalArgumentException(
                    "La estancia no puede ser nula."
            );
        }

        if (apartamentos == null || reservas == null) {
            throw new IllegalArgumentException(
                    "Los apartamentos y las reservas son obligatorios."
            );
        }

        if (apartamentos.length == 0) {
            throw new IllegalArgumentException(
                    "Debe existir al menos un apartamento."
            );
        }
    }
}

