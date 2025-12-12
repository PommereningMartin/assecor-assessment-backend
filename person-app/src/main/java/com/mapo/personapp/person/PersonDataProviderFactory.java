package com.mapo.personapp.person;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PersonDataProviderFactory {
    private final Map<PersonDataProviderType, PersonDataProvider> providerType;

    public PersonDataProviderFactory(List<PersonDataProvider> providers) {
        this.providerType = providers.stream().collect(Collectors.toMap(PersonDataProvider::getProviderType, p -> p));
    }

    public PersonDataProvider build(PersonDataProviderType providerType) {
        PersonDataProvider processor = this.providerType.get(providerType);
        if (processor == null) {
            throw new IllegalArgumentException("No provider found for type: " + providerType);
        }
        return processor;
    };
}
