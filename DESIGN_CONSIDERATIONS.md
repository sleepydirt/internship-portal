# Design Considerations and OO Concepts

## Overview

This document provides a comprehensive analysis of the object-oriented design principles, patterns, and architectural decisions implemented in the Internship Placement Management System. The system demonstrates strong adherence to SOLID principles whilst maintaining simplicity and clarity appropriate for an educational project.

**Key System Features:**
- **User Management**: Three user types (Student, Company Representative, Career Centre Staff) with role-based functionality
- **Internship Management**: Creation, approval, and filtering of internship opportunities with eligibility rules
- **Application Workflow**: Complete application lifecycle including submission, review, acceptance/rejection, and withdrawal
- **Data Persistence**: Text-based file storage with graceful fallback to default data
- **Sample Data Support**: CSV files for initial system setup and testing

**Technologies & Approaches:**
- Java with object-oriented design patterns
- Command-line interface (CLI) with menu-driven navigation
- In-memory collections with file persistence
- Stream API for functional-style filtering and transformations
- Enum-based type safety for statuses and categories

## Table of Contents

1. [Object-Oriented Concepts Applied](#1-object-oriented-concepts-applied)
2. [Design Patterns Implementation](#2-design-patterns-implementation)
3. [System Architecture](#3-system-architecture)
4. [Extensibility Considerations](#4-extensibility-considerations)
5. [Maintainability Features](#5-maintainability-features)
6. [Trade-offs and Design Decisions](#6-trade-offs-and-design-decisions)
7. [Alternative Patterns Considered](#7-alternative-patterns-considered)
8. [Reflection on Design Choices](#8-reflection-on-design-choices)

---

## 1. Object-Oriented Concepts Applied

### 1.1 Encapsulation

**Implementation:**
- All entity classes (`User`, `Student`, `CompanyRepresentative`, `CareerCenterStaff`, `Application`, `InternshipOpportunity`) use private fields with controlled access through public getters and setters
- Business logic is encapsulated within appropriate classes (e.g., `Student.canApplyForMore()`, `InternshipOpportunity.isStudentEligible()`)
- Data validation occurs within entity methods rather than exposing raw data manipulation

**Example:**
```java
public class Student extends User {
    private int yearOfStudy;
    private Major major;
    private List<String> appliedInternships;
    private String acceptedInternshipID;
    
    public boolean canApplyForMore() {
        return appliedInternships.size() < 3 && acceptedInternshipID == null;
    }
}
```

**Benefits:**
- Data integrity is maintained through controlled access
- Implementation details can be changed without affecting client code
- Business rules are enforced consistently at the entity level

### 1.2 Inheritance

**Implementation:**
- `User` serves as an abstract base class for all user types
- Three concrete subclasses: `Student`, `CompanyRepresentative`, `CareerCenterStaff`
- Abstract methods (`getUserType()`, `getRole()`) enforce consistent interface across all user types
- `BaseMenu` abstract class provides common functionality for all menu types

**Class Hierarchy:**
```
User (abstract)
├── Student
├── CompanyRepresentative
└── CareerCenterStaff

BaseMenu (abstract)
├── StudentMenu
├── CompanyRepresentativeMenu
└── CareerCenterStaffMenu
```

**Benefits:**
- Code reusability through shared functionality in base classes
- Consistent interface across different user types
- Easy addition of new user types without modifying existing code

### 1.3 Polymorphism

**Implementation:**
- User objects are stored in a single collection (`Map<String, User>`) and handled polymorphically
- Runtime type identification using `instanceof` for type-specific operations
- Polymorphic method calls (`getUserType()`, `getRole()`) enable dynamic behaviour

**Example:**
```java
// Polymorphic storage
Map<String, User> users = new HashMap<>();

// Polymorphic method dispatch
public User authenticateUser(String userID, String password) {
    User user = systemManager.getUser(userID);
    if (user instanceof CompanyRepresentative) {
        CompanyRepresentative rep = (CompanyRepresentative) user;
        if (!rep.isApproved()) {
            return null;
        }
    }
    return user;
}
```

**Benefits:**
- Flexible handling of different user types through common interface
- Reduces code duplication in authentication and general user management
- Facilitates type-specific behaviour when needed

### 1.4 Abstraction

**Implementation:**
- Abstract `User` class defines common user properties and methods
- Abstract `BaseMenu` class defines common menu operations
- Enum classes (`Major`, `InternshipLevel`, `ApplicationStatus`, `InternshipStatus`) provide type-safe abstractions
- Manager classes abstract complex business operations from UI layer

**Benefits:**
- Hides implementation complexity from client code
- Provides clear contracts through abstract methods
- Simplifies system understanding through well-defined abstractions

**Key Business Rules Encapsulated:**
The system encapsulates important business rules within entity methods:
- `Student.canApplyForMore()`: Enforces 3-application limit and checks for accepted internships
- `InternshipOpportunity.isStudentEligible()`: Validates major and year-of-study requirements
- `InternshipOpportunity.isOpenForApplications()`: Checks status, visibility, dates, and slot availability
- `Application.canBeWithdrawn()`: Determines if withdrawal is allowed based on current status
- `CompanyRepresentative.isApproved()`: Controls access based on approval status

### 1.5 Composition and Aggregation

**Implementation:**
- `SystemManager` composes multiple manager objects (`UserManager`, `InternshipManager`, `ApplicationManager`, `DataManager`)
- `Student` aggregates a list of applied internship IDs
- `CompanyRepresentative` aggregates created internship IDs
- `InternshipOpportunity` aggregates applicant IDs

**Example:**
```java
public class SystemManager {
    private UserManager userManager;
    private InternshipManager internshipManager;
    private ApplicationManager applicationManager;
    private DataManager dataManager;
    
    private SystemManager() {
        userManager = new UserManager(this);
        internshipManager = new InternshipManager(this);
        applicationManager = new ApplicationManager(this);
        dataManager = new DataManager(this);
    }
}
```

**Benefits:**
- Separation of concerns through dedicated manager classes
- Flexible relationships between objects
- Managers can be modified independently without affecting the overall system

---

## 2. Design Patterns Implementation

### 2.1 Model-View-Controller (MVC) Pattern

**Implementation:**
- **Model:** Entity classes (`User`, `Student`, `Application`, `InternshipOpportunity`)
- **View:** Boundary classes (`MainMenu`, `StudentMenu`, `CompanyRepresentativeMenu`, `CareerCenterStaffMenu`)
- **Controller:** Control classes (`SystemManager`, `UserManager`, `InternshipManager`, `ApplicationManager`)

**Structure:**
```
Model (Entity Layer)
├── User, Student, CompanyRepresentative, CareerCenterStaff
├── Application
└── InternshipOpportunity

View (Boundary Layer)
├── MainMenu
├── StudentMenu
├── CompanyRepresentativeMenu
└── CareerCenterStaffMenu

Controller (Control Layer)
├── SystemManager
├── UserManager
├── InternshipManager
├── ApplicationManager
└── DataManager
```

**Benefits:**
- Clear separation of concerns
- UI can be changed (e.g., to GUI) without affecting business logic
- Business logic can be tested independently of UI
- Multiple views can use the same controller/model

### 2.2 Template Method Pattern

**Implementation:** `BaseMenu` abstract class

**Rationale:**
- Defines skeleton of menu operations in base class
- Allows subclasses to override specific steps without changing overall structure
- Provides common utilities (password change, input validation) to all menu types

**Code:**
```java
public abstract class BaseMenu {
    protected SystemManager systemManager;
    protected User currentUser;
    protected Scanner scanner;
    
    public abstract void displayMenu(); // Template method
    
    protected void handlePasswordChange() { /* common implementation */ }
    protected void pauseForUser() { /* common implementation */ }
    protected int getIntInput(String prompt, int min, int max) { /* common implementation */ }
}
```

**Benefits:**
- Eliminates code duplication across menu classes
- Enforces consistent user interaction patterns
- Easy to add new menu types with shared functionality

### 2.3 Factory Pattern (Lightweight)

**Implementation:** User creation in `DataManager` and `UserManager`

**Rationale:**
- Centralises object creation logic
- Encapsulates complex construction process (especially for data parsing from text files)
- Allows for different creation strategies (file-based vs. programmatic)

**Code:**
```java
// Factory-like methods in DataManager
private User parseUserFromLine(String line) { /* ... */ }
private InternshipOpportunity parseInternshipFromLine(String line) { /* ... */ }
private Application parseApplicationFromLine(String line) { /* ... */ }

// Factory-like methods in UserManager
public boolean registerCompanyRepresentative(String email, String name, ...) {
    CompanyRepresentative rep = new CompanyRepresentative(email, name, ...);
    return systemManager.addUser(rep);
}
```

**Benefits:**
- Centralises creation logic for consistency
- Handles complex initialisation (parsing, validation)
- Can easily add new user types or creation methods

### 2.4 Facade Pattern

**Implementation:** Manager classes provide simplified interfaces to complex subsystems

**Rationale:**
- `UserManager`, `InternshipManager`, and `ApplicationManager` hide complex business logic
- Boundary classes interact with high-level methods rather than directly manipulating entities
- Simplifies the interface for common operations

**Example:**
```java
// Complex operation hidden behind simple facade
public Application submitApplication(String studentID, String internshipID) {
    // Validates student eligibility
    // Checks internship availability
    // Creates application
    // Updates student and internship records
    // All in one method call
}
```

**Benefits:**
- Simplified interface for complex operations
- Reduces coupling between UI and business logic
- Easier to maintain and test business logic

---

## 3. System Architecture

### 3.1 Layered Architecture

The system follows a clear **three-tier architecture**:

**1. Presentation Layer (Boundary)**
- Handles user interaction
- Input validation and formatting
- Display of information
- Menu navigation

**2. Business Logic Layer (Control)**
- Implements business rules and workflows
- Coordinates operations across entities
- Manages data consistency
- Enforces authorization and permissions

**3. Data Layer (Entity + DataManager)**
- Represents domain objects
- Handles data persistence
- Manages relationships between entities

### 3.2 Package Structure

```
src/
├── Main.java                    # Application entry point
├── entity/                      # Domain models
│   ├── User.java
│   ├── Student.java
│   ├── CompanyRepresentative.java
│   ├── CareerCenterStaff.java
│   ├── Application.java
│   └── InternshipOpportunity.java
├── control/                     # Business logic
│   ├── SystemManager.java
│   ├── UserManager.java
│   ├── InternshipManager.java
│   ├── ApplicationManager.java
│   └── DataManager.java
├── boundary/                    # User interface
│   ├── BaseMenu.java
│   ├── MainMenu.java
│   ├── StudentMenu.java
│   ├── CompanyRepresentativeMenu.java
│   └── CareerCenterStaffMenu.java
└── enums/                       # Type-safe constants
    ├── Major.java
    ├── InternshipLevel.java
    ├── InternshipStatus.java
    └── ApplicationStatus.java

data/                            # Data persistence files
├── users.txt                    # Serialised user data
├── internships.txt              # Serialised internship data
└── applications.txt             # Serialised application data
```

### 3.3 Data Flow

```
User Input → Boundary Layer → Control Layer → Entity Layer → Data Storage
           ← Boundary Layer ← Control Layer ← Entity Layer ← Data Retrieval
```

**Example Flow (Student applies for internship):**
1. `StudentMenu` receives user input
2. Calls `ApplicationManager.submitApplication()`
3. `ApplicationManager` validates through multiple checks:
   - Gets `Student` and `InternshipOpportunity` entities from `SystemManager`
   - Checks eligibility using business rules (`student.canApplyForMore()`, `internship.isStudentEligible()`)
   - Creates `Application` entity with unique ID
4. Updates related entities:
   - `Student.applyForInternship()` adds internship to student's applied list
   - `InternshipOpportunity.addApplicant()` adds student to internship's applicant list
5. Stores application in `SystemManager` collections
6. Returns result to `StudentMenu` for display

### 3.4 Data Persistence Strategy

**Runtime Data Storage:**
- In-memory collections managed by `SystemManager` (Maps for users, internships, applications)
- Fast access and manipulation during application runtime
- Changes are persisted to text files on shutdown via `DataManager`

**Persistent Data Files:**
The system uses text-based serialisation for data persistence:
- `data/users.txt` - Serialised user data (Students, Company Representatives, Staff)
- `data/internships.txt` - Serialised internship opportunities
- `data/applications.txt` - Serialised application records

**Sample Data Files:**
The data folder also contains sample CSV files for initial system setup:
- `data/sample_student_list.csv` - Sample student data for testing
- `data/sample_staff_list.csv` - Sample staff data for testing  
- `data/sample_company_representative_list.csv` - Sample company representative data for testing

**Data Loading Process:**
1. On startup, `DataManager.loadAllData()` is called
2. Reads from text files and deserialises into entity objects
3. Populates `SystemManager` collections
4. If no data exists, creates default users for testing
5. On shutdown, `DataManager.saveAllData()` persists all changes

---

## 4. Extensibility Considerations

### 4.1 Adding New User Types

**Current Design Support:**
- New user types can extend `User` abstract class
- Implement required abstract methods (`getUserType()`, `getRole()`)
- Add corresponding menu by extending `BaseMenu`
- Update authentication logic in `MainMenu`

**Example Extension:**
```java
public class AlumniMentor extends User {
    private String company;
    private int graduationYear;
    
    @Override
    public String getUserType() { return "ALUMNI_MENTOR"; }
    
    @Override
    public String getRole() { return "Alumni Mentor"; }
}
```

**Minimal Changes Required:**
- Add case in `MainMenu.handleLogin()` switch statement
- Create `AlumniMentorMenu` extending `BaseMenu`
- Update `DataManager` parsing methods

### 4.2 Adding New Internship Types

**Current Design Support:**
- `InternshipLevel` enum can be extended with new values
- `InternshipStatus` enum manages internship lifecycle: `PENDING`, `APPROVED`, `REJECTED`, `FILLED`
- `InternshipOpportunity` is flexible enough to support variations
- Filtering logic automatically handles new enum values
- Eligibility checking is encapsulated in entity methods

**Current Implementation:**
```java
// Actual InternshipLevel enum
public enum InternshipLevel {
    BASIC("Basic"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");
}

// Actual InternshipStatus enum
public enum InternshipStatus {
    PENDING("Pending"),      // Awaiting staff approval
    APPROVED("Approved"),    // Approved and visible to students
    REJECTED("Rejected"),    // Rejected by staff
    FILLED("Filled");        // All slots filled
}

// Eligibility logic in InternshipOpportunity
public boolean isStudentEligible(Student student) {
    // Check major preference
    if (preferredMajor != student.getMajor() && preferredMajor != Major.OTHER) {
        return false;
    }
    
    // Check level eligibility based on year of study
    if (student.getYearOfStudy() <= 2 && level != InternshipLevel.BASIC) {
        return false; // Year 1-2 can only apply for basic level
    }
    
    return true;
}

// Check if internship is open for applications
public boolean isOpenForApplications() {
    LocalDate today = LocalDate.now();
    return status == InternshipStatus.APPROVED && 
           visible && 
           !today.isBefore(openingDate) && 
           !today.isAfter(closingDate) &&
           filledSlots < totalSlots;
}
```

**Example Extension:**
```java
// Add new levels
public enum InternshipLevel {
    BASIC("Basic"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    RESEARCH("Research"),        // New
    LEADERSHIP("Leadership");    // New
}

// Filtering automatically handles new values
List<InternshipOpportunity> filtered = internships.stream()
    .filter(i -> levelFilter == null || i.getLevel() == levelFilter)
    .collect(Collectors.toList());
```

### 4.3 Adding New Application Workflows

**Current Design Support:**
- `ApplicationStatus` enum supports multiple statuses: `PENDING`, `SUCCESSFUL`, `UNSUCCESSFUL`, `WITHDRAWN`
- `ApplicationManager` can be extended with new workflow methods
- Status transitions are centralised and easy to modify
- Application entity includes withdrawal workflow with approval mechanism

**Current Implementation:**
```java
// Actual ApplicationStatus enum
public enum ApplicationStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    WITHDRAWN("Withdrawn");
}

// Withdrawal workflow already implemented in Application entity
public class Application {
    private boolean withdrawalRequested;
    private boolean withdrawalApproved;
    private String withdrawalReason;
    
    public void requestWithdrawal(String reason) {
        this.withdrawalReason = reason;
        this.withdrawalRequested = true;
        this.withdrawalApproved = false;
    }
    
    public void approveWithdrawal() {
        this.withdrawalApproved = true;
        setStatus(ApplicationStatus.WITHDRAWN);
    }
    
    public boolean canBeWithdrawn() {
        return status == ApplicationStatus.PENDING || 
               status == ApplicationStatus.SUCCESSFUL;
    }
}
```

**Example Future Extension:**
```java
// Add interview stage to existing enum
public enum ApplicationStatus {
    PENDING, INTERVIEW_SCHEDULED, SUCCESSFUL, UNSUCCESSFUL, WITHDRAWN
}

// Add corresponding methods in ApplicationManager
public boolean scheduleInterview(String applicationID, LocalDateTime interviewTime) {
    Application app = systemManager.getApplication(applicationID);
    if (app != null && app.getStatus() == ApplicationStatus.PENDING) {
        app.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        app.setInterviewTime(interviewTime);
        return true;
    }
    return false;
}
```

### 4.4 Database Integration

**Current Design Facilitates Migration:**
- `DataManager` encapsulates all persistence logic in dedicated methods
- Entity classes are POJO-compatible (could easily add JPA annotations)
- Manager classes use collections that could be replaced with database queries
- Text-based serialisation can be replaced without affecting business logic

**Migration Path:**
```java
// Current file-based approach
public void loadUsers() { 
    // Read from users.txt and deserialise
    BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
    // Parse lines and create User objects
}

// Future database approach (minimal changes to DataManager only)
public void loadUsers() { 
    // JPA EntityManager query
    EntityManager em = emf.createEntityManager();
    List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
    // Populate SystemManager
}
```

**What Would Need to Change:**
- Only `DataManager` implementation
- Add JPA annotations to entity classes
- Add database configuration
- Business logic and UI remain unchanged

### 4.5 GUI Integration

**Current Design Facilitates GUI:**
- Business logic is completely separated from UI
- Manager classes provide clean APIs for GUI components
- Could implement Observer pattern for real-time updates

**Potential GUI Architecture:**
```
JavaFX Controllers → Manager Classes → Entity Classes
     ↓                    ↓                 ↓
  FXML Views        Business Logic    Domain Models
```

---

## 5. Maintainability Features

### 5.1 Code Organization

**Package-based Separation:**
- Clear boundaries between layers
- Easy to locate functionality
- Reduces cognitive load when navigating codebase

**Naming Conventions:**
- Descriptive class and method names
- Consistent naming patterns (e.g., `getXxx()`, `setXxx()`, `isXxx()`)
- Self-documenting code reduces need for excessive comments

### 5.2 Documentation

**Comprehensive Javadoc:**
- Every class has class-level documentation
- All public methods documented with parameters and return values
- Complex logic includes inline comments

**Example:**
```java
/**
 * Submit an application for an internship
 * @param studentID student ID
 * @param internshipID internship ID
 * @return application if successful, null otherwise
 */
public Application submitApplication(String studentID, String internshipID)
```

### 5.3 Error Handling

**Validation at Multiple Levels:**
- Input validation in boundary layer
- Business rule validation in control layer
- Data integrity validation in entity layer

**Graceful Degradation:**
- System creates default users if no data files exist
- Handles missing or corrupted data files gracefully
- Detailed error messages for debugging

**Example:**
```java
try {
    // Attempt operation
} catch (IOException e) {
    System.err.println("Error loading users: " + e.getMessage());
    // Graceful fallback
}
```

### 5.4 Consistent Patterns

**Standard CRUD Operations:**
- All manager classes follow similar patterns for create, read, update, delete
- Consistent method signatures across managers
- Predictable behaviour reduces learning curve

**Example Consistency:**
```java
// All managers follow similar patterns
userManager.getAllStudents()
internshipManager.getAllInternships(filter, filter, filter)
applicationManager.getApplicationsByStudent(studentID)
```

### 5.5 Testing Support

**Testable Design:**
- Manager classes accept dependencies (SystemManager) via constructor injection
- Business logic separated from I/O operations
- Pure functions for validation and filtering (e.g., `isStudentEligible()`, `canApplyForMore()`)
- Could easily introduce dependency injection framework for unit testing

**Example of Testable Design:**
```java
// ApplicationManager accepts dependency
public class ApplicationManager {
    private SystemManager systemManager;
    
    public ApplicationManager(SystemManager systemManager) {
        this.systemManager = systemManager;  // Dependency injection
    }
    
    // Business logic can be tested by providing mock SystemManager
    public Application submitApplication(String studentID, String internshipID) {
        // Pure business logic - no direct I/O
        User user = systemManager.getUser(studentID);
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        
        // Validation logic that can be tested
        if (!(user instanceof Student)) return null;
        Student student = (Student) user;
        if (!student.canApplyForMore()) return null;
        if (!internship.isStudentEligible(student)) return null;
        
        // Create application
        return new Application(applicationID, studentID, internshipID);
    }
}
```

**Default Test Data:**
The system includes default users for testing:
```java
// In UserManager.createDefaultUsers()
// Student: S001/password
// Company Rep: CR001/password  
// Staff: ST001/password
```

---

## 6. Trade-offs and Design Decisions

### 6.1 In-Memory Storage vs. Database

**Decision:** In-memory collections with file persistence

**Trade-offs:**
- ✅ **Pros:**
  - Simple implementation
  - Fast access without network overhead
  - No external dependencies
  - Suitable for assignment requirements

- ❌ **Cons:**
  - Limited scalability
  - No concurrent access support
  - Risk of data loss if save fails
  - No complex querying capabilities

**Justification:**
For an educational project demonstrating OOP concepts, file-based persistence is appropriate. The `DataManager` abstraction allows for future database integration without changing business logic.

**Alternative Considered:** Database (SQLite or H2)
- Would add external dependency
- Overkill for single-user CLI application demonstrating OOP concepts
- Could be implemented later by modifying only `DataManager`
- Current text-based approach is simpler for educational purposes and code review

### 6.2 Inheritance vs. Composition for User Types

**Decision:** Used inheritance with `User` base class

**Trade-offs:**
- ✅ **Pros:**
  - Natural "is-a" relationship (Student IS-A User)
  - Code reuse through inherited methods
  - Polymorphic storage and handling
  - Clear type hierarchy

- ❌ **Cons:**
  - Tight coupling to base class
  - Less flexible than pure composition
  - Changes to base class affect all subclasses

**Justification:**
User types have strong "is-a" relationships and share significant common functionality (authentication, password management). Inheritance is the natural choice here.

### 6.3 Validation Location

**Decision:** Distributed validation across layers

**Trade-offs:**
- ✅ **Pros:**
  - Input validation at boundary prevents invalid data entry
  - Business rule validation at control layer enforces policies
  - Entity validation ensures data integrity
  - Defence in depth approach

- ❌ **Cons:**
  - Some duplication of validation logic
  - Need to keep validations synchronised
  - Harder to track where validation occurs

**Justification:**
Multiple validation layers provide robustness and clear separation of concerns. Input validation differs from business rule validation, so separation is logical.

**Alternative Considered:** Centralised Validation Service
- Would reduce duplication
- But would create tight coupling to validation service
- Less clear where validation responsibility lies

### 6.4 Stream API vs. Traditional Loops

**Decision:** Used Java Stream API for filtering and transformations

**Trade-offs:**
- ✅ **Pros:**
  - More expressive and readable
  - Declarative style matches intent
  - Easily composable filters
  - Functional programming benefits

- ❌ **Cons:**
  - Slightly less performant for small collections
  - Requires Java 8+ knowledge
  - Debugging can be harder

**Justification:**
For an OOP course assignment, demonstrating modern Java practices is valuable. Streams make filtering logic much more readable and maintainable.

**Example:**
```java
// Stream approach (chosen)
return systemManager.getInternships().values().stream()
    .filter(internship -> internship.isVisible())
    .filter(internship -> internship.isStudentEligible(student))
    .collect(Collectors.toList());

// Traditional approach (alternative)
List<InternshipOpportunity> result = new ArrayList<>();
for (InternshipOpportunity internship : systemManager.getInternships().values()) {
    if (internship.isVisible() && internship.isStudentEligible(student)) {
        result.add(internship);
    }
}
return result;
```

### 6.5 Entity Relationships: IDs vs. Object References

**Decision:** Used String IDs for relationships between entities

**Trade-offs:**
- ✅ **Pros:**
  - Easier serialisation to files
  - No circular reference issues
  - Clearer ownership and lifecycle
  - Works well with lazy loading

- ❌ **Cons:**
  - Requires lookups to get related objects
  - No compile-time reference checking
  - Potential for orphaned references

**Justification:**
For file-based persistence, ID references are much simpler to serialise. The lookup overhead is negligible for small datasets, and the `SystemManager` provides efficient lookup methods.

**Example:**
```java
// Chosen approach: ID references (actual implementation)
public class Application {
    private String applicationID;
    private String studentID;
    private String internshipID;
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
    
    // Lookup required to get related objects from SystemManager
    // Student student = (Student) systemManager.getUser(studentID);
    // InternshipOpportunity internship = systemManager.getInternship(internshipID);
}

// Student maintains list of applied internship IDs
public class Student extends User {
    private List<String> appliedInternships;  // IDs, not objects
    private String acceptedInternshipID;      // ID reference
}

// InternshipOpportunity maintains list of applicant IDs
public class InternshipOpportunity {
    private List<String> applicantIDs;  // Student IDs who applied
}

// Alternative approach (not used): Direct references
public class Application {
    private Student student;              // Direct reference
    private InternshipOpportunity internship;  // Direct reference
    
    // Pros: No lookup needed, compile-time type safety
    // Cons: Circular references, serialisation complexity, tight coupling
}
```

---

## 7. Alternative Patterns Considered

### 7.1 Observer Pattern for Status Updates

**Not Implemented:**
Considered for notifying users of application status changes

**Why Not Chosen:**
- CLI application doesn't require real-time notifications
- Single-user context doesn't benefit from observer pattern
- Would add complexity without clear benefit
- Could be added later if GUI is implemented

**When It Would Be Useful:**
- GUI application with multiple windows
- Real-time dashboard updates
- Email notification system
- Multi-user concurrent access

### 7.2 Repository Pattern for Data Access

**Not Implemented:**
Considered for abstracting data access logic

**Why Not Chosen:**
- `DataManager` already provides similar abstraction
- Repository pattern is more valuable with database access
- Would add an extra layer without significant benefit
- Current approach is sufficient for file-based storage

**When It Would Be Useful:**
- Multiple data sources (database, file, API)
- Complex querying requirements
- Need for data access testing mocks
- Migration to ORM framework

### 7.3 Command Pattern for User Actions

**Not Implemented:**
Considered for encapsulating menu actions

**Why Not Chosen:**
- Menu actions are simple and don't require undo/redo
- No need for action queuing or logging
- Would significantly increase code complexity
- Direct method calls are more straightforward

**When It Would Be Useful:**
- Implementing undo/redo functionality
- Action logging and audit trails
- Macro recording for testing
- Remote execution of commands

### 7.4 State Pattern for Application Status

**Not Implemented:**
Considered for managing application state transitions

**Why Not Chosen:**
- Enum-based status management is simpler and sufficient
- Status transitions are straightforward (no complex state machines)
- Would require many additional classes
- Current approach is easier to understand and maintain

**When It Would Be Useful:**
- Complex state transitions with many rules
- State-specific behaviour that varies significantly
- Need for state transition validation
- Multiple concurrent state machines

### 7.5 Builder Pattern for Entity Creation

**Not Implemented:**
Considered for constructing complex entities

**Why Not Chosen:**
- Entities don't have enough optional parameters to justify builders
- Constructor parameters are reasonable in number
- Setters provide flexibility for optional fields
- Would add boilerplate code without clear benefit

**When It Would Be Useful:**
- Entities with 10+ parameters
- Many optional configuration options
- Immutable entity requirements
- Complex validation during construction

### 7.6 Decorator Pattern for User Permissions

**Not Implemented:**
Considered for adding dynamic permissions to users

**Why Not Chosen:**
- User types have static permissions
- No requirement for dynamic permission changes
- Type-based checking is simpler and more efficient
- Would over-complicate the design

**When It Would Be Useful:**
- Dynamic role assignment at runtime
- Temporary permission elevation
- Fine-grained permission management
- Role-based access control (RBAC) system

---

## 8. Reflection on Design Choices

### 8.1 What Worked Well

**1. MVC Architecture**
- Clear separation of concerns made development straightforward
- Easy to locate and modify specific functionality
- Business logic is completely testable without UI
- Could easily migrate to GUI framework

**2. Manager Classes (Facade Pattern)**
- Simplified complex operations into intuitive method calls
- Reduced coupling between UI and business logic
- Made the system much more maintainable
- Provided clear API for each functional area

**3. Inheritance Hierarchy**
- `User` base class eliminated significant code duplication
- Polymorphic handling simplified authentication and user management
- Abstract methods enforced consistent interface
- Easy to add new user types

**4. Enum-based Type Safety**
- Eliminated magic strings and constants
- Provided compile-time checking for status values
- Made code more self-documenting
- Simplified filtering and querying logic

### 8.2 What Could Be Improved

**1. Testing Support**
- Could benefit from explicit interfaces for better mocking
- Consider introducing dependency injection for easier testing
- Unit tests would benefit from more modular design
- **Future Improvement:** Introduce interfaces and DI container for better testability

**2. Error Handling**
- Some methods return `null` for failures
- Could use `Optional<T>` for clearer semantics
- Exception handling could be more granular
- **Future Improvement:** Use custom exception hierarchy and Optional

**3. Data Persistence**
- File-based storage has scalability limitations
- No transaction support
- Risk of data corruption
- **Future Improvement:** Migrate to database with transaction support

**4. Validation Duplication**
- Some validation logic is repeated across layers
- Could benefit from centralised validation rules
- **Future Improvement:** Create validation service with reusable rules

**5. Bidirectional Relationships**
- Managing relationships through IDs requires careful coordination
- Potential for inconsistent state if updates aren't synchronised
- **Future Improvement:** Consider bidirectional relationship management or ORM

### 8.3 Key Lessons Learned

**1. Design Patterns Should Solve Problems**
- Don't force patterns where they don't fit
- Simplicity is often better than pattern perfection
- Consider the context (CLI vs. web vs. desktop)
- Patterns should make code clearer, not more complex

**2. Separation of Concerns is Critical**
- MVC architecture made the system much easier to develop
- Clear boundaries reduce cognitive load
- Changes in one layer don't cascade to others
- Testing becomes much simpler

**3. Extensibility Requires Planning**
- Abstract classes and interfaces enable future extension
- Enums and polymorphism support new types
- Manager pattern makes adding features straightforward
- File-based architecture can migrate to database

**4. Trade-offs Are Inevitable**
- Centralised management is simpler but creates coupling
- File storage is easier but less scalable
- Inheritance is natural but less flexible than composition
- Each decision has pros and cons that must be weighed

**5. Context Matters**
- CLI application has different needs than web application
- Educational project can prioritise clarity over optimisation
- Single-user system doesn't need concurrency controls
- Assignment requirements influence design choices

### 8.4 Design Principles Demonstrated

**SOLID Principles:**

The system architecture strongly demonstrates adherence to SOLID principles, which form the foundation of good object-oriented design. Let's examine each principle in detail:

#### 1. Single Responsibility Principle (SRP)

**Definition:** A class should have only one reason to change.

**Implementation in the System:**

**Manager Classes:**
Each manager has a single, well-defined responsibility:
- `UserManager`: Handles user registration, authentication, and user-related queries
- `InternshipManager`: Manages internship opportunities and filtering
- `ApplicationManager`: Controls application lifecycle and status transitions
- `DataManager`: Responsible solely for data persistence (loading/saving)

**Example from UserManager:**
```java
public class UserManager {
    // Single responsibility: User management
    public boolean registerCompanyRepresentative(...) { }
    public User authenticateUser(String userID, String password) { }
    public List<Student> getAllStudents() { }
    public List<CompanyRepresentative> getPendingCompanyReps() { }
    // All methods relate to user management only
}
```

**Entity Classes:**
Each entity represents a single concept:
- `Student`: Represents student data and student-specific behaviour
- `InternshipOpportunity`: Represents an internship posting
- `Application`: Represents the relationship between student and internship

**Menu Classes:**
Each menu handles UI for one user type:
- `StudentMenu`: Only handles student interactions
- `CompanyRepresentativeMenu`: Only handles company representative interactions
- `CareerCenterStaffMenu`: Only handles staff interactions

**Benefits:**
- Easy to locate functionality
- Changes to one area don't ripple across the system
- Classes are smaller and easier to understand
- Testing is simplified (each class has focused tests)

**Counter-example (what we avoided):**
We didn't create a monolithic `System` class that handles authentication, data persistence, business logic, and UI all in one class.

**Impact on Codebase Quality:**
This separation means:
- Bug in user authentication? Check `UserManager` only
- Need to change how applications are processed? Modify `ApplicationManager` only  
- Data persistence issue? Fix `DataManager` without touching business logic
- UI improvements? Update menu classes without affecting controllers

---

#### 2. Open/Closed Principle (OCP)

**Definition:** Software entities should be open for extension but closed for modification.

**Implementation in the System:**

**Extensible User Types:**
Adding a new user type doesn't require modifying existing user types:
```java
// Existing code remains unchanged
public abstract class User {
    protected String userID;
    protected String password;
    protected String name;
    
    public abstract String getUserType();
    public abstract String getRole();
}

// New user type can be added without modifying User class
public class AlumniMentor extends User {
    private String company;
    
    @Override
    public String getUserType() { return "ALUMNI"; }
    
    @Override
    public String getRole() { return "Alumni Mentor"; }
}
```

**Extensible Enums:**
New values can be added without breaking existing code:
```java
// Can add new majors without modifying existing code
public enum Major {
    COMPUTER_SCIENCE("Computer Science"),
    BUSINESS("Business"),
    ENGINEERING("Engineering"),
    // New majors can be added here
    DATA_SCIENCE("Data Science"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence");
}
```

**Extensible Filtering:**
The filtering methods use parameters, making them extensible:
```java
// No modification needed to add new filter criteria
public List<InternshipOpportunity> getVisibleInternships(
    Student student,
    InternshipStatus statusFilter,    // Can add more filter parameters
    Major majorFilter,
    InternshipLevel levelFilter
) {
    return systemManager.getInternships().values().stream()
        .filter(i -> statusFilter == null || i.getStatus() == statusFilter)
        .filter(i -> majorFilter == null || i.getPreferredMajor() == majorFilter)
        .filter(i -> levelFilter == null || i.getLevel() == levelFilter)
        .collect(Collectors.toList());
}
```

**BaseMenu Template:**
New menu types extend `BaseMenu` without modifying it:
```java
// BaseMenu is closed for modification
public abstract class BaseMenu {
    protected abstract void displayMenu();
    
    // Common functionality provided
    protected void handlePasswordChange() { /* ... */ }
    protected void pauseForUser() { /* ... */ }
}

// New menus extend without modifying BaseMenu
public class StudentMenu extends BaseMenu {
    @Override
    protected void displayMenu() {
        // Student-specific implementation
    }
}
```

**Benefits:**
- System can grow without breaking existing functionality
- Lower risk when adding features
- Existing code doesn't need retesting when extended
- Follows the principle of "write once, extend many times"

---

#### 3. Liskov Substitution Principle (LSP)

**Definition:** Objects of a superclass should be replaceable with objects of its subclasses without breaking the application.

**Implementation in the System:**

**User Polymorphism:**
All User subclasses can be used wherever User is expected:
```java
// Polymorphic storage - any User subclass works
Map<String, User> users = new HashMap<>();
users.put("S001", new Student(...));
users.put("CR001", new CompanyRepresentative(...));
users.put("ST001", new CareerCenterStaff(...));

// Polymorphic retrieval - works with any User subclass
User user = systemManager.getUser(userID);
boolean authenticated = user.checkPassword(password);  // Works for all subclasses

// Polymorphic method calls
String type = user.getUserType();    // Each subclass provides correct implementation
String role = user.getRole();        // Each subclass provides correct implementation
```

**Contract Adherence:**
All subclasses honour the base class contract:
```java
// Student honours User contract
public class Student extends User {
    @Override
    public String getUserType() { 
        return "STUDENT";  // Always returns non-null string
    }
    
    @Override
    public String getRole() { 
        return "Student";  // Always returns non-null string
    }
    
    @Override
    public boolean checkPassword(String password) {
        return super.checkPassword(password);  // Honours parent contract
    }
}

// CompanyRepresentative honours User contract
public class CompanyRepresentative extends User {
    @Override
    public String getUserType() { 
        return "COMPANY_REP";  // Always returns non-null string
    }
    
    @Override
    public String getRole() { 
        return "Company Representative";  // Always returns non-null string
    }
}
```

**No Strengthened Preconditions:**
Subclasses don't add stricter requirements than the parent:
```java
// User allows any valid password
public boolean checkPassword(String password) {
    return this.password.equals(password);
}

// Student doesn't make it stricter (e.g., doesn't require longer passwords)
// CompanyRepresentative doesn't make it stricter
```

**No Weakened Postconditions:**
Subclasses maintain the guarantees of the parent:
```java
// User guarantees getUserType() returns a string
// All subclasses maintain this guarantee
```

**Benefits:**
- Code that works with User works with all subclasses
- Simplifies authentication and user management
- No need for type-specific handling in common operations
- Enables true polymorphism throughout the system

**How it enhances the system:**
The authentication system in `MainMenu` can work with any User type without knowing the specific type until runtime, making it easy to add new user types.

**Real-world benefit in the codebase:**
```java
// SystemManager can store all user types in a single collection
private Map<String, User> users = new HashMap<>();

// Authentication works polymorphically
public User getUser(String userID) {
    return users.get(userID);  // Returns any User subclass
}

// Type-specific behaviour only when needed
if (user instanceof Student) {
    Student student = (Student) user;
    // Student-specific operations
} else if (user instanceof CompanyRepresentative) {
    CompanyRepresentative rep = (CompanyRepresentative) user;
    // Company representative-specific operations
}
```

---

#### 4. Interface Segregation Principle (ISP)

**Definition:** Clients should not be forced to depend on interfaces they don't use.

**Implementation in the System:**

**Minimal Abstract Methods:**
The `User` abstract class only requires methods that all users need:
```java
public abstract class User {
    // Only two abstract methods - minimal interface
    public abstract String getUserType();
    public abstract String getRole();
    
    // Common methods that all users share
    public boolean checkPassword(String password) { }
    public void changePassword(String newPassword) { }
}
```

Each subclass only implements what it needs:
- Student doesn't implement company-specific methods
- CompanyRepresentative doesn't implement student-specific methods
- CareerCenterStaff doesn't implement methods for either

**Focused Entity Interfaces:**
Each entity class provides only relevant methods:
```java
// Student provides student-specific methods
public class Student extends User {
    public boolean canApplyForMore() { }
    public void addAppliedInternship(String internshipID) { }
    // No company-related methods forced on Student
}

// CompanyRepresentative provides company-specific methods
public class CompanyRepresentative extends User {
    public boolean isApproved() { }
    public void approve() { }
    public void addCreatedInternship(String internshipID) { }
    // No student-related methods forced on CompanyRepresentative
}
```

**Specific Manager Interfaces:**
Each manager provides focused operations:
```java
// UserManager: Only user-related operations
public class UserManager {
    public User authenticateUser(String userID, String password) { }
    public List<Student> getAllStudents() { }
    // No internship or application methods
}

// InternshipManager: Only internship-related operations
public class InternshipManager {
    public InternshipOpportunity createInternship(...) { }
    public List<InternshipOpportunity> getVisibleInternships(...) { }
    // No user or application methods
}

// ApplicationManager: Only application-related operations
public class ApplicationManager {
    public Application submitApplication(...) { }
    public boolean updateApplicationStatus(...) { }
    // No user or internship management methods
}
```

**Menu Specificity:**
Each menu type provides only relevant UI options:
```java
// StudentMenu doesn't force staff-only operations
public class StudentMenu extends BaseMenu {
    private void viewAvailableInternships() { }
    private void applyForInternship() { }
    // No approval or management operations
}

// CareerCenterStaffMenu provides staff-only operations
public class CareerCenterStaffMenu extends BaseMenu {
    private void viewAllStudents() { }
    private void approveCompanyRep() { }
    // No student application operations
}
```

**Benefits:**
- Classes don't depend on methods they don't use
- Changes to one interface don't affect unrelated classes
- Smaller, more cohesive interfaces
- Easier to understand what each class does

**Counter-example (what we avoided):**
We didn't create a `UserInterface` with methods like `applyForInternship()`, `createInternship()`, and `approveUser()` that would force all users to implement irrelevant methods.

---

#### 5. Dependency Inversion Principle (DIP)

**Definition:** High-level modules should not depend on low-level modules. Both should depend on abstractions.

**Implementation in the System:**

**Abstraction-based Design:**
Managers depend on the abstract `User` class, not concrete implementations:
```java
public class UserManager {
    // Depends on abstraction (User), not concretions
    public User authenticateUser(String userID, String password) {
        User user = systemManager.getUser(userID);  // Returns User (abstraction)
        
        // Works with any User subclass
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }
    
    // Method returns abstraction, not concrete type
    public List<Student> getAllStudents() {
        return systemManager.getUsers().values().stream()
            .filter(user -> user instanceof Student)  // Runtime type check
            .map(user -> (Student) user)
            .collect(Collectors.toList());
    }
}
```

**High-level Policy Independent of Low-level Details:**
Business logic doesn't depend on UI details:
```java
// ApplicationManager (high-level) doesn't depend on StudentMenu (low-level)
public class ApplicationManager {
    public Application submitApplication(String studentID, String internshipID) {
        // Business logic independent of how it's called
        Student student = (Student) systemManager.getUser(studentID);
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        
        // Validation and business rules
        if (!student.canApplyForMore()) return null;
        if (!internship.isStudentEligible(student)) return null;
        
        // Create and return application
        Application app = new Application(studentID, internshipID);
        return app;
    }
}

// StudentMenu (low-level) depends on ApplicationManager (high-level)
public class StudentMenu extends BaseMenu {
    private void applyForInternship() {
        // UI layer depends on business logic layer
        Application app = applicationManager.submitApplication(studentID, internshipID);
        if (app != null) {
            System.out.println("Application submitted successfully!");
        }
    }
}
```

**Manager Coordination Through Abstraction:**
Managers coordinate through `SystemManager` which provides abstracted access:
```java
public class ApplicationManager {
    private SystemManager systemManager;
    
    public ApplicationManager(SystemManager systemManager) {
        this.systemManager = systemManager;  // Depends on coordinator
    }
    
    public Application submitApplication(String studentID, String internshipID) {
        // Accesses data through abstraction
        User user = systemManager.getUser(studentID);  // Abstraction
        InternshipOpportunity internship = systemManager.getInternship(internshipID);
        
        // High-level policy doesn't depend on how data is stored
    }
}
```

**Data Persistence Abstraction:**
Business logic doesn't depend on file storage details:
```java
// DataManager (low-level) provides abstraction for persistence
public class DataManager {
    public void loadUsers() {
        // Implementation details hidden
        try {
            // Could be file, database, API, etc.
        } catch (IOException e) {
            // Error handling
        }
    }
}

// SystemManager (high-level) just calls the abstraction
public class SystemManager {
    private DataManager dataManager;
    
    private SystemManager() {
        dataManager = new DataManager(this);
        dataManager.loadUsers();  // Doesn't care how it's implemented
    }
}
```

**Benefits:**
- Business logic independent of UI and data storage
- Can change data storage without affecting business logic
- Can change UI without affecting business logic
- Testability improved (can mock abstractions)
- System is more flexible and maintainable

**How this enables future changes:**
- Can migrate from file storage to database by only changing `DataManager`
- Can add GUI by creating new menu classes without touching managers
- Can add REST API by creating API controllers that use existing managers

---

### Summary of SOLID Application

The system demonstrates strong adherence to SOLID principles throughout its architecture:

| Principle | Key Implementation | Benefits Realized |
|-----------|-------------------|-------------------|
| **SRP** | Focused manager classes, single-purpose entities | Easy maintenance, clear responsibilities |
| **OCP** | Extensible enums, abstract base classes | Add features without breaking existing code |
| **LSP** | User polymorphism, contract adherence | Flexible user handling, simplified code |
| **ISP** | Minimal abstract methods, focused interfaces | No unnecessary dependencies |
| **DIP** | Abstraction-based managers, layered architecture | Flexible, testable, maintainable |

**Real-World Impact of SOLID Principles:**

These principles work together to create a system that is:
- **Maintainable:** Easy to understand and modify - each class has a clear purpose
- **Extensible:** New features can be added with minimal changes - demonstrated by how easy it would be to add new user types, internship levels, or application statuses
- **Testable:** Components can be tested independently - managers accept dependencies and business logic is separated from I/O
- **Flexible:** Implementation details can change without breaking the system - can migrate from text files to database by changing only DataManager
- **Professional:** Follows industry best practices and design patterns used in enterprise applications

**Why SOLID Matters in This Project:**

1. **Educational Value**: Demonstrates proper OOP design that can be applied in professional settings
2. **Code Quality**: Results in cleaner, more organised code that's easier to review and grade
3. **Collaboration**: Clear separation of concerns makes it easier for team members to work on different parts
4. **Future-Proofing**: Design decisions support future enhancements (GUI, database, additional features)
5. **Industry Alignment**: Mirrors patterns used in real-world enterprise applications

**Evidence of SOLID Throughout the Codebase:**

- **21 Java files** organised into clear packages (entity, control, boundary, enums)
- **Zero circular dependencies** between packages
- **Consistent patterns** across all manager classes
- **Polymorphic collections** enable flexible user management
- **Encapsulated business logic** in entity methods
- **Separation of concerns** between UI, business logic, and data layers
- **Type safety** through enum usage instead of magic strings

### 8.5 Suggestions for Future Development

**Short-term Improvements:**
1. Add comprehensive unit tests with mocking framework
2. Implement `Optional<T>` for nullable returns
3. Create custom exception hierarchy for better error handling
4. Add logging framework for debugging and audit trails
5. Implement input sanitization and security checks

**Medium-term Enhancements:**
1. Migrate to database (H2 or PostgreSQL)
2. Implement Repository pattern for data access
3. Add Observer pattern for status notifications
4. Create validation framework for reusable rules
5. Implement JavaFX or Swing GUI

**Long-term Evolution:**
1. Migrate to Spring Boot for dependency injection
2. Implement REST API for web/mobile clients
3. Add authentication with JWT tokens
4. Implement role-based access control (RBAC)
5. Add email notification system
6. Integrate with external job portals
