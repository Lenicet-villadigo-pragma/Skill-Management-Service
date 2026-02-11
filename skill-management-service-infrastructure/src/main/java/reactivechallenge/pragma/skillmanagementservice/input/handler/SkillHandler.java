package reactivechallenge.pragma.skillmanagementservice.input.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactivechallenge.pragma.skillmanagementservice.api.IRegisterSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.input.dto.SkillCreateDto;
import reactor.core.publisher.Mono;

@Component
public class SkillHandler {

    private final IRegisterSkillServicePort registerSkillServicePort;

    public SkillHandler(IRegisterSkillServicePort registerSkillServicePort) {
        this.registerSkillServicePort = registerSkillServicePort;
    }

    public Mono<ServerResponse> createSkill(ServerRequest request) {
        return request.bodyToMono(SkillCreateDto.class)
                .map(SkillCreateDto::toModel)
                .flatMap(registerSkillServicePort::registerSkill)
                .map(SkillCreateDto::fromModel)
                .flatMap(skillDto -> ServerResponse.created(request.uri()) // O simplemente .ok() dependiendo de si quiero devolver la URI
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(skillDto));
    }
}
