package uniquindio.edu.co.proyectofinal.servicios;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/** Bitácora persistente de inicios y finalizaciones de preparación. */
public final class HistorialTiemposPreparacion {
    private static final Path ARCHIVO=Path.of("tiempos-preparacion-sga.tsv");
    private HistorialTiemposPreparacion() {}
    public record Registro(String apartamento,LocalDateTime inicio,LocalDateTime fin) {
        public long segundos() { return Math.max(0,Duration.between(inicio,fin).getSeconds()); }
    }
    private static synchronized List<String> lineas() {
        try {return Files.exists(ARCHIVO)?Files.readAllLines(ARCHIVO,StandardCharsets.UTF_8):List.of();}
        catch(IOException ex) {throw new IllegalStateException("No se pudo leer historial de preparación",ex);}
    }
    private static synchronized void agregar(String tipo,String apartamento) {
        try {Files.writeString(ARCHIVO,tipo+"\t"+apartamento+"\t"+LocalDateTime.now()+System.lineSeparator(),
            StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND);}
        catch(IOException ex) {throw new IllegalStateException("No se pudo guardar historial de preparación",ex);}
    }
    public static void iniciar(String apartamento) {agregar("INICIO",apartamento);}
    public static void finalizar(String apartamento) {
        // No registrar finalización sin un inicio: una preparación anterior puede carecer de bitácora.
        if(inicioPendiente(apartamento)!=null) agregar("FIN",apartamento);
    }
    private static LocalDateTime inicioPendiente(String apartamento) {
        LocalDateTime inicio=null;
        for(String l:lineas()) {
            String[] x=l.split("\t",-1);
            if(x.length!=3||!x[1].equalsIgnoreCase(apartamento))continue;
            try {
                if(x[0].equals("INICIO"))inicio=LocalDateTime.parse(x[2]);
                if(x[0].equals("FIN"))inicio=null;
            }catch(Exception ignored) {}
        }
        return inicio;
    }
    public static synchronized List<Registro> listar() {
        List<Registro> resultados=new ArrayList<>();
        Map<String,LocalDateTime> inicios=new HashMap<>();
        for(String l:lineas()) {
            String[] x=l.split("\t",-1);
            if(x.length!=3)continue;
            try {
                LocalDateTime fecha=LocalDateTime.parse(x[2]);
                if(x[0].equals("INICIO"))inicios.put(x[1],fecha);
                else if(x[0].equals("FIN")) {
                    LocalDateTime inicio=inicios.remove(x[1]);
                    if(inicio!=null&&!fecha.isBefore(inicio))resultados.add(new Registro(x[1],inicio,fecha));
                }
            }catch(Exception ignored) {}
        }
        return resultados;
    }
}
