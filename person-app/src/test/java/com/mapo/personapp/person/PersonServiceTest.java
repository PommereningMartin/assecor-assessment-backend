package com.mapo.personapp.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonDataProviderFactory factory;

    @Mock
    private PersonDataProvider provider;

    private PersonService personService;

    private Person testPerson1;
    private Person testPerson2;

    @BeforeEach
    void setUp() {
        when(factory.build(PersonDataProviderType.CSV)).thenReturn(provider);
        personService = new PersonService(factory);

        testPerson1 = new Person("John", "Doe", "12345", "Berlin", "blau");
        testPerson1.setId(1L);

        testPerson2 = new Person("Jane", "Smith", "54321", "Munich", "rot");
        testPerson2.setId(2L);
    }

    @Test
    void constructor_ShouldBuildProviderWithCSVType() {
        verify(factory).build(PersonDataProviderType.CSV);
    }

    @Test
    void all_ShouldReturnAllPersonsFromProvider() {
        List<Person> expectedPersons = Arrays.asList(testPerson1, testPerson2);
        when(provider.all()).thenReturn(expectedPersons);

        List<Person> result = personService.all();

        assertEquals(2, result.size());
        assertEquals(expectedPersons, result);
        verify(provider).all();
    }

    @Test
    void all_ShouldReturnEmptyListWhenNoPersons() {
        when(provider.all()).thenReturn(List.of());

        List<Person> result = personService.all();

        assertTrue(result.isEmpty());
        verify(provider).all();
    }

    @Test
    void findById_ShouldReturnPersonWhenFound() {
        when(provider.findById(1L)).thenReturn(testPerson1);

        Person result = personService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstname());
        assertEquals("Doe", result.getLastname());
        verify(provider).findById(1L);
    }

    @Test
    void findById_ShouldThrowExceptionWhenNotFound() {
        when(provider.findById(999L)).thenThrow(new PersonNotFoundException(999L));

        assertThrows(PersonNotFoundException.class, () -> personService.findById(999L));
        verify(provider).findById(999L);
    }

    @Test
    void findByColor_ShouldReturnPersonsWithMatchingColor() {
        List<Person> bluePersons = List.of(testPerson1);
        when(provider.findByColor("blau")).thenReturn(bluePersons);

        List<Person> result = personService.findByColor("blau");

        assertEquals(1, result.size());
        assertEquals("blau", result.get(0).getColor());
        verify(provider).findByColor("blau");
    }

    @Test
    void findByColor_ShouldReturnEmptyListWhenNoMatch() {
        when(provider.findByColor("grün")).thenReturn(List.of());

        List<Person> result = personService.findByColor("grün");

        assertTrue(result.isEmpty());
        verify(provider).findByColor("grün");
    }

    @Test
    void findByColor_ShouldReturnMultiplePersonsWithSameColor() {
        Person testPerson3 = new Person("Bob", "Brown", "99999", "Frankfurt", "blau");
        testPerson3.setId(3L);
        List<Person> bluePersons = Arrays.asList(testPerson1, testPerson3);
        when(provider.findByColor("blau")).thenReturn(bluePersons);

        List<Person> result = personService.findByColor("blau");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> "blau".equals(p.getColor())));
        verify(provider).findByColor("blau");
    }

    @Test
    void save_ShouldSaveAndReturnPerson() {
        Person newPerson = new Person("Alice", "Johnson", "11111", "Hamburg", "gelb");
        Person savedPerson = new Person("Alice", "Johnson", "11111", "Hamburg", "gelb");
        savedPerson.setId(3L);

        when(provider.save(newPerson)).thenReturn(savedPerson);

        Person result = personService.save(newPerson);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Alice", result.getFirstname());
        assertEquals("Johnson", result.getLastname());
        verify(provider).save(newPerson);
    }

    @Test
    void save_ShouldCallProviderSaveMethod() {
        Person newPerson = new Person("Test", "Person", "00000", "City", "rot");
        when(provider.save(newPerson)).thenReturn(newPerson);

        personService.save(newPerson);

        verify(provider, times(1)).save(newPerson);
    }
}
