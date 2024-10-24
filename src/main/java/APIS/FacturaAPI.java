package APIS;

import DTO.FacturaDTO;
import DTO.PartidaDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class FacturaAPI {

    private  RestTemplate restTemplate = new RestTemplate();
    private String url = "http://localhost:8080/facturas";

    public List<FacturaDTO> getAll() throws Exception {
        FacturaDTO[] facturasArray = restTemplate.getForObject(url, FacturaDTO[].class);
        List<FacturaDTO> facturas = Arrays.asList(facturasArray);
        
        return facturas;
    }

    public FacturaDTO getById(Integer id) throws Exception {
        FacturaDTO factura = restTemplate.getForObject(url + "/" + id, FacturaDTO.class);
        
        return factura;
    }

    public void save(String folio, Integer id, List<PartidaDTO> partidas) throws Exception {
        FacturaDTO facturaNueva = new FacturaDTO();
        facturaNueva.folio = folio;
        facturaNueva.cliente_id = id;
        facturaNueva.partidas = partidas;

        HttpEntity<FacturaDTO> request = new HttpEntity<>(facturaNueva);
        ResponseEntity<FacturaDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, FacturaDTO.class);
    }

    public void delete(Integer id) throws Exception {
        restTemplate.delete(url + "/" + id);
    }

    public void update(FacturaDTO factura) throws Exception {
        HttpEntity<FacturaDTO> request = new HttpEntity<>(factura);
        ResponseEntity<FacturaDTO> response = restTemplate.exchange(url + "/" + factura.id, HttpMethod.PUT, request, FacturaDTO.class);
    }
}
