package APIS;

import DTO.FacturaDTO;
import DTO.PartidaDTO;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class FacturaAPI {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:8080/facturas";

    public FacturaDTO getUltima() throws Exception {
        try {
            FacturaDTO factura = restTemplate.getForObject(url + "/ultima", FacturaDTO.class);
            return factura;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    public FacturaDTO getByFolio(String folio) throws Exception {
        try {
            FacturaDTO factura = restTemplate.getForObject(url + "/folio?folio=" + folio, FacturaDTO.class);
            return factura;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    public ResponseEntity<FacturaDTO> save(String folio, Integer id, List<PartidaDTO> partidas) throws Exception {
        FacturaDTO facturaNueva = new FacturaDTO();
        facturaNueva.folio = folio;
        facturaNueva.cliente_id = id;
        facturaNueva.partidas = partidas;

        HttpEntity<FacturaDTO> request = new HttpEntity<>(facturaNueva);
        ResponseEntity<FacturaDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, FacturaDTO.class);
        return response;
    }

    public void delete(Integer id) throws Exception {
        restTemplate.delete(url + "/" + id);
    }

    public ResponseEntity<FacturaDTO> update(FacturaDTO factura) throws Exception {
        HttpEntity<FacturaDTO> request = new HttpEntity<>(factura);
        ResponseEntity<FacturaDTO> response = restTemplate.exchange(url + "/" + factura.id, HttpMethod.PUT, request, FacturaDTO.class);
        return response;
    }

}
