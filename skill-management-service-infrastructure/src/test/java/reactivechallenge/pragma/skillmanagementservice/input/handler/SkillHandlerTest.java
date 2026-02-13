package reactivechallenge.pragma.skillmanagementservice.input.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactivechallenge.pragma.skillmanagementservice.api.IRegisterSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;
import reactivechallenge.pragma.skillmanagementservice.input.dto.SkillCreateDto;
import reactivechallenge.pragma.skillmanagementservice.input.dto.TechnologyExternalDto;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SkillHandlerTest {
    @Mock
    private IRegisterSkillServicePort registerSkillServicePort;

    private SkillHandler skillHandler;

    @BeforeEach
    void setUp() {
        skillHandler = new SkillHandler(registerSkillServicePort);
    }

    @Test
    @DisplayName("Create skill fails validation when name is blank")
    void createSkillValidationFailureForInvalidName() {
        // Arrange
        List<TechnologyExternalDto> technologyIds = List.of(new TechnologyExternalDto(1L), new TechnologyExternalDto(2L));
        SkillCreateDto invalidDto = new SkillCreateDto("", "Valid description", technologyIds);
        ServerRequest request = mock(ServerRequest.class);

        given(request.bodyToMono(SkillCreateDto.class)).willReturn(Mono.just(invalidDto));

        // Act
        Mono<ServerResponse> responseMono = skillHandler.createSkill(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectErrorMatches(BusinessDomainException.class::isInstance)
                .verify();
    }

    @Test
    @DisplayName("Create skill fails validation when list of technology ids is empty")
    void createSkillValidationFailureForEmptyTechIds() {
        // Arrange
        List<TechnologyExternalDto> technologyIds = new ArrayList<>();
        SkillCreateDto invalidDto = new SkillCreateDto("valid name", "", technologyIds);
        ServerRequest request = mock(ServerRequest.class);

        given(request.bodyToMono(SkillCreateDto.class)).willReturn(Mono.just(invalidDto));

        // Act
        Mono<ServerResponse> responseMono = skillHandler.createSkill(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectErrorMatches(BusinessDomainException.class::isInstance)
                .verify();
    }

    @Test
    @DisplayName("Create skill fails validation when list size of technology ids is less than 3")
    void createSkillValidationFailureForLessThan3TechIds() {
        // Arrange
        List<TechnologyExternalDto> technologyIds = List.of(new TechnologyExternalDto(1L), new TechnologyExternalDto(2L));
        SkillCreateDto invalidDto = new SkillCreateDto("Java", "Valid description", technologyIds);
        ServerRequest request = mock(ServerRequest.class);

        given(request.bodyToMono(SkillCreateDto.class)).willReturn(Mono.just(invalidDto));

        // Act
        Mono<ServerResponse> responseMono = skillHandler.createSkill(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectErrorMatches(BusinessDomainException.class::isInstance)
                .verify();
    }

    @Test
    @DisplayName("Create skill succeeds when dto is valid")
    void createSkillSuccess() {
        // Arrange
        List<TechnologyExternalDto> technologyIds = List.of(new TechnologyExternalDto(1L), new TechnologyExternalDto(2L), new TechnologyExternalDto(3L));
        List<TechnologyExternalModel> technologyModelIds = List.of(new TechnologyExternalModel(1L), new TechnologyExternalModel(2L), new TechnologyExternalModel(3L));
        SkillCreateDto validDto = new SkillCreateDto("Java", "Valid description", technologyIds);
        ServerRequest request = mock(ServerRequest.class);

        given(request.bodyToMono(SkillCreateDto.class)).willReturn(Mono.just(validDto));
        given(registerSkillServicePort.registerSkill(any(SkillModel.class)))
                .willReturn(Mono.just(new SkillModel(1L, "Java", "Valid description", technologyModelIds)));

        // Act
        Mono<ServerResponse> responseMono = skillHandler.createSkill(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode() == HttpStatus.CREATED)
                .verifyComplete();
    }
}