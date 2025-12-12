package com.mapo.personapp.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbPersonDataProviderTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private DbPersonDataProvider dbPersonDataProvider;

    private Person testPerson1;
    private Person testPerson2;

    @BeforeEach
    void setUp() {
        testPerson1 = new Person("John", "Doe", "12345", "Berlin", "blau");
        testPerson1.setId(1L);

        testPerson2 = new Person("Jane", "Smith", "54321", "Munich", "rot");
        testPerson2.setId(2L);
    }

    @Test
    void getProviderType_ShouldReturnDB() {
        PersonDataProviderType result = dbPersonDataProvider.getProviderType();

        assertEquals(PersonDataProviderType.DB, result);
    }

    @Test
    void all_ShouldReturnAllPersonsFromRepository() {
        List<Person> expectedPersons = Arrays.asList(testPerson1, testPerson2);
        when(personRepository.findAll()).thenReturn(expectedPersons);

        List<Person> result = dbPersonDataProvider.all();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedPersons, result);
        verify(personRepository).findAll();
    }

    @Test
    void all_ShouldReturnEmptyListWhenNoPersons() {
        when(personRepository.findAll()).thenReturn(List.of());

        List<Person> result = dbPersonDataProvider.all();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(personRepository).findAll();
    }

    @Test
    void save_ShouldSaveAndReturnPerson() {
        Person newPerson = new Person("Alice", "Johnson", "11111", "Hamburg", "gelb");
        Person savedPerson = new Person("Alice", "Johnson", "11111", "Hamburg", "gelb");
        savedPerson.setId(3L);

        when(personRepository.save(newPerson)).thenReturn(savedPerson);

        Person result = dbPersonDataProvider.save(newPerson);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Alice", result.getFirstname());
        assertEquals("Johnson", result.getLastname());
        verify(personRepository).save(newPerson);
    }

    @Test
    void save_ShouldCallRepositorySaveOnce() {
        Person person = new Person("Test", "User", "00000", "TestCity", "grün");
        when(personRepository.save(person)).thenReturn(person);

        dbPersonDataProvider.save(person);

        verify(personRepository, times(1)).save(person);
    }

    @Test
    void findById_ShouldReturnPersonWhenFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson1));

        Person result = dbPersonDataProvider.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstname());
        assertEquals("Doe", result.getLastname());
        verify(personRepository).findById(1L);
    }

    @Test
    void findById_ShouldThrowExceptionWhenNotFound() {
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        PersonNotFoundException exception = assertThrows(PersonNotFoundException.class, () -> {
            dbPersonDataProvider.findById(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
        verify(personRepository).findById(999L);
    }

    @Test
    void findByColor_ShouldReturnPersonsWithMatchingColor() {
        List<Person> bluePersons = List.of(testPerson1);
        when(personRepository.findByColor("blau")).thenReturn(bluePersons);

        List<Person> result = dbPersonDataProvider.findByColor("blau");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("blau", result.get(0).getColor());
        verify(personRepository).findByColor("blau");
    }

    @Test
    void findByColor_ShouldReturnEmptyListWhenNoMatch() {
        when(personRepository.findByColor("grün")).thenReturn(List.of());

        List<Person> result = dbPersonDataProvider.findByColor("grün");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(personRepository).findByColor("grün");
    }

    @Test
    void findByColor_ShouldReturnMultiplePersonsWithSameColor() {
        Person testPerson3 = new Person("Bob", "Brown", "99999", "Frankfurt", "blau");
        testPerson3.setId(3L);
        List<Person> bluePersons = Arrays.asList(testPerson1, testPerson3);

        when(personRepository.findByColor("blau")).thenReturn(bluePersons);

        List<Person> result = dbPersonDataProvider.findByColor("blau");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> "blau".equals(p.getColor())));
        verify(personRepository).findByColor("blau");
    }

    @Test
    void save_ShouldHandlePersonWithoutId() {
        Person personWithoutId = new Person("New", "Person", "22222", "City", "violett");
        Person savedPersonWithId = new Person("New", "Person", "22222", "City", "violett");
        savedPersonWithId.setId(10L);

        when(personRepository.save(personWithoutId)).thenReturn(savedPersonWithId);

        Person result = dbPersonDataProvider.save(personWithoutId);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(10L, result.getId());
        verify(personRepository).save(personWithoutId);
    }

    @Test
    void save_ShouldHandlePersonUpdate() {
        Person existingPerson = new Person("John", "Doe", "12345", "Berlin", "blau");
        existingPerson.setId(1L);

        Person updatedPerson = new Person("John", "Doe", "12345", "Berlin", "rot");
        updatedPerson.setId(1L);

        when(personRepository.save(updatedPerson)).thenReturn(updatedPerson);

        Person result = dbPersonDataProvider.save(updatedPerson);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("rot", result.getColor());
        verify(personRepository).save(updatedPerson);
    }
}
