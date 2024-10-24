package APIS;

import DTO.ClienteDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ClienteAPI {

    RestTemplate restTemplate = new RestTemplate();

    public List<ClienteDTO> getClientes() throws Exception {
        String url = "http://localhost:8080/clientes";

        ClienteDTO[] clientesArray = restTemplate.getForObject(url, ClienteDTO[].class);
        List<ClienteDTO> clientes = Arrays.asList(clientesArray);

        return clientes;
    }

    public void guardarCliente(String nombre, String telefono, String direccion, String codigo) throws Exception {
        String url = "http://localhost:8080/clientes";

        ClienteDTO clienteNuevo = new ClienteDTO();
        clienteNuevo.codigo = codigo;
        clienteNuevo.nombre = nombre;
        clienteNuevo.telefono = telefono;
        clienteNuevo.direccion = direccion;

        HttpEntity<ClienteDTO> request = new HttpEntity<>(clienteNuevo);
        ResponseEntity<ClienteDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, ClienteDTO.class);
    }
    
}
