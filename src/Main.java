import java.util.Scanner;

import implementation.view.*;
import shared.repository.AdminRepository;
import shared.repository.AppointmentRepository;
import shared.repository.DoctorRepository;
import shared.repository.PatientRepository;
import shared.repository.SpecialtyRepository;

// Driver class

public class Main {
    public static void main(String[] args) throws Exception {
        // load patient repository        
        SpecialtyRepository.load();
        PatientRepository.load();
        DoctorRepository.load();
        AdminRepository.load();
        AppointmentRepository.load();
        
        // initialize scanner for inputs
        Scanner scanner = new Scanner(System.in);
        
        // user login
        while (true) {
            LoginMenu.show(scanner);
            break;
        }

        scanner.close();
    }
}
