package com.website.bitsa;



import com.website.bitsa.model.*;
import com.website.bitsa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventTypeRepository eventTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Only seed if database is empty
        if (roleRepository.count() == 0) {
            seedRoles();
        }

        if (userRepository.count() == 0) {
            seedUsers();
        }

        if (categoryRepository.count() == 0) {
            seedCategories();
        }

        if (eventTypeRepository.count() == 0) {
            seedEventTypes();
        }

        System.out.println("✅ Database seeding completed!");
    }

    private void seedRoles() {
        System.out.println("🌱 Seeding roles...");

        // Create ADMIN role
        Role adminRole = Role.builder()
                .name("ADMIN")
                .description("Administrator with full system access")
                .canManageUsers(true)
                .canManagePosts(true)
                .canManageEvents(true)
                .canManageGallery(true)
                .canComment(true)
                .canRegisterEvents(true)
                .isActive(true)
                .build();
        roleRepository.save(adminRole);

        // Create STUDENT role
        Role studentRole = Role.builder()
                .name("STUDENT")
                .description("Regular student account")
                .canManageUsers(false)
                .canManagePosts(false)
                .canManageEvents(false)
                .canManageGallery(false)
                .canComment(true)
                .canRegisterEvents(true)
                .isActive(true)
                .build();
        roleRepository.save(studentRole);

        System.out.println("✅ Roles seeded: ADMIN, STUDENT");
    }

    private void seedUsers() {
        System.out.println("🌱 Seeding users...");

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Student role not found"));

        // Create Admin User
        User admin = User.builder()
                .name("BITSA Admin")
                .email("admin@bitsa.com")
                .password(passwordEncoder.encode("admin123"))
                .studentId("ADMIN001")
                .course("Information Technology")
                .year(4)
                .role(adminRole)
                .isActive(true)
                .isEmailVerified(true)
                .build();
        userRepository.save(admin);

        // Create Test Student User
        User student = User.builder()
                .name("John Doe")
                .email("student@bitsa.com")
                .password(passwordEncoder.encode("student123"))
                .studentId("BIT/2023/001")
                .course("Information Technology")
                .year(2)
                .role(studentRole)
                .isActive(true)
                .isEmailVerified(true)
                .build();
        userRepository.save(student);

        System.out.println("✅ Users seeded:");
        System.out.println("   - Admin: admin@bitsa.com / admin123");
        System.out.println("   - Student: student@bitsa.com / student123");
    }

    private void seedCategories() {
        System.out.println("🌱 Seeding blog categories...");

        Category tech = Category.builder()
                .name("Technology")
                .description("Latest tech trends, programming tips, and software updates")
                .slug("technology")
                .iconClass("fas fa-laptop-code")
                .colorHex("#3B82F6")
                .displayOrder(1)
                .isFeatured(true)
                .isActive(true)
                .postCount(0)
                .build();
        categoryRepository.save(tech);

        Category events = Category.builder()
                .name("Events")
                .description("BITSA events, meetups, and announcements")
                .slug("events")
                .iconClass("fas fa-calendar-alt")
                .colorHex("#10B981")
                .displayOrder(2)
                .isFeatured(true)
                .isActive(true)
                .postCount(0)
                .build();
        categoryRepository.save(events);

        Category tutorials = Category.builder()
                .name("Tutorials")
                .description("Step-by-step guides and learning resources")
                .slug("tutorials")
                .iconClass("fas fa-graduation-cap")
                .colorHex("#F59E0B")
                .displayOrder(3)
                .isFeatured(true)
                .isActive(true)
                .postCount(0)
                .build();
        categoryRepository.save(tutorials);

        Category news = Category.builder()
                .name("News")
                .description("BITSA news and general announcements")
                .slug("news")
                .iconClass("fas fa-newspaper")
                .colorHex("#EF4444")
                .displayOrder(4)
                .isFeatured(false)
                .isActive(true)
                .postCount(0)
                .build();
        categoryRepository.save(news);

        Category spotlight = Category.builder()
                .name("Student Spotlight")
                .description("Featuring outstanding BITSA members")
                .slug("student-spotlight")
                .iconClass("fas fa-star")
                .colorHex("#8B5CF6")
                .displayOrder(5)
                .isFeatured(false)
                .isActive(true)
                .postCount(0)
                .build();
        categoryRepository.save(spotlight);

        System.out.println("✅ Categories seeded: 5 categories");
    }

    private void seedEventTypes() {
        System.out.println("🌱 Seeding event types...");

        EventType workshop = EventType.builder()
                .name("Workshop")
                .description("Technical workshops and hands-on training sessions")
                .slug("workshop")
                .iconClass("fas fa-laptop-code")
                .colorHex("#3B82F6")
                .badgeColor("bg-blue-500")
                .displayOrder(1)
                .requiresRegistration(true)
                .hasCapacityLimit(true)
                .defaultDurationMinutes(120)
                .isFeatured(true)
                .isActive(true)
                .eventCount(0)
                .totalAttendees(0)
                .build();
        eventTypeRepository.save(workshop);

        EventType meeting = EventType.builder()
                .name("Meeting")
                .description("General BITSA meetings and discussions")
                .slug("meeting")
                .iconClass("fas fa-users")
                .colorHex("#10B981")
                .badgeColor("bg-green-500")
                .displayOrder(2)
                .requiresRegistration(false)
                .hasCapacityLimit(false)
                .defaultDurationMinutes(60)
                .isFeatured(true)
                .isActive(true)
                .eventCount(0)
                .totalAttendees(0)
                .build();
        eventTypeRepository.save(meeting);

        EventType competition = EventType.builder()
                .name("Competition")
                .description("Coding competitions, hackathons, and challenges")
                .slug("competition")
                .iconClass("fas fa-trophy")
                .colorHex("#F59E0B")
                .badgeColor("bg-yellow-500")
                .displayOrder(3)
                .requiresRegistration(true)
                .hasCapacityLimit(true)
                .defaultDurationMinutes(240)
                .isFeatured(true)
                .isActive(true)
                .eventCount(0)
                .totalAttendees(0)
                .build();
        eventTypeRepository.save(competition);

        EventType social = EventType.builder()
                .name("Social Event")
                .description("Networking events, meetups, and social gatherings")
                .slug("social-event")
                .iconClass("fas fa-glass-cheers")
                .colorHex("#EC4899")
                .badgeColor("bg-pink-500")
                .displayOrder(4)
                .requiresRegistration(false)
                .hasCapacityLimit(false)
                .defaultDurationMinutes(90)
                .isFeatured(false)
                .isActive(true)
                .eventCount(0)
                .totalAttendees(0)
                .build();
        eventTypeRepository.save(social);

        EventType hackathon = EventType.builder()
                .name("Hackathon")
                .description("Extended coding marathons and project development")
                .slug("hackathon")
                .iconClass("fas fa-code")
                .colorHex("#8B5CF6")
                .badgeColor("bg-purple-500")
                .displayOrder(5)
                .requiresRegistration(true)
                .hasCapacityLimit(true)
                .defaultDurationMinutes(480)
                .isFeatured(true)
                .isActive(true)
                .eventCount(0)
                .totalAttendees(0)
                .build();
        eventTypeRepository.save(hackathon);

        System.out.println("✅ Event types seeded: 5 types");
    }
}