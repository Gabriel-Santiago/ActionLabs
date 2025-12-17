package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.model.*;
import br.com.actionlabs.carboncalc.repository.*;
import br.com.actionlabs.carboncalc.utils.*;
import br.com.actionlabs.carboncalc.exceptions.*;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarbonCalculationServiceTest {

    @Mock
    private CarbonCalculationRepository carbonCalculationRepository;

    @Mock
    private EnergyEmissionFactorRepository energyEmissionFactorRepository;

    @Mock
    private TransportationEmissionFactorRepository transportationEmissionFactorRepository;

    @Mock
    private SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;

    @Mock
    private NameValidator nameValidator;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @Mock
    private UfValidator ufValidator;

    @Mock
    private EmailValidator emailValidator;

    @Mock
    private RecyclePercentageValidator recyclePercentageValidator;

    @InjectMocks
    private CarbonCalculationService carbonCalculationService;

    @Test
    void startCalculation_ValidRequest_ShouldReturnId() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf("SP");
        request.setPhoneNumber("11999999999");

        CarbonCalculation savedCalculation = new CarbonCalculation();
        savedCalculation.setId("12345");

        doNothing().when(nameValidator).validateName(any());
        doNothing().when(emailValidator).validateEmail(any());
        doNothing().when(ufValidator).validateUf(any());
        doNothing().when(phoneNumberValidator).validatePhoneNumber(any());

        when(carbonCalculationRepository.save(any(CarbonCalculation.class)))
                .thenReturn(savedCalculation);

        String result = carbonCalculationService.startCalculation(request);

        assertEquals("12345", result);
        verify(nameValidator).validateName("João Silva");
        verify(emailValidator).validateEmail("joao@email.com");
        verify(ufValidator).validateUf("SP");
        verify(phoneNumberValidator).validatePhoneNumber("11999999999");
        verify(carbonCalculationRepository).save(any(CarbonCalculation.class));
    }

    @Test
    void startCalculation_InvalidName_ShouldThrowNullNameException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName(null);
        request.setEmail("joao@email.com");
        request.setUf("SP");
        request.setPhoneNumber("11999999999");

        doThrow(new NullNameException())
                .when(nameValidator).validateName(null);

        NullNameException exception = assertThrows(
                NullNameException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("This name is null", exception.getMessage());
        verify(carbonCalculationRepository, never()).save(any());
    }

    @Test
    void startCalculation_NameTooShort_ShouldThrowInvalidNameLengthException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("Jo");
        request.setEmail("joao@email.com");
        request.setUf("SP");
        request.setPhoneNumber("11999999999");

        doThrow(new InvalidNameLengthException())
                .when(nameValidator).validateName(request.getName());

        InvalidNameLengthException exception = assertThrows(
                InvalidNameLengthException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("This name is shorter than 3 characters", exception.getMessage());
    }

    @Test
    void startCalculation_InvalidEmail_ShouldThrowInvalidEmailException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("email-invalido");
        request.setUf("SP");
        request.setPhoneNumber("11999999999");

        doThrow(new InvalidEmailException())
                .when(emailValidator).validateEmail(request.getEmail());

        InvalidEmailException exception = assertThrows(
                InvalidEmailException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("Invalid email address", exception.getMessage());
    }

    @Test
    void validateEmail_EmailAlreadyExists_ShouldThrowEmailAlreadyExists() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João");
        request.setEmail("existente@email.com");
        request.setUf("SP");
        request.setPhoneNumber("11999999999");

        doThrow(new EmailAlreadyExists())
                .when(emailValidator).validateEmail(request.getEmail());

        assertThrows(
                EmailAlreadyExists.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        verify(carbonCalculationRepository, never()).save(any());
    }

    @Test
    void startCalculation_InvalidUf_ShouldThrowUpperCaseUfValidationException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf("sp");
        request.setPhoneNumber("11999999999");

        doThrow(new UpperCaseUfValidationException())
                .when(ufValidator).validateUf(request.getUf());

        UpperCaseUfValidationException exception = assertThrows(
                UpperCaseUfValidationException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("UF must be in uppercase", exception.getMessage());
    }

    @Test
    void startCalculation_NullUf_ShouldThrowNullUfException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf(null);
        request.setPhoneNumber("11999999999");

        doThrow(new NullUfException())
                .when(ufValidator).validateUf(null);

        NullUfException exception = assertThrows(
                NullUfException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("This UF is null", exception.getMessage());
    }

    @Test
    void startCalculation_InvalidPhoneNumber_ShouldThrowInvalidPhoneNumberException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf("SP");
        request.setPhoneNumber("abc123");

        doThrow(new InvalidPhoneNumberException())
                .when(phoneNumberValidator).validatePhoneNumber(request.getPhoneNumber());

        InvalidPhoneNumberException exception = assertThrows(
                InvalidPhoneNumberException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("Invalid phone number. The phone number contains non-numeric characters.",
                exception.getMessage());
    }

    @Test
    void startCalculation_PhoneNumberTooShort_ShouldThrowInvalidPhoneNumberLengthException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf("SP");
        request.setPhoneNumber("123");

        doThrow(new InvalidPhoneNumberLengthException())
                .when(phoneNumberValidator).validatePhoneNumber(request.getPhoneNumber());

        InvalidPhoneNumberLengthException exception = assertThrows(
                InvalidPhoneNumberLengthException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("This phone number is shorter than 11 characters", exception.getMessage());
    }

    @Test
    void startCalculation_UfTooShort_ShouldThrowInvalidUfLengthException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf("S");
        request.setPhoneNumber("11999999999");

        doThrow(new InvalidUfLengthException())
                .when(ufValidator).validateUf(request.getUf());

        InvalidUfLengthException exception = assertThrows(
                InvalidUfLengthException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("The state code must be exactly 2 characters long",
                exception.getMessage());
        verify(carbonCalculationRepository, never()).save(any());
    }

    @Test
    void startCalculation_UfTooLong_ShouldThrowInvalidUfLengthException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf("SPP");
        request.setPhoneNumber("11999999999");

        doThrow(new InvalidUfLengthException())
                .when(ufValidator).validateUf(request.getUf());

        InvalidUfLengthException exception = assertThrows(
                InvalidUfLengthException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("The state code must be exactly 2 characters long",
                exception.getMessage());
        verify(carbonCalculationRepository, never()).save(any());
    }


    @Test
    void startCalculation_NullPhoneNumber_ShouldThrowNullPhoneNumberException() {
        StartCalcRequestDTO request = new StartCalcRequestDTO();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setUf("SP");
        request.setPhoneNumber(null);

        doThrow(new NullPhoneNumberException())
                .when(phoneNumberValidator).validatePhoneNumber(null);

        NullPhoneNumberException exception = assertThrows(
                NullPhoneNumberException.class,
                () -> carbonCalculationService.startCalculation(request)
        );

        assertEquals("This phone number is null", exception.getMessage());
    }

    @Test
    void updateInfo_ValidRequest_ShouldUpdateAndCalculate() {
        String calculationId = "12345";
        UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
        request.setId(calculationId);
        request.setEnergyConsumption(300);
        request.setSolidWasteTotal(50);
        request.setRecyclePercentage(0.3);
        request.setTransportation(List.of(
                createTransportationDTO("CAR", 150),
                createTransportationDTO("PUBLIC_TRANSPORT", 200)
        ));

        CarbonCalculation existingCalculation = new CarbonCalculation();
        existingCalculation.setId(calculationId);
        existingCalculation.setUf("SP");

        EnergyEmissionFactor energyFactor = new EnergyEmissionFactor();
        energyFactor.setUf("SP");
        energyFactor.setFactor(0.47);

        TransportationEmissionFactor carFactor = new TransportationEmissionFactor();
        carFactor.setType(TransportationType.CAR);
        carFactor.setFactor(0.19);

        TransportationEmissionFactor publicTransportFactor = new TransportationEmissionFactor();
        publicTransportFactor.setType(TransportationType.PUBLIC_TRANSPORT);
        publicTransportFactor.setFactor(0.04);

        SolidWasteEmissionFactor solidWasteFactor = new SolidWasteEmissionFactor();
        solidWasteFactor.setUf("SP");
        solidWasteFactor.setRecyclableFactor(0.42);
        solidWasteFactor.setNonRecyclableFactor(0.94);

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.of(existingCalculation));
        doNothing().when(recyclePercentageValidator).validate(0.3);
        when(energyEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(energyFactor));
        when(transportationEmissionFactorRepository.findById(TransportationType.CAR))
                .thenReturn(Optional.of(carFactor));
        when(transportationEmissionFactorRepository.findById(TransportationType.PUBLIC_TRANSPORT))
                .thenReturn(Optional.of(publicTransportFactor));
        when(solidWasteEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(solidWasteFactor));
        when(carbonCalculationRepository.save(any(CarbonCalculation.class)))
                .thenReturn(existingCalculation);

        boolean result = carbonCalculationService.updateInfo(request);

        assertTrue(result);
        verify(carbonCalculationRepository).findById(calculationId);
        verify(recyclePercentageValidator).validate(0.3);
        verify(energyEmissionFactorRepository).findById("SP");
        verify(transportationEmissionFactorRepository, times(2)).findById(any());
        verify(solidWasteEmissionFactorRepository).findById("SP");
        verify(carbonCalculationRepository).save(existingCalculation);
    }

    @Test
    void updateInfo_CalculationNotFound_ShouldThrowCarbonCalculationNotFoundException() {
        String calculationId = "nonexistent";
        UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
        request.setId(calculationId);

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.empty());

        CarbonCalculationNotFoundException exception = assertThrows(
                CarbonCalculationNotFoundException.class,
                () -> carbonCalculationService.updateInfo(request)
        );

        assertEquals("Carbon Calculation not found for id: " + calculationId,
                exception.getMessage());
    }

    @Test
    void updateInfo_InvalidRecyclePercentage_ShouldThrowInvalidRecyclePercentageException() {
        String calculationId = "12345";
        UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
        request.setId(calculationId);
        request.setRecyclePercentage(1.5);

        CarbonCalculation existingCalculation = new CarbonCalculation();
        existingCalculation.setId(calculationId);

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.of(existingCalculation));

        doThrow(new InvalidRecyclePercentageException())
                .when(recyclePercentageValidator).validate(1.5);

        InvalidRecyclePercentageException exception = assertThrows(
                InvalidRecyclePercentageException.class,
                () -> carbonCalculationService.updateInfo(request)
        );

        assertEquals("Invalid Recycle Percentage. The percentage must be between 0 and 1.",
                exception.getMessage());
    }

    @Test
    void updateInfo_NegativeRecyclePercentage_ShouldThrowInvalidRecyclePercentageException() {
        String calculationId = "12345";
        UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
        request.setId(calculationId);
        request.setRecyclePercentage(-0.5);

        CarbonCalculation existingCalculation = new CarbonCalculation();
        existingCalculation.setId(calculationId);

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.of(existingCalculation));

        doThrow(new InvalidRecyclePercentageException())
                .when(recyclePercentageValidator).validate(-0.5);

        InvalidRecyclePercentageException exception = assertThrows(
                InvalidRecyclePercentageException.class,
                () -> carbonCalculationService.updateInfo(request)
        );

        assertEquals("Invalid Recycle Percentage. The percentage must be between 0 and 1.",
                exception.getMessage());
    }

    @Test
    void updateInfo_NoTransportation_ShouldCalculateZero() {
        String calculationId = "12345";
        UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
        request.setId(calculationId);
        request.setEnergyConsumption(300);
        request.setSolidWasteTotal(50);
        request.setRecyclePercentage(0.3);
        request.setTransportation(List.of());

        CarbonCalculation existingCalculation = new CarbonCalculation();
        existingCalculation.setId(calculationId);
        existingCalculation.setUf("SP");

        EnergyEmissionFactor energyFactor = new EnergyEmissionFactor();
        energyFactor.setUf("SP");
        energyFactor.setFactor(0.47);

        SolidWasteEmissionFactor solidWasteFactor = new SolidWasteEmissionFactor();
        solidWasteFactor.setUf("SP");
        solidWasteFactor.setRecyclableFactor(0.42);
        solidWasteFactor.setNonRecyclableFactor(0.94);

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.of(existingCalculation));
        doNothing().when(recyclePercentageValidator).validate(0.3);
        when(energyEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(energyFactor));
        when(solidWasteEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(solidWasteFactor));
        when(carbonCalculationRepository.save(any(CarbonCalculation.class)))
                .thenReturn(existingCalculation);

        boolean result = carbonCalculationService.updateInfo(request);

        assertTrue(result);
        verify(transportationEmissionFactorRepository, never()).findById(any());
    }

    @Test
    void getCarbonCalculationResult_ValidId_ShouldReturnFormattedResult() {
        String calculationId = "12345";
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setId(calculationId);
        calculation.setEnergyEmission(97.234567);
        calculation.setTransportationEmission(6.012345);
        calculation.setSolidWasteEmission(21.345678);
        calculation.setTotalEmission(124.592590);

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.of(calculation));

        CarbonCalculationResultDTO result = carbonCalculationService.getCarbonCalculationResult(calculationId);

        assertNotNull(result);
        assertEquals(97.23, result.getEnergy());
        assertEquals(6.01, result.getTransportation());
        assertEquals(21.35, result.getSolidWaste());
        assertEquals(124.59, result.getTotal());

        verify(carbonCalculationRepository).findById(calculationId);
    }

    @Test
    void getCarbonCalculationResult_NotFound_ShouldThrowCarbonCalculationNotFoundException() {
        String calculationId = "nonexistent";

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.empty());

        CarbonCalculationNotFoundException exception = assertThrows(
                CarbonCalculationNotFoundException.class,
                () -> carbonCalculationService.getCarbonCalculationResult(calculationId)
        );

        assertEquals("Carbon Calculation not found for id: " + calculationId,
                exception.getMessage());
    }

    @Test
    void calculateEnergyEmissionFactor_ValidUF_ShouldCalculateCorrectly() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setUf("SP");
        calculation.setEnergyConsumption(350);

        EnergyEmissionFactor factor = new EnergyEmissionFactor();
        factor.setUf("SP");
        factor.setFactor(0.47);

        when(energyEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(factor));

        carbonCalculationService.calculateEnergyEmissionFactor(calculation);

        assertEquals(164.5, calculation.getEnergyEmission());
        verify(energyEmissionFactorRepository).findById("SP");
    }

    @Test
    void calculateEnergyEmissionFactor_UFNotFound_ShouldThrowEnergyEmissionFactorNotFoundException() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setUf("XX");

        when(energyEmissionFactorRepository.findById("XX"))
                .thenReturn(Optional.empty());

        EnergyEmissionFactorNotFoundException exception = assertThrows(
                EnergyEmissionFactorNotFoundException.class,
                () -> carbonCalculationService.calculateEnergyEmissionFactor(calculation)
        );

        assertEquals("Energy Emission Factor not found for UF: " + calculation.getUf(), exception.getMessage());
    }

    @Test
    void calculateTransportationEmissionFactor_NullList_ShouldSetZero() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setTransportation(null);

        carbonCalculationService.calculateTransportationEmissionFactor(calculation);

        assertEquals(0.0, calculation.getTransportationEmission());
        verify(transportationEmissionFactorRepository, never()).findById(any());
    }

    @Test
    void calculateTransportationEmissionFactor_EmptyList_ShouldSetZero() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setTransportation(List.of());

        carbonCalculationService.calculateTransportationEmissionFactor(calculation);

        assertEquals(0.0, calculation.getTransportationEmission());
        verify(transportationEmissionFactorRepository, never()).findById(any());
    }

    @Test
    void calculateTransportationEmissionFactor_ValidTransportation_ShouldCalculateCorrectly() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setTransportation(List.of(
                createTransportationDTO("CAR", 200),
                createTransportationDTO("BICYCLE", 100)
        ));

        TransportationEmissionFactor carFactor = new TransportationEmissionFactor();
        carFactor.setType(TransportationType.CAR);
        carFactor.setFactor(0.19);

        TransportationEmissionFactor bikeFactor = new TransportationEmissionFactor();
        bikeFactor.setType(TransportationType.BICYCLE);
        bikeFactor.setFactor(0.0);

        when(transportationEmissionFactorRepository.findById(TransportationType.CAR))
                .thenReturn(Optional.of(carFactor));
        when(transportationEmissionFactorRepository.findById(TransportationType.BICYCLE))
                .thenReturn(Optional.of(bikeFactor));

        carbonCalculationService.calculateTransportationEmissionFactor(calculation);

        assertEquals(38.0, calculation.getTransportationEmission());
        verify(transportationEmissionFactorRepository, times(2)).findById(any());
    }

    @Test
    void calculateTransportationEmissionFactor_TransportTypeNotFound_ShouldThrowTransportationEmissionFactorNotFoundException() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setTransportation(List.of(
                createTransportationDTO("CAR", 200)
        ));

        when(transportationEmissionFactorRepository.findById(TransportationType.CAR))
                .thenReturn(Optional.empty());

        TransportationEmissionFactorNotFoundException exception = assertThrows(
                TransportationEmissionFactorNotFoundException.class,
                () -> carbonCalculationService.calculateTransportationEmissionFactor(calculation)
        );

        assertEquals("Transportation Emission Factor not found for transport type: CAR", exception.getMessage());
    }

    @Test
    void calculateSolidWasteEmissionFactor_ValidData_ShouldCalculateCorrectly() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setUf("SP");
        calculation.setSolidWasteProduction(60);
        calculation.setRecyclePercentage(0.25);

        SolidWasteEmissionFactor factor = new SolidWasteEmissionFactor();
        factor.setUf("SP");
        factor.setRecyclableFactor(0.42);
        factor.setNonRecyclableFactor(0.94);

        when(solidWasteEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(factor));

        carbonCalculationService.calculateSolidWasteEmissionFactor(calculation);

        assertEquals(48.6, calculation.getSolidWasteEmission(), 0.001);
    }

    @Test
    void calculateSolidWasteEmissionFactor_UFNotFound_ShouldThrowSolidWasteEmissionFactorNotFoundException() {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setUf("XX");
        calculation.setSolidWasteProduction(60);
        calculation.setRecyclePercentage(0.25);

        when(solidWasteEmissionFactorRepository.findById("XX"))
                .thenReturn(Optional.empty());

        SolidWasteEmissionFactorNotFoundException exception = assertThrows(
                SolidWasteEmissionFactorNotFoundException.class,
                () -> carbonCalculationService.calculateSolidWasteEmissionFactor(calculation)
        );

        assertEquals("Solid Waste Emission Factor not found for UF: " + calculation.getUf(), exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.25, 0.5, 0.75, 1.0})
    void getTotalSolidWasteEmission_VariousPercentages_ShouldCalculateCorrectly(double percentage) {
        CarbonCalculation calculation = new CarbonCalculation();
        calculation.setSolidWasteProduction(100);
        calculation.setRecyclePercentage(percentage);

        SolidWasteEmissionFactor factor = new SolidWasteEmissionFactor();
        factor.setRecyclableFactor(0.5);
        factor.setNonRecyclableFactor(1.0);

        double result = CarbonCalculationService.getTotalSolidWasteEmission(calculation, percentage, factor);

        double expected = (100 * percentage * 0.5) + (100 * (1 - percentage) * 1.0);
        assertEquals(expected, result, 0.001);
    }

    @Test
    void updateInfo_FullCalculation_ShouldCalculateAllEmissionsCorrectly() {
        String calculationId = "12345";
        UpdateCalcInfoRequestDTO request = new UpdateCalcInfoRequestDTO();
        request.setId(calculationId);
        request.setEnergyConsumption(200);
        request.setSolidWasteTotal(40);
        request.setRecyclePercentage(0.5);
        request.setTransportation(List.of(createTransportationDTO("CAR", 100)));

        CarbonCalculation existingCalculation = new CarbonCalculation();
        existingCalculation.setId(calculationId);
        existingCalculation.setUf("SP");

        EnergyEmissionFactor energyFactor = new EnergyEmissionFactor();
        energyFactor.setUf("SP");
        energyFactor.setFactor(0.47);

        TransportationEmissionFactor carFactor = new TransportationEmissionFactor();
        carFactor.setType(TransportationType.CAR);
        carFactor.setFactor(0.19);

        SolidWasteEmissionFactor solidWasteFactor = new SolidWasteEmissionFactor();
        solidWasteFactor.setUf("SP");
        solidWasteFactor.setRecyclableFactor(0.42);
        solidWasteFactor.setNonRecyclableFactor(0.94);

        when(carbonCalculationRepository.findById(calculationId))
                .thenReturn(Optional.of(existingCalculation));
        doNothing().when(recyclePercentageValidator).validate(0.5);
        when(energyEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(energyFactor));
        when(transportationEmissionFactorRepository.findById(TransportationType.CAR))
                .thenReturn(Optional.of(carFactor));
        when(solidWasteEmissionFactorRepository.findById("SP"))
                .thenReturn(Optional.of(solidWasteFactor));
        when(carbonCalculationRepository.save(any(CarbonCalculation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        carbonCalculationService.updateInfo(request);

        verify(carbonCalculationRepository).save(argThat(saved -> {
            assertEquals(94.0, saved.getEnergyEmission(), 0.001);

            assertEquals(19.0, saved.getTransportationEmission(), 0.001);

            assertEquals(27.2, saved.getSolidWasteEmission(), 0.001);

            assertEquals(140.2, saved.getTotalEmission(), 0.001);

            return true;
        }));
    }

    private TransportationDTO createTransportationDTO(String type, int distance) {
        TransportationDTO dto = new TransportationDTO();
        dto.setType(TransportationType.valueOf(type));
        dto.setMonthlyDistance(distance);
        return dto;
    }
}