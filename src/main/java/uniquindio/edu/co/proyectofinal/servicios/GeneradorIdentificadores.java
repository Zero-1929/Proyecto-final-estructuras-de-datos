package uniquindio.edu.co.proyectofinal.servicios;

/**
 * Genera identificadores únicos para grupos,
 * reservas y folios.
 *
 * Los consecutivos iniciales deben ser mayores
 * que los identificadores existentes en el sistema.
 */
public class GeneradorIdentificadores {

    private int siguienteGrupo;
    private int siguienteReserva;
    private int siguienteFolio;

    /**
     * Constructor del generador.
     *
     * @param siguienteGrupo primer consecutivo para grupos.
     * @param siguienteReserva primer consecutivo para reservas.
     * @param siguienteFolio primer consecutivo para folios.
     */
    public GeneradorIdentificadores(
            int siguienteGrupo,
            int siguienteReserva,
            int siguienteFolio) {

        if (siguienteGrupo <= 0
                || siguienteReserva <= 0
                || siguienteFolio <= 0) {
            throw new IllegalArgumentException(
                    "Los consecutivos deben ser mayores que cero."
            );
        }

        this.siguienteGrupo = siguienteGrupo;
        this.siguienteReserva = siguienteReserva;
        this.siguienteFolio = siguienteFolio;
    }

    /**
     * Genera el código de un grupo.
     *
     * @return código del grupo.
     */
    public String generarCodigoGrupo() {
        return String.format("GRU-%05d", siguienteGrupo++);
    }

    /**
     * Genera el código de una reserva.
     *
     * @return código de la reserva.
     */
    public String generarCodigoReserva() {
        return String.format("RES-%05d", siguienteReserva++);
    }

    /**
     * Genera el identificador de un folio.
     *
     * @return identificador numérico del folio.
     */
    public int generarIdFolio() {
        return siguienteFolio++;
    }

    public int getSiguienteGrupo() {
        return siguienteGrupo;
    }

    public int getSiguienteReserva() {
        return siguienteReserva;
    }

    public int getSiguienteFolio() {
        return siguienteFolio;
    }
}

