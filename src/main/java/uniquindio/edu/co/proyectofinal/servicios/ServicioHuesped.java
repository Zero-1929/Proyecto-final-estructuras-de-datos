package uniquindio.edu.co.proyectofinal.servicios;

import uniquindio.edu.co.proyectofinal.estructuras.ABB;
import uniquindio.edu.co.proyectofinal.estructuras.ListaDoble;
import uniquindio.edu.co.proyectofinal.modelo.Huesped;

/**
 * Servicio encargado de gestionar los huéspedes del SGA.
 *
 * Utiliza un árbol binario de búsqueda (ABB) para
 * almacenar los huéspedes ordenados por documento.
 */
public class ServicioHuesped {

    /**
     * Árbol que almacena los huéspedes registrados.
     */
    private final ABB<Huesped> huespedes;

    /**
     * Crea el servicio con un árbol vacío.
     */
    public ServicioHuesped() {
        this.huespedes = new ABB<>();
    }

    /**
     * Crea el servicio utilizando un árbol existente.
     *
     * @param huespedes árbol de huéspedes
     */
    public ServicioHuesped(ABB<Huesped> huespedes) {
        if (huespedes == null) {
            throw new IllegalArgumentException(
                    "El árbol de huéspedes no puede ser nulo");
        }

        this.huespedes = huespedes;
    }

    /**
     * Registra un nuevo huésped.
     *
     * No permite registrar dos huéspedes con
     * el mismo documento de identidad.
     *
     * @param huesped huésped que se desea registrar
     * @return true si se registró correctamente
     */
    public boolean registrarHuesped(Huesped huesped) {
        if (huesped == null) {
            throw new IllegalArgumentException(
                    "El huésped no puede ser nulo");
        }

        if (existeHuesped(huesped.getDocumento())) {
            return false;
        }

        huespedes.insertar(huesped);
        return true;
    }

    /**
     * Busca un huésped por su documento.
     *
     * @param documento documento de identidad
     * @return huésped encontrado o null si no existe
     */
    public Huesped buscarPorDocumento(String documento) {
        String documentoValidado = validarDocumento(documento);

        for (Huesped huesped : huespedes) {
            if (huesped.getDocumento().equals(documentoValidado)) {
                return huesped;
            }
        }

        return null;
    }

    /**
     * Comprueba si un huésped está registrado.
     *
     * @param documento documento de identidad
     * @return true si existe, false en caso contrario
     */
    public boolean existeHuesped(String documento) {
        return buscarPorDocumento(documento) != null;
    }

    /**
     * Actualiza el teléfono y el correo de un huésped.
     *
     * @param documento documento del huésped
     * @param telefono nuevo teléfono
     * @param correo nuevo correo electrónico
     * @return true si se actualizaron los datos
     */
    public boolean actualizarContacto(
            String documento,
            String telefono,
            String correo) {

        Huesped huesped = buscarPorDocumento(documento);

        if (huesped == null) {
            return false;
        }

        // Validar ambos valores antes de modificar el huésped.
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono no puede ser nulo o vacío");
        }

        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException(
                    "El correo no puede ser nulo o vacío");
        }

        huesped.setTelefono(telefono);
        huesped.setCorreo(correo);

        return true;
    }

    /**
     * Actualiza la fotografía de un huésped.
     *
     * La ruta puede ser null para eliminar la fotografía.
     *
     * @param documento documento del huésped
     * @param rutaFoto nueva ruta de la fotografía
     * @return true si se actualizó correctamente
     */
    public boolean actualizarFoto(
            String documento,
            String rutaFoto) {

        Huesped huesped = buscarPorDocumento(documento);

        if (huesped == null) {
            return false;
        }

        huesped.setRutaFoto(rutaFoto);
        return true;
    }

    /**
     * Elimina un huésped del árbol.
     *
     * @param documento documento del huésped
     * @return true si fue eliminado
     */
    public boolean eliminarHuesped(String documento) {
        Huesped huesped = buscarPorDocumento(documento);

        if (huesped == null) {
            return false;
        }

        return huespedes.eliminar(huesped);
    }

    /**
     * Obtiene todos los huéspedes ordenados
     * ascendentemente por documento.
     *
     * @return lista doble con los huéspedes
     */
    public ListaDoble<Huesped> listarHuespedes() {
        return huespedes.inOrden();
    }

    /**
     * Obtiene la cantidad de huéspedes registrados.
     *
     * @return número total de huéspedes
     */
    public int contarHuespedes() {
        return huespedes.tamano();
    }

    /**
     * Comprueba si existen huéspedes registrados.
     *
     * @return true si no hay huéspedes
     */
    public boolean estaVacio() {
        return huespedes.estaVacia();
    }

    /**
     * Valida y normaliza un documento de identidad.
     *
     * @param documento documento que se desea validar
     * @return documento sin espacios externos
     */
    private String validarDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException(
                    "El documento no puede ser nulo o vacío");
        }

        return documento.trim();
    }
}
