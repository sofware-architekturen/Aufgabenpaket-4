package de.hskl.itanalyst.buchservice.DemoDataGenerator;

import com.github.javafaker.Faker;
import de.hskl.itanalyst.buchservice.domain.model.AddressEntity;
import de.hskl.itanalyst.buchservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.buchservice.domain.model.BookEntity;
import de.hskl.itanalyst.buchservice.domain.model.PublisherEntity;
import de.hskl.itanalyst.buchservice.repository.AddressRepository;
import de.hskl.itanalyst.buchservice.repository.AuthorRepository;
import de.hskl.itanalyst.buchservice.repository.BookRepository;
import de.hskl.itanalyst.buchservice.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
@Profile("!test")
public class DataGenerator {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @PostConstruct
    @Transactional
    public void postConstructFakeData() {
        System.out.println("Starting to create fake data...");

        if(bookRepository.count() > 0) {
            System.out.println("Data already available. Stopping data generation.");
            return;
        }

        final Faker faker = new Faker();
        final Random random = new SecureRandom();
        final int numberOfBooks = random.nextInt(30) + 10;

        addressRepository.deleteAll();
        publisherRepository.deleteAll();
        authorRepository.deleteAll();
        bookRepository.deleteAll();

        for (int i = 0; i < numberOfBooks; i++) {
            final int numOfAuthors = random.nextInt(2) + 1;

            AddressEntity addressEntity = new AddressEntity(faker.address().cityName());

            PublisherEntity publisherEntity = new PublisherEntity(addressEntity, faker.company().name());

            final Set<AuthorEntity> authorEntityList = new HashSet<>(numOfAuthors);
            for (int j = 0; j < numOfAuthors; j++) {
                final AuthorEntity authorEntity = new AuthorEntity(faker.name().firstName(), faker.name().lastName());
                authorEntityList.add(authorEntity);
            }

            BookEntity bookEntity = new BookEntity(publisherEntity, authorEntityList, faker.book().title(), random.nextInt(50));
            bookEntity = bookRepository.save(bookEntity);
            publisherRepository.save(publisherEntity);
        }

        System.out.println("Fake data generated ...");
    }
}
