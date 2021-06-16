package io.github.metheax.mgt.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import io.github.metheax.mgt.data.service.TAccountRepository;
import io.github.metheax.mgt.data.entity.TAccount;
import io.github.metheax.mgt.data.service.TGroupRepository;
import io.github.metheax.mgt.data.entity.TGroup;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.artur.exampledata.DataType;
import org.vaadin.artur.exampledata.ExampleDataGenerator;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(TAccountRepository tAccountRepository, TGroupRepository tGroupRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (tAccountRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 T Account entities...");
            ExampleDataGenerator<TAccount> tAccountRepositoryGenerator = new ExampleDataGenerator<>(TAccount.class,
                    LocalDateTime.of(2021, 6, 15, 0, 0, 0));
            tAccountRepositoryGenerator.setData(TAccount::setId, DataType.ID);
            tAccountRepositoryGenerator.setData(TAccount::setFirstName, DataType.FIRST_NAME);
            tAccountRepositoryGenerator.setData(TAccount::setLastName, DataType.LAST_NAME);
            tAccountRepositoryGenerator.setData(TAccount::setEmail, DataType.EMAIL);
            tAccountRepositoryGenerator.setData(TAccount::setPhone, DataType.PHONE_NUMBER);
            tAccountRepository.saveAll(tAccountRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 T Group entities...");
            ExampleDataGenerator<TGroup> tGroupRepositoryGenerator = new ExampleDataGenerator<>(TGroup.class,
                    LocalDateTime.of(2021, 6, 15, 0, 0, 0));
            tGroupRepositoryGenerator.setData(TGroup::setId, DataType.ID);
            tGroupRepositoryGenerator.setData(TGroup::setFirstName, DataType.FIRST_NAME);
            tGroupRepositoryGenerator.setData(TGroup::setLastName, DataType.LAST_NAME);
            tGroupRepositoryGenerator.setData(TGroup::setEmail, DataType.EMAIL);
            tGroupRepositoryGenerator.setData(TGroup::setPhone, DataType.PHONE_NUMBER);
            tGroupRepository.saveAll(tGroupRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}