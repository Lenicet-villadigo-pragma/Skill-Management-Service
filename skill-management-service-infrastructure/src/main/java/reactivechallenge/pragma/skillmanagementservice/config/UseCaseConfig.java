package reactivechallenge.pragma.skillmanagementservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivechallenge.pragma.skillmanagementservice.api.IRegisterSkillServicePort;
import reactivechallenge.pragma.skillmanagementservice.api.IRetrieveSkillsServicePort;
import reactivechallenge.pragma.skillmanagementservice.spi.ISkillRepositoryPort;
import reactivechallenge.pragma.skillmanagementservice.spi.ITechnologyServicePort;
import reactivechallenge.pragma.skillmanagementservice.usecase.CreateSkillUseCase;
import reactivechallenge.pragma.skillmanagementservice.usecase.ListSkillUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public IRegisterSkillServicePort creatorRegisterSkillServicePort(ISkillRepositoryPort skillRepositoryPort,
                                                                     ITechnologyServicePort technologyServicePort){
        return new CreateSkillUseCase(skillRepositoryPort, technologyServicePort);
    }

    @Bean
    public IRetrieveSkillsServicePort creatorRetrieverSkillServicePort(ISkillRepositoryPort skillRepositoryPort) {
        return new ListSkillUseCase(skillRepositoryPort);
    }
}
