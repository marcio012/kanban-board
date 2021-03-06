package org.seariver.kanbanboard.write.adapter.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.seariver.kanbanboard.write.CommandBus;
import org.seariver.kanbanboard.write.domain.application.CreateCardCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;

@RestController
@RequestMapping("v1/buckets")
public class WriteCardController {

    private CommandBus commandBus;

    public WriteCardController(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @PostMapping(path = "/{bucketId}/cards")
    public ResponseEntity<String> create(@Valid @NotNull @PathVariable(name = "bucketId") String bucketExternalId,
                                         @RequestBody CardDto dto) throws URISyntaxException {

        commandBus.execute(new CreateCardCommand(bucketExternalId, dto.externalId(), dto.position(), dto.name()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    record CardDto(
            @JsonProperty("id")
            String externalId,
            double position,
            String name) {
        // silence sonarqube
    }
}
