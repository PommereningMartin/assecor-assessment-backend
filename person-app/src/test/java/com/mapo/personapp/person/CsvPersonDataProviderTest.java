package com.mapo.personapp.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CsvPersonDataProviderTest {

    private CsvPersonDataProvider csvPersonDataProvider;

    @BeforeEach
    void setUp() {
        csvPersonDataProvider = new CsvPersonDataProvider();
    }

    @Test
    void getProviderType_ShouldReturnCSV() {
        PersonDataProviderType result = csvPersonDataProvider.getProviderType();

        assertEquals(PersonDataProviderType.CSV, result);
    }

    @Test
    void all_ShouldReturnNonEmptyList() {
        List<Person> result = csvPersonDataProvider.all();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void all_ShouldReturnPersonsWithValidData() {
        List<Person> result = csvPersonDataProvider.all();

        for (Person person : result) {
            assertNotNull(person.getId());
            assertNotNull(person.getFirstname());
            assertNotNull(person.getLastname());
            assertNotNull(person.getColor());
        }
    }

    @Test
    void all_ShouldReturnPersonsWithValidColorCodes() {
        List<Person> result = csvPersonDataProvider.all();

        List<String> validColors = List.of("blau", "grün", "violett", "rot", "gelb", "türkis", "weiß", "unknown");

        for (Person person : result) {
            assertTrue(validColors.contains(person.getColor()),
                    "Color " + person.getColor() + " is not in valid color list");
        }
    }

    @Test
    void findById_ShouldReturnPersonWhenExists() {
        List<Person> allPersons = csvPersonDataProvider.all();
        if (!allPersons.isEmpty()) {
            Long existingId = allPersons.get(0).getId();

            Person result = csvPersonDataProvider.findById(existingId);

            assertNotNull(result);
            assertEquals(existingId, result.getId());
        }
    }

    @Test
    void findById_ShouldThrowExceptionWhenNotFound() {
        assertThrows(PersonNotFoundException.class, () -> {
            csvPersonDataProvider.findById(999999L);
        });
    }

    @Test
    void findByColor_ShouldReturnPersonsWithMatchingColor() {
        List<Person> allPersons = csvPersonDataProvider.all();
        if (!allPersons.isEmpty()) {
            String existingColor = allPersons.get(0).getColor();

            List<Person> result = csvPersonDataProvider.findByColor(existingColor);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.stream().allMatch(p -> existingColor.equals(p.getColor())));
        }
    }

    @Test
    void findByColor_ShouldReturnEmptyListWhenNoMatch() {
        List<Person> result = csvPersonDataProvider.findByColor("nonexistentcolor");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByColor_ShouldReturnOnlyMatchingPersons() {
        String searchColor = "blau";
        List<Person> result = csvPersonDataProvider.findByColor(searchColor);

        for (Person person : result) {
            assertEquals(searchColor, person.getColor());
        }
    }

    @Test
    void save_ShouldReturnNull() {
        Person newPerson = new Person("Test", "Person", "12345", "TestCity", "blau");

        Person result = csvPersonDataProvider.save(newPerson);

        assertNull(result, "Save method should return null as it's not implemented yet");
    }

    @Test
    void all_ShouldReturnConsistentData() {
        List<Person> firstCall = csvPersonDataProvider.all();
        List<Person> secondCall = csvPersonDataProvider.all();

        assertEquals(firstCall.size(), secondCall.size());
        assertEquals(firstCall, secondCall);
    }

    @Test
    void all_ShouldReturnPersonsWithSequentialIds() {
        List<Person> persons = csvPersonDataProvider.all();

        for (int i = 0; i < persons.size(); i++) {
            assertEquals((long) (i + 1), persons.get(i).getId());
        }
    }

    @Test
    void findById_ShouldReturnCorrectPersonData() {
        List<Person> allPersons = csvPersonDataProvider.all();
        if (!allPersons.isEmpty()) {
            Person expected = allPersons.get(0);

            Person result = csvPersonDataProvider.findById(expected.getId());

            assertEquals(expected.getId(), result.getId());
            assertEquals(expected.getFirstname(), result.getFirstname());
            assertEquals(expected.getLastname(), result.getLastname());
            assertEquals(expected.getZipcode(), result.getZipcode());
            assertEquals(expected.getCity(), result.getCity());
            assertEquals(expected.getColor(), result.getColor());
        }
    }
}
