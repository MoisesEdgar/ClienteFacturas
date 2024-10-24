
package DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartidaDTO implements Serializable{
        public Long id;
        public String nombre_articulo;
        public Integer cantidad;
        public Double precio;
        public Long factura_id;
    
}
