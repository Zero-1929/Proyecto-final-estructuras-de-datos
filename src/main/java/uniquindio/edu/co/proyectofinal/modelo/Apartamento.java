package uniquindio.edu.co.proyectofinal.modelo;

import java.math.BigDecimal;

public class Apartamento {

    private String codigo;
    private String nombre;
    private String bloque;
    private int piso;
    private int numeroDormitorios;
    private int capacidad;
    private String caracteristicas;

    private EstadoApartamento estado;
    private String motivoFueraServicio;

    private BigDecimal tarifaBaja;
    private BigDecimal tarifaMedia;
    private BigDecimal tarifaAlta;

    /**
     * Constructor principal.
     *
     * Las tarifas pueden ser null mientras todavía no se hayan configurado.
     */
    public Apartamento(
            String codigo,
            String nombre,
            String bloque,
            int piso,
            int numeroDormitorios,
            int capacidad,
            String caracteristicas) {

        setCodigo(codigo);
        setNombre(nombre);
        setBloque(bloque);
        setPiso(piso);
        setNumeroDormitorios(numeroDormitorios);
        setCapacidad(capacidad);
        setCaracteristicas(caracteristicas);

        this.estado = EstadoApartamento.PREPARADO;

        this.tarifaBaja = null;
        this.tarifaMedia = null;
        this.tarifaAlta = null;

        this.motivoFueraServicio = null;
    }

    // -------------------- GETTERS --------------------

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getBloque() {
        return bloque;
    }

    public int getPiso() {
        return piso;
    }

    public int getNumeroDormitorios() {
        return numeroDormitorios;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public EstadoApartamento getEstado() {
        return estado;
    }

    public String getMotivoFueraServicio() {
        return motivoFueraServicio;
    }

    public BigDecimal getTarifaBaja() {
        return tarifaBaja;
    }

    public BigDecimal getTarifaMedia() {
        return tarifaMedia;
    }

    public BigDecimal getTarifaAlta() {
        return tarifaAlta;
    }

    // -------------------- SETTERS --------------------

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código del apartamento es obligatorio."
            );
        }

        this.codigo = codigo.trim();
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del apartamento es obligatorio."
            );
        }

        this.nombre = nombre.trim();
    }

    public void setBloque(String bloque) {
        if (bloque == null || bloque.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El bloque del apartamento es obligatorio."
            );
        }

        this.bloque = bloque.trim();
    }

    public void setPiso(int piso) {
        if (piso < 0) {
            throw new IllegalArgumentException(
                    "El piso no puede ser negativo."
            );
        }

        this.piso = piso;
    }

    public void setNumeroDormitorios(int numeroDormitorios) {
        if (numeroDormitorios <= 0) {
            throw new IllegalArgumentException(
                    "El apartamento debe tener al menos un dormitorio."
            );
        }

        this.numeroDormitorios = numeroDormitorios;
    }

    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero."
            );
        }

        this.capacidad = capacidad;
    }

    public void setCaracteristicas(String caracteristicas) {
        if (caracteristicas == null) {
            this.caracteristicas = "";
        } else {
            this.caracteristicas = caracteristicas.trim();
        }
    }

    // -------------------- ESTADO DEL APARTAMENTO --------------------

    /**
     * Cambia el estado operativo del apartamento.
     *
     * Para marcarlo como FUERA_DE_SERVICIO se debe utilizar
     * el método marcarFueraDeServicio().
     */
    public void setEstado(EstadoApartamento estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del apartamento no puede ser nulo."
            );
        }

        if (estado == EstadoApartamento.FUERA_DE_SERVICIO) {
            throw new IllegalArgumentException(
                    "Utilice marcarFueraDeServicio() para registrar el motivo."
            );
        }

        this.estado = estado;
        this.motivoFueraServicio = null;
    }

    /**
     * Marca el apartamento como fuera de servicio
     * y registra el motivo correspondiente.
     */
    public void marcarFueraDeServicio(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe especificar el motivo por el cual está fuera de servicio."
            );
        }

        this.estado = EstadoApartamento.FUERA_DE_SERVICIO;
        this.motivoFueraServicio = motivo.trim();
    }

    /**
     * Devuelve el apartamento al estado preparado.
     */
    public void habilitarApartamento() {
        this.estado = EstadoApartamento.PREPARADO;
        this.motivoFueraServicio = null;
    }

    public boolean estaDisponibleOperativamente() {
        return estado == EstadoApartamento.PREPARADO;
    }

    // -------------------- TARIFAS --------------------

    /**
     * Configura la tarifa correspondiente a una temporada.
     */
    public void configurarTarifa(
            TipoTemporada tipoTemporada,
            BigDecimal tarifa) {

        if (tipoTemporada == null) {
            throw new IllegalArgumentException(
                    "El tipo de temporada no puede ser nulo."
            );
        }

        validarTarifa(tarifa);

        switch (tipoTemporada) {

            case BAJA:
                this.tarifaBaja = tarifa;
                break;

            case MEDIA:
                this.tarifaMedia = tarifa;
                break;

            case ALTA:
                this.tarifaAlta = tarifa;
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de temporada no válido."
                );
        }
    }

    /**
     * Obtiene la tarifa configurada para una temporada.
     */
    public BigDecimal obtenerTarifa(TipoTemporada tipoTemporada) {

        if (tipoTemporada == null) {
            throw new IllegalArgumentException(
                    "El tipo de temporada no puede ser nulo."
            );
        }

        BigDecimal tarifa;

        switch (tipoTemporada) {

            case BAJA:
                tarifa = tarifaBaja;
                break;

            case MEDIA:
                tarifa = tarifaMedia;
                break;

            case ALTA:
                tarifa = tarifaAlta;
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de temporada no válido."
                );
        }

        if (tarifa == null) {
            throw new IllegalStateException(
                    "No se ha configurado la tarifa de temporada "
                            + tipoTemporada + " para el apartamento " + codigo
            );
        }

        return tarifa;
    }

    /**
     * Comprueba que una tarifa no sea nula ni negativa.
     */
    private void validarTarifa(BigDecimal tarifa) {
        if (tarifa == null) {
            throw new IllegalArgumentException(
                    "La tarifa no puede ser nula."
            );
        }

        if (tarifa.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "La tarifa no puede ser negativa."
            );
        }
    }

    // -------------------- OTROS MÉTODOS --------------------

    public boolean tieneCapacidadPara(int cantidadPersonas) {
        return cantidadPersonas > 0 && cantidadPersonas <= capacidad;
    }

    @Override
    public String toString() {
        return "Apartamento " + codigo
                + " | Nombre: " + nombre
                + " | Bloque: " + bloque
                + " | Piso: " + piso
                + " | Dormitorios: " + numeroDormitorios
                + " | Capacidad: " + capacidad
                + " | Estado: " + estado;
    }
}