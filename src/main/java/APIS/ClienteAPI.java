package APIS;

import DTO.ClienteDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ClienteAPI {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/clientes";

    public ClienteDTO getByNombre(String nombre) throws Exception {
        ClienteDTO cliente = restTemplate.getForObject(url + "?nombre=" + nombre, ClienteDTO.class);
        return cliente;
    }

    public List<ClienteDTO> getAll() throws Exception {
        ClienteDTO[] clientesArray = restTemplate.getForObject(url, ClienteDTO[].class);
        List<ClienteDTO> clientes = new ArrayList<>(Arrays.asList(clientesArray));
        return clientes;
    }

    public ResponseEntity<ClienteDTO> save(String nombre, String telefono, String direccion) throws Exception {
        ClienteDTO clienteNuevo = new ClienteDTO();

        clienteNuevo.nombre = nombre;
        clienteNuevo.telefono = telefono;
        clienteNuevo.direccion = direccion;

        HttpEntity<ClienteDTO> request = new HttpEntity<>(clienteNuevo);
        ResponseEntity<ClienteDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, ClienteDTO.class);
        return response;
    }

}
