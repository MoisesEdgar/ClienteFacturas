package com.mycompany.clientefacturas;

import java.awt.Component;
import java.awt.event.ActionEvent;
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

        btnConsultar.addActionListener(this::onButonConsultarClicked);
        btnCrearFactura.addActionListener(this::onButonCrearFacturaClicked);
        btnEliminarFactura.addActionListener(this::onButonEliminarFacturaClicked);

        btnAgregarPartida.addActionListener(this::onButonAgregarPartidaClicked);
        btnEliminarPartida.addActionListener(this::onButonEliminarPartidaClicked);

        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreKeyTyped(evt);
            }
        });
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadKeyTyped(evt);
            }
        });
        txtPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPrecioKeyTyped(evt);
            }
        });

    }

    //*****************************BOTONES*****************************
    private void onButonConsultarClicked(ActionEvent evt) {
        limpiarLblFactura();
        limpiarTabla();
        String folio = getFolio();

        if (validarTxtFolio()) {
            try {
                Integer id = getIdFactura(peticionGetFacturas(), folio);

                if (id != null) {
                    parsearJson(peticionGetFactura(id));
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontro el folio de la factura");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
            }
        }

        limpiarTxtsFactura();
    }

    private void onButonCrearFacturaClicked(ActionEvent evt) {
        limpiarLblFactura();
        limpiarTabla();
        String folio = getFolio();
        String codigo = getCodigo();
        String nombre = getNombre();
        Integer cantidad = getCantidad();
        Double precio = getPrecio();

        try {
            if (validarTxtFolio()) {
                Integer idFactura = getIdFactura(peticionGetFacturas(), folio);

                if (idFactura == null) {
                    if (validarTxtCodigo()) {
                        if (validarTxtPartida()) {
                            Integer idCliente = getIdClientes(peticionGetClientes(), codigo);

                            if (idCliente != null) {
                                peticionPostFactura(folio, codigo, nombre, cantidad, precio);
                                idFactura = getIdFactura(peticionGetFacturas(), folio);
                                folio = "F" + String.valueOf(idFactura);
                                peticionPutFolio(idFactura, folio);
                                JOptionPane.showMessageDialog(this, "Se agrego un nuevo cliente con el codigo: " + folio);

                                limpiarTxtsFactura();
                                limpiarTxtsPartida();
                                JOptionPane.showMessageDialog(this, "Se creo una nueva factura");

                                parsearJson(peticionGetFactura(idFactura));
                            } else {
                                int eleccion = JOptionPane.showConfirmDialog(null, "No existe un cliente con ese codigo ¿Desea agregar un nuevo cliente?", "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);

                                if (eleccion == JOptionPane.YES_OPTION) {
                                    Cliente cliente = new Cliente();
                                    cliente.setVisible(true);

                                } else if (eleccion == JOptionPane.NO_OPTION) {
                                    limpiarTxtsFactura();
                                }

                            }
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "La Factura con ese folio ya existe");
                    parsearJson(peticionGetFactura(idFactura));
                }

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
        }
    }

    private void onButonEliminarFacturaClicked(ActionEvent evt) {
        limpiarLblFactura();
        limpiarTabla();
        String folio = getFolio();

        if (validarTxtFolio()) {
            try {
                Integer id = getIdFactura(peticionGetFacturas(), folio);

                if (id != null) {
                    peticionDelFactura(id);
                    JOptionPane.showMessageDialog(this, "Se elimino la factura con el folio " + folio);

                    limpiarTxtsFactura();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontro el folio de la factura");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
            }

        }
    }

    private void onButonAgregarPartidaClicked(ActionEvent evt) {

        if (validarTxtPartida()) {
            String nombre = getNombre();
            Integer cantidad = getCantidad();
            Double precio = getPrecio();

            try {

                if (lblFolio.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe consultar o crear una factura para poder agregar partidas");
                } else {
                    String folio = lblFolio.getText();
                    Integer idFactura = getIdFactura(peticionGetFacturas(), folio);
                    Integer idPartida = getIdPartida(peticionGetFactura(idFactura), nombre);

                    if (idPartida == null) {

                        Partida partida = new Partida();
                        partida.nombreArticulo = nombre;
                        partida.cantidad = cantidad;
                        partida.precio = precio;

                        modeloFacturas.agregar(partida);

                        idFactura = getIdFactura(peticionGetFacturas(), folio);
                        idPartida = 0;
                        peticionPutFactura(idFactura, idPartida, nombre, cantidad, precio);

                        limpiarTxtsPartida();

                    } else {
                        JOptionPane.showMessageDialog(this, "El articulo ya esta ingresado");
                    }

                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
            }

        }

    }

    private void onButonEliminarPartidaClicked(ActionEvent evt) {

        int index = tblFactura.getSelectedRow();
        if (index > -1) {
            Partida partida = modeloFacturas.getPartida(index);
            String nombre = partida.nombreArticulo;

            try {

                Integer id = getIdFactura(peticionGetFacturas(), lblFolio.getText());
                peticionDelPartida(getIdPartida(peticionGetFactura(id), nombre));
                modeloFacturas.eliminar(index);
                JOptionPane.showMessageDialog(this, "Se elimino el producto: " + nombre);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione la partida a Eliminar");
        }
    }

    private void onModeloFacturasModificado(TableModelEvent evt) {

        int rowIndex = evt.getFirstRow();
        int colIndex = evt.getColumn();
        boolean valido = false;

        switch (evt.getType()) {
            case TableModelEvent.UPDATE:
               try {
                String folio = lblFolio.getText();
                Integer idFactura = getIdFactura(peticionGetFacturas(), folio);
                String nombreAnterior = getNommbreAnterior(peticionGetFactura(idFactura), rowIndex);

                if (colIndex == 0) {
                    String nombreArticulo = (String) modeloFacturas.getValueAt(rowIndex, 0);
                    if (nombreArticulo.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "El campo nombre no puede estar vacio");
                    } else {

                        if (validarExistenciaPartida(peticionGetFacturas(), nombreArticulo)) {
                            JOptionPane.showMessageDialog(this, "El articulo ya esta registrado");
                        } else {
                            valido = true;
                        }
                    }

                }

                if (colIndex == 1) {
                    Partida partida = modeloFacturas.getPartida(rowIndex);
                    Integer cantidad = partida.cantidad;

                    if (cantidad <= 0) {
                        JOptionPane.showMessageDialog(this, "La cantidad deve ser mayor a cero");
                    } else {
                        valido = true;
                    }

                }

                if (colIndex == 2) {
                    Partida partida = modeloFacturas.getPartida(rowIndex);
                    Double precio = partida.precio;

                    if (precio < 0.1) {
                        JOptionPane.showMessageDialog(this, "El precio deve ser mayora a 0.1");
                    } else {
                        valido = true;
                    }

                }

                if (valido) {
                    String nombre = (String) modeloFacturas.getValueAt(rowIndex, 0);
                    Integer cantidad = (Integer) modeloFacturas.getValueAt(rowIndex, 1);
                    Double precio = (Double) modeloFacturas.getValueAt(rowIndex, 2);

                    Integer idPartida = getIdPartida(peticionGetFactura(idFactura), nombreAnterior);
                    peticionPutFactura(idFactura, idPartida, nombre, cantidad, precio);
                }

                if (colIndex == 0 || colIndex == 1 || colIndex == 2) {
                    limpiarTabla();
                    parsearJson(peticionGetFactura(idFactura));
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
            }

            break;

            //case TableModelEvent.INSERT:
            //case TableModelEvent.DELETE:
        }
        calcularTotales();
    }

    //*****************************API*****************************
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

    private String getNommbreAnterior(StringBuilder factura, Integer index) {
        JSONObject jsonObject = new JSONObject(factura.toString());
        JSONArray jsonPartidas = jsonObject.getJSONArray("partidas");

        JSONObject jsonObjectpPartidas = jsonPartidas.getJSONObject(index);

        return jsonObjectpPartidas.getString("nombre_articulo");

    }

    private StringBuilder peticionGetFacturas() throws Exception {
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

    private StringBuilder peticionGetFactura(Integer id) throws Exception {
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

    private StringBuilder peticionGetClientes() throws Exception {
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

    private boolean peticionDelPartida(Integer id) throws Exception {
        URL url = new URL("http://localhost:8080/partidas/" + id);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("DELETE");
        conexion.connect();

        int responseCode = conexion.getResponseCode();
        conexion.disconnect();

        return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    }

    private boolean peticionDelFactura(Integer id) throws Exception {
        URL url = new URL("http://localhost:8080/facturas/" + id);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("DELETE");
        conexion.connect();

        int responseCode = conexion.getResponseCode();
        conexion.disconnect();

        return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    }

    private void peticionPutFactura(Integer idFactura, Integer idPartida, String nombre, Integer cantidad, Double precio) throws Exception {
        URL url = new URL("http://localhost:8080/facturas/" + idFactura);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("PUT");

        conexion.setRequestProperty("Content-Type", "application/json; utf-8");
        conexion.setRequestProperty("Accept", "application/json");
        conexion.setDoOutput(true);

        JSONObject facturaJson = new JSONObject();
        JSONArray partidasJson = new JSONArray();

        JSONObject partidaJson = new JSONObject();

        partidaJson.put("id", idPartida);
        partidaJson.put("nombre_articulo", nombre);
        partidaJson.put("cantidad", cantidad);
        partidaJson.put("precio", precio);
        partidasJson.put(partidaJson);

        facturaJson.put("partidas", partidasJson);

        try (OutputStream os = conexion.getOutputStream()) {
            byte[] input = facturaJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conexion.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {

            Scanner scanner = new Scanner(conexion.getInputStream());
            StringBuilder respuesta = new StringBuilder();
            while (scanner.hasNext()) {
                respuesta.append(scanner.nextLine());
            }
            scanner.close();

            System.out.println("Factura actualizada exitosamente: " + respuesta.toString());

        } else {
            System.out.println("Error al actualizar factura. Código de respuesta: " + responseCode);

        }
    }

    private void peticionPutFolio(Integer idFactura, String folio) throws Exception {
        URL url = new URL("http://localhost:8080/facturas/" + idFactura);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("PUT");

        conexion.setRequestProperty("Content-Type", "application/json; utf-8");
        conexion.setRequestProperty("Accept", "application/json");
        conexion.setDoOutput(true);

        JSONObject facturaJson = new JSONObject();
        facturaJson.put("folio", folio);

        try (OutputStream os = conexion.getOutputStream()) {
            byte[] input = facturaJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conexion.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {

            Scanner scanner = new Scanner(conexion.getInputStream());
            StringBuilder respuesta = new StringBuilder();
            while (scanner.hasNext()) {
                respuesta.append(scanner.nextLine());
            }
            scanner.close();

            System.out.println("Folio de la factura actualizada exitosamente: " + respuesta.toString());

        } else {
            System.out.println("Error al actualizar el folio de la factura. Código de respuesta: " + responseCode);

        }
    }

    private void peticionPutTotales(Integer idFactura, Double subtotal, Double total) throws Exception {
        URL url = new URL("http://localhost:8080/facturas/" + idFactura);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("PUT");

        conexion.setRequestProperty("Content-Type", "application/json; utf-8");
        conexion.setRequestProperty("Accept", "application/json");
        conexion.setDoOutput(true);

        JSONObject facturaJson = new JSONObject();
        facturaJson.put("subtotal", subtotal);
        facturaJson.put("total", total);

        try (OutputStream os = conexion.getOutputStream()) {
            byte[] input = facturaJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conexion.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {

            Scanner scanner = new Scanner(conexion.getInputStream());
            StringBuilder respuesta = new StringBuilder();
            while (scanner.hasNext()) {
                respuesta.append(scanner.nextLine());
            }
            scanner.close();

            System.out.println("Folio de la factura actualizada exitosamente: " + respuesta.toString());

        } else {
            System.out.println("Error al actualizar el folio de la factura. Código de respuesta: " + responseCode);

        }
    }

    private boolean peticionPostFactura(String folio, String codigo, String nombre, Integer cantidad, Double precio) throws Exception {
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

        JSONObject partidaJson = new JSONObject();
        partidaJson.put("nombre_articulo", nombre);
        partidaJson.put("cantidad", cantidad);
        partidaJson.put("precio", precio);
        partidasJson.put(partidaJson);

        facturaJson.put("partidas", partidasJson);

        try (OutputStream os = conexion.getOutputStream()) {
            byte[] input = facturaJson.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conexion.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {

            Scanner scanner = new Scanner(conexion.getInputStream());
            StringBuilder respuesta = new StringBuilder();
            while (scanner.hasNext()) {
                respuesta.append(scanner.nextLine());
            }
            scanner.close();

            System.out.println("Factura creada exitosamente: " + respuesta.toString());
            return true;
        } else {
            System.out.println("Error al crear factura. Código de respuesta: " + responseCode);
            return false;
        }
    }

    private void parsearJson(StringBuilder factura) {

        JSONObject jsonObjectFacturas = new JSONObject(factura.toString());

        lblSubtotal.setText(String.valueOf(jsonObjectFacturas.getDouble("subtotal")));
        lblTotalIva.setText(String.valueOf(jsonObjectFacturas.getDouble("total")));

        lblFolio.setText(jsonObjectFacturas.getString("folio"));
        lblFecha.setText(jsonObjectFacturas.getString("fecha_expedicion"));
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

    //*****************************VARIABLES*****************************
    private String getFolio() {
        if (txtFolio.getText().isEmpty()) {
            return "";
        } else {
            return txtFolio.getText();
        }
    }

    private String getCodigo() {
        if (txtCodigo.getText().isEmpty()) {
            return "";
        } else {
            return txtCodigo.getText();
        }
    }

    private String getNombre() {
        if (txtNombre.getText().isEmpty()) {
            return "";
        } else {
            return txtNombre.getText();
        }
    }

    private Integer getCantidad() {
        if (txtCantidad.getText().isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(txtCantidad.getText());
        }
    }

    private Double getPrecio() {
        if (txtPrecio.getText().isEmpty()) {
            return 0.0;
        } else {
            return Double.parseDouble(txtPrecio.getText());
        }
    }

    //*****************************VALIDACIONES*****************************
    private boolean validarExistenciaPartida(StringBuilder facturas, String nombre) {
        JSONArray jsonArray = new JSONArray(facturas.toString());
        String folio = lblFolio.getText();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (folio.equalsIgnoreCase(jsonObject.getString("folio"))) {
                JSONArray jsonPartidas = jsonObject.getJSONArray("partidas");
                for (int j = 0; j < jsonPartidas.length(); j++) {
                    JSONObject jsonObjectpPartidas = jsonPartidas.getJSONObject(j);

                    if (nombre.equalsIgnoreCase(jsonObjectpPartidas.getString("nombre_articulo"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validarTxtPartida() {

        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre del articulo no fue especificado");
            return false;
        }

        if (txtCantidad.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cantidad no fue espesificada");
            return false;
        }

        if (txtPrecio.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El precio no fue espesificado");
            return false;
        }

        if (getCantidad() <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0");
            return false;
        }

        if (getPrecio() < 0.1) {
            JOptionPane.showMessageDialog(this, "El precio debe ser mayor o igual a 0.1");
            return false;
        }

        return true;
    }

    private boolean validarTxtFolio() {
        if (txtFolio.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se espesifico el folio de la factura");
            return false;
        }

        return true;
    }

    private boolean validarTxtCodigo() {
        if (txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se espesifico el codigo del cliente");
            return false;
        }

        return true;

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
        lblTotalIva.setText(String.valueOf(totalIva));

        try {
            String folio = lblFolio.getText();
            Integer id = getIdFactura(peticionGetFacturas(), folio);
            peticionPutTotales(id, totalNeto, totalIva);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z'))) {
            evt.consume();
        }
    }

    private void txtCantidadKeyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (c < '0' || c > '9') {
            evt.consume();
        }
    }

    private void txtPrecioKeyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9') && (c != '.')) {
            evt.consume();
        }
    }

    private void limpiarTxtsFactura() {
        txtFolio.setText("");
        txtCodigo.setText("");
        txtFolio.requestFocus();
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
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFolio = new javax.swing.JTextField();
        btnConsultar = new javax.swing.JButton();
        btnCrearFactura = new javax.swing.JButton();
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
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add(txtFolio, gridBagConstraints);

        btnConsultar.setText("Consultar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 150, 0, 150);
        jPanel4.add(btnConsultar, gridBagConstraints);

        btnCrearFactura.setText("Crear factura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 150, 0, 150);
        jPanel4.add(btnCrearFactura, gridBagConstraints);

        jLabel2.setText("Codigo del cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 46;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(txtCodigo, gridBagConstraints);

        btnEliminarFactura.setText("Elmiminar Factura");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
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

        jLabel5.setText("Total con IVA");
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
        jPanel6.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 450);
        jPanel6.add(lblFolio, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Fecha de expedicion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel6.add(jLabel10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 450);
        jPanel6.add(lblFecha, gridBagConstraints);

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
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnCrearFactura;
    private javax.swing.JButton btnEliminarFactura;
    private javax.swing.JButton btnEliminarPartida;
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
