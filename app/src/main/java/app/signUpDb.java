package app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class signUpDb {
    /** Nombre de la clase para añadir en mensajes de depuración. */
    private static final String TAG = "signUpDb";

    /** Nombre del atributo correo. */
    public static final String MAIL = "mail";

    /** Nombre del atributo nombre. */
    public static final String NOMBRE = "nombre";

    /** Nombre del atributo contraseña. */
    public static final String CONTRASEÑA = "contraseña";

    /** Nombre del atributo verificar contraseña. */
    public static final String VERIFICAR_CONTRASEÑA = "verifContraseña";

    /**HASTA AQUI */

    /** Nombre de la tabla de reservas. */
    public static final String TABLA_RESERVAS = "reservas";

    /** Nombre del atributo id de reserva en la tabla cantidad. */
    public static final String TC_RESERVA = "reserva";

    /** Nombre del atributo id de habitación en la tabla cantidad. */
    public static final String TC_HABITACION = "habitacion";

    /** Nombre del atributo número de ocupantes en la tabla cantidad. */
    public static final String TC_NUM_OCUPANTES = "ocupantes";

    /** Nombre de la tabla cantidad (relaciona habitaciones y reservas). */
    public static final String TABLA_CANTIDAD = "cantidad";

    /** Consulta SQL para obtener calcular el precio de una habitación. */
    public static final String CALCULAR_PRECIO =
            "SELECT sum(precioHabitacion) AS precioReserva FROM (" +
                    "SELECT  H." + HabitacionDbAdapter.PRECIO_1_OCUPANTE + " + " +
                    "H." + HabitacionDbAdapter.PORCENTAJE_OCUPANTE_EXTRA + " * " +
                    "H." + HabitacionDbAdapter.PRECIO_1_OCUPANTE + " * " +
                    "(C." + TC_NUM_OCUPANTES + " - 1) AS precioHabitacion " +
                    "FROM " + HabitacionDbAdapter.TABLA_HABITACIONES + " H " +
                    "JOIN " + TABLA_CANTIDAD + " C " +
                    "ON C." + TC_HABITACION + " = H." + HabitacionDbAdapter.IDENTIFICADOR +
                    " WHERE C." + TC_RESERVA + " = ? " +
                    ") AS subquery ";

    /** Consulta SQL para obtener todas las reservas. */
    private static final String CONSULTA_RESERVAS =
            "SELECT " + ID + " AS _id," +
                    NOMBRE_CLIENTE + "," +
                    MOVIL + "," +
                    FECHA_ENTRADA + "," +
                    FECHA_SALIDA + "," +
                    PRECIO +
                    " FROM " + TABLA_RESERVAS + " ORDER BY ";

    /**
     * Consulta SQL para obtener la cuenta de habitaciones reservadas
     * en un periodo de tiempo, sin contar las contenidas en la reserva a modificar
     */
    public static final String CUENTA_SOLAPAMIENTOS_ID =
            "SELECT count(*) AS cuenta " +
                    "FROM " + TABLA_CANTIDAD + " C JOIN " + TABLA_RESERVAS + " R " +
                    "ON C." + TC_RESERVA + " = R." + ID +
                    " WHERE C." + TC_RESERVA + " != ? and " +
                    "C." + TC_HABITACION + " = ? and " +
                    "((R." + FECHA_ENTRADA + " <= ? and R." + FECHA_SALIDA + " > ?) or " +
                    "(R." + FECHA_ENTRADA + " < ? and R." + FECHA_SALIDA + " >= ?) or " +
                    "(R." + FECHA_ENTRADA + " > ? and R." + FECHA_SALIDA + " < ?)) ";

    /** Consulta SQL para obtener la cuenta de habitaciones reservadas en un periodo de tiempo. */
    public static final String CUENTA_SOLAPAMIENTOS =
            "SELECT count(*) AS cuenta " +
                    "FROM " + TABLA_CANTIDAD + " C JOIN " + TABLA_RESERVAS + " R " +
                    "ON C." + TC_RESERVA + " = R." + ID +
                    " WHERE C." + TC_HABITACION + " = ? and " +
                    "((R." + FECHA_ENTRADA + " <= ? and R." + FECHA_SALIDA + " > ?) or " +
                    "(R." + FECHA_ENTRADA + " < ? and R." + FECHA_SALIDA + " >= ?) or " +
                    "(R." + FECHA_ENTRADA + " > ? and R." + FECHA_SALIDA + " < ?)) ";
    /**DESDE AQUI */

    /** Objeto de acceso a la base de datos. */
    private DatabaseHelper mDbHelper;

    /** Objeto para operar sobre la base de datos. */
    private SQLiteDatabase mDb;

    /** Contexto de la actividad en el que es creado este objeto. */
    private final Context mCtx;

    /**
     * Constructor - recibe un contexto para trabajar con la base de datos.
     *
     * @param ctx contexto de la actividad en el que es creado
     */
    public loginDb(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Abre la base de datos de la aplicación. Si no puede, intenta crear
     * una instancia de la base de datos. Si no puede, lanza una excepción
     * como señal de fracaso.
     *
     * @return this (referencia al objeto para encadenar órdenes)
     * @throws SQLException si no se ha podido abrir ni crear la base de datos
     */
    public loginDb open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Cierra la conexión con la base de datos.
     */
    public void close() {
        mDbHelper.close();
    }

    /**HASTA AQUI*/

    /**
     * Inserta un nuevo usuario en la base de datos con los atributos establecidos como parámetros.
     *
     * @param mail mail del usuario que se registra
     * @param nombre nombre del usuario que se registra
     * @param contraseña contraseña del usuario que se registra
     * @param verifContraseña repetición de la contraseña del usuario que se registra
     */
    public void signUp(String mail, String nombre, String contraseña,String verifContraseña) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_DB);

        //Primero se inserta la reserva
        ContentValues valoresReserva = new ContentValues();
        valoresReserva.put(NOMBRE_CLIENTE, nombreCliente);
        valoresReserva.put(MOVIL, movil);
        valoresReserva.put(FECHA_ENTRADA, dateFormat.format(fechaIN));
        valoresReserva.put(FECHA_SALIDA, dateFormat.format(fechaOUT));
        valoresReserva.put(PRECIO, 0.0);

        long idReserva;
        try {
            //La sentencia devuelve el id con autoincrement
            idReserva = mDb.insertOrThrow(TABLA_RESERVAS, ID, valoresReserva);
        } catch (SQLException e) {
            android.util.Log.e(TAG, "No se pudo insertar la reserva. Causa:",e);
            return;
        }

        //Después se insertan las habitaciones del listado
        for (int i = 0; i < habitaciones.size(); i++) {
            ContentValues valoresCantidad = new ContentValues();
            valoresCantidad.put(TC_RESERVA, idReserva);
            valoresCantidad.put(TC_HABITACION, habitaciones.identificador(i));
            valoresCantidad.put(TC_NUM_OCUPANTES, habitaciones.ocupante(i));

            try {
                mDb.insertOrThrow(TABLA_CANTIDAD, null, valoresCantidad);

            } catch (SQLException e) {
                android.util.Log.e(TAG,
                        "No se pudo insertar la habitación del listado. Causa:", e);
            }

        }

        //Por último fijamos el precio
        try {
            calcularPrecio(idReserva);
        } catch (SQLException e) {
            android.util.Log.e(TAG, "No se pudo calcular el precio. Causa:", e);
        }

    }

    /**
     * Calcula el precio de la reserva y lo actualiza en la base de datos
     *
     * @param idReserva id de la reserva
     * @return precio de la reserva
     * @throws SQLException en caso de que la reserva no exista
     */
    public Float calcularPrecio(Long idReserva) throws SQLException {
        Float precio = null;

        Cursor mCursor = mDb.rawQuery(CALCULAR_PRECIO, new String[]{idReserva.toString()});
        if (mCursor != null) {
            mCursor.moveToFirst();
            precio =  mCursor.getFloat(0);

            ContentValues args = new ContentValues();
            args.put(PRECIO, precio);

            if (mDb.update(TABLA_RESERVAS, args, ID + "=" + idReserva, null) == 0) {
                android.util.Log.e(TAG, "No se pudo actualizar la habitación");
            }
            mCursor.close();
        }

        return precio;
    }

    /**
     * Devuelve el listado ordenado de reservas.
     *
     * @param campo nombre del atributo a ordenar
     * @return cursor al listado de reservas
     */
    public Cursor consultarReservasOrdenadas(String campo) {
        return  mDb.rawQuery(CONSULTA_RESERVAS + campo + " ;", null);
    }

    /**
     * Devuelve el listado de las habitaciones reservadas.
     *
     * @param idReserva id de la reserva
     * @return listado de las habitaciones reservadas
     */
    public ListadoHabitaciones obtenerHabitacionesOcupadas(Long idReserva) {
        ListadoHabitaciones habitaciones = new ListadoHabitaciones();
        try {
            try {
                Cursor mCursor = mDb.query(TABLA_RESERVAS, null, //from...
                        TC_RESERVA + " = ?", new String[]{idReserva.toString()}, //where...
                        null, null, TC_HABITACION, null); //order by...

                //Iterar la consulta y rellenar el listado de las habitaciones
                while (mCursor.moveToNext()) {
                    String idHabitacion =
                            mCursor.getString(mCursor.getColumnIndexOrThrow(TC_HABITACION));

                    int numOcupantes =
                            mCursor.getInt(mCursor.getColumnIndexOrThrow(TC_NUM_OCUPANTES));

                    Cursor cursorHabitacion =
                            mDb.query(HabitacionDbAdapter.TABLA_HABITACIONES, null, //from...
                                    HabitacionDbAdapter.IDENTIFICADOR + " = ?",
                                    new String[]{idHabitacion}, //where...
                                    null, null, null, null);

                    cursorHabitacion.moveToFirst();
                    int maxOcupantes = cursorHabitacion.getInt(
                            cursorHabitacion.getColumnIndexOrThrow(
                                    HabitacionDbAdapter.MAXIMO_OCUPANTES));

                    habitaciones.add(idHabitacion, numOcupantes, maxOcupantes);

                    cursorHabitacion.close();
                }

                mCursor.close();

            } catch (SQLException e) {
                android.util.Log.e(TAG, "No se pudo obtener la habitación del listado. Causa:", e);
            }
        } catch (SQLException e) {
            android.util.Log.e(TAG, "No se pudo obtener el listado de habitaciones. Causa:", e);
        }

        return habitaciones;
    }

    /**
     * Actualiza los atributos de una reserva en la base de datos.
     *
     * @param id id de la reserva
     * @param nombreCliente nombre del cliente que reserva
     * @param movil móvil del cliente que reserva
     * @param fechaIN fecha de entrada de la reserva
     * @param fechaOUT fecha de salida de la reserva
     * @param habitaciones lista de habitaciones de la reserva
     */
    public void actualizarReserva(Long id, String nombreCliente, String movil, Date fechaIN,
                                  Date fechaOUT, ListadoHabitaciones habitaciones) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_DB);

        //Primero se actualiza la reserva
        ContentValues valoresReserva = new ContentValues();
        valoresReserva.put(NOMBRE_CLIENTE, nombreCliente);
        valoresReserva.put(MOVIL, movil);
        valoresReserva.put(FECHA_ENTRADA, dateFormat.format(fechaIN));
        valoresReserva.put(FECHA_SALIDA, dateFormat.format(fechaOUT));

        if (mDb.update(TABLA_RESERVAS, valoresReserva, ID + " = ? ",
                new String[]{id.toString()}) == 0) {

            android.util.Log.e(TAG, "No se pudo actualizar la habitación");
        }

        //Después se actualizan las habitaciones del listado

        //Elimino las habitaciones reservadas
        mDb.delete(TABLA_RESERVAS, ID + " = ? ", new String[]{id.toString()});

        //Y las vuelvo a crear con los datos actualizados
        for (int i = 0; i < habitaciones.size(); i++) {
            ContentValues valoresCantidad = new ContentValues();
            valoresCantidad.put(TC_RESERVA, id);
            valoresCantidad.put(TC_HABITACION, habitaciones.identificador(i));
            valoresCantidad.put(TC_NUM_OCUPANTES, habitaciones.ocupante(i));

            //Insertar la habitación en la reserva
            try {
                mDb.insertOrThrow(TABLA_CANTIDAD, null, valoresCantidad);

            } catch (SQLException e) {
                android.util.Log.e(TAG,
                        "No se pudo insertar la habitación del listado. Causa:", e);
            }
        }

        //Por último fijamos el precio
        try {
            calcularPrecio(id);
        } catch (SQLException e) {
            android.util.Log.e(TAG, "No se pudo calcular el precio. Causa:", e);
        }

    }

    /**
     * Elimina una reserva de la base de datos
     *
     * @param id id de la reserva
     */
    public void borrarReserva(Long id) {
        //Primero se eliminan las habitaciones reservadas
        mDb.delete(TABLA_RESERVAS, ID + " = ? ", new String[]{id.toString()});

        //Ahora se puede eliminar la reserva
        if (mDb.delete(TABLA_RESERVAS, ID + " = ? ",
                new String[]{id.toString()}) == 0) {

            android.util.Log.e(TAG, "No se pudo eliminar la reserva");
        }

    }

    /**
     * Comprueba si la reserva de una habitación coincide en fechas con
     * otra reserva de la misma habitación
     *
     * @param idReserva id de la reserva a comprobar
     * @param habitacion identificador de la habitación a comprobar
     * @param fechaIN fecha de entrada de la reserva
     * @param fechaOUT fecha de salida de la reserva
     * @return true si se han encontrado coincidencias
     */
    public boolean haySolapamiento(Long idReserva, String habitacion,
                                   Date fechaIN, Date fechaOUT) {
        boolean respuesta = false;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_DB);
        String fechaEntrada = dateFormat.format(fechaIN);
        String fechaSalida = dateFormat.format(fechaOUT);

        Cursor mCursor = mDb.rawQuery(CUENTA_SOLAPAMIENTOS_ID,
                new String[]{idReserva.toString(),
                        habitacion,
                        fechaEntrada, fechaEntrada,
                        fechaSalida, fechaSalida,
                        fechaEntrada, fechaSalida, });

        if (mCursor != null) {
            mCursor.moveToFirst();
            int solapamientos = mCursor.getInt(0);
            respuesta = solapamientos > 0;

            android.util.Log.d(TAG, "Solapamientos encontrados: " + solapamientos);
            mCursor.close();
        }

        return respuesta;
    }

    /**
     * Comprueba si la reserva de una habitación coincide en fechas con
     * otra reserva de la misma habitación
     *
     * @param habitacion identificador de la habitación a comprobar
     * @param fechaIN fecha de entrada de la reserva
     * @param fechaOUT fecha de salida de la reserva
     * @return true si se han encontrado coincidencias
     */
    public boolean haySolapamiento(String habitacion, Date fechaIN, Date fechaOUT) {
        boolean respuesta = false;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_DB);
        String fechaEntrada = dateFormat.format(fechaIN);
        String fechaSalida = dateFormat.format(fechaOUT);

        Cursor mCursor = mDb.rawQuery(CUENTA_SOLAPAMIENTOS,
                new String[]{habitacion,
                        fechaEntrada, fechaEntrada,
                        fechaSalida, fechaSalida,
                        fechaEntrada, fechaSalida, });

        if (mCursor != null) {
            mCursor.moveToFirst();
            int solapamientos = mCursor.getInt(0);
            respuesta = solapamientos > 0;

            android.util.Log.d(TAG, "Solapamientos encontrados: " + solapamientos);
            mCursor.close();
        }

        return respuesta;
    }
}
