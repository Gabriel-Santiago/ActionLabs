package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.CarbonCalculationResultDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.exceptions.CarbonCalculationNotFoundException;
import br.com.actionlabs.carboncalc.exceptions.EnergyEmissionFactorNotFoundException;
import br.com.actionlabs.carboncalc.exceptions.SolidWasteEmissionFactorNotFoundException;
import br.com.actionlabs.carboncalc.exceptions.TransportationEmissionFactorNotFoundException;
import br.com.actionlabs.carboncalc.model.CarbonCalculation;
import br.com.actionlabs.carboncalc.model.EnergyEmissionFactor;
import br.com.actionlabs.carboncalc.model.SolidWasteEmissionFactor;
import br.com.actionlabs.carboncalc.model.TransportationEmissionFactor;
import br.com.actionlabs.carboncalc.repository.CarbonCalculationRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarbonCalculationService {

    private final CarbonCalculationRepository carbonCalculationRepository;
    private final EnergyEmissionFactorRepository energyEmissionFactorRepository;
    private final SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;
    private final TransportationEmissionFactorRepository transportationEmissionFactorRepository;

    public CarbonCalculationService(CarbonCalculationRepository carbonCalculationRepository,
                                    EnergyEmissionFactorRepository energyEmissionFactorRepository,
                                    SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository,
                                    TransportationEmissionFactorRepository transportationEmissionFactorRepository) {
        this.carbonCalculationRepository = carbonCalculationRepository;
        this.energyEmissionFactorRepository = energyEmissionFactorRepository;
        this.solidWasteEmissionFactorRepository = solidWasteEmissionFactorRepository;
        this.transportationEmissionFactorRepository = transportationEmissionFactorRepository;
    }

    public String startCalculation(StartCalcRequestDTO request) {
        CarbonCalculation carbonCalculation = new CarbonCalculation();
        carbonCalculation.setName(request.getName());
        carbonCalculation.setEmail(request.getEmail());
        carbonCalculation.setUf(request.getUf());
        carbonCalculation.setPhoneNumber(request.getPhoneNumber());

        CarbonCalculation carbonCalculationSalved =  carbonCalculationRepository
                .save(carbonCalculation);

        return carbonCalculationSalved.getId();
    }

    public boolean updateInfo(UpdateCalcInfoRequestDTO request) {
        CarbonCalculation carbonCalculation = carbonCalculationRepository
                .findById(request.getId())
                .orElseThrow(() -> new CarbonCalculationNotFoundException(request.getId()));

        carbonCalculation.setEnergyConsumption(request.getEnergyConsumption());
        carbonCalculation.setSolidWasteProduction(request.getSolidWasteTotal());
        carbonCalculation.setRecyclePercentage(request.getRecyclePercentage());
        carbonCalculation.setTransportation(request.getTransportation());

        calculateEmissionFactor(carbonCalculation);

        carbonCalculationRepository.save(carbonCalculation);

        return true;
    }

    public CarbonCalculationResultDTO getCarbonCalculationResult(String id) {
        CarbonCalculation carbonCalculation = carbonCalculationRepository
                .findById(id)
                .orElseThrow(() -> new CarbonCalculationNotFoundException(id));

        CarbonCalculationResultDTO carbonCalculationDTO = new CarbonCalculationResultDTO();
        carbonCalculationDTO.setEnergy(carbonCalculation.getEnergyEmission());
        carbonCalculationDTO.setTransportation(carbonCalculation.getTransportationEmission());
        carbonCalculationDTO.setSolidWaste(carbonCalculation.getSolidWasteEmission());
        carbonCalculationDTO.setTotal(carbonCalculation.getTotalEmission());

        return carbonCalculationDTO;
    }

    private void calculateEmissionFactor(CarbonCalculation carbonCalculation) {
        calculateEnergyEmissionFactor(carbonCalculation);
        calculateTransportationEmissionFactor(carbonCalculation);
        calculateSolidWasteEmissionFactor(carbonCalculation);

        double total = carbonCalculation.getEnergyEmission() +
                carbonCalculation.getTransportationEmission() +
                carbonCalculation.getSolidWasteEmission();

        carbonCalculation.setTotalEmission(total);
    }

    private void calculateEnergyEmissionFactor(CarbonCalculation carbonCalculation) {
        EnergyEmissionFactor energyEmissionFactor = energyEmissionFactorRepository
                .findByUf(carbonCalculation.getUf())
                .orElseThrow(() -> new EnergyEmissionFactorNotFoundException(carbonCalculation.getUf()));

        Double emission = carbonCalculation.getEnergyConsumption() * energyEmissionFactor.getFactor();
        carbonCalculation.setEnergyEmission(emission);
    }

    private void calculateTransportationEmissionFactor(CarbonCalculation carbonCalculation) {
        List<TransportationDTO> transportationList = carbonCalculation.getTransportation();

        if (transportationList == null || transportationList.isEmpty()) {
            carbonCalculation.setTransportationEmission(0.0);
            return;
        }

        double totalTransportationEmission = 0.0;

        for (TransportationDTO transportationDTO : transportationList) {
            TransportationEmissionFactor transportationEmissionFactor = transportationEmissionFactorRepository
                    .findByType(transportationDTO.getType())
                    .orElseThrow(() -> new TransportationEmissionFactorNotFoundException(transportationDTO.getType()));

            totalTransportationEmission += transportationDTO.getMonthlyDistance() *
                    transportationEmissionFactor.getFactor();
        }

        carbonCalculation.setTransportationEmission(totalTransportationEmission);
    }

    private void calculateSolidWasteEmissionFactor(CarbonCalculation carbonCalculation) {
        SolidWasteEmissionFactor solidWasteEmissionFactor = solidWasteEmissionFactorRepository
                .findByUf(carbonCalculation.getUf())
                .orElseThrow(() -> new SolidWasteEmissionFactorNotFoundException(carbonCalculation.getUf()));

        double recyclePercentage = carbonCalculation.getRecyclePercentage();
        double totalSolidWasteEmission = getTotalSolidWasteEmission(carbonCalculation, recyclePercentage, solidWasteEmissionFactor);

        carbonCalculation.setSolidWasteEmission(totalSolidWasteEmission);
    }

    private static double getTotalSolidWasteEmission(CarbonCalculation carbonCalculation,
                                                     double recyclePercentage,
                                                     SolidWasteEmissionFactor solidWasteEmissionFactor) {
        double solidWasteTotal = carbonCalculation.getSolidWasteProduction();

        double recyclableWaste = solidWasteTotal * recyclePercentage;
        double recyclableEmission = recyclableWaste * solidWasteEmissionFactor.getRecyclableFactor();

        double nonRecyclableWaste = solidWasteTotal * (1 - recyclePercentage);
        double nonRecyclableEmission = nonRecyclableWaste * solidWasteEmissionFactor.getNonRecyclableFactor();

        return recyclableEmission + nonRecyclableEmission;
    }
}
