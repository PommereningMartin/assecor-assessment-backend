package com.mapo.personapp.person;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.*;
import java.util.*;

@Component
public class CsvPersonDataProvider implements PersonDataProvider {
    private final List<Person> personData;
    private static final Map<Integer, String> COLOR_MAP = new HashMap<>();

    static {
        COLOR_MAP.put(1, "blau");
        COLOR_MAP.put(2, "grün");
        COLOR_MAP.put(3, "violett");
        COLOR_MAP.put(4, "rot");
        COLOR_MAP.put(5, "gelb");
        COLOR_MAP.put(6, "türkis");
        COLOR_MAP.put(7, "weiß");
    }

    public CsvPersonDataProvider() {
        this.personData = loadObjectList();
        System.out.println("Loaded data"+this.personData);
    }

    private List<Person> loadObjectList() {
        ClassPathResource resource = new ClassPathResource("sample-input.csv");
        List<Person> persons = new ArrayList<>();
        Long lineNumber = 1L;

        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile().getAbsolutePath()))) {
            String line;
            StringBuilder currentLine = new StringBuilder();

            while ((line = br.readLine()) != null) {
                currentLine.append(line.trim());

                // Check if line ends with a number (complete entry)
                if (currentLine.toString().matches(".*,\\s*\\d+\\s*$")) {
                    String[] parts = currentLine.toString().split(",");

                    if (parts.length >= 4) {
                        String lastName = parts[0].trim();
                        String firstName = parts[1].trim();
                        String address = parts[2].trim();
                        int colorCode = Integer.parseInt(parts[3].trim());

                        // Split address into zipcode and city
                        String zipcode = "";
                        String city = "";
                        String[] addressParts = address.split("\\s+", 2);
                        if (addressParts.length >= 1) {
                            zipcode = addressParts[0];
                        }
                        if (addressParts.length >= 2) {
                            city = addressParts[1];
                        }

                        String color = COLOR_MAP.getOrDefault(colorCode, "unknown");
                        Person person = new Person(firstName, lastName, zipcode, city, color);
                        person.setId(lineNumber);
                        persons.add(person);
                        lineNumber++;
                    }

                    currentLine = new StringBuilder();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert to JSON using Jackson
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for (Person person : persons) {
            ObjectNode personNode = mapper.createObjectNode();
            personNode.put("id", person.getId());
            personNode.put("lastName", person.getFirstname());
            personNode.put("firstName", person.getLastname());
            personNode.put("zipcode", person.getZipcode());
            personNode.put("city", person.getCity());
            personNode.put("color", person.getColor());
            arrayNode.add(personNode);
        }
        return persons;
    }

    @Override
    public List<Person> all() {
        return this.personData;
    }

    @Override
    public Person save(Person person) {
        // TODO: implement an csv write to handle save
        return null;
    }

    @Override
    public Person findById(Long id) {
        return this.personData.stream().
                filter(item -> item.getId().equals(id)).findFirst().orElseThrow(() -> new PersonNotFoundException(id));
    }

    @Override
    public List<Person> findByColor(String color) {
        return this.personData.stream().
                filter(item -> item.getColor().equals(color)).toList();
    }

    @Override
    public PersonDataProviderType getProviderType() {
        return PersonDataProviderType.CSV;
    }
}
