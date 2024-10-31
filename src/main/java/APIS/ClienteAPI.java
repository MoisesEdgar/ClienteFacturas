package APIS;

import DTO.ClienteDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ClienteAPI {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/clientes";

    public ClienteDTO getByNombre(String nombre) throws Exception {
        try {
            ClienteDTO cliente = restTemplate.getForObject(url + "/nombre?nombre=" + nombre, ClienteDTO.class);
            return cliente;

        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }

    }

    public ClienteDTO getByCodigo(String codigo) throws Exception {
        try {
            ClienteDTO cliente = restTemplate.getForObject(url + "/codigo?codigo=" + codigo, ClienteDTO.class);
            return cliente;
            
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }

    }

    public ClienteDTO getById(Long id) throws Exception {
        ClienteDTO cliente = restTemplate.getForObject(url + "/" + id, ClienteDTO.class);
        return cliente;
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