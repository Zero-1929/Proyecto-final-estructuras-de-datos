package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

/**
 * Representa un huésped con perfil completo en el sistema.
 * El orden natural se establece por documento.
 */
public class Huesped extends PersonaIdentificada implements Comparable<Huesped> {

    /**
     * Ruta de la fotografía, puede ser nula.
     */
    private String rutaFoto;

    /**
     * Crea un huésped sin fotografía.
     *
     * @param nombre nombre completo
     * @param fechaNacimiento fecha de nacimiento
     * @param documento documento de identidad
     * @param telefono teléfono de contacto
     * @param correo correo electrónico
     */
    public Huesped(String nombre, LocalDate fechaNacimiento, String documento,
                   String telefono, String correo) {
        this(nombre, fechaNacimiento, documento, telefono, correo, null);
    }

    /**
     * Crea un huésped con todos sus datos.
     *
     * @param nombre nombre completo
     * @param fechaNacimiento fecha de nacimiento
     * @param documento documento de identidad
     * @param telefono teléfono de contacto
     * @param correo correo electrónico
     * @param rutaFoto ruta de la fotografía
     */
    public Huesped(String nombre, LocalDate fechaNacimiento, String documento,
                   String telefono, String correo, String rutaFoto) {
        super(nombre, fechaNacimiento, documento, telefono, correo);
        setRutaFoto(rutaFoto);
    }

    /**
     * Obtiene la ruta de la fotografía.
     *
     * @return ruta de la foto o null si no tiene
     */
    public String getRutaFoto() {
        return rutaFoto;
    }

    /**
     * Cambia la ruta de la fotografía.
     *
     * @param rutaFoto nueva ruta, puede ser null
     */
    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = (rutaFoto == null || rutaFoto.isBlank()) ? null : rutaFoto.trim();
    }

    /**
     * Compara huéspedes por documento.
     *
     * @param otro huésped con el que se compara
     * @return resultado de la comparación de documentos
     */
    @Override
    public int compareTo(Huesped otro) {
        return getDocumento().compareTo(otro.getDocumento());
    }

    /**
     * Compara dos huéspedes por documento.
     *
     * @param obj objeto a comparar
     * @return true si tienen el mismo documento
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Huesped otro)) {
            return false;
        }
        return getDocumento().equals(otro.getDocumento());
    }

    /**
     * Genera el código hash a partir del documento.
     *
     * @return código hash del documento
     */
    @Override
    public int hashCode() {
        return getDocumento().hashCode();
    }
}
