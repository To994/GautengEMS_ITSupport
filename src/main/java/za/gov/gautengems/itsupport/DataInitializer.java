package za.gov.gautengems.itsupport;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import za.gov.gautengems.itsupport.entity.User;
import za.gov.gautengems.itsupport.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User();

                admin.setPersonalNumber("EMS000001");
                admin.setFirstName("IT");
                admin.setSurname("Administrator");
                admin.setEmail("admin@ems.gov.za");
                admin.setPhone("");
                admin.setDepartment("IT Management");
                admin.setUsername("admin");

                admin.setPassword(
                        passwordEncoder.encode("Admin@123")
                );

                admin.setRole(User.Role.ADMIN);
                admin.setActive(true);

                userRepository.save(admin);

                System.out.println("====================================");
                System.out.println("EMS ADMIN ACCOUNT CREATED");
                System.out.println("Username: admin");
                System.out.println("Password: Admin@123");
                System.out.println("====================================");
            }
        };
    }
}