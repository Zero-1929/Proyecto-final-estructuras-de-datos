package uniquindio.edu.co.proyectofinal.persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Lee y escribe snapshots del SGA en archivos JSON.
 *
 * La escritura se hace primero en un archivo temporal y luego se reemplaza el
 * archivo definitivo. Asi se evita dejar un JSON incompleto si el programa se
 * cierra durante el guardado.
 */
public final class RepositorioJSON {

    /**
     * Gson configurado para generar un JSON legible.
     */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    /**
     * Evita crear instancias de la clase.
     */
    private RepositorioJSON() {
    }

    /**
     * Guarda un snapshot en la ruta indicada.
     *
     * @param datos datos que se van a guardar
     * @param ruta archivo de destino
     * @throws IOException si no se puede escribir el archivo
     */
    public static void guardar(DatosSGA datos, Path ruta) throws IOException {

        if (datos == null) {
            throw new IllegalArgumentException(
                    "Los datos no pueden ser nulos."
            );
        }

        if (ruta == null) {
            throw new IllegalArgumentException(
                    "La ruta no puede ser nula."
            );
        }

        Path destino = ruta.toAbsolutePath();
        Path carpeta = destino.getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        Path temporal = Files.createTempFile(
                carpeta,
                "sga-",
                ".json"
        );

        try {
            try (Writer escritor = Files.newBufferedWriter(
                    temporal,
                    StandardCharsets.UTF_8
            )) {
                GSON.toJson(datos, escritor);
            }

            try {
                Files.move(
                        temporal,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException excepcion) {
                Files.move(
                        temporal,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    /**
     * Carga un snapshot desde la ruta indicada.
     *
     * Si el archivo no existe se devuelve un snapshot vacio.
     *
     * @param ruta archivo de origen
     * @return datos cargados
     * @throws IOException si el archivo existe pero no se puede leer o el JSON
     *                     no tiene el formato esperado
     */
    public static DatosSGA cargar(Path ruta) throws IOException {

        if (ruta == null) {
            throw new IllegalArgumentException(
                    "La ruta no puede ser nula."
            );
        }

        if (!Files.exists(ruta)) {
            return DatosSGA.vacio();
        }

        try (Reader lector = Files.newBufferedReader(
                ruta,
                StandardCharsets.UTF_8
        )) {
            DatosSGA datos = GSON.fromJson(lector, DatosSGA.class);
            return datos == null ? DatosSGA.vacio() : datos;
        } catch (JsonParseException excepcion) {
            throw new IOException(
                    "El archivo de datos no tiene un formato valido.",
                    excepcion
            );
        }
    }
}
