package com.mycompany.clientefacturas;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class PantallaPrincipal extends javax.swing.JFrame {

    private final ModeloFactura modeloFacturas = new ModeloFactura();

    public PantallaPrincipal() {
        initComponents();

        modeloFacturas.addTableModelListener(this::onModeloFacturasModificado);

        tblFactura.setModel(modeloFacturas);
        tblFactura.getColumnModel().getColumn(2).setCellRenderer(new DecimalesRenderer());
        tblFactura.getColumnModel().getColumn(3).setCellRenderer(new DecimalesRenderer());

        btnGuardarFactura.addActionListener(this::onButonGuardarFacturaClicked);
        btnEliminarFactura.addActionListener(this::onButonEliminarFacturaClicked);

        btnAgregarPartida.addActionListener(this::onButonAgregarPartidaClicked);
        btnEliminarPartida.addActionListener(this::onButonEliminarPartidaClicked);

        btnLimpiar.addActionListener(this::onButonLimpiarClicked);

        txtFolio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                txtFolioKeyPressed(evt);

            }
        });

        txtCodigo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                txtCodigoKeyPressed(evt);
            }
        });

        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                txtNombreKeyTyped(evt);
            }
        });

        txtCantidad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                txtCantidadKeyTyped(evt);
            }
        });

        txtPrecio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                txtPrecioKeyTyped(evt);
            }
        });
    }

    //*****************************BOTONES*****************************
    private void onButonGuardarFacturaClicked(ActionEvent evt) {
        limpiarLblFactura();
        String folio = txtFolio.getText();
        String codigo = txtCodigo.getText();

        try {
            if (validarTxtFolio()) {
                if (validarTxtCodigo()) {
                    getIdFactura(getFacturas(), folio);

                    if (getIdFactura(getFacturas(), folio) == null) {
                        Integer idCliente = getIdClientes(getClientes(), codigo);
                        if (validarProducto("NOMBRE NO VALIDO")) {
                            postFactura(folio, idCliente, getPartidas());
                            limpiarTabla();
                            limpiarTxtsPartida();
                            limpiarTxtsFactura();
                            txtFolio.requestFocus();
                            JOptionPane.showMessageDialog(this, "Se agrego una nueva factura con el folio: " + folio);

                        } else {
                            JOptionPane.showMessageDialog(this, "Modificar los nombres no validos para poder guardar la factura");
                        }

                    } else {

                        limpiarPartidas();

                        putFactura(getIdFactura(getFacturas(), folio), getPartidas());
                        limpiarTabla();
                        limpiarTxtsPartida();
                        limpiarTxtsFactura();
                        JOptionPane.showMessageDialog(this, "Se modifico la factura con el folio: " + folio);
                    }

                }
            } else {
                limpiarTabla();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la factura. Verifique su conexion.");
        }
    }

    private void limpiarPartidas() {
        try {

            String folio = txtFolio.getText();
            Integer idFactura = getIdFactura(getFacturas(), folio);
            StringBuilder factura = getFactura(idFactura);

            JSONArray partidasCreadas = getPartidasCreadas(factura);
            List<Partida> partidasNuevas = getPartidas();

            for (int i = 0; i < partidasCreadas.length(); i++) {
                boolean des = false;
                JSONObject partidaid = partidasCreadas.getJSONObject(i);

                for (int j = 0; j < partidasNuevas.size(); j++) {
                    Partida partida = (Partida) partidasNuevas.get(j);

                    Integer idPartida = getIdPartida(getFactura(idFactura), partida.nombreArticulo);

                    if (idPartida != null) {
                        if (partidaid.getInt("id") == idPartida) {
                            des = true;
                        }
                    }

                }
                if (des == false) {
                    delPartida(partidaid.getInt("id"));
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectarse con el servidor. verifique seu conecxion");
        }
    }

    private JSONArray getPartidasCreadas(StringBuilder factura) {
        JSONObject jsonObject = new JSONObject(factura.toString());
        return jsonObject.getJSONArray("partidas");
    }

    private void onButonEliminarFacturaClicked(ActionEvent evt) {
        String folio = lblFolio.getText();
        if (folio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Especifique la factura a eliminar");
        } else {
            try {
                Integer id = getIdFactura(getFacturas(), folio);

                if (id != null) {
                    delFactura(id);
                    JOptionPane.showMessageDialog(this, "Se elimino la factura con el folio " + folio);
                    limpiarLblFactura();
                    limpiarTabla();
                    limpiarTxtsFactura();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontro el folio de la factura");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la factura. Verifique su conexión.");
            }
        }

    }

    private void onButonAgregarPartidaClicked(ActionEvent evt) {

        if (validarTxtPartida()) {
            String nombre = txtNombre.getText();
            Integer cantidad = Integer.parseInt(txtCantidad.getText());
            Double precio = Double.parseDouble(txtPrecio.getText());

            Partida partida = new Partida();
            partida.nombreArticulo = nombre;
            partida.cantidad = cantidad;
            partida.precio = precio;

            if (validarProducto(nombre)) {
                modeloFacturas.agregar(partida);
                limpiarTxtsPartida();
            } else {
                JOptionPane.showMessageDialog(this, "Ese articulo ya esta registrado");
                limpiarTxtsPartida();
            }
        }

    }

    private void onButonEliminarPartidaClicked(ActionEvent evt) {

        int index = tblFactura.getSelectedRow();
        if (index > -1) {

            Partida partida = modeloFacturas.getPartida(index);
            String nombre = partida.nombreArticulo;
            modeloFacturas.eliminar(index);
            JOptionPane.showMessageDialog(this, "Se elimino el producto: " + nombre);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione la partida a Eliminar");
        }

    }

    private void onModeloFacturasModificado(TableModelEvent evt) {
        int rowIndex = evt.getFirstRow();
        int colIndex = evt.getColumn();

        switch (evt.getType()) {
            case TableModelEvent.UPDATE:

                if (colIndex == 0) {
                    String nombreArticulo = (String) modeloFacturas.getValueAt(rowIndex, 0);
                    if (nombreArticulo.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "El campo nombre no puede estar vacio");
                        modeloFacturas.setValueAt("NOMBRE NO VALIDO", evt.getFirstRow(), 0);

                    } else {
                        for (int i = 0; i < modeloFacturas.getRowCount(); i++) {
                            if (nombreArticulo.equalsIgnoreCase("NOMBRE NO VALIDO") == false) {
                                if (nombreArticulo.equalsIgnoreCase((String) modeloFacturas.getValueAt(i, 0))) {
                                    if (i != rowIndex) {
                                        JOptionPane.showMessageDialog(this, "El articulos ya esta registrado");
                                        modeloFacturas.setValueAt("NOMBRE NO VALIDO", evt.getFirstRow(), 0);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }

                if (colIndex == 1) {
                    Partida partida = modeloFacturas.getPartida(rowIndex);
                    Integer cantidad = partida.cantidad;

                    if (cantidad == null) {
                        JOptionPane.showMessageDialog(this, "El campo cantidad no puede estar vacio");
                        modeloFacturas.setValueAt(1, evt.getFirstRow(), 1);
                    } else if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(this, "La cantidad deve ser mayor a 0");
                        modeloFacturas.setValueAt(1, evt.getFirstRow(), 1);
                    }
                }

                if (colIndex == 2) {
                    Partida partida = modeloFacturas.getPartida(rowIndex);
                    Double precio = partida.precio;

                    if (precio == null) {
                        JOptionPane.showMessageDialog(this, "El campo precio no puede estar vacio");
                        modeloFacturas.setValueAt(1.0, evt.getFirstRow(), 2);
                    } else if (precio < 0.1) {
                        JOptionPane.showMessageDialog(this, "El precio deve ser mayora a 0.1");
                        modeloFacturas.setValueAt(1.0, evt.getFirstRow(), 2);
                    }
                }

                break;

            //case TableModelEvent.INSERT:
            //case TableModelEvent.DELETE:
        }
        txtNombre.requestFocus();
        calcularTotales();
    }

    private void onButonLimpiarClicked(ActionEvent evt) {
        limpiarTxtsPartida();
        limpiarTxtsFactura();
        limpiarLblFactura();
        limpiarTabla();
        txtFolio.requestFocus();
    }

    //*****************************API*****************************
    private Integer getIdClientes(StringBuilder clientes, String codigo) {
        JSONArray jsonArray = new JSONArray(clientes.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (codigo.equalsIgnoreCase(jsonObject.getString("codigo"))) {
                return jsonObject.getInt("id");
            }
        }
        return null;
    }

    private String getCodigo(StringBuilder clientes, Integer id) {
        JSONArray jsonArray = new JSONArray(clientes.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (id == jsonObject.getInt("id")) {
                return jsonObject.getString("codigo");
            }
        }
        return null;
    }

    private StringBuilder getClientes() throws Exception {
        URL url = new URL("http://localhost:8080/clientes");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        conexion.connect();

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder jsonClientes = new StringBuilder();

        while (scanner.hasNext()) {
            jsonClientes.append(scanner.nextLine());
        }

        scanner.close();

        return jsonClientes;
    }

    private List<Partida> getPartidas() {

        List<Partida> partidas = new ArrayList<>();

        for (int i = 0; i < modeloFacturas.getRowCount(); i++) {
            Partida partida = new Partida();

            partida.nombreArticulo = (String) modeloFacturas.getValueAt(i, 0);
            partida.cantidad = (Integer) modeloFacturas.getValueAt(i, 1);
            partida.precio = (Double) modeloFacturas.getValueAt(i, 2);

            partidas.add(partida);
        }

        return partidas;
    }

    private Integer getIdFactura(StringBuilder facturas, String folio) {
        JSONArray jsonArray = new JSONArray(facturas.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (folio.equalsIgnoreCase(jsonObject.getString("folio"))) {
                return jsonObject.getInt("id");
            }
        }
        return null;
    }

    private StringBuilder getFacturas() throws Exception {
        URL url = new URL("http://localhost:8080/facturas");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        conexion.connect();

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder jsonFacturas = new StringBuilder();

        while (scanner.hasNext()) {
            jsonFacturas.append(scanner.nextLine());
        }

        scanner.close();

        return jsonFacturas;
    }

    private StringBuilder getFactura(Integer id) throws Exception {
        URL url = new URL("http://localhost:8080/facturas/" + id);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        conexion.connect();

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder jsonFacturas = new StringBuilder();

        while (scanner.hasNext()) {
            jsonFacturas.append(scanner.nextLine());
        }

        scanner.close();

        return jsonFacturas;
    }

    private void postFactura(String folio, Integer codigo, List partidas) throws Exception {
        URL url = new URL("http://localhost:8080/facturas");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/json; utf-8");
        conexion.setRequestProperty("Accept", "application/json");
        conexion.setDoOutput(true);

        JSONObject facturaJson = new JSONObject();
        facturaJson.put("folio", folio);
        facturaJson.put("cliente_id", codigo);

        JSONArray partidasJson = new JSONArray();

        for (int i = 0; i < partidas.size(); i++) {
            JSONObject partidaJson = new JSONObject();
            Partida partida = (Partida) partidas.get(i);

            String nombre = (String) partida.nombreArticulo;
            Integer cantidad = (Integer) partida.cantidad;
            Double precio = (Double) partida.precio;

            partidaJson.put("nombre_articulo", nombre);
            partidaJson.put("cantidad", cantidad);
            partidaJson.put("precio", precio);

            partidasJson.put(partidaJson);
        }

        facturaJson.put("partidas", partidasJson);

        try (OutputStream os = conexion.getOutputStream()) {
            byte[] input = facturaJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conexion.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Factura creada exitosamente");

        } else {
            System.out.println("Error al crear factura. Código de respuesta: " + responseCode);
        }
    }

    private void putFactura(Integer idFactura, List partidas) throws Exception {
        URL url = new URL("http://localhost:8080/facturas/" + idFactura);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("PUT");

        conexion.setRequestProperty("Content-Type", "application/json; utf-8");
        conexion.setRequestProperty("Accept", "application/json");
        conexion.setDoOutput(true);
        JSONObject facturaJson = new JSONObject();

        JSONArray partidasJson = new JSONArray();

        for (int i = 0; i < partidas.size(); i++) {
            JSONObject partidaJson = new JSONObject();
            Partida partida = (Partida) partidas.get(i);
            Integer id = 0;

            try {
                id = getIdPartida(getFactura(idFactura), partida.nombreArticulo);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al conectarse con el servidor. verifique seu conecxion");
            }

            partidaJson.put("id", id);
            partidaJson.put("nombre_articulo", (String) partida.nombreArticulo);
            partidaJson.put("cantidad", (Integer) partida.cantidad);
            partidaJson.put("precio", (Double) partida.precio);

            partidasJson.put(partidaJson);
        }

        facturaJson.put("partidas", partidasJson);

        try (OutputStream os = conexion.getOutputStream()) {
            byte[] input = facturaJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conexion.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {

            System.out.println("Factura actualizada exitosamente");

        } else {
            System.out.println("Error al actualizar factura. Código de respuesta: " + responseCode);

        }
    }

    private boolean delFactura(Integer id) throws Exception {
        URL url = new URL("http://localhost:8080/facturas/" + id);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("DELETE");
        conexion.connect();

        int responseCode = conexion.getResponseCode();
        conexion.disconnect();

        return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    }

    private void llenarTabla(StringBuilder factura) {

        JSONObject jsonObjectFacturas = new JSONObject(factura.toString());

        lblSubtotal.setText(String.valueOf(jsonObjectFacturas.getDouble("subtotal")));
        lblTotalIva.setText(String.valueOf(jsonObjectFacturas.getDouble("total")));

        lblFolio.setText(jsonObjectFacturas.getString("folio"));
        lblFecha.setText(jsonObjectFacturas.getString("fecha_expedicion"));

        try {
            String codigo = getCodigo(getClientes(), jsonObjectFacturas.getInt("cliente_id"));
            txtCodigo.setText(codigo);
        } catch (Exception e) {

        }

        JSONArray jsonPartidas = jsonObjectFacturas.getJSONArray("partidas");

        for (int j = 0; j < jsonPartidas.length(); j++) {
            JSONObject jsonObjectpPartidas = jsonPartidas.getJSONObject(j);

            Partida partida = new Partida();
            partida.nombreArticulo = jsonObjectpPartidas.getString("nombre_articulo");
            partida.cantidad = jsonObjectpPartidas.getInt("cantidad");
            partida.precio = jsonObjectpPartidas.getDouble("precio");

            modeloFacturas.agregar(partida);
        }

        modeloFacturas.fireTableDataChanged();
    }

    private Integer getIdPartida(StringBuilder factura, String nombre) {

        JSONObject jsonObject = new JSONObject(factura.toString());
        JSONArray jsonPartidas = jsonObject.getJSONArray("partidas");

        for (int j = 0; j < jsonPartidas.length(); j++) {
            JSONObject jsonObjectpPartidas = jsonPartidas.getJSONObject(j);

            if (nombre.equalsIgnoreCase(jsonObjectpPartidas.getString("nombre_articulo"))) {
                return jsonObjectpPartidas.getInt("id");
            }

        }
        return null;
    }

    private boolean delPartida(Integer id) throws Exception {
        URL url = new URL("http://localhost:8080/partidas/" + id);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("DELETE");
        conexion.connect();

        int responseCode = conexion.getResponseCode();
        conexion.disconnect();

        return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    }

    //*****************************VALIDACIONES*****************************
    private boolean validarProducto(String producto) {
        for (int i = 0; i < modeloFacturas.getRowCount(); i++) {
            if (producto.equalsIgnoreCase((String) modeloFacturas.getValueAt(i, 0))) {
                return false;
            }
        }
        return true;
    }

    private boolean validarTxtPartida() {

        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del articulo no fue especificado");
            txtNombre.requestFocus();
            return false;
        }

        if (txtCantidad.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cantidad no fue espesificada");
            txtCantidad.requestFocus();
            return false;
        }

        if (txtPrecio.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El precio no fue espesificado");
            txtPrecio.requestFocus();
            return false;
        }

        if (Integer.parseInt(txtCantidad.getText()) <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0");
            txtCantidad.requestFocus();
            return false;
        }

        if (Double.parseDouble(txtPrecio.getText()) < 0.1) {
            JOptionPane.showMessageDialog(this, "El precio debe ser mayor o igual a 0.1");
            txtPrecio.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarTxtFolio() {
        if (txtFolio.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se espesifico el folio de la factura");
            txtFolio.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validarTxtCodigo() {
        if (txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se espesifico el codigo del cliente");
            txtCodigo.requestFocus();
            return false;
        }

        return true;

    }

    private boolean validarFolio(String folio) {
        try {
            if (folio.matches("^F-\\d\\d\\d")) {

                String ultimoFolio = "";
                JSONArray jsonArray = new JSONArray(getFacturas().toString());

                if (jsonArray.isEmpty()) {
                    if (folio.equalsIgnoreCase("F-001")) {
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(this, "El formato del folio no es valido. La nuemracion debe ser: F-001");
                        txtFolio.setText("F-001");
                        return false;
                    }

                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ultimoFolio = jsonObject.getString("folio");
                    }

                    Integer ultimoNumeracion = getNumeracionFolio(ultimoFolio);
                    Integer nuevoNumero = getNumeracionFolio(folio);

                    if (nuevoNumero != ultimoNumeracion + 1) {
                        String con = String.valueOf(ultimoNumeracion + 1);
                        String ceros = "";

                        for (int i = con.length(); i < 3;) {
                            i++;
                            ceros = ceros + "0";
                        }

                        JOptionPane.showMessageDialog(this, "El formato del folio no es valido. La nuemracion debe ser F-" + ceros + (ultimoNumeracion + 1));
                        txtFolio.setText("F-" + ceros + (ultimoNumeracion + 1));
                        return false;
                    }

                    return true;
                }
            } else {
                JOptionPane.showMessageDialog(this, "El formato del folio no es valido. Debe seguir el formato de F-000");
                txtFolio.setText("");
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
        }

        return false;
    }

    private boolean validarCodigo(String codigo) {
        try {
            if (codigo.matches("^C-\\d\\d\\d")) {
                return true;

            } else {
                JOptionPane.showMessageDialog(this, "El formato del codigo no es valido. Debe seguir el formato de C-000");
                txtCodigo.setText("");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
        }

        return false;
    }

    private Integer getNumeracionFolio(String folio) {
        String[] salto = folio.split("-");
        return Integer.parseInt(salto[1]);
    }

    private void calcularTotales() {
        Double totalNeto = 0.0;
        Double totalIva = 0.0;

        for (int rowIndex = 0; rowIndex < modeloFacturas.getRowCount(); rowIndex++) {
            Double total = (Double) modeloFacturas.getValueAt(rowIndex, 3);
            totalNeto += total;
        }

        totalIva = Math.round((totalNeto + totalNeto * .16) * 100) / 100d;

        lblSubtotal.setText(String.valueOf(Math.round(totalNeto * 100) / 100d));
        lblTotalIva.setText(String.valueOf(Math.round(totalIva * 100) / 100d));
    }

    //*****************************MODELO*****************************
    private static class Partida {

        public String nombreArticulo;
        public Integer cantidad;
        public Double precio;
        public Double total;

    }

    private static class ModeloFactura extends AbstractTableModel {

        private final List<Partida> partidas = new ArrayList<>();

        @Override
        public int getRowCount() {
            return partidas.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Class<?> getColumnClass(int columIndex) {
            switch (columIndex) {
                case 0:
                    return String.class;
                case 1:
                    return Integer.class;
                case 2:
                    return Double.class;
                case 3:
                    return Double.class;
            }
            return null;
        }

        @Override
        public String getColumnName(int colum) {
            switch (colum) {
                case 0:
                    return "Nombre del articulo";
                case 1:
                    return "Cantidad";
                case 2:
                    return "Precio";
                case 3:
                    return "Total";
            }
            return null;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            Partida partida = partidas.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return partida.nombreArticulo;
                case 1:
                    return partida.cantidad;
                case 2:
                    return partida.precio;
                case 3:
                    return (partida.cantidad * partida.precio);

            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            Partida partida = partidas.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    partida.nombreArticulo = (String) aValue;
                    fireTableCellUpdated(rowIndex, columnIndex);
                    break;
                case 1:
                    partida.cantidad = (Integer) aValue;
                    fireTableCellUpdated(rowIndex, columnIndex);
                    fireTableCellUpdated(rowIndex, 3);
                    break;
                case 2:
                    partida.precio = (Double) aValue;
                    fireTableCellUpdated(rowIndex, columnIndex);
                    fireTableCellUpdated(rowIndex, 3);
                    break;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 3;
        }

        public void agregar(Partida partida) {
            partidas.add(partida);
            fireTableRowsInserted(getRowCount(), getRowCount());
        }

        public Partida getPartida(int rowIndex) {
            return partidas.get(rowIndex);
        }

        public void eliminar(int index) {
            partidas.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }

    private static class DecimalesRenderer extends DefaultTableCellRenderer {

        private final DecimalFormat formatter = new DecimalFormat("#.00");

        public DecimalesRenderer() {
            super.setHorizontalAlignment(RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setValue(formatter.format(value));
            return this;
        }

    }

//*****************************TEXTO*****************************
    private void txtFolioKeyPressed(KeyEvent evt) {
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            limpiarLblFactura();
            limpiarTabla();
            String folio = txtFolio.getText();

            if (validarTxtFolio()) {
                try {
                    Integer id = getIdFactura(getFacturas(), folio);
                    if (id != null) {
                        llenarTabla(getFactura(id));
                    } else {
                        if (validarFolio(folio)) {
                            txtCodigo.requestFocus();
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "No se pudo consultar la factura. Verifique su conexion.");
                }
            } else {
                limpiarTabla();
            }
        }
    }

    private void txtCodigoKeyPressed(KeyEvent evt) {

        if (txtFolio.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Se debe espesificar un folio");
            evt.consume();
            txtFolio.requestFocus();
        } else if (evt.getKeyChar() == KeyEvent.VK_ENTER) {

            if (validarTxtCodigo()) {
                try {
                    String codigo = txtCodigo.getText();

                    if (validarCodigo(codigo)) {
                        Integer idCliente = getIdClientes(getClientes(), codigo);

                        if (idCliente == null) {
                            JOptionPane.showMessageDialog(this, "No existe un cliente con ese codigo");
                            Cliente cliente = new Cliente();
                            cliente.setVisible(true);
                        } else {
                            txtNombre.requestFocus();
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexion.");
                }

            }
        }
    }

    private void txtNombreKeyTyped(KeyEvent evt) {
        char c = evt.getKeyChar();

        if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z'))) {
            evt.consume();
        }
        if (lblFolio.getText().isEmpty()) {
            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se debe espesificar un folio");
                evt.consume();
                txtFolio.requestFocus();
            } else if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se debe espesificar un codigo de cliente");
                evt.consume();
                txtCodigo.requestFocus();
            }
        }

    }

    private void txtCantidadKeyTyped(KeyEvent evt) {
        char c = evt.getKeyChar();

        if (c < '0' || c > '9') {
            evt.consume();
        }
        if (lblFolio.getText().isEmpty()) {
            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se debe espesificar un folio");
                evt.consume();
                txtFolio.requestFocus();
            } else if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se debe espesificar un codigo de cliente");
                evt.consume();
                txtCodigo.requestFocus();
            }
        }
    }

    private void txtPrecioKeyTyped(KeyEvent evt) {
        char c = evt.getKeyChar();

        if ((c < '0' || c > '9') && (c != '.')) {
            evt.consume();
        }
        if (lblFolio.getText().isEmpty()) {
            if (txtFolio.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se debe espesificar un folio");
                evt.consume();
                txtFolio.requestFocus();
            } else if (txtCodigo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Se debe espesificar un codigo de cliente");
                evt.consume();
                txtCodigo.requestFocus();
            }

        }

    }

    private void limpiarTxtsFactura() {
        txtFolio.setText("");
        txtCodigo.setText("");
    }

    private void limpiarTxtsPartida() {
        txtNombre.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        txtNombre.requestFocus();
    }

    private void limpiarLblFactura() {
        lblFecha.setText("");
        lblFolio.setText("");
        lblTotalIva.setText("");
        lblSubtotal.setText("");
    }

    private void limpiarTabla() {
        modeloFacturas.partidas.clear();
        modeloFacturas.fireTableDataChanged();
        txtFolio.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFolio = new javax.swing.JTextField();
        btnGuardarFactura = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        btnEliminarFactura = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        lblTotalIva = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFactura = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnEliminarPartida = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        btnAgregarPartida = new javax.swing.JButton();
        txtNombre = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblFolio = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();

        jPanel1.setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout jPanel4Layout1 = new java.awt.GridBagLayout();
        jPanel4Layout1.columnWidths = new int[] {0, 4, 0, 4, 0};
        jPanel4Layout1.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0};
        jPanel4.setLayout(jPanel4Layout1);

        jLabel1.setText("Folio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(txtFolio, gridBagConstraints);

        btnGuardarFactura.setText("Guardar Factura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 150, 0, 150);
        jPanel4.add(btnGuardarFactura, gridBagConstraints);

        jLabel2.setText("Codigo del cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel4.add(txtCodigo, gridBagConstraints);

        btnEliminarFactura.setText("Eliminar Factura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 150, 0, 150);
        jPanel4.add(btnEliminarFactura, gridBagConstraints);

        getContentPane().add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Subtotal:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 3, 0);
        jPanel5.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Total con IVA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanel5.add(jLabel5, gridBagConstraints);

        lblSubtotal.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel5.add(lblSubtotal, gridBagConstraints);

        lblTotalIva.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jPanel5.add(lblTotalIva, gridBagConstraints);

        getContentPane().add(jPanel5, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.BorderLayout());

        tblFactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblFactura);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout jPanel3Layout = new java.awt.GridBagLayout();
        jPanel3Layout.columnWidths = new int[] {0, 4, 0, 4, 0, 4, 0};
        jPanel3Layout.rowHeights = new int[] {0, 7, 0, 7, 0, 7, 0, 7, 0};
        jPanel3.setLayout(jPanel3Layout);

        btnEliminarPartida.setText("Eliminar Partida");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 147, 0, 160);
        jPanel3.add(btnEliminarPartida, gridBagConstraints);

        jLabel6.setText("Nombre del articulo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel3.add(jLabel6, gridBagConstraints);

        jLabel8.setText("Cantidad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel8, gridBagConstraints);

        jLabel9.setText("Precio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel3.add(jLabel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(txtCantidad, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(txtPrecio, gridBagConstraints);

        btnAgregarPartida.setText("Agregar Partida");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 147, 0, 160);
        jPanel3.add(btnAgregarPartida, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel3.add(txtNombre, gridBagConstraints);

        jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Folio de la factura:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel6.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel6.add(lblFolio, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Fecha de expedicion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel6.add(jLabel10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel6.add(lblFecha, gridBagConstraints);

        btnLimpiar.setText("Limpiar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 31;
        gridBagConstraints.insets = new java.awt.Insets(9, 200, 0, 10);
        jPanel6.add(btnLimpiar, gridBagConstraints);

        jPanel2.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PantallaPrincipal().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarPartida;
    private javax.swing.JButton btnEliminarFactura;
    private javax.swing.JButton btnEliminarPartida;
    private javax.swing.JButton btnGuardarFactura;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblFolio;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotalIva;
    private javax.swing.JTable tblFactura;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtFolio;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
