package com.mycompany.clientefacturas;

import java.awt.event.ActionEvent;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;

public class Cliente extends javax.swing.JFrame {

    public Cliente() {
        initComponents();

        btnAgregar.addActionListener(this::onButonAgregarClicked);

        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreKeyTyped(evt);
            }
        });
        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoKeyTyped(evt);
            }
        });

    }

    private void onButonAgregarClicked(ActionEvent evt) {
        String nombre = getNombre();
        String telefono = getTelefono();
        String direccion = getDireccion();

        try {
            if (validarTxt()) {
                StringBuilder clientes = getClientes();

                if (existenciaCliente(clientes, nombre)) {
                    JOptionPane.showMessageDialog(this, "El nombre del cliente ya esta registrado");
                } else {
                    postCliente(nombre, telefono ,direccion);
                    JOptionPane.showMessageDialog(this, "Se agrego un nuevo cliente");
                        this.dispose();
                    
                }
                limpiarTxt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean existenciaCliente(StringBuilder clientes, String nombre) {
        JSONArray jsonArray = new JSONArray(clientes.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (nombre.equalsIgnoreCase(jsonObject.getString("nombre"))) {
                return true;
            }
        }
        return false;
    }

    private boolean postCliente(String nombre, String telefono, String direccion) throws Exception {
        URL url = new URL("http://localhost:8080/clientes");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();

        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "application/json; utf-8");
        conexion.setRequestProperty("Accept", "application/json");
        conexion.setDoOutput(true);
        
         JSONObject clienteJson = new JSONObject();
        clienteJson.put("codigo", "1234");
        clienteJson.put("nombre", nombre);
        clienteJson.put("telefono", telefono);
        clienteJson.put("direccion", direccion);

        
 
        try (OutputStream os = conexion.getOutputStream()) {
            byte[] input = clienteJson.toString().getBytes("utf-8");
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

            System.out.println("Factura creada exitosamente: " + respuesta.toString());
            return true;
        } else {
            System.out.println("Error al crear factura. Código de respuesta: " + responseCode);
            return false;
        }
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

    private String getNombre() {
        if (txtNombre.getText().isEmpty()) {
            return "";
        } else {
            return txtNombre.getText();
        }
    }

    private String getTelefono() {
        if (txtTelefono.getText().isEmpty()) {
            return "";
        } else {
            return txtTelefono.getText();
        }
    }

    private String getDireccion() {
        if (txtDireccion.getText().isEmpty()) {
            return "";
        } else {
            return txtDireccion.getText();
        }
    }

    private boolean validarTxt() {

        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se especifico el nombre del cliente");
            return false;
        }

        if (txtTelefono.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se especifico el telefono del cliente");
            return false;
        }

        if (txtDireccion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NO se espesifico la direccion");
            return false;
        }

        return true;
    }

    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z')) && (c != ' ')) {
            evt.consume();
        }
    }

    private void txtTelefonoKeyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        if (c < '0' || c > '9') {
            evt.consume();
        }
    }

    private void limpiarTxt() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel4 = new javax.swing.JPanel();
        txtTelefono = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 86;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 0, 0);
        jPanel4.add(txtTelefono, gridBagConstraints);

        btnAgregar.setText("Agregar");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 35;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 38, 0);
        jPanel4.add(btnAgregar, gridBagConstraints);

        jLabel2.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(27, 36, 0, 0);
        jPanel4.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Telefono:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 36, 0, 0);
        jPanel4.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Direccion:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 36, 0, 0);
        jPanel4.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 170;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 0, 0);
        jPanel4.add(txtDireccion, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 160;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 4, 0, 30);
        jPanel4.add(txtNombre, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Datos del Cliente");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(25, 4, 0, 0);
        jPanel4.add(jLabel1, gridBagConstraints);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cliente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
