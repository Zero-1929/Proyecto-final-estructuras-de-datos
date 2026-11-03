package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

public class Huesped extends PersonaIdentificada implements Comparable<Huesped> {

    private String rutaFoto;

    public Huesped(String nombre, LocalDate fechaNacimiento, String documento,
                   String telefono, String correo) {
        this(nombre, fechaNacimiento, documento, telefono, correo, null);
    }

    public Huesped(String nombre, LocalDate fechaNacimiento, String documento,
                   String telefono, String correo, String rutaFoto) {
        super(nombre, fechaNacimiento, documento, telefono, correo);
        setRutaFoto(rutaFoto);
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = (rutaFoto == null || rutaFoto.isBlank()) ? null : rutaFoto.trim();
    }

    @Override
    public int compareTo(Huesped otro) {
        return getDocumento().compareTo(otro.getDocumento());
    }

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

    @Override
    public int hashCode() {
        return getDocumento().hashCode();
    }
}
