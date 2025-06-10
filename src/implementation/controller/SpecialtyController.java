package implementation.controller;

import java.util.Scanner;

import implementation.model.Specialty;
import shared.repository.SpecialtyRepository;
import utility.Input;
import utility.UserInterface;

public class SpecialtyController {
    public static void addSpecialty(Scanner scanner) {
        while (true) {
            UserInterface.update("Add a Specialty");
            System.out.println("*) Enter 0 to exit\n");

            int specialtyId = -1, specialtyMaxSlots;
            String specialtyName;

            // specialty id input
            while (specialtyId == -1) {
                Input _specialtyId = new Input(scanner, "Enter specialty's ID (in number): ")
                                        .isNotEmpty().isNumeric().isPositiveNumber().validate();
                if (_specialtyId.isExit()) return;
                specialtyId = _specialtyId.getInteger();

                if (SpecialtyRepository.findById(specialtyId) != null) {
                    UserInterface.warning("A specialty with this ID already exist!");
                    specialtyId = -1;
                }
            }

            // specialty name input
            Input _specialtyName = new Input(scanner, "Enter specialty's name: ")
                                    .isNotEmpty().isAlphabetic().validate();
            if (_specialtyName.isExit()) return;
            specialtyName = _specialtyName.get();

            // specialty max slots input
            Input _specialtyMaxSlots = new Input(scanner, "Enter specialty's maximum number of appointments allowed: ")
                                            .isNotEmpty().isNumeric().isPositiveNumber().validate();
            if (_specialtyMaxSlots.isExit()) return;
            specialtyMaxSlots = _specialtyMaxSlots.getInteger();

            Specialty newSpecialty = new Specialty(specialtyId, specialtyName, specialtyMaxSlots);
            SpecialtyRepository.add(newSpecialty);

            System.out.println();

            UserInterface.success("Successfully added new specialty!");
            UserInterface.enter(scanner);
        }
    }

    public static void findSpecialty(Scanner scanner) {
        while (true) {
            UserInterface.update("Find a Specialty by ID");
            System.out.println("*) Enter 0 to exit\n");

            Specialty foundSpecialty = null;

            // specialty id input
            while (foundSpecialty == null) {
                Input _specialtyId = new Input(scanner, "Enter specialty's ID (in number): ")
                                        .isNotEmpty().isNumeric().validate();
                if (_specialtyId.isExit()) return;
                int specialtyId = _specialtyId.getInteger();
                foundSpecialty = SpecialtyRepository.findById(specialtyId);

                if (foundSpecialty == null) {
                    UserInterface.warning("Can't find a specialty with this ID!");
                }
            }
            
            System.out.println();
            System.out.println(foundSpecialty);
            
            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void removeSpecialty(Scanner scanner) {
        while (true) {
            UserInterface.update("Remove a Specialty");
            System.out.println("*) Enter 0 to exit\n");

            int specialtyId = -1;

            // specialty id input
            while (specialtyId == -1) {
                Input _specialtyId = new Input(scanner, "Enter specialty's ID (in number): ")
                                        .isNotEmpty().isNumeric().validate();
                if (_specialtyId.isExit()) return;
                specialtyId = _specialtyId.getInteger();

                if (SpecialtyRepository.findById(specialtyId) == null) {
                    UserInterface.warning("Can't find a specialty with this ID!");
                    specialtyId = -1;
                }
            }

            SpecialtyRepository.remove(specialtyId);

            System.out.println();

            UserInterface.success("Successfully removed specialty " + UserInterface.colorize("#" + specialtyId, UserInterface.YELLOW) + "!");
            UserInterface.enter(scanner);
        }
    }

    public static void viewSpecialties(Scanner scanner) {
        UserInterface.update("View All Specialties");

        SpecialtyRepository.viewAll();

        UserInterface.enter(scanner);
        return;
    }
}
