package de.christophlorenz.weatherflux.controller;

import de.christophlorenz.weatherflux.forwarder.Forwarder;
import de.christophlorenz.weatherflux.service.DataReportService;
import de.christophlorenz.weatherflux.service.PersistException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

  private final DataReportService dataReportService;
  private final List<Forwarder> forwarders;

  public DataController(DataReportService dataReportService, List<Forwarder> forwarders) {
    this.dataReportService = dataReportService;
    this.forwarders = forwarders;

    LOGGER.info("Active Forwarders " + forwarders.stream().filter(Forwarder::isActive).map(f -> f.getClass().getSimpleName()).collect(Collectors.toList()));
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

    forwarders.forEach(
        f -> {
          if (f.isActive()) {
            f.forward(data);
          }
        }
    );

    return new ResponseEntity(HttpStatus.NO_CONTENT);
  }

}
