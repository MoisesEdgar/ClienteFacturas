package APIS;

import DTO.ClienteDTO;
import DTO.FacturaDTO;
import DTO.PartidaDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FacturaAPI {

    RestTemplate restTemplate = new RestTemplate();

    public List<ClienteDTO> getClientes() throws Exception {
        String url = "http://localhost:8080/clientes";

        ClienteDTO[] clientesArray = restTemplate.getForObject(url, ClienteDTO[].class);
        List<ClienteDTO> clientes = Arrays.asList(clientesArray);

        return clientes;
    }

    public List<FacturaDTO> getFacturas() throws Exception {
        String url = "http://localhost:8080/facturas";

        FacturaDTO[] facturasArray = restTemplate.getForObject(url, FacturaDTO[].class);
        List<FacturaDTO> facturas = Arrays.asList(facturasArray);

        return facturas;
    }

    public FacturaDTO getFactura(Integer id) throws Exception {
        String url = "http://localhost:8080/facturas/" + id;

        FacturaDTO factura = restTemplate.getForObject(url, FacturaDTO.class);

        return factura;
    }

    public void guardarFactura(String folio, Integer id, List<PartidaDTO> partidas) throws Exception {
        String url = "http://localhost:8080/facturas";

        FacturaDTO facturaNueva = new FacturaDTO();
        facturaNueva.folio = folio;
        facturaNueva.cliente_id = id;
        facturaNueva.partidas = partidas;

        HttpEntity<FacturaDTO> request = new HttpEntity<>(facturaNueva);
        ResponseEntity<FacturaDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, FacturaDTO.class);
    }

    public void eliminarFactura(Integer id) throws Exception {
        String url = "http://localhost:8080/facturas/" + id;
        restTemplate.delete(url);
    }

    public void actualizarFactura(FacturaDTO factura) throws Exception {
        String url = "http://localhost:8080/facturas/" + factura.id;

        HttpEntity<FacturaDTO> request = new HttpEntity<>(factura);
        ResponseEntity<FacturaDTO> response = restTemplate.exchange(url, HttpMethod.PUT, request, FacturaDTO.class);

    }
}
