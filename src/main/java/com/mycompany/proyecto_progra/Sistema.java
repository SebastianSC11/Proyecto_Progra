/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_progra;

/**
 *
 * @author isaac
 */
public class Sistema {
    private Evento[] listaEventos;
    private Usuario[] listaUsuarios;
    private Entrada[] listaEntradas;

    private int contadorEventos;
    private int contadorUsuarios;
    private int contadorEntradas;

    // Contadores para IDs únicos
    private int generadorIdEventos;
    private int generadorIdUsuarios;
    private int generadorIdEntradas;

    public Sistema() {
        listaEventos = new Evento[50];
        listaUsuarios = new Usuario[200];
        listaEntradas = new Entrada[500];

        contadorEventos = 0;
        contadorUsuarios = 0;
        contadorEntradas = 0;

        generadorIdEventos = 1;
        generadorIdUsuarios = 1;
        generadorIdEntradas = 1;
    }

    // --------------------------
    // Búsquedas internas
    // --------------------------
    private Evento buscarEventoPorId(String idEvento) {
        for (int i = 0; i < contadorEventos; i++) {
            if (listaEventos[i] != null && listaEventos[i].getIdEvento().equals(idEvento)) {
                return listaEventos[i];
            }
        }
        return null;
    }

    private Usuario buscarUsuarioPorCorreo(String correo) {
        for (int i = 0; i < contadorUsuarios; i++) {
            if (listaUsuarios[i] != null && listaUsuarios[i].getCorreo().equals(correo)) {
                return listaUsuarios[i];
            }
        }
        return null;
    }

    public Usuario buscarUsuarioPorId(String idUsuario) {
        for (int i = 0; i < contadorUsuarios; i++) {
            if (listaUsuarios[i] != null && listaUsuarios[i].getIdUsuario().equals(idUsuario)) {
                return listaUsuarios[i];
            }
        }
        return null;
    }

    
    
    // (1) Registrar Evento (Admin)
    public String registrarEvento(String nombre, String ubicacion, String fecha,
                                  int capacidadMaxima, String tipoEvento) {
        if (contadorEventos >= 50) {
            return "No se pueden registrar más eventos (límite 50).";
        }
        // Generar ID: EVT-####
        String idEvento = "EVT-" + String.format("%04d", generadorIdEventos);
        generadorIdEventos++;

        Evento nuevo = new Evento(idEvento, nombre, ubicacion, fecha, capacidadMaxima, tipoEvento);
        listaEventos[contadorEventos] = nuevo;
        contadorEventos++;

        return "Evento registrado con éxito. ID: " + idEvento;
    }

    // (2) Registrar Usuario
    public String registrarUsuario(String nombre, String correo, String seleccionTipoUsuario) {
        if (contadorUsuarios >= 200) {
            return "No se pueden registrar más usuarios (límite 200).";
        }
        // Validar duplicado por correo
        if (buscarUsuarioPorCorreo(correo) != null) {
            return "Ya existe un usuario con ese correo.";
        }

        // Generar ID: USR-####
        String idUsuario = "USR-" + String.format("%04d", generadorIdUsuarios);
        generadorIdUsuarios++;

        Usuario nuevo = new Usuario(idUsuario, nombre, correo, seleccionTipoUsuario);
        listaUsuarios[contadorUsuarios] = nuevo;
        contadorUsuarios++;

        return "Usuario registrado con éxito. ID asignado: " + idUsuario;
    }

    // (3) Comprar Entrada (una por llamada)
    public String comprarEntrada(String idUsuario, String idEvento) {
        Usuario u = buscarUsuarioPorId(idUsuario);
        if (u == null) {
            return "El usuario con ID " + idUsuario + " no está registrado.";
        }
        Evento e = buscarEventoPorId(idEvento);
        if (e == null) {
            return "El evento con ID " + idEvento + " no existe.";
        }
        // Verificar cupo
        if (e.estaLleno()) {
            return "No se pueden vender más entradas. El evento está lleno.";
        }
        // Verificar que el usuario no exceda 5 entradas en ese evento
        int entradasActuales = u.contarEntradasPorEvento(idEvento);
        if (entradasActuales >= 5) {
            return "No se pueden comprar más de 5 entradas para este evento.";
        }
        // Verificar que no exceda 500 entradas totales
        if (contadorEntradas >= 500) {
            return "No se pueden gestionar más entradas (límite 500).";
        }

        // Generar código TKT-####
        String codigoEntrada = "TKT-" + String.format("%04d", generadorIdEntradas);
        generadorIdEntradas++;

        // Crear la entrada
        Entrada nuevaEntrada = new Entrada(codigoEntrada, u.getNombre(), e.getIdEvento());
        listaEntradas[contadorEntradas] = nuevaEntrada;
        contadorEntradas++;

        // Asociarla al usuario
        u.agregarEntrada(nuevaEntrada);

        // Incrementar en el evento
        e.incrementarEntradasVendidas();

        return "Compra exitosa. Código de entrada: " + codigoEntrada;
    }

    // (4) Listar Eventos (para que el usuario estándar los vea)
   public String listarEventos() {
    if (contadorEventos == 0) {
        return "No hay eventos registrados.\n";
    }
    String resultado = "=== Eventos Disponibles ===\n";
    for (int i = 0; i < contadorEventos; i++) {
        Evento ev = listaEventos[i];
        if (ev != null) {
            resultado += "- " + ev.getIdEvento() + ": " + ev.getNombre() + " (" + ev.getTipoEvento() + 
                         ") | Capacidad: " + ev.getCapacidadMaxima() + " | Vendidas: " + ev.getEntradasVendidas() + "\n";
        }
    }
    return resultado;
    }

    public Entrada[] buscarEntradasPorUsuario(String idUsuario, Usuario usuario, String idEvento){
        Entrada[] entradasEncontradas = usuario.getEntradasPorEvento(idEvento);
        return entradasEncontradas;
    }

    /**
    * Elimina un evento dado su ID.
    * idEvento el identificador del evento a eliminar
    * return mensaje indicando éxito o que no se encontró el evento
    */
    public String eliminarEvento(String idEvento) {
        // Buscar posición del evento
        for (int i = 0; i < contadorEventos; i++) {
            Evento ev = listaEventos[i];
            if (ev != null && ev.getIdEvento().equals(idEvento)) {
                // Desplazar todo lo que viene después una posición hacia atrás
                for (int j = i; j < contadorEventos - 1; j++) {
                    listaEventos[j] = listaEventos[j + 1];
                }
                // Limpiar el último hueco
                listaEventos[contadorEventos - 1] = null;
                // Ajustar contador
                contadorEventos--;
                return "Evento con ID " + idEvento + " eliminado correctamente.";
            }
        }
        return "No se encontró ningún evento con ID: " + idEvento;
    }

     /**
     * Valida que la entrada existe, pertenece al evento indicado y no ha sido usada.
     * Luego la invalida para que no pueda volver a usarse.
     *
     * codigoEntrada el código TKT-####
     * idEvento      el ID del evento al que se quiere ingresar
     * return mensaje indicando si se permitió o denegó el acceso
     */
    public String controlarAcceso(String codigoEntrada, String idEvento) {
        // Recorremos solo hasta contadorEntradas
        for (int i = 0; i < contadorEntradas; i++) {
            Entrada ent = listaEntradas[i];
            if (ent != null && ent.getCodigoEntrada().equals(codigoEntrada)) {
                // 1) Verificamos que la entrada sea para el evento correcto
                if (!ent.getIdEvento().equals(idEvento)) {
                    return "Acceso denegado. La entrada " 
                        + codigoEntrada 
                        + " no corresponde al evento " 
                        + idEvento + ".";
                }
                // 2) Verificamos si ya fue usada
                if (!ent.getValidez()) {
                    return "Acceso denegado. La entrada " 
                        + codigoEntrada 
                        + " ya fue utilizada.";
                }
                // 3) Invalida y permite el acceso
                ent.invalidar();
                return "Acceso permitido. Entrada " 
                    + codigoEntrada 
                    + " marcada como usada.";
            }
        }
        // No existe ninguna entrada con ese código
        return "Acceso denegado. No se encontró la entrada " 
            + codigoEntrada + ".";
    }

    // En Sistema.java
    public Evento[] getListaEventos() {
        return listaEventos;
    }
    public int getContadorEventos() {
        return contadorEventos;
    }
    public Usuario[] getListaUsuarios() {
        return listaUsuarios;
    }
    public int getContadorUsuarios() {
        return contadorUsuarios;
    }




}

