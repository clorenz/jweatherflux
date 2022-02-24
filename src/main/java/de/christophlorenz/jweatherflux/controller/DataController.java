package de.christophlorenz.jweatherflux.controller;

import de.christophlorenz.jweatherflux.forwarder.Forwarder;
import de.christophlorenz.jweatherflux.service.DataReportService;
import de.christophlorenz.jweatherflux.service.ForwarderService;
import de.christophlorenz.jweatherflux.service.PersistException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

  private final DataReportService dataReportService;
  private final ForwarderService forwarderService;
  private final List<Forwarder> forwarders;

  public DataController(DataReportService dataReportService, ForwarderService forwarderService, List<Forwarder> forwarders) {
    this.dataReportService = dataReportService;
    this.forwarders = forwarders;
    this.forwarderService = forwarderService;
  }

  @PostMapping("/data/report")
  public ResponseEntity report(@RequestBody String requestBody) {
    LOGGER.debug("requestBody=" + requestBody);
    Map<String,String> data = Arrays.stream(requestBody.split("&")).map(d -> d.split("=")).collect(
        Collectors.toMap(e -> e[0], e -> e[1]));

    try {
      dataReportService.persist(data);
    } catch (PersistException e) {
      LOGGER.error("Cannot persist data=" + data + ": " + e);
    }

    forwarderService.forwardAll(data);

    LOGGER.debug("Started all forwarders; returning OK to weather station");
    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

}
