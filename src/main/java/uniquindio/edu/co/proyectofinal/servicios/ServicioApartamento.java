package uniquindio.edu.co.proyectofinal.servicios;

import java.math.BigDecimal;

import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.modelo.EstadoApartamento;
import uniquindio.edu.co.proyectofinal.modelo.TipoTemporada;

/**
 * Servicio encargado de gestionar los apartamentos del SGA.
 *
 * Utiliza una ListaDoble propia del proyecto.
 */
public class ServicioApartamento {

    private final ListaDoble<Apartamento> apartamentos;

    /**
     * Crea el servicio con una lista vacía.
     */
    public ServicioApartamento() {
        this.apartamentos = new ListaDoble<>();
    }

    /**
     * Crea el servicio con una lista existente.
     */
    public ServicioApartamento(ListaDoble<Apartamento> apartamentos) {
        if (apartamentos == null) {
            throw new IllegalArgumentException(
                    "La lista de apartamentos no puede ser nula.");
        }

        this.apartamentos = apartamentos;
    }

    /**
     * Registra un apartamento.
     *
     * @return true si fue registrado, false si el código existe.
     */
    public boolean registrarApartamento(Apartamento apartamento) {
        validarApartamento(apartamento);

        if (existeApartamento(apartamento.getCodigo())) {
            return false;
        }

        apartamentos.agregarFinal(apartamento);
        return true;
    }

    /**
     * Busca un apartamento por su código.
     */
    public Apartamento buscarPorCodigo(String codigo) {
        String codigoValidado = validarCodigo(codigo);

        for (int i = 0; i < apartamentos.tamano(); i++) {
            Apartamento apartamento = apartamentos.obtener(i);

            if (apartamento.getCodigo().equalsIgnoreCase(codigoValidado)) {
                return apartamento;
            }
        }

        return null;
    }

    /**
     * Comprueba si un apartamento está registrado.
     */
    public boolean existeApartamento(String codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    /**
     * Elimina un apartamento registrado.
     *
     * No comprueba reservas asociadas. Esa validación
     * deberá integrarse con ServicioReserva.
     */
    public boolean eliminarApartamento(String codigo) {
        Apartamento apartamento = buscarPorCodigo(codigo);

        if (apartamento == null) {
            return false;
        }

        return apartamentos.eliminar(apartamento);
    }

    /**
     * Actualiza la información general del apartamento.
     *
     * El código no se modifica para conservar su identidad.
     */
    public boolean actualizarApartamento(
            String codigo,
            String nombre,
            String bloque,
            int piso,
            int numeroDormitorios,
            int capacidad,
            String caracteristicas) {

        Apartamento apartamento = buscarPorCodigo(codigo);

        if (apartamento == null) {
            return false;
        }

        // Validamos antes de modificar para evitar cambios
        // parciales si algún dato es incorrecto.
        Apartamento datosValidados = new Apartamento(
                apartamento.getCodigo(),
                nombre,
                bloque,
                piso,
                numeroDormitorios,
                capacidad,
                caracteristicas
        );

        apartamento.setNombre(datosValidados.getNombre());
        apartamento.setBloque(datosValidados.getBloque());
        apartamento.setPiso(datosValidados.getPiso());
        apartamento.setNumeroDormitorios(
                datosValidados.getNumeroDormitorios());
        apartamento.setCapacidad(datosValidados.getCapacidad());
        apartamento.setCaracteristicas(
                datosValidados.getCaracteristicas());

        return true;
    }

    /**
     * Obtiene todos los apartamentos registrados.
     *
     * Devuelve una lista independiente, para evitar
     * que se modifique directamente la lista interna.
     */
    public ListaDoble<Apartamento> listarApartamentos() {
        ListaDoble<Apartamento> resultado = new ListaDoble<>();

        for (int i = 0; i < apartamentos.tamano(); i++) {
            resultado.agregarFinal(apartamentos.obtener(i));
        }

        return resultado;
    }

    /**
     * Filtra apartamentos por estado operativo.
     */
    public ListaDoble<Apartamento> filtrarPorEstado(
            EstadoApartamento estado) {

        validarEstado(estado);

        ListaDoble<Apartamento> resultado = new ListaDoble<>();

        for (int i = 0; i < apartamentos.tamano(); i++) {
            Apartamento apartamento = apartamentos.obtener(i);

            if (apartamento.getEstado() == estado) {
                resultado.agregarFinal(apartamento);
            }
        }

        return resultado;
    }

    /**
     * Filtra apartamentos por bloque.
     */
    public ListaDoble<Apartamento> filtrarPorBloque(String bloque) {
        String bloqueValidado = validarTexto(bloque, "bloque");

        ListaDoble<Apartamento> resultado = new ListaDoble<>();

        for (int i = 0; i < apartamentos.tamano(); i++) {
            Apartamento apartamento = apartamentos.obtener(i);

            if (apartamento.getBloque().equalsIgnoreCase(bloqueValidado)) {
                resultado.agregarFinal(apartamento);
            }
        }

        return resultado;
    }

    /**
     * Filtra apartamentos por capacidad mínima.
     */
    public ListaDoble<Apartamento> filtrarPorCapacidad(
            int cantidadPersonas) {

        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero.");
        }

        ListaDoble<Apartamento> resultado = new ListaDoble<>();

        for (int i = 0; i < apartamentos.tamano(); i++) {
            Apartamento apartamento = apartamentos.obtener(i);

            if (apartamento.tieneCapacidadPara(cantidadPersonas)) {
                resultado.agregarFinal(apartamento);
            }
        }

        return resultado;
    }

