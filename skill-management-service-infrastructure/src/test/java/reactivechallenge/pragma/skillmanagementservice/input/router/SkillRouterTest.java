package reactivechallenge.pragma.skillmanagementservice.input.router;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactivechallenge.pragma.skillmanagementservice.input.dto.SkillCreateDto;
import reactivechallenge.pragma.skillmanagementservice.input.dto.TechnologyExternalDto;
import reactivechallenge.pragma.skillmanagementservice.input.handler.SkillHandler;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillRouterTest {

    @Mock
    private SkillHandler skillHandlerMock;

    @InjectMocks
    private SkillRouter skillRouter;

    @Test
    @DisplayName("Router routes POST /create to handler")
    void createSkillRouteTest() {
        // Arrange
        List<TechnologyExternalDto> technologyIds = List.of(new TechnologyExternalDto(1L), new TechnologyExternalDto(2L), new TechnologyExternalDto(3L));
        SkillCreateDto skillCreateDto = new SkillCreateDto("Java", "Programming Language", technologyIds);

        when(skillHandlerMock.createSkill(any())).thenReturn(
                ServerResponse.created(java.net.URI.create("/create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(skillCreateDto), SkillCreateDto.class)
        );

        WebTestClient webTestClient = WebTestClient
                .bindToRouterFunction(skillRouter.skillRoutes(skillHandlerMock))
                .build();

        // Act & Assert
        webTestClient.post()
                .uri("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(skillCreateDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(SkillCreateDto.class)
                .isEqualTo(skillCreateDto);
    }

}
