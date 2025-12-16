package br.com.actionlabs.carboncalc.rest;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.service.CarbonCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Slf4j
public class OpenRestController {

  private final CarbonCalculationService carbonCalculationService;

  @PostMapping("start-calc")
  public ResponseEntity<StartCalcResponseDTO> startCalculation(
      @RequestBody StartCalcRequestDTO request) {
    String id = carbonCalculationService.startCalculation(request);

    StartCalcResponseDTO dto = new StartCalcResponseDTO();
    dto.setId(id);

    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  @PutMapping("info")
  public ResponseEntity<UpdateCalcInfoResponseDTO> updateInfo(
      @RequestBody UpdateCalcInfoRequestDTO request) {
    boolean result = carbonCalculationService.updateInfo(request);

    UpdateCalcInfoResponseDTO dto = new UpdateCalcInfoResponseDTO();
    dto.setSuccess(result);

    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @GetMapping("result/{id}")
  public ResponseEntity<CarbonCalculationResultDTO> getResult(@PathVariable String id) {
    return new ResponseEntity<>(carbonCalculationService.getCarbonCalculationResult(id),
            HttpStatus.OK);
  }
}
