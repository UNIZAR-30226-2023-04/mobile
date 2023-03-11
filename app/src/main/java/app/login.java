package app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class login {

    public static String ACCION_FORMULARIO = "accion_formulario";

    //public static final int CREAR = 0;
    //public static final int MODIFICAR = 1;

    private static final String TAG = "FormularioLogin";

    private int accion;

    private long Usuario;

    private String Contraseña;

    private EditText cuadroUsuario;

    private EditText cuadroContraseña;

//CREO QUE HASTA AQUÍ VA BIEN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        accion = intent.getIntExtra(ACCION_FORMULARIO, 0);

        /*ActionBar barraSuperior = getSupportActionBar();
        if (barraSuperior != null) {
            if (accion == CREAR) {
                barraSuperior.setTitle(R.string.formulario_crear_reserva);
            } else if (accion == MODIFICAR) {
                barraSuperior.setTitle(R.string.formulario_modificar_reserva);
            }
            barraSuperior.setDisplayHomeAsUpEnabled(true);
        }*/

        mDbHelper = new ReservaDbAdapter(this).open();

        toast = new Toast(this);

        cuadroFechaIn = findViewById(R.id.formulario_fecha_entrada);
        iniciarDatePicker(cuadroFechaIn);
        cuadroFechaOut = findViewById(R.id.formulario_fecha_salida);
        iniciarDatePicker(cuadroFechaOut);

        cuadroNombre = findViewById(R.id.formulario_nombre_cliente);
        cuadroMovil = findViewById(R.id.formulario_movil_cliente);

        View listadoVacio = findViewById(R.id.listadoHabitacionesVacio);
        listado = findViewById(R.id.formulario_listado_habitaciones);
        listado.setEmptyView(listadoVacio); //Si la lista está vacía se muestra un mensaje

        Button botonConfirmar = findViewById(R.id.formulario_reserva_confirmar);
        botonConfirmar.setOnClickListener(this::confirmar);

        Button botonAnyadir = findViewById(R.id.formulario_anyadir_habitacion);
        botonAnyadir.setOnClickListener(this::anyadirHabitacion);

        habitaciones = new ListadoHabitaciones();
        if (accion == MODIFICAR) {
            //Rellenar Formulario
            idReserva = intent.getLongExtra(ReservaDbAdapter.ID, -1);
            if (idReserva > -1) {
                habitaciones = mDbHelper.obtenerHabitacionesOcupadas(idReserva);
            }
            cuadroNombre.setText(intent.getStringExtra(ReservaDbAdapter.NOMBRE_CLIENTE));
            cuadroMovil.setText(intent.getStringExtra(ReservaDbAdapter.MOVIL));
            cuadroFechaIn.setText(intent.getStringExtra(ReservaDbAdapter.FECHA_ENTRADA));
            cuadroFechaOut.setText(intent.getStringExtra(ReservaDbAdapter.FECHA_SALIDA));
        }

        actualizarListado();
        registerForContextMenu(listado);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    private void confirmar(View view) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_FORMULARIO);

        nombre = cuadroNombre.getText().toString();
        movil = cuadroMovil.getText().toString();
        fechaIn = cuadroFechaIn.getText().toString();
        fechaOut = cuadroFechaOut.getText().toString();

        if (nombre.isEmpty() || movil.isEmpty() ||
                fechaIn.isEmpty() || fechaOut.isEmpty()) {
            toast.cancel();
            toast = Toast.makeText(this,
                    "Quedan campos por rellenar", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Date fechaEntrada = null;
        Date fechaSalida = null;

        try {
            fechaEntrada = dateFormat.parse(fechaIn);
            fechaSalida = dateFormat.parse(fechaOut);

            if (fechaEntrada.compareTo(fechaSalida) >= 0) {
                toast.cancel();
                toast = Toast.makeText(this,
                        "La fecha de entrada debe ser anterior a la de salida", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        } catch (ParseException e) {
            //No debería ocurrir, porque la entrada de las fechas es mediante un calendario
            android.util.Log.e(TAG, "Error extraño al convertir fechas");
        }

        if (habitaciones.ocupantesArray().isEmpty()) {
            toast.cancel();
            toast = Toast.makeText(this,
                    "Debes agregar habitaciones a la reserva", Toast.LENGTH_LONG);
            toast.show();

            return;
        }

        if (esValida(fechaEntrada, fechaSalida)) {
            if (accion == CREAR) {
                mDbHelper.crearReserva(nombre, movil, fechaEntrada,
                        fechaSalida, habitaciones);
            } else if (accion == MODIFICAR){
                mDbHelper.actualizarReserva(idReserva, nombre, movil,
                        fechaEntrada, fechaSalida, habitaciones);
            }
            finish();

        } else {
            toast.cancel();
            toast = Toast.makeText(this,
                    "Alguna habitación de la selección ya está reservada esos días",
                    Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private boolean esValida(Date fechaEntrada, Date fechaSalida) {
        if (accion == CREAR) {

            for (String habitacion : habitaciones.identificadoresArray()) {
                if (mDbHelper.haySolapamiento(habitacion, fechaEntrada, fechaSalida)) {
                    return false;
                }
            }

        } else if (accion == MODIFICAR) {

            for (String habitacion : habitaciones.identificadoresArray()) {
                if (mDbHelper.haySolapamiento(idReserva, habitacion,
                        fechaEntrada, fechaSalida)) {
                    return false;
                }
            }

        } else {
            return false;
        }

        return true;
    }

    private void actualizarListado() {
        ListadoHabitacionesAdapter adapter = new ListadoHabitacionesAdapter(this, habitaciones);
        listado.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(Menu.NONE, ELIMINAR_HABITACION, Menu.NONE, R.string.formulario_quitar_habitacion_listado);
        menu.add(Menu.NONE, MODIFICAR_CANTIDAD, Menu.NONE, R.string.formulario_modificar_habitacion_listado);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case ELIMINAR_HABITACION:
                //Quitar del listado y volver a mostrar
                habitaciones.delete(info.position);
                actualizarListado();
                return true;
            case MODIFICAR_CANTIDAD:
                //Desplegar cuadro de texto, actualizar y volver a mostrar
                Intent intent = new Intent(this, ModificarOcupantes.class);
                intent.putExtra(ModificarOcupantes.INDICE_MODIFICADO, info.position);
                intent.putExtra(ModificarOcupantes.NUMERO_DE_OCUPANTES,
                        habitaciones.ocupante(info.position));
                intent.putExtra(ModificarOcupantes.MAXIMO_DE_OCUPANTES,
                        habitaciones.maximoOcupantes(info.position));
                startActivityForResult(intent,MODIFICAR_CANTIDAD);

                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null) {
            switch (requestCode) {
                case MODIFICAR_CANTIDAD:
                    int indice = intent.getIntExtra(
                            ModificarOcupantes.INDICE_MODIFICADO,-1);
                    int numOcupantes = intent.getIntExtra(
                            ModificarOcupantes.NUMERO_DE_OCUPANTES,0);

                    if (indice > -1) {
                        habitaciones.set(indice, numOcupantes);
                    }
                    break;
                case INSERTAR_HABITACION:
                    String idHabitacion = intent.getStringExtra(
                            HabitacionDbAdapter.IDENTIFICADOR);
                    int maximo = intent.getIntExtra(
                            HabitacionDbAdapter.MAXIMO_OCUPANTES, 1);

                    habitaciones.add(idHabitacion,1,maximo);
                    break;
            }
            actualizarListado();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void iniciarDatePicker(EditText fecha) {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anyo, int mes, int dia) {
                calendario.set(Calendar.YEAR, anyo);
                calendario.set(Calendar.MONTH, mes);
                calendario.set(Calendar.DAY_OF_MONTH, dia);

                updateCalendar();
            }

            private void updateCalendar() {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_FORMULARIO);

                fecha.setText(dateFormat.format(calendario.getTime()));
            }
        };

        fecha.setOnClickListener(view ->
                new DatePickerDialog(FormularioReserva.this, date,
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Se ha pulsado la flecha de vuelta atrás
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void anyadirHabitacion(View view) {
        Intent intent = new Intent(this, SeleccionHabitaciones.class);

        intent.putExtra(SeleccionHabitaciones.ACCION_LISTADO,SeleccionHabitaciones.ANYADIR);
        startActivityForResult(intent, INSERTAR_HABITACION);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        nombre = cuadroNombre.getText().toString();
        movil = cuadroMovil.getText().toString();
        fechaIn = cuadroFechaIn.getText().toString();
        fechaOut = cuadroFechaOut.getText().toString();

        outState.putSerializable(ReservaDbAdapter.NOMBRE_CLIENTE, nombre);
        outState.putSerializable(ReservaDbAdapter.MOVIL, movil);
        outState.putSerializable(ReservaDbAdapter.FECHA_ENTRADA, fechaIn);
        outState.putSerializable(ReservaDbAdapter.FECHA_SALIDA, fechaOut);
        outState.putSerializable(ReservaDbAdapter.TABLA_CANTIDAD, habitaciones);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle outState) {
        nombre = (String) outState.getSerializable(ReservaDbAdapter.NOMBRE_CLIENTE);
        movil = (String) outState.getSerializable(ReservaDbAdapter.MOVIL);
        fechaIn = (String) outState.getSerializable(ReservaDbAdapter.FECHA_ENTRADA);
        fechaOut = (String) outState.getSerializable(ReservaDbAdapter.FECHA_SALIDA);
        habitaciones = (ListadoHabitaciones)
                outState.getSerializable(ReservaDbAdapter.TABLA_CANTIDAD);

        cuadroNombre.setText(nombre);
        cuadroMovil.setText(movil);
        cuadroFechaIn.setText(fechaIn);
        cuadroFechaOut.setText(fechaOut);
        actualizarListado();
    }

}
