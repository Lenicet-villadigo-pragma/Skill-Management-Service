package reactivechallenge.pragma.skillmanagementservice.input.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactivechallenge.pragma.skillmanagementservice.api.IRegisterSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.api.IRetrieveSkillsServicePort;
import reactivechallenge.pragma.skillmanagementservice.exception.BusinessDomainException;
import reactivechallenge.pragma.skillmanagementservice.input.dto.*;
import reactivechallenge.pragma.skillmanagementservice.model.SkillModel;
import reactivechallenge.pragma.skillmanagementservice.model.TechnologyExternalModel;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillPaginationResult;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortField;
import reactivechallenge.pragma.skillmanagementservice.model.criteria.SkillSortOrder;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillHandlerTest {
    @Mock
    private IRegisterSkillServicePort registerSkillServicePort;

    @Mock
    private IRetrieveSkillsServicePort retrieveSkillsServicePort;

    @Mock
    ITechnologyServicePort technologyServicePort;

    private SkillHandler skillHandler;

    @BeforeEach
    void setUp() {
        skillHandler = new SkillHandler(registerSkillServicePort, retrieveSkillsServicePort
        ,technologyServicePort,0,10);
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
        List<TechnologyExternalModel> technologyModelIds = List.of(new TechnologyExternalModel(1L,""), new TechnologyExternalModel(2L,""), new TechnologyExternalModel(3L,""));
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

    @Test
    @DisplayName("List skill respond successfully when valid path variables")
    void listSkillsSuccess() {
        // Arrange
        ServerRequest request = mock(ServerRequest.class);
        List<TechnologyExternalModel> externalModelList = List.of(new TechnologyExternalModel(1L, "test")
                , new TechnologyExternalModel(3L, "test"), new TechnologyExternalModel(2L, "test"));
        SkillModel skillModel = new SkillModel(1L, "test","description",externalModelList);
        SkillPaginationResult<SkillModel> skillPaginationSkillModel = new SkillPaginationResult<>(List.of(skillModel),1L);
        Mono<SkillPaginationResult<SkillModel>> skillPaginationResultMono = Mono.just(skillPaginationSkillModel);
        Flux<TechnologyExternalModel> technologyExternalModelFlux = Flux.just(new TechnologyExternalModel(1L, "test")
                , new TechnologyExternalModel(3L, "test"), new TechnologyExternalModel(2L, "test"));
        List<TechResponseForListDto> techResponseForListDtos = List.of(new TechResponseForListDto(1L, "test")
                , new TechResponseForListDto(3L, "test"), new TechResponseForListDto(2L, "test"));
        List<ListSkillResponseDto> listSkillResponseDto = List.of(new ListSkillResponseDto(1L, "test", techResponseForListDtos));
        SkillPaginatedDto<ListSkillResponseDto> responseExpected = new SkillPaginatedDto<>(listSkillResponseDto, 1L, 0, 10);

        given(request.queryParam("sortField")).willReturn(Optional.of("name"));
        given(request.queryParam("sortOrder")).willReturn(Optional.of("asc"));
        given(request.queryParam("pageNumber")).willReturn(Optional.of("0"));
        given(request.queryParam("pageSize")).willReturn(Optional.of("10"));
        when(retrieveSkillsServicePort.retrieveSkills(Mockito.any(SkillSortField.class), Mockito.any(SkillSortOrder.class)
                , Mockito.any(Integer.class), Mockito.any(Integer.class))).thenReturn(skillPaginationResultMono);
        when(technologyServicePort.getTechsByIds(anyList())).thenReturn(technologyExternalModelFlux);

        // Act
        Mono<ServerResponse> responseMono = skillHandler.listSkills(request);

        // Assert
        StepVerifier.create(responseMono)
                .assertNext(serverResponse -> {
                    Assertions.assertEquals(HttpStatus.OK, serverResponse.statusCode());

                    if (serverResponse instanceof EntityResponse<?> entityResponse) {
                        SkillPaginatedDto<ListSkillResponseDto> body = (SkillPaginatedDto<ListSkillResponseDto>) entityResponse.entity();
                        Assertions.assertEquals(body, responseExpected);
                    }
                })
                .verifyComplete();
        Mockito.verify(retrieveSkillsServicePort).retrieveSkills(Mockito.any(SkillSortField.class), Mockito.any(SkillSortOrder.class)
                , Mockito.any(Integer.class), Mockito.any(Integer.class));
        Mockito.verify(technologyServicePort).getTechsByIds(anyList());
    }

    @Test
    @DisplayName("List skill respond successfully when invalid path variables and takes by default")
    void listSkillsSuccessWhenInvalidPathVariables() {
        // Arrange
        ServerRequest request = mock(ServerRequest.class);
        List<TechnologyExternalModel> externalModelList = List.of(new TechnologyExternalModel(1L, "test")
                , new TechnologyExternalModel(3L, "test"), new TechnologyExternalModel(2L, "test"));
        SkillModel skillModel = new SkillModel(1L, "test","description",externalModelList);
        SkillPaginationResult<SkillModel> skillPaginationSkillModel = new SkillPaginationResult<>(List.of(skillModel),1L);
        Mono<SkillPaginationResult<SkillModel>> skillPaginationResultMono = Mono.just(skillPaginationSkillModel);
        Flux<TechnologyExternalModel> technologyExternalModelFlux = Flux.just(new TechnologyExternalModel(1L, "test")
                , new TechnologyExternalModel(3L, "test"), new TechnologyExternalModel(2L, "test"));
        List<TechResponseForListDto> techResponseForListDtos = List.of(new TechResponseForListDto(1L, "test")
                , new TechResponseForListDto(3L, "test"), new TechResponseForListDto(2L, "test"));
        List<ListSkillResponseDto> listSkillResponseDto = List.of(new ListSkillResponseDto(1L, "test", techResponseForListDtos));
        SkillPaginatedDto<ListSkillResponseDto> responseExpected = new SkillPaginatedDto<>(listSkillResponseDto, 1L, 0, 10);

        given(request.queryParam("sortField")).willReturn(Optional.empty());
        given(request.queryParam("sortOrder")).willReturn(Optional.empty());
        given(request.queryParam("pageNumber")).willReturn(Optional.empty());
        given(request.queryParam("pageSize")).willReturn(Optional.of("string"));
        when(retrieveSkillsServicePort.retrieveSkills(Mockito.any(SkillSortField.class), Mockito.any(SkillSortOrder.class)
                , Mockito.any(Integer.class), Mockito.any(Integer.class))).thenReturn(skillPaginationResultMono);
        when(technologyServicePort.getTechsByIds(anyList())).thenReturn(technologyExternalModelFlux);

        // Act
        Mono<ServerResponse> responseMono = skillHandler.listSkills(request);

        // Assert
        StepVerifier.create(responseMono)
                .assertNext(serverResponse -> {
                    Assertions.assertEquals(HttpStatus.OK, serverResponse.statusCode());

                    if (serverResponse instanceof EntityResponse<?> entityResponse) {
                        SkillPaginatedDto<ListSkillResponseDto> body = (SkillPaginatedDto<ListSkillResponseDto>) entityResponse.entity();
                        Assertions.assertEquals(body, responseExpected);
                    }
                })
                .verifyComplete();
        Mockito.verify(retrieveSkillsServicePort).retrieveSkills(Mockito.any(SkillSortField.class), Mockito.any(SkillSortOrder.class)
                , Mockito.any(Integer.class), Mockito.any(Integer.class));
        Mockito.verify(technologyServicePort).getTechsByIds(anyList());
    }

    @Test
    @DisplayName("Verify if skill exists returns true")
    void verifyIfSkillExistReturnsTrue() {
        // Arrange
        List<Long> skillsIds = List.of(1L);
        String id1 = "1";
        ServerRequest request = mock(ServerRequest.class);

        given(request.queryParam("skillIds")).willReturn(Optional.of(id1));
        given(retrieveSkillsServicePort.verifyIfExists(anyList())).willReturn(Mono.just(true));
        given(retrieveSkillsServicePort.verifySkillIds(anyString())).willReturn(skillsIds);

        // Act
        Mono<ServerResponse> responseMono = skillHandler.verifyIfSkillsExists(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    @DisplayName("Verify if skill exists returns false")
    void verifyIfSkillExistReturnsFalse() {
        // Arrange
        String skillId = "999";
        List<Long> skillsIds= List.of(999L);
        ServerRequest request = mock(ServerRequest.class);

        given(request.queryParam("skillIds")).willReturn(Optional.of(skillId));
        given(retrieveSkillsServicePort.verifyIfExists(anyList())).willReturn(Mono.just(false));
        given(retrieveSkillsServicePort.verifySkillIds(anyString())).willReturn(skillsIds);

        // Act
        Mono<ServerResponse> responseMono = skillHandler.verifyIfSkillsExists(request);

        // Assert
        StepVerifier.create(responseMono)
                .expectNextMatches(serverResponse -> serverResponse.statusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    @DisplayName("Verify if skill exists fails when skillIds are not sent")
    void verifyIfSkillExistFailsWithInvalidTechId() {
        // Arrange
        ServerRequest request = mock(ServerRequest.class);

        given(request.queryParam("skillIds")).willReturn(Optional.empty());
        given(retrieveSkillsServicePort.verifySkillIds(null)).willThrow(new IllegalArgumentException());

        // Act & Assert
        StepVerifier.create(
                        Mono.defer(() -> skillHandler.verifyIfSkillsExists(request))
                )
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}