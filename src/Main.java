import java.util.Scanner;

import implementation.model.Admin;
import implementation.view.*;
import shared.repository.AdminRepository;
import shared.repository.DoctorRepository;
import shared.repository.PatientRepository;
import shared.repository.SpecialtyRepository;
import utility.Hasher;

// Driver class

public class Main {
    public static void main(String[] args) throws Exception {
        Admin newAdmin = new Admin(333, Hasher.hash("tes123"), "Johan");
        AdminRepository.add(newAdmin);
        // load patient repository        
        SpecialtyRepository.load();
        PatientRepository.load();
        DoctorRepository.load();
        AdminRepository.load();
        
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
