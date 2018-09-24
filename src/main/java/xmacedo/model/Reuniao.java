package xmacedo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import xmacedo.enums.EStatusReuniao;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "Reuniao")
public class Reuniao implements Serializable {

    private static final long serialVersionUID = 3180631241462465939L;

    @Id
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmX")
    private Date dataInicio;
    private int duracao;
    private String nome;
    private String local;
    private EStatusReuniao statusEntrevista;
    private String descricao;
}
