package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

public abstract class PersonaIdentificada extends Persona {

    private final String documento;
    private String telefono;
    private String correo;

    protected PersonaIdentificada(String nombre, LocalDate fechaNacimiento, String documento,
                                  String telefono, String correo) {
        super(nombre, fechaNacimiento);
        this.documento = validarTexto(documento, "documento");
        setTelefono(telefono);
        setCorreo(correo);
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = validarTexto(telefono, "teléfono");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = validarTexto(correo, "correo");
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser nulo o vacío");
        }
        return valor.trim();
    }
}
