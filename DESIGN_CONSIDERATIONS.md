# Design Considerations

This document outlines the SOLID design principles and object-oriented concepts applied to our **Internship Placement Management System**, demonstrating how the codebase exemplifies clean architecture and maintainable design.

---

## Table of Contents

1. [Object-Oriented Programming (OOP) Principles](#object-oriented-programming-oop-principles)
   - [Encapsulation](#1-encapsulation)
   - [Inheritance](#2-inheritance)
   - [Polymorphism](#3-polymorphism)
   - [Abstraction](#4-abstraction)
   - [Composition](#5-composition)
   - [Aggregation](#6-aggregation)
2. [SOLID Principles](#solid-principles)
   - [Single Responsibility Principle (SRP)](#1-single-responsibility-principle-srp)
   - [Open/Closed Principle (OCP)](#2-openclosed-principle-ocp)
   - [Liskov Substitution Principle (LSP)](#3-liskov-substitution-principle-lsp)
   - [Interface Segregation Principle (ISP)](#4-interface-segregation-principle-isp)
   - [Dependency Inversion Principle (DIP)](#5-dependency-inversion-principle-dip)
3. [MVC Architecture Pattern](#mvc-architecture-pattern)

---

## Object-Oriented Programming (OOP) Principles

### 1. Encapsulation

**1. Private Fields with Public Accessors**

All entity classes encapsulate their data with private fields and controlled access:

```java
public class Student extends User {
    // Private fields - data hiding
    private int yearOfStudy;
    private Major major;
    private List<String> appliedInternships;
    private String acceptedInternshipID;
    
    // Public getters - controlled read access
    public int getYearOfStudy() {
        return yearOfStudy;
    }
    
    public Major getMajor() {
        return major;
    }
    
    // Defensive copying to protect internal state
    public List<String> getAppliedInternships() {
        return new ArrayList<>(appliedInternships);
    }
    
    // Public setters with validation
    public void setYearOfStudy(int yearOfStudy) {
        if (yearOfStudy >= 1 && yearOfStudy <= 4) {
            this.yearOfStudy = yearOfStudy;
        }
    }
}
```

**2. Business Logic Encapsulation**

Business rules are encapsulated within entities:

```java
public class Student extends User {
    // Encapsulated business logic
    public boolean canApplyForMore() {
        return appliedInternships.size() < 3 && acceptedInternshipID == null;
    }
    
    public boolean applyForInternship(String internshipID) {
        if (!canApplyForMore()) {
            return false;
        }
        appliedInternships.add(internshipID);
        return true;
    }
    
    public boolean acceptInternship(String internshipID) {
        if (appliedInternships.contains(internshipID) && acceptedInternshipID == null) {
            this.acceptedInternshipID = internshipID;
            return true;
        }
        return false;
    }
}
```

```java
public class InternshipOpportunity {
    private int filledSlots;
    private int totalSlots;
    private LocalDate openingDate;
    private LocalDate closingDate;
    
    // Encapsulated validation logic
    public boolean isOpenForApplications() {
        LocalDate today = LocalDate.now();
        return status == InternshipStatus.APPROVED && 
               visible && 
               !today.isBefore(openingDate) && 
               !today.isAfter(closingDate) &&
               filledSlots < totalSlots;
    }
    
    public boolean isStudentEligible(Student student) {
        // Major matching logic encapsulated
        if (preferredMajor != student.getMajor() && preferredMajor != Major.OTHER) {
            return false;
        }
        
        // Year eligibility logic encapsulated
        if (student.getYearOfStudy() <= 2 && level != InternshipLevel.BASIC) {
            return false;
        }
        
        return true;
    }
}
```

**3. Password Protection**

Sensitive data like passwords are encapsulated with validation:

```java
public abstract class User {
    private String password;  // Private - cannot be accessed directly
    
    // Validation encapsulated in method
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    // Controlled password change with validation
    public void changePassword(String newPassword) {
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            this.password = newPassword;
        }
    }
}
```

**4. Repository Encapsulation**

Data storage mechanisms are hidden behind repository interfaces:

```java
public class UserRepository {
    // Private storage - implementation hidden
    private Map<String, User> users;
    
    public UserRepository() {
        this.users = new HashMap<>();
    }
    
    // Public interface for data access
    public User getById(String userID) {
        return users.get(userID);
    }
    
    public boolean add(User user) {
        if (user == null || users.containsKey(user.getUserID())) {
            return false;
        }
        users.put(user.getUserID(), user);
        return true;
    }
    
    // Internal storage structure hidden from clients
    public Map<String, User> getAll() {
        return users;
    }
}
```

#### Benefits

1. **Data Protection**: Internal state cannot be corrupted by external code
2. **Flexibility**: Implementation can change without affecting clients
3. **Maintainability**: Changes localized to the class
4. **Control**: Validation and business rules enforced at access points

---

### 2. Inheritance

**1. User Hierarchy**

The core user inheritance structure:

```java
// Abstract superclass
public abstract class User {
    // Common attributes inherited by all users
    protected String userID;
    protected String name;
    protected String password;
    protected String email;
    
    // Common concrete methods inherited by all users
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    
    // Abstract methods - subclasses must implement
    public abstract String getUserType();
    public abstract String getRole();
}
```

```java
// Student "is-a" User
public class Student extends User {
    // Additional attributes specific to Student
    private int yearOfStudy;
    private Major major;
    private List<String> appliedInternships;
    private String acceptedInternshipID;
    
    // Constructor calls super
    public Student(String userID, String name, String password, String email,
                   int yearOfStudy, Major major) {
        // Inherit initialization from parent
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
        
        // Initialize student-specific fields
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.appliedInternships = new ArrayList<>();
    }
    
    // Implement abstract methods
    @Override
    public String getUserType() {
        return "STUDENT";
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
    
    // Student-specific methods
    public boolean canApplyForMore() { ... }
    public boolean applyForInternship(String internshipID) { ... }
}
```

```java
// CompanyRepresentative "is-a" User
public class CompanyRepresentative extends User {
    // Additional attributes specific to CompanyRepresentative
    private String companyName;
    private boolean isApproved;
    private List<String> createdInternships;
    
    // Implement abstract methods
    @Override
    public String getUserType() {
        return "COMPANY_REPRESENTATIVE";
    }
    
    @Override
    public String getRole() {
        return "Company Representative";
    }
    
    // Company representative-specific methods
    public boolean canCreateMore() { ... }
    public boolean addCreatedInternship(String internshipID) { ... }
}
```

```java
// CareerCenterStaff "is-a" User
public class CareerCenterStaff extends User {
    // Only needs to implement abstract methods
    // No additional attributes needed
    
    @Override
    public String getUserType() {
        return "CAREER_CENTER_STAFF";
    }
    
    @Override
    public String getRole() {
        return "Career Center Staff";
    }
}
```

**2. BaseMenu Inheritance**

Menu hierarchy using inheritance:

```java
// Abstract base class for all menus
public abstract class BaseMenu {
    protected SystemManager systemManager;
    protected User currentUser;
    protected Scanner scanner;
    
    // Constructor
    public BaseMenu(SystemManager systemManager, User currentUser) {
        this.systemManager = systemManager;
        this.currentUser = currentUser;
        this.scanner = new Scanner(System.in);
    }
    
    // Common methods inherited by all menus
    protected void handlePasswordChange() { ... }
    protected void pauseForUser() { ... }
    protected int getIntInput(String prompt, int min, int max) { ... }
    protected String getStringInput(String prompt, boolean required) { ... }
    protected boolean confirmAction(String message) { ... }
    protected void displayUserInfo() { ... }
    
    // Abstract method - each menu implements differently
    public abstract void displayMenu();
}
```

```java
// StudentMenu "is-a" BaseMenu
public class StudentMenu extends BaseMenu {
    public StudentMenu(SystemManager systemManager, User currentUser) {
        super(systemManager, currentUser);  // Call parent constructor
    }
    
    @Override
    public void displayMenu() {
        // Student-specific menu implementation
        // Inherits all utility methods from BaseMenu
        Student student = (Student) currentUser;
        
        while (true) {
            displayUserInfo();  // Inherited method
            System.out.println("\n=== Student Menu ===");
            // ... menu options ...
            
            int choice = getIntInput("Enter choice: ", 0, 6);  // Inherited method
            // ... handle choices ...
        }
    }
}
```

#### Benefits

1. **Code Reuse**: Common functionality defined once in superclass
2. **Extensibility**: New user types easily added by extending User
3. **Polymorphism Enabler**: Inheritance enables polymorphic behavior
4. **Logical Hierarchy**: Models real-world "is-a" relationships

---

### 3. Polymorphism

**1. Method Overriding (Runtime Polymorphism)**

Subclasses provide specific implementations of abstract methods:

```java
// Polymorphic method calls
User user1 = new Student(...);
User user2 = new CompanyRepresentative(...);
User user3 = new CareerCenterStaff(...);

// Same method call, different behavior based on actual object type
System.out.println(user1.getUserType());  // Outputs: "STUDENT"
System.out.println(user2.getUserType());  // Outputs: "COMPANY_REPRESENTATIVE"
System.out.println(user3.getUserType());  // Outputs: "CAREER_CENTER_STAFF"

System.out.println(user1.getRole());  // Outputs: "Student"
System.out.println(user2.getRole());  // Outputs: "Company Representative"
System.out.println(user3.getRole());  // Outputs: "Career Center Staff"
```

**2. Polymorphic Collections**

Collections can hold different user types:

```java
public class UserRepository {
    // Single collection holds all user types polymorphically
    private Map<String, User> users;
    
    public User getById(String userID) {
        // Returns User reference, actual type determined at runtime
        return users.get(userID);
    }
}
```

```java
// Polymorphic iteration
Map<String, User> allUsers = userRepository.getAll();
for (User user : allUsers.values()) {
    // Polymorphic method calls work for all user types
    System.out.println(user.getName() + " - " + user.getRole());
}
```

**3. Polymorphic Method Parameters**

Methods accept superclass type, work with any subclass:

```java
public class UserManager {
    private UserRepository userRepository;
    
    // Accepts any User subtype
    public boolean changePassword(String userID, String oldPassword, String newPassword) {
        User user = userRepository.getById(userID);  // Polymorphic return
        
        if (user == null) {
            return false;
        }
        
        // Polymorphic method calls
        if (user.validatePassword(oldPassword)) {
            user.changePassword(newPassword);
            return true;
        }
        return false;
    }
}
```

**4. Polymorphic Menu Display**

Different menu types handled polymorphically:

```java
public class MainMenu {
    public void showMenu() {
        User user = systemManager.getUserManager().login(userID, password);
        
        if (user == null) {
            System.out.println("Invalid credentials");
            return;
        }
        
        // Polymorphic menu selection based on user type
        BaseMenu menu = null;
        
        if (user instanceof Student) {
            menu = new StudentMenu(systemManager, user);
        } else if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            if (rep.isApproved()) {
                menu = new CompanyRepresentativeMenu(systemManager, user);
            }
        } else if (user instanceof CareerCenterStaff) {
            menu = new CareerCenterStaffMenu(systemManager, user);
        }
        
        if (menu != null) {
            // Polymorphic call - actual implementation depends on menu type
            menu.displayMenu();
        }
    }
}
```

**5. Type-Specific Behavior with instanceof**

Safe downcasting for type-specific operations:

```java
// Polymorphic storage, type-specific access when needed
User user = userRepository.getById(userID);

if (user instanceof Student) {
    Student student = (Student) user;
    // Access student-specific methods
    if (student.canApplyForMore()) {
        student.applyForInternship(internshipID);
    }
} else if (user instanceof CompanyRepresentative) {
    CompanyRepresentative rep = (CompanyRepresentative) user;
    // Access rep-specific methods
    if (rep.canCreateMore()) {
        rep.addCreatedInternship(internshipID);
    }
}
```

#### Benefits

1. **Flexibility**: Single interface for multiple implementations
2. **Extensibility**: New types added without modifying existing code
3. **Simplification**: Reduces conditional logic and type checking
4. **Maintainability**: Changes isolated to specific implementations

---

### 4. Abstraction

**Definition:** *Abstraction is the process of hiding implementation details and showing only essential features of an object. It focuses on what an object does rather than how it does it.*

#### Implementation in Our System

**1. Abstract User Class**

User defines the essential interface without implementation details:

```java
// Abstract class provides abstraction
public abstract class User {
    // Concrete attributes and methods
    protected String userID;
    protected String name;
    protected String password;
    
    // Concrete behavior available to all users
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    // Abstract methods - define "what" without "how"
    public abstract String getUserType();
    public abstract String getRole();
    
    // The "how" is defined by concrete subclasses
}
```

**2. Manager Layer Abstraction**

Managers provide high-level operations hiding complex logic:

```java
// ApplicationManager abstracts complex application logic
public class ApplicationManager {
    // Complex internal dependencies hidden
    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private InternshipRepository internshipRepository;
    private IdGenerator idGenerator;
    
    // Simple public interface - hides complexity
    public Application submitApplication(String studentID, String internshipID) {
        // Complex logic hidden from caller:
        // 1. Validate student exists and is eligible
        // 2. Validate internship exists and is open
        // 3. Check student hasn't already applied
        // 4. Check student application limit
        // 5. Generate unique application ID
        // 6. Create application object
        // 7. Update student's applied internships
        // 8. Store application
        
        // Caller doesn't need to know these details
        // Just calls one method and gets result
    }
    
    // High-level operation hides multi-step process
    public boolean approveApplication(String applicationID, String repID) {
        // Hidden complexity:
        // - Validate application exists
        // - Validate rep owns the internship
        // - Update application status
        // - Update internship filled slots
        // - Notify student (future enhancement)
    }
}
```

**3. Repository Pattern as Abstraction**

Repositories abstract data storage mechanism:

```java
// UserRepository abstracts storage details
public class UserRepository {
    // How data is stored is hidden (could be Map, List, Database, etc.)
    private Map<String, User> users;
    
    // Simple, abstract interface for data access
    public User getById(String userID) { ... }
    public boolean add(User user) { ... }
    public boolean remove(String userID) { ... }
    public Map<String, User> getAll() { ... }
    
    // Client code doesn't know or care about internal storage
}
```

```java
// DataManager abstracts file I/O complexity
public class DataManager {
    // Public interface - simple and abstract
    public void loadAllData() {
        // Hides details: file paths, parsing, error handling
        loadUsers();
        loadInternships();
        loadApplications();
    }
    
    public void saveAllData() {
        // Hides details: formatting, writing, error handling
        saveUsers();
        saveInternships();
        saveApplications();
    }
    
    // Private methods contain actual implementation
    private void loadUsers() {
        // Complex file reading logic hidden
    }
}
```

**4. SystemManager as Facade**

SystemManager abstracts the entire system:

```java
// SystemManager provides abstract, high-level interface
public class SystemManager {
    // Complex internal structure hidden
    private UserRepository userRepository;
    private InternshipRepository internshipRepository;
    private ApplicationRepository applicationRepository;
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    
    // Simple public interface
    public UserManager getUserManager() { return userManager; }
    public InternshipManager getInternshipManager() { return internshipManager; }
    public ApplicationManager getApplicationManager() { return applicationManager; }
    
    // High-level operations
    public void initializeSystem() {
        // Complex initialization hidden
        dataManager.loadAllData();
        System.out.println("System initialized successfully");
    }
    
    public void saveSystem() {
        // Complex save logic hidden
        dataManager.saveAllData();
        System.out.println("Data saved successfully");
    }
}
```

**5. Enum Abstraction**

Enums abstract status management:

```java
// Status management abstracted into enum
public enum ApplicationStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    WITHDRAWN("Withdrawn");
    
    private final String displayName;
    
    // Internal implementation hidden
    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    // Simple interface for display
    public String getDisplayName() {
        return displayName;
    }
}

// Usage - no need to know internal representation
Application app = new Application(...);
app.setStatus(ApplicationStatus.PENDING);

// Display abstracted
System.out.println("Status: " + app.getStatus().getDisplayName());
```

#### Benefits

1. **Simplification**: Complex operations reduced to simple method calls
2. **Separation of Concerns**: Interface separated from implementation
3. **Flexibility**: Implementation can change without affecting clients
4. **Reduced Complexity**: Users work with high-level concepts

---

### 5. Composition

**1. SystemManager Composition**

SystemManager is composed of managers and repositories:

```java
public class SystemManager {
    // SystemManager "has-a" UserRepository (strong ownership)
    private final UserRepository userRepository;
    
    // SystemManager "has-a" InternshipRepository
    private final InternshipRepository internshipRepository;
    
    // SystemManager "has-a" ApplicationRepository
    private final ApplicationRepository applicationRepository;
    
    // SystemManager "has-a" managers
    private final UserManager userManager;
    private final InternshipManager internshipManager;
    private final ApplicationManager applicationManager;
    private final DataManager dataManager;
    
    // SystemManager "has-a" utility
    private final IdGenerator idGenerator;
    
    private SystemManager() {
        // Create owned components - they exist because SystemManager exists
        this.userRepository = new UserRepository();
        this.internshipRepository = new InternshipRepository();
        this.applicationRepository = new ApplicationRepository();
        
        this.idGenerator = new IdGenerator(internshipRepository, applicationRepository);
        
        // Composed objects created and owned by SystemManager
        this.userManager = new UserManager(userRepository);
        this.internshipManager = new InternshipManager(
            internshipRepository, userRepository, idGenerator);
        this.applicationManager = new ApplicationManager(
            applicationRepository, userRepository, internshipRepository, idGenerator);
        this.dataManager = new DataManager(
            userRepository, internshipRepository, applicationRepository, userManager);
    }
    
    // When SystemManager is destroyed, all composed objects are destroyed
}
```

**2. Manager Composition**

Managers are composed of repositories:

```java
public class ApplicationManager {
    // ApplicationManager "has-a" ApplicationRepository
    private ApplicationRepository applicationRepository;
    
    // ApplicationManager "has-a" UserRepository
    private UserRepository userRepository;
    
    // ApplicationManager "has-a" InternshipRepository
    private InternshipRepository internshipRepository;
    
    // ApplicationManager "has-a" IdGenerator
    private IdGenerator idGenerator;
    
    // Dependencies injected but strongly tied to this manager
    public ApplicationManager(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            InternshipRepository internshipRepository,
            IdGenerator idGenerator) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.idGenerator = idGenerator;
    }
    
    // Manager uses composed objects to fulfill its responsibilities
    public Application submitApplication(String studentID, String internshipID) {
        User user = userRepository.getById(studentID);
        InternshipOpportunity internship = internshipRepository.getById(internshipID);
        String appID = idGenerator.generateApplicationID();
        
        // Uses all composed objects
        Application app = new Application(...);
        applicationRepository.add(app);
        return app;
    }
}
```

**3. Repository Composition**

Repositories are composed of data structures:

```java
public class UserRepository {
    // UserRepository "has-a" Map (strong ownership)
    private Map<String, User> users;
    
    public UserRepository() {
        // Create owned data structure
        this.users = new HashMap<>();
    }
    
    // Repository manages the lifecycle of the Map
    // When repository is destroyed, the Map is destroyed
}
```

**4. BaseMenu Composition**

Menus are composed of SystemManager and Scanner:

```java
public abstract class BaseMenu {
    // BaseMenu "has-a" SystemManager reference
    protected SystemManager systemManager;
    
    // BaseMenu "has-a" User reference
    protected User currentUser;
    
    // BaseMenu "has-a" Scanner (strong ownership)
    protected Scanner scanner;
    
    public BaseMenu(SystemManager systemManager, User currentUser) {
        this.systemManager = systemManager;
        this.currentUser = currentUser;
        // Create owned Scanner
        this.scanner = new Scanner(System.in);
    }
    
    // Uses composed objects in operations
    protected void handlePasswordChange() {
        String oldPassword = getStringInput("Enter old password: ", true);
        String newPassword = getStringInput("Enter new password: ", true);
        
        // Uses composed systemManager
        boolean success = systemManager.getUserManager()
            .changePassword(currentUser.getUserID(), oldPassword, newPassword);
    }
}
```

**5. Entity Composition**

Entities composed of other objects:

```java
public class Student extends User {
    // Student "has-a" Major (enum type - value composition)
    private Major major;
    
    // Student "has-a" List (strong ownership)
    private List<String> appliedInternships;
    
    public Student(..., Major major) {
        // ...
        this.major = major;
        // Create owned list
        this.appliedInternships = new ArrayList<>();
    }
    
    // When Student is destroyed, the list is destroyed
}
```

#### Benefits

1. **Strong Encapsulation**: Composed objects hidden within container
2. **Lifecycle Control**: Container controls creation and destruction
3. **Flexibility**: Easier to change composed objects than inheritance
4. **Code Reuse**: Combine existing classes to create new functionality

---

### 6. Aggregation


**1. Menu Aggregation of User**

Menus aggregate User (User exists independently of menu):

```java
public abstract class BaseMenu {
    protected SystemManager systemManager;  // Aggregation
    protected User currentUser;             // Aggregation
    
    public BaseMenu(SystemManager systemManager, User currentUser) {
        // Menu receives existing user, doesn't create it
        this.systemManager = systemManager;
        this.currentUser = currentUser;
    }
    
    // User exists before menu is created
    // User continues to exist after menu is destroyed
}
```

```java
// Usage shows aggregation
User user = userManager.login(userID, password);  // User created elsewhere

if (user instanceof Student) {
    // Menu aggregates user - doesn't own it
    StudentMenu menu = new StudentMenu(systemManager, user);
    menu.displayMenu();
    // When menu is destroyed, user still exists
}
```

**2. Student Aggregation of Internship IDs**

Student aggregates references to internships:

```java
public class Student extends User {
    // List of internship IDs (references, not ownership)
    private List<String> appliedInternships;
    private String acceptedInternshipID;
    
    public boolean applyForInternship(String internshipID) {
        // Stores reference to internship, doesn't own it
        appliedInternships.add(internshipID);
        return true;
    }
    
    // Internship exists independently in InternshipRepository
    // If student is deleted, internship still exists
}
```

**3. CompanyRepresentative Aggregation**

CompanyRepresentative aggregates internship references:

```java
public class CompanyRepresentative extends User {
    // References to created internships (not owned)
    private List<String> createdInternships;
    
    public boolean addCreatedInternship(String internshipID) {
        // Stores reference, doesn't create or own internship
        if (createdInternships.size() >= 5) {
            return false;
        }
        createdInternships.add(internshipID);
        return true;
    }
    
    // Internships exist independently
    // If rep is deleted, internships can still exist (based on business rules)
}
```

**4. Application Aggregation**

Application aggregates references to Student and Internship:

```java
public class Application {
    private String applicationID;
    
    // Aggregation - references to independent entities
    private String studentID;
    private String internshipID;
    
    private ApplicationStatus status;
    private LocalDate applicationDate;
    
    public Application(String applicationID, String studentID, String internshipID) {
        this.applicationID = applicationID;
        this.studentID = studentID;      // Reference, not ownership
        this.internshipID = internshipID; // Reference, not ownership
        this.applicationDate = LocalDate.now();
        this.status = ApplicationStatus.PENDING;
    }
    
    // Student and Internship exist independently
    // Application just holds references
}
```

**5. Manager Aggregation of Repositories**

While managers have strong ties to repositories in composition, they receive them as parameters (aggregation pattern):

```java
public class InternshipManager {
    // Aggregated repositories (passed in, not created)
    private InternshipRepository internshipRepository;
    private UserRepository userRepository;
    private IdGenerator idGenerator;
    
    // Constructor receives existing objects (aggregation)
    public InternshipManager(
            InternshipRepository internshipRepository,
            UserRepository userRepository,
            IdGenerator idGenerator) {
        // Doesn't create these objects, receives them
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
    }
    
    // Uses aggregated objects
    public InternshipOpportunity createInternship(...) {
        User user = userRepository.getById(repID);  // Uses aggregated repo
        // ...
    }
}
```

**6. InternshipFilterSettings Aggregation**

Filter settings aggregate enum values:

```java
public class InternshipFilterSettings {
    // Aggregates enum values (they exist independently)
    private InternshipStatus statusFilter;
    private Major majorFilter;
    private InternshipLevel levelFilter;
    private String companyFilter;
    
    public InternshipFilterSettings() {
        // Initially null - no ownership
        this.statusFilter = null;
        this.majorFilter = null;
        this.levelFilter = null;
        this.companyFilter = null;
    }
    
    // Setters receive existing values
    public void setStatusFilter(InternshipStatus status) {
        this.statusFilter = status;  // Reference, not ownership
    }
}
```

#### Benefits

1. **Loose Coupling**: Objects are independent and reusable
2. **Flexibility**: Objects can be shared between multiple containers
3. **Lifecycle Independence**: Objects exist beyond container lifetime
4. **Realistic Modeling**: Represents real-world relationships accurately

---

## SOLID Principles

### 1. Single Responsibility Principle (SRP)

**Definition:** *A class should have only one reason to change—it should have a single, well-defined responsibility.*

#### Implementation in Our System

**Separation by Layer:**

The system demonstrates SRP through clear separation across three architectural layers:

**Entity Layer** - Each entity has one responsibility:
- `User` (abstract): Manages authentication and common user properties
- `Student`: Handles student-specific data (year of study, major, applications)
- `CompanyRepresentative`: Manages company representative data (company, approval status)
- `CareerCenterStaff`: Represents administrative staff
- `InternshipOpportunity`: Represents internship postings
- `Application`: Represents application submissions

**Control Layer** - Each manager has one domain responsibility:
- `UserManager`: User authentication and registration
- `InternshipManager`: Internship CRUD operations and filtering
- `ApplicationManager`: Application lifecycle management
- `DataManager`: File I/O operations and data persistence
- `SystemManager`: Dependency injection and system coordination

**Boundary Layer** - Each menu has one user type responsibility:
- `MainMenu`: Login and system entry point
- `StudentMenu`: Student user interface
- `CompanyRepresentativeMenu`: Company representative interface
- `CareerCenterStaffMenu`: Staff interface
- `BaseMenu`: Common menu functionality (Template Method)

**Repository Classes** - Each repository manages one entity type:
- `UserRepository`: User data storage and retrieval
- `InternshipRepository`: Internship data storage and retrieval
- `ApplicationRepository`: Application data storage and retrieval

#### Code Examples

```java
// UserManager - Single Responsibility: User Management
public class UserManager {
    private UserRepository userRepository;
    
    public User login(String userID, String password) { ... }
    public boolean registerCompanyRepresentative(...) { ... }
    public boolean changePassword(String userID, String oldPass, String newPass) { ... }
    public List<Student> getAllStudents() { ... }
}
```

```java
// DataManager - Single Responsibility: Data Persistence
public class DataManager {
    public void loadAllData() { 
        loadUsers();
        loadInternships();
        loadApplications();
    }
    
    public void saveAllData() {
        saveUsers();
        saveInternships();
        saveApplications();
    }
}
```

```java
// ApplicationManager - Single Responsibility: Application Lifecycle
public class ApplicationManager {
    public Application submitApplication(String studentID, String internshipID) { ... }
    public boolean approveApplication(String applicationID, String repID) { ... }
    public boolean rejectApplication(String applicationID, String repID) { ... }
    public boolean acceptPlacement(String applicationID, String studentID) { ... }
    public boolean requestWithdrawal(String applicationID, String studentID, String reason) { ... }
}
```

#### Benefits

1. **Maintainability**: Changes to one responsibility don't affect others
2. **Testability**: Each class can be tested independently
3. **Clarity**: Easy to locate functionality
4. **Reduced Coupling**: Classes have minimal dependencies

---

### 2. Open/Closed Principle (OCP)

**Definition:** *Software entities should be open for extension but closed for modification.*

#### Implementation in Our System

**1. Abstract User Base Class**

The system is designed to accommodate new user types without modifying existing code:

```java
// Closed for modification
public abstract class User {
    protected String userID;
    protected String name;
    protected String password;
    
    // Common functionality
    public boolean validatePassword(String inputPassword) { ... }
    public void changePassword(String newPassword) { ... }
    
    // Abstract methods for extension
    public abstract String getUserType();
    public abstract String getRole();
}

// Open for extension - add new user types without modifying User
public class Student extends User {
    private int yearOfStudy;
    private Major major;
    
    @Override
    public String getUserType() { return "STUDENT"; }
    
    @Override
    public String getRole() { return "Student"; }
}

public class CompanyRepresentative extends User {
    private String companyName;
    private boolean isApproved;
    
    @Override
    public String getUserType() { return "COMPANY_REPRESENTATIVE"; }
    
    @Override
    public String getRole() { return "Company Representative"; }
}
```

**2. Enum-Based Type Safety**

Enums allow adding new values without breaking existing code:

```java
// Can add new statuses without modifying existing logic
public enum ApplicationStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    WITHDRAWN("Withdrawn");
    
    private final String displayName;
    
    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() { return displayName; }
}

// Filtering automatically handles new enum values
public List<Application> getApplicationsByStatus(ApplicationStatus status) {
    return applications.stream()
        .filter(app -> app.getStatus() == status)
        .collect(Collectors.toList());
}
```

**3. Template Method in BaseMenu**

BaseMenu provides extension points without modification:

```java
// Closed for modification
public abstract class BaseMenu {
    protected SystemManager systemManager;
    protected User currentUser;
    protected Scanner scanner;
    
    // Template method - subclasses implement
    public abstract void displayMenu();
    
    // Common functionality - not modified when extending
    protected void handlePasswordChange() { ... }
    protected void pauseForUser() { ... }
    protected int getIntInput(String prompt, int min, int max) { ... }
    protected boolean confirmAction(String message) { ... }
}

// Open for extension - new menus extend without modifying base
public class StudentMenu extends BaseMenu {
    @Override
    public void displayMenu() {
        // Student-specific menu implementation
    }
}
```

**4. Extensible Filtering**

Filter methods use parameters to support extension:

```java
// Adding new filter criteria doesn't require modifying the method
public List<InternshipOpportunity> filterInternships(
    InternshipStatus statusFilter,
    Major majorFilter,
    InternshipLevel levelFilter,
    String companyFilter
) {
    return internships.stream()
        .filter(i -> statusFilter == null || i.getStatus() == statusFilter)
        .filter(i -> majorFilter == null || i.getPreferredMajor() == majorFilter)
        .filter(i -> levelFilter == null || i.getLevel() == levelFilter)
        .filter(i -> companyFilter == null || i.getCompanyName().contains(companyFilter))
        .collect(Collectors.toList());
}
```

#### Benefits

1. **Easy Extension**: New features added through inheritance/composition
2. **Reduced Risk**: Existing code unchanged, so existing functionality protected
3. **Polymorphism**: Different implementations can coexist
4. **Future-Proof**: System grows without breaking changes

---

### 3. Liskov Substitution Principle (LSP)

**Definition:** *Derived classes must be substitutable for their base classes without affecting program correctness.*

#### Implementation in Our System

**1. User Polymorphism**

All User subclasses maintain the contract established by the base class:

```java
// Base class reference can hold any subtype
Map<String, User> users = new HashMap<>();

// All subtypes can be stored polymorphically
users.put("S001", new Student(...));
users.put("CR001", new CompanyRepresentative(...));
users.put("ST001", new CareerCenterStaff(...));

// Common operations work for all subtypes
User user = userRepository.getById(userID);
if (user.validatePassword(password)) {
    System.out.println("Welcome, " + user.getName());
    System.out.println("Role: " + user.getRole());
}
```

**2. Consistent Method Behavior**

All subclasses provide implementations that honor the base contract:

```java
// User defines contract
public abstract class User {
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    public abstract String getUserType();
    public abstract String getRole();
}

// Student honors contract - returns non-null string
public class Student extends User {
    @Override
    public String getUserType() {
        return "STUDENT";
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
}

// CompanyRepresentative honors contract - returns non-null string
public class CompanyRepresentative extends User {
    @Override
    public String getUserType() {
        return "COMPANY_REPRESENTATIVE";
    }
    
    @Override
    public String getRole() {
        return "Company Representative";
    }
}
```

**3. Type-Safe Downcasting**

When specific behavior is needed, safe type checking is used:

```java
// Polymorphic handling with safe downcasting
User user = userManager.login(userID, password);

if (user instanceof Student) {
    Student student = (Student) user;
    StudentMenu menu = new StudentMenu(systemManager, student);
    menu.displayMenu();
} else if (user instanceof CompanyRepresentative) {
    CompanyRepresentative rep = (CompanyRepresentative) user;
    if (rep.isApproved()) {
        CompanyRepresentativeMenu menu = new CompanyRepresentativeMenu(systemManager, rep);
        menu.displayMenu();
    }
} else if (user instanceof CareerCenterStaff) {
    CareerCenterStaff staff = (CareerCenterStaff) user;
    CareerCenterStaffMenu menu = new CareerCenterStaffMenu(systemManager, staff);
    menu.displayMenu();
}
```

**4. No Violated Contracts**

Subclasses don't weaken postconditions or strengthen preconditions:

```java
// Base class allows password changes
public void changePassword(String newPassword) {
    this.password = newPassword;
}

// Subclasses don't add restrictions (e.g., don't require admin approval)
// All User subclasses inherit this behavior without modification
```

#### Benefits

1. **Polymorphic Code**: Can write code that works with base type
2. **Type Safety**: Compile-time checks ensure correct usage
3. **Predictable Behavior**: Subclasses behave as expected
4. **Reduced Conditionals**: Less need for type checking

---

### 4. Interface Segregation Principle (ISP)

**Definition:** *Clients should not be forced to depend on interfaces they do not use.*

#### Implementation in Our System

**1. Minimal Abstract Methods**

Abstract classes only define essential methods that all subclasses need:

```java
// User abstract class - only 2 abstract methods
public abstract class User {
    // Concrete methods all users need
    public boolean validatePassword(String password) { ... }
    public void changePassword(String newPassword) { ... }
    public String getUserID() { ... }
    public String getName() { ... }
    
    // Only essential abstract methods
    public abstract String getUserType();
    public abstract String getRole();
}
```

```java
// BaseMenu abstract class - only 1 abstract method
public abstract class BaseMenu {
    // Concrete utility methods
    protected void handlePasswordChange() { ... }
    protected void pauseForUser() { ... }
    protected int getIntInput(String prompt, int min, int max) { ... }
    protected String getStringInput(String prompt, boolean required) { ... }
    protected boolean confirmAction(String message) { ... }
    
    // Only essential abstract method
    public abstract void displayMenu();
}
```

**2. Role-Specific Methods in Subclasses**

Each subclass only implements methods relevant to its role:

```java
// Student-specific methods only in Student class
public class Student extends User {
    public boolean applyForInternship(String internshipID) { ... }
    public boolean acceptInternship(String internshipID) { ... }
    public boolean withdrawFromInternship(String internshipID) { ... }
    public boolean canApplyForMore() { ... }
    public List<String> getAppliedInternships() { ... }
}

// CompanyRepresentative-specific methods only in CompanyRepresentative
public class CompanyRepresentative extends User {
    public boolean addCreatedInternship(String internshipID) { ... }
    public boolean removeCreatedInternship(String internshipID) { ... }
    public boolean canCreateMore() { ... }
    public boolean isApproved() { ... }
}

// CareerCenterStaff has no additional methods beyond User base
public class CareerCenterStaff extends User {
    // Only implements required abstract methods
    // No forced implementation of irrelevant methods
}
```

**3. Focused Manager Interfaces**

Each manager exposes only relevant operations:

```java
// ApplicationManager - only application-related methods
public class ApplicationManager {
    public Application submitApplication(String studentID, String internshipID) { ... }
    public boolean approveApplication(String appID, String repID) { ... }
    public boolean rejectApplication(String appID, String repID) { ... }
    public List<Application> getApplicationsByStudent(String studentID) { ... }
    // No user management methods
    // No internship creation methods
}

// InternshipManager - only internship-related methods
public class InternshipManager {
    public InternshipOpportunity createInternship(...) { ... }
    public boolean updateInternship(String id, ...) { ... }
    public boolean deleteInternship(String id) { ... }
    public List<InternshipOpportunity> getVisibleInternships(...) { ... }
    // No user authentication methods
    // No application approval methods
}
```

**4. Utility Helper Classes**

Specialized utilities avoid interface bloat:

```java
// FilterUIHelper - focused on filter input only
public class FilterUIHelper {
    public static void applyFilters(InternshipFilterSettings settings, Scanner scanner) { ... }
    public static void displayCurrentFilters(InternshipFilterSettings settings) { ... }
    // Only filter-related methods
}

// IdGenerator - focused on ID generation only
public class IdGenerator {
    public String generateInternshipID() { ... }
    public String generateApplicationID() { ... }
    // Only ID generation methods
}
```

#### Benefits

1. **No Interface Pollution**: Classes don't implement unnecessary methods
2. **Clear Responsibilities**: Each interface/abstract class has focused purpose
3. **Easy to Understand**: Smaller interfaces are easier to grasp
4. **Reduced Coupling**: Changes to one interface don't affect unrelated classes

---

### 5. Dependency Inversion Principle (DIP)

**Definition:** *High-level modules should not depend on low-level modules. Both should depend on abstractions.*

#### Implementation in Our System

**1. Layered Architecture with Clear Dependencies**

The system follows a three-tier architecture where dependencies point toward abstractions:

```
Boundary Layer (High-level)
      ↓ (depends on)
Control Layer (Mid-level)
      ↓ (depends on)
Entity Layer (Abstractions)
```

**2. Dependency Injection in SystemManager**

SystemManager acts as a dependency injection container:

```java
public class SystemManager {
    // Singleton instance
    private static SystemManager instance;
    
    // Dependencies (repositories - data layer)
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final ApplicationRepository applicationRepository;
    
    // Utilities
    private final IdGenerator idGenerator;
    
    // Managers (business logic layer)
    private final UserManager userManager;
    private final InternshipManager internshipManager;
    private final ApplicationManager applicationManager;
    private final DataManager dataManager;
    
    // Private constructor - dependency injection
    private SystemManager() {
        // Initialize repositories
        this.userRepository = new UserRepository();
        this.internshipRepository = new InternshipRepository();
        this.applicationRepository = new ApplicationRepository();
        
        // Initialize utilities
        this.idGenerator = new IdGenerator(internshipRepository, applicationRepository);
        
        // Inject dependencies into managers
        this.userManager = new UserManager(userRepository);
        this.internshipManager = new InternshipManager(
            internshipRepository, userRepository, idGenerator);
        this.applicationManager = new ApplicationManager(
            applicationRepository, userRepository, internshipRepository, idGenerator);
        this.dataManager = new DataManager(
            userRepository, internshipRepository, applicationRepository, userManager);
    }
}
```

**3. Manager Dependencies**

Managers receive dependencies through constructors:

```java
// ApplicationManager depends on repositories (abstraction)
public class ApplicationManager {
    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private InternshipRepository internshipRepository;
    private IdGenerator idGenerator;
    
    // Constructor injection
    public ApplicationManager(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            InternshipRepository internshipRepository,
            IdGenerator idGenerator) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.idGenerator = idGenerator;
    }
    
    // Business logic uses injected dependencies
    public Application submitApplication(String studentID, String internshipID) {
        User user = userRepository.getById(studentID);
        InternshipOpportunity internship = internshipRepository.getById(internshipID);
        // ... logic
    }
}
```

```java
// InternshipManager depends on repositories
public class InternshipManager {
    private InternshipRepository internshipRepository;
    private UserRepository userRepository;
    private IdGenerator idGenerator;
    
    // Constructor injection
    public InternshipManager(
            InternshipRepository internshipRepository,
            UserRepository userRepository,
            IdGenerator idGenerator) {
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
    }
}
```

**4. Boundary Layer Depends on Control Layer**

Menus depend on SystemManager (high-level abstraction):

```java
// StudentMenu depends on SystemManager, not on individual repositories
public class StudentMenu extends BaseMenu {
    public StudentMenu(SystemManager systemManager, User currentUser) {
        super(systemManager, currentUser);
    }
    
    private void handleViewInternships() {
        Student student = (Student) currentUser;
        
        // Access through manager layer, not directly
        List<InternshipOpportunity> internships = 
            systemManager.getInternshipManager().getVisibleInternships(student);
            
        // Display internships
    }
    
    private void handleApplyForInternship() {
        // Access through manager layer
        Application app = systemManager.getApplicationManager()
            .submitApplication(currentUser.getUserID(), internshipID);
    }
}
```

**5. Repository Pattern as Abstraction**

Repositories provide abstraction over data storage:

```java
// Repository provides abstraction over storage mechanism
public class UserRepository {
    private Map<String, User> users;
    
    public User getById(String userID) { return users.get(userID); }
    public boolean add(User user) { ... }
    public boolean remove(String userID) { ... }
    public Map<String, User> getAll() { return users; }
}

// Managers depend on repository abstraction, not storage details
// Could replace HashMap with database without changing managers
```

#### Benefits

1. **Decoupling**: High-level code doesn't depend on low-level implementation details
2. **Testability**: Dependencies can be mocked or stubbed for testing
3. **Flexibility**: Implementation can change without affecting business logic
4. **Maintainability**: Changes isolated to specific layers

#### Architecture Diagram

```
┌─────────────────────────────────────────┐
│        Boundary Layer (UI)              │
│  MainMenu, StudentMenu, CompanyMenu,    │
│  BaseMenu, CareerCenterStaffMenu        │
└──────────────┬──────────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────────┐
│      Control Layer (Business Logic)     │
│  SystemManager (Facade + DI Container)  │
│  ├─ UserManager                         │
│  ├─ InternshipManager                   │
│  ├─ ApplicationManager                  │
│  └─ DataManager                         │
└──────────────┬──────────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────────┐
│    Data Layer (Repositories)            │
│  UserRepository, InternshipRepository,  │
│  ApplicationRepository                  │
└──────────────┬──────────────────────────┘
               │ stores
               ↓
┌─────────────────────────────────────────┐
│      Entity Layer (Domain Models)       │
│  User, Student, CompanyRepresentative,  │
│  Application, InternshipOpportunity,    │
│  CareerCenterStaff                      │
└─────────────────────────────────────────┘
```
---

## MVC Architecture Pattern

**Definition:** *Model-View-Controller (MVC) is an architectural pattern that separates an application into three interconnected components: Model (data and business logic), View (user interface), and Controller (handles user input and coordinates between Model and View).*

### Implementation in Our System

While our system uses a **three-tier architecture** (Entity-Control-Boundary), it closely aligns with the MVC pattern with some adaptations for the console-based nature of the application.

#### Mapping to MVC Components

```
MVC Component          ?  Our System Package      ?  Responsibility
================================================================================
Model                  ?  entity/ + control/      ?  Data structures + Business logic
View                   ?  boundary/               ?  User interface and display
Controller             ?  control/ (Managers)     ?  Coordinates between View and Model
```

---

### Model Layer

**Components:** `entity/` package + Repository classes in `control/`

The Model represents the application's data and business logic. In our system, the Model is split between entity classes (domain objects) and repositories (data access).

**Key Classes:**
- **Entities:** `User`, `Student`, `CompanyRepresentative`, `CareerCenterStaff`, `InternshipOpportunity`, `Application`
- **Enums:** `ApplicationStatus`, `InternshipStatus`, `InternshipLevel`, `Major`
- **Repositories:** `UserRepository`, `InternshipRepository`, `ApplicationRepository`

**Responsibilities:**
- Encapsulate application data
- Implement business rules and validation
- Manage data persistence through repositories
- Provide data to controllers

---

### View Layer

**Components:** `boundary/` package

The View handles all user interface and presentation logic for the console-based application.

**Key Classes:**
- `MainMenu` - Entry point and login interface
- `StudentMenu` - Student-specific interface
- `CompanyRepresentativeMenu` - Company representative interface
- `CareerCenterStaffMenu` - Staff interface
- `BaseMenu` - Common menu functionality (Template Method pattern)
- `FilterUIHelper` - Utility for filter operations

**Responsibilities:**
- Display information to user (console output)
- Collect user input (console input)
- Format data for presentation
- Route user actions to appropriate controllers
- No business logic - only presentation logic

---

### Controller Layer

**Components:** Manager classes in `control/` package

The Controller handles user input from views, processes business logic, and coordinates between View and Model.

**Key Classes:**
- `SystemManager` - Facade and dependency injection container
- `UserManager` - User authentication and management
- `InternshipManager` - Internship operations
- `ApplicationManager` - Application lifecycle management
- `DataManager` - Data persistence operations

**Responsibilities:**
- Receive input from views
- Coordinate business logic operations
- Update model based on user actions
- Return data to views for display
- No UI code - only logic and coordination

---

### MVC Flow Example: Student Applies for Internship

```
1. VIEW (StudentMenu):
   - Displays list of available internships
   - Gets user selection (internship ID)
   - Calls controller method
   
2. CONTROLLER (ApplicationManager.submitApplication):
   - Validates student and internship exist
   - Checks eligibility rules
   - Creates Application object
   - Updates repositories
   
3. MODEL (Application, Student, InternshipOpportunity):
   - Application created with PENDING status
   - Student's appliedInternships list updated
   - Data persisted to repository
   
4. CONTROLLER returns result to VIEW:
   - Returns Application object or null
   
5. VIEW displays result:
   - "Application submitted successfully!" or
   - "Cannot apply: eligibility requirements not met"
```

---

