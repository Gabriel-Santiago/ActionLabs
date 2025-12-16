package br.com.actionlabs.carboncalc.model;

import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("carbonCalculation")
public class CarbonCalculation {

    @Id
    private String id;

    private String name;
    private String email;
    private String phoneNumber;
    private String uf;

    private double energyConsumption;
    private List<TransportationDTO> transportation;
    private double transportationDistance;
    private double solidWasteProduction;
    private double recyclePercentage;

    private Double energyEmission;
    private Double transportationEmission;
    private Double solidWasteEmission;
    private Double totalEmission;

    private TransportationType transportationType;
}
