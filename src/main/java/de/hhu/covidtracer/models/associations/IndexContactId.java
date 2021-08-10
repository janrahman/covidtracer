package de.hhu.covidtracer.models.associations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class IndexContactId implements Serializable {
    private static final long serialVersionUID = 1L;
    private long indexId;
    private long contactId;
}
