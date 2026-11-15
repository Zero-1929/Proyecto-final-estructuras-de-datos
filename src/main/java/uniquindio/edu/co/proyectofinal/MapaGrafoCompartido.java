package uniquindio.edu.co.proyectofinal;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import uniquindio.edu.co.proyectofinal.logica.SistemaSGA;
import uniquindio.edu.co.proyectofinal.modelo.Apartamento;
import uniquindio.edu.co.proyectofinal.servicios.ServicioRutas;

/** Grafo único por SGA, persistido en archivo separado del estado de reservas. */
public final class MapaGrafoCompartido {
    private static final Map<SistemaSGA, Datos> MAPAS=new IdentityHashMap<>();
    private record Conexion(String a,String b,double peso) {}
    private static final class Datos {
        final ServicioRutas<String> grafo=new ServicioRutas<>();
        final Set<String> servicios=new LinkedHashSet<>();
        final Map<String,Conexion> conexiones=new LinkedHashMap<>();
    }
    private MapaGrafoCompartido(){}
    private static Path archivo() {
        return SistemaSGA.rutaPorDefecto().resolveSibling("mapa-sga.tsv");
    }
    private static String codificar(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    private static String decodificar(String s) {
        return new String(Base64.getUrlDecoder().decode(s),StandardCharsets.UTF_8);
    }
    private static String clave(String a,String b) {
        return a.compareTo(b)<=0?codificar(a)+"|"+codificar(b):codificar(b)+"|"+codificar(a);
    }
    private static Datos datos(SistemaSGA sistema) {
        Datos d=MAPAS.get(sistema);
        if(d!=null)return d;
        d=new Datos();
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos())
            d.grafo.registrarUbicacion(a.getCodigo());
        if(Files.exists(archivo())) {
            try {
                List<String> lineas=Files.readAllLines(archivo(),StandardCharsets.UTF_8);
                // Primero vértices de servicio, después conexiones.
                for(String linea:lineas) {
                    String[] t=linea.split("\\t");
                    if(t.length==2&&t[0].equals("N")) {
                        String nombre=decodificar(t[1]);
                        if(!d.grafo.existeUbicacion(nombre)) {
                            d.grafo.registrarUbicacion(nombre);d.servicios.add(nombre);
                        }
                    }
                }
                for(String linea:lineas) {
                    String[] t=linea.split("\\t");
                    if(t.length==4&&t[0].equals("E")) {
                        String a=decodificar(t[1]),b=decodificar(t[2]);
                        double peso=Double.parseDouble(t[3]);
                        if(d.grafo.existeUbicacion(a)&&d.grafo.existeUbicacion(b)
                                &&!a.equals(b)&&Double.isFinite(peso)&&peso>0) {
                            d.grafo.conectarUbicaciones(a,b,peso);
                            d.conexiones.put(clave(a,b),new Conexion(a,b,peso));
                        }
                    }
                }
            }catch(Exception ex) {
                throw new IllegalStateException("No se pudo leer "+archivo()+": "+ex.getMessage(),ex);
            }
        }
        MAPAS.put(sistema,d);
        return d;
    }
    public static synchronized ServicioRutas<String> obtener(SistemaSGA sistema) {
        Datos d=datos(sistema);
        for(Apartamento a:sistema.getServicioApartamento().listarApartamentos())
            if(!d.grafo.existeUbicacion(a.getCodigo()))d.grafo.registrarUbicacion(a.getCodigo());
        return d.grafo;
    }
    private static void guardar(Datos d) {
        Path archivo=archivo();
        List<String> lineas=new ArrayList<>();
        lineas.add("# MAPA SGA v1: N=servicio, E=conexion; distancias en metros");
        for(String s:d.servicios)lineas.add("N\t"+codificar(s));
        for(Conexion c:d.conexiones.values())
            lineas.add("E\t"+codificar(c.a())+"\t"+codificar(c.b())+"\t"+c.peso());
        try {
            Path parent=archivo.getParent();
            if(parent!=null)Files.createDirectories(parent);
            Path temp=Files.createTempFile(parent,"mapa-sga-",".tmp");
            try {
                Files.write(temp,lineas,StandardCharsets.UTF_8);
                try{Files.move(temp,archivo,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
                catch(AtomicMoveNotSupportedException ex){Files.move(temp,archivo,StandardCopyOption.REPLACE_EXISTING);}
            }finally{Files.deleteIfExists(temp);}
        }catch(IOException ex){throw new IllegalStateException("No se pudo guardar "+archivo()+": "+ex.getMessage(),ex);}
    }
    public static synchronized void registrarServicio(SistemaSGA sistema,String codigo) {
        if(codigo==null||codigo.isBlank())throw new IllegalArgumentException("Escribe un nombre.");
        codigo=codigo.strip();
        Datos d=datos(sistema);
        if(d.grafo.existeUbicacion(codigo))throw new IllegalArgumentException("La ubicación ya existe.");
        d.grafo.registrarUbicacion(codigo);
        d.servicios.add(codigo);
        guardar(d);
    }
    public static synchronized void conectar(SistemaSGA sistema,String origen,String destino,double peso) {
        if(origen==null||destino==null||origen.equals(destino))
            throw new IllegalArgumentException("Selecciona dos ubicaciones distintas.");
        if(!Double.isFinite(peso)||peso<=0)throw new IllegalArgumentException("La distancia debe ser positiva.");
        Datos d=datos(sistema);
        d.grafo.conectarUbicaciones(origen,destino,peso);
        d.conexiones.put(clave(origen,destino),new Conexion(origen,destino,peso));
        guardar(d);
    }
    public static synchronized void desconectar(SistemaSGA sistema,String origen,String destino) {
        Datos d=datos(sistema);
        if(!d.grafo.eliminarConexion(origen,destino))
            throw new IllegalArgumentException("Estas ubicaciones no están conectadas.");
        d.conexiones.remove(clave(origen,destino));
        guardar(d);
    }
    public static Path rutaArchivo(){return archivo();}
}