    /**
     * Lista apartamentos preparados y con capacidad suficiente.
     *
     * Esta consulta solo verifica disponibilidad operativa,
     * no disponibilidad por fechas o reservas.
     */
    public ListaDoble<Apartamento> buscarPreparadosPorCapacidad(
            int cantidadPersonas) {

        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor que cero.");
        }

        ListaDoble<Apartamento> resultado = new ListaDoble<>();

        for (int i = 0; i < apartamentos.tamano(); i++) {
            Apartamento apartamento = apartamentos.obtener(i);

            if (apartamento.estaDisponibleOperativamente()
                    && apartamento.tieneCapacidadPara(cantidadPersonas)) {

                resultado.agregarFinal(apartamento);
            }
        }

        return resultado;
    }

    /**
     * Cambia el estado operativo del apartamento.
     *
     * Para FUERA_DE_SERVICIO se utiliza otro método,
     * porque es obligatorio registrar un motivo.
     */
    public boolean cambiarEstado(
            String codigo,
            EstadoApartamento estado) {

        validarEstado(estado);

        if (estado == EstadoApartamento.FUERA_DE_SERVICIO) {
            throw new IllegalArgumentException(
                    "Utilice marcarFueraDeServicio() para registrar el motivo.");
        }

        Apartamento apartamento = buscarPorCodigo(codigo);

        if (apartamento == null) {
            return false;
        }

        apartamento.setEstado(estado);
        return true;
    }

    /**
     * Marca un apartamento como fuera de servicio.
     */
    public boolean marcarFueraDeServicio(
            String codigo,
            String motivo) {

        Apartamento apartamento = buscarPorCodigo(codigo);

        if (apartamento == null) {
            return false;
        }

        apartamento.marcarFueraDeServicio(motivo);
        return true;
    }

    /**
     * Habilita nuevamente un apartamento.
     */
    public boolean habilitarApartamento(String codigo) {
        Apartamento apartamento = buscarPorCodigo(codigo);

        if (apartamento == null) {
            return false;
        }

        apartamento.habilitarApartamento();
        return true;
    }

    /**
     * Comprueba si el apartamento está preparado.
     *
     * No comprueba si existen reservas para una fecha.
     */
    public boolean estaDisponibleOperativamente(String codigo) {
        Apartamento apartamento = buscarPorCodigo(codigo);

        return apartamento != null
                && apartamento.estaDisponibleOperativamente();
    }

    /**
     * Configura la tarifa de un apartamento
     * para una temporada determinada.
     */
    public boolean configurarTarifa(
            String codigo,
            TipoTemporada temporada,
            BigDecimal tarifa) {

        Apartamento apartamento = buscarPorCodigo(codigo);

        if (apartamento == null) {
            return false;
        }

        apartamento.configurarTarifa(temporada, tarifa);
        return true;
    }

    /**
     * Consulta la tarifa de un apartamento.
     *
     * @return tarifa o null si el apartamento no existe.
     */
    public BigDecimal obtenerTarifa(
            String codigo,
            TipoTemporada temporada) {

        Apartamento apartamento = buscarPorCodigo(codigo);

        if (apartamento == null) {
            return null;
        }

        return apartamento.obtenerTarifa(temporada);
    }

    /**
     * Cuenta apartamentos con un estado determinado.
     */
    public int contarPorEstado(EstadoApartamento estado) {
        validarEstado(estado);

        int contador = 0;

        for (int i = 0; i < apartamentos.tamano(); i++) {
            Apartamento apartamento = apartamentos.obtener(i);

            if (apartamento.getEstado() == estado) {
                contador++;
            }
        }

        return contador;
    }

    /**
     * Obtiene la cantidad total de apartamentos.
     */
    public int contarApartamentos() {
        return apartamentos.tamano();
    }

    /**
     * Indica si no hay apartamentos registrados.
     */
    public boolean estaVacio() {
        return apartamentos.estaVacia();
    }

    /**
     * Valida un apartamento.
     */
    private void validarApartamento(Apartamento apartamento) {
        if (apartamento == null) {
            throw new IllegalArgumentException(
                    "El apartamento no puede ser nulo.");
        }
    }

    /**
     * Valida un código de apartamento.
     */
    private String validarCodigo(String codigo) {
        return validarTexto(codigo, "código");
    }

    /**
     * Valida un estado operativo.
     */
    private void validarEstado(EstadoApartamento estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado no puede ser nulo.");
        }
    }

    /**
     * Valida y normaliza un texto obligatorio.
     */
    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede ser nulo o vacío.");
        }

        return valor.trim();
    }
}

