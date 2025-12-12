package com.mapo.personapp.person;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonService {
    private final PersonDataProvider provider;

    public PersonService(PersonDataProviderFactory factory) {
        this.provider = factory.build(PersonDataProviderType.CSV);
    }

    public List<Person> all() {
        return this.provider.all();
    }

    public Person findById(Long id) {
        return this.provider.findById(id);
    }

    public List<Person> findByColor(String color) {
        return this.provider.findByColor(color);
    }

    public Person save(Person newPerson) {
        return this.provider.save(newPerson);
    }
}
