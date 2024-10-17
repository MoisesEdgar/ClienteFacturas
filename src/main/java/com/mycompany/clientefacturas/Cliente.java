package com.mycompany.clientefacturas;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Cliente extends javax.swing.JFrame {

    RestTemplate restTemplate = new RestTemplate();

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
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String direccion = txtDireccion.getText();

        try {
            getClientes();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
        }

//        try {
//
//            if (validarTxt()) {
//                StringBuilder clientes = getClientes();
//                if (existenciaCliente(clientes, nombre)) {
//                    JOptionPane.showMessageDialog(this, "El nombre del cliente ya esta registrado");
//                } else {
//                    guardarCliente(nombre, telefono, direccion);
//                    this.dispose();
//                }
//                limpiarTxt();
//                txtNombre.requestFocus();
//            }
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "No se pudo conectar con el servidor. Verifique su conexión.");
//        }
    }

    public class ClientePojo implements Serializable {

        public Long id;
        public String codigo;
        public String nombre;
        public String telefono;
        public String direccion;
        
        public ClientePojo(Long id, String codigo, String nombre, String telefono, String direccion){
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
            this.telefono = telefono;
            this.direccion = direccion;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

    }

//    private void guardarCliente(String nombre, String telefono, String direccion) throws Exception {
//        String url = "http://localhost:8080/clientes";
//
//        JSONObject clienteJson = new JSONObject();
//        String ultimoCodigo = "";
//        JSONArray jsonArray = new JSONArray(getClientes().toString());
//        String codigo = "C-";
//
//        if (jsonArray.isEmpty()) {
//            codigo = "C-001";
//        } else {
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                ultimoCodigo = jsonObject.getString("codigo");
//            }
//
//            String[] salto = ultimoCodigo.split("-");
//
//            Integer cont = Integer.parseInt(salto[1]);
//            String ceros = "";
//
//            for (int i = String.valueOf(cont).length(); i < 3;) {
//                i++;
//                ceros = ceros + "0";
//            }
//            codigo = "C-" + ceros + (Integer.parseInt(salto[1]) + 1);
//        }
//        
//        
//        
//
//        HttpEntity<ClientePojo> request = new HttpEntity<>(new ClientePojo(null,codigo,nombre, telefono, direccion));
//
//        ResponseEntity<ClientePojo> response = restTemplate.exchange(url, HttpMethod.POST, request, ClientePojo.class);
//
//        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
//
//        ClientePojo cliente = response.getBody();
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            codigo = getCodigo(getClientes(), codigo);
//            JOptionPane.showMessageDialog(this, "Se agrego un nuevo cliente con el codigo: " + codigo);
//        }
//    }
//    
    
    
//        private StringBuilder getClientes() throws Exception {
//        String url = "http://localhost:8080/clientes";
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return new StringBuilder(response.getBody());
//        } else {
//            return null;
//        }
//    }
//        
        
    
    private void getClientes() throws Exception {
        String url = "http://localhost:8080/clientes";
        ClientePojo clienteD = restTemplate.getForObject(url, ClientePojo.class);
        
        Assertions.assertNotNull(clienteD.getNombre());
        Assertions.assertEquals(clienteD.getId(), 1L);
    }

    private String getCodigo(StringBuilder clientes, String codigo) {

        JSONArray jsonArray = new JSONArray(clientes.toString());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String buscarCodigo = jsonObject.getString("codigo");
            String[] salto = buscarCodigo.split("-");
            buscarCodigo = "C-" + salto[1];

            if (buscarCodigo.equalsIgnoreCase(codigo)) {
                return jsonObject.getString("codigo");
            }

        }
        return null;
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

    public String generarCodigo(String nombre, String telefono, String direccion) {
        String datos = nombre + telefono + direccion;

        return Integer.toHexString(datos.hashCode()).toUpperCase();
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

    private boolean validarTxt() {

        if (txtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se especifico el nombre del cliente");
            txtNombre.setText("");
            txtNombre.requestFocus();
            return false;
        }

        if (txtTelefono.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se especifico el telefono del cliente");
            txtTelefono.setText("");
            txtTelefono.requestFocus();
            return false;
        }

        if (txtTelefono.getText().length() < 10) {
            JOptionPane.showMessageDialog(this, "Numero de telefono no valido");
            txtTelefono.setText("");
            txtTelefono.requestFocus();
            return false;
        }

        if (txtDireccion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se espesifico la direccion");
            txtDireccion.setText("");
            txtDireccion.requestFocus();
            return false;
        }

        return true;
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
