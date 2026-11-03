package uniquindio.edu.co.proyectofinal.modelo;

import java.time.LocalDate;

public class Ocupante extends Persona {

    private Huesped perfilVinculado;

    public Ocupante(String nombre, LocalDate fechaNacimiento) {
        super(nombre, fechaNacimiento);
    }

    public Ocupante(String nombre, LocalDate fechaNacimiento, Huesped perfilVinculado) {
        this(nombre, fechaNacimiento);
        setPerfilVinculado(perfilVinculado);
    }

    public Huesped getPerfilVinculado() {
        return perfilVinculado;
    }

    public void setPerfilVinculado(Huesped perfilVinculado) {
        this.perfilVinculado = perfilVinculado;
    }

    public boolean tienePerfil() {
        return perfilVinculado != null;
    }

    public boolean esMenorDeSeis(LocalDate fechaEstancia) {
        return edad(fechaEstancia) < 6;
    }
}
