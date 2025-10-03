package ua.edu.ukma.event_management_system;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Test to verify Spring Modulith is working correctly.
 */
class ModulithTest {

    @Test
    void verifyModularArchitecture() {
        ApplicationModules modules = ApplicationModules.of(EventManagementSystemApplication.class);
        
        System.out.println("Spring Modulith Modules");
        modules.forEach(module -> {
            System.out.println("Module: " + module.getName());
            System.out.println("  Display Name: " + module.getDisplayName());
            System.out.println("  Base Package: " + module.getBasePackage());
            System.out.println("---");
        });
    }
}
