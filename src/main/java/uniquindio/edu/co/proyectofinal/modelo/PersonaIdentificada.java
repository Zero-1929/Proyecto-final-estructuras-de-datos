package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

/**
 * Representa una persona con documento de identidad y datos de contacto.
 * Es la base para huéspedes y empleados.
 */
public abstract class PersonaIdentificada extends Persona {

    /**
     * Atributos
     */
    private final String documento;
    private String telefono;
    private String correo;

    /**
     * Crea una persona identificada.
     *
     * @param nombre nombre completo
     * @param fechaNacimiento fecha de nacimiento
     * @param documento documento de identidad
     * @param telefono teléfono de contacto
     * @param correo correo electrónico
     */
    protected PersonaIdentificada(String nombre, LocalDate fechaNacimiento, String documento,
                                  String telefono, String correo) {
        super(nombre, fechaNacimiento);
        this.documento = validarTexto(documento, "documento");
        setTelefono(telefono);
        setCorreo(correo);
    }

    /**
     * Obtiene el documento.
     *
     * @return documento de identidad
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * Obtiene el teléfono.
     *
     * @return teléfono de contacto
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Cambia el teléfono.
     *
     * @param telefono nuevo teléfono
     */
    public void setTelefono(String telefono) {
        this.telefono = validarTexto(telefono, "teléfono");
    }

    /**
     * Obtiene el correo.
     *
     * @return correo electrónico
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Cambia el correo.
     *
     * @param correo nuevo correo
     */
    public void setCorreo(String correo) {
        this.correo = validarTexto(correo, "correo");
    }

    /**
     * Valida que un texto no sea nulo ni vacío.
     *
     * @param valor texto a validar
     * @param campo nombre del campo para el mensaje
     * @return texto sin espacios externos
     */
    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser nulo o vacío");
        }
        return valor.trim();
    }
}
