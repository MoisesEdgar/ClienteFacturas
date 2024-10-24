package DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacturaDTO implements Serializable{

    public Long id;
    public String folio;
    public Date fecha_expedicion;
    public Double subtotal;
    public Double total;
    public Integer cliente_id;
    public List<PartidaDTO> partidas;

}
