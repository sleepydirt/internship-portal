# Internship Placement Management System

## Overview

This is a Java-based Object-Oriented Programming (OOP) application designed for managing internship placements in an educational institution. The system provides a centralized platform for students, company representatives, and career center staff to manage internship opportunities and applications.

## Features

### For Students

- View available internship opportunities based on profile (year of study, major)
- Apply for up to 3 internships simultaneously
- View application status and history
- Accept internship placements
- Request withdrawal from applications

### For Company Representatives

- Register and create company accounts (subject to approval)
- Create up to 5 internship opportunities
- Manage internship applications (approve/reject)
- Toggle internship visibility
- View application details and student information

### For Career Center Staff

- Approve/reject company representative registrations
- Approve/reject internship opportunities
- Manage student withdrawal requests
- Generate comprehensive reports with filtering options
- View system statistics

## System Architecture

The application follows Object-Oriented Design principles with clear separation of concerns:

- **Entity Classes**: User, Student, CompanyRepresentative, CareerCenterStaff, InternshipOpportunity, Application
- **Control Classes**: SystemManager, UserManager, InternshipManager, ApplicationManager, DataManager
- **Boundary Classes**: MainMenu, StudentMenu, CompanyRepresentativeMenu, CareerCenterStaffMenu
- **Enums**: Major, InternshipLevel, InternshipStatus, ApplicationStatus

## Key OOP Principles Implemented

1. **Encapsulation**: All entity classes have private fields with public getter/setter methods
2. **Inheritance**: User is an abstract base class extended by Student, CompanyRepresentative, and CareerCenterStaff
3. **Polymorphism**: User types are handled polymorphically through the base User class
4. **Abstraction**: Abstract base classes and interfaces separate implementation from interface
5. **Composition**: SystemManager contains and coordinates multiple manager classes

## Design Patterns Used

1. **Singleton Pattern**: SystemManager ensures single instance throughout the application
2. **Factory Pattern**: User creation based on type
3. **MVC Pattern**: Clear separation between Model (entities), View (boundary classes), and Controller (control classes)

## Installation and Setup

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans) or text editor

### Running the Application

> [!IMPORTANT]  
> The commands are slightly different depending on whether you are on a bash terminal or on Windows Powershell.

1. **Compile the application:**
   Bash

   ```bash
   mkdir -p build
   javac -d build $(find src -name "*.java")
   ```

   Powershell

   ```ps
   javac -sourcepath src -d build (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
   ```

   These will compile all the `.class` files into a `/build` directory.

2. **Run the application:**
   Bash/Powershell
   ```ps
   java -cp build src.Main
   ```

### Default Users

The system comes with pre-configured default users for testing:

**Students:**

- ID: U1234567A, Password: password (Year 3, CSC)
- ID: U2345678B, Password: password (Year 2, EEE)
- ID: U3456789C, Password: password (Year 4, MAE)

**Career Center Staff:**

- ID: staff01@ntu.edu.sg, Password: password
- ID: staff02@ntu.edu.sg, Password: password

**Company Representative (Pre-approved):**

- ID: hr@techcorp.com, Password: password

## Business Rules

### Student Eligibility

- Year 1-2 students can only apply for Basic-level internships
- Year 3-4 students can apply for any level (Basic, Intermediate, Advanced)
- Students can apply for maximum 3 internships at once
- Only 1 internship placement can be accepted
- Applications must match preferred major (unless major is set to "Other")

### Company Representative Rules

- Must be approved by Career Center Staff before login
- Can create maximum 5 internship opportunities
- Can only manage applications for their own internships
- Internships require approval before becoming visible to students

### System Workflow

1. Company representatives register and await approval
2. Approved representatives create internship opportunities
3. Career center staff approve internship opportunities
4. Students view and apply for approved, visible internships
5. Company representatives review and approve/reject applications
6. Students accept successful placements
7. Withdrawal requests require career center staff approval

## Data Persistence

The system uses file-based data persistence. The data folder contains sample CSV files for initial system setup:

- `data/sample_student_list.csv` - Sample student data with StudentID, Name, Major, Year, and Email
- `data/sample_staff_list.csv` - Sample career center staff data with StaffID, Name, Role, Department, and Email
- `data/sample_company_representative_list.csv` - Sample company representative data with CompanyRepID, Name, CompanyName, Department, Position, Email, and Status

Data is automatically loaded on startup and saved on shutdown.

## Testing

The system includes comprehensive validation for:

- User authentication and authorization
- Business rule enforcement
- Input validation and error handling
- Data consistency and integrity

Test the application using the default users and follow the workflow scenarios provided in the assignment requirements.

## Future Enhancements

- GUI implementation using JavaFX or Swing
- Database integration for improved data management
- Email notification system
- Advanced reporting with charts and graphs
- Mobile application interface
- Integration with external job portals

## Project Structure

```
src/
├── Main.java
├── boundary/
│   ├── BaseMenu.java
│   ├── CareerCenterStaffMenu.java
│   ├── CompanyRepresentativeMenu.java
│   ├── MainMenu.java
│   └── StudentMenu.java
├── control/
│   ├── ApplicationManager.java
│   ├── DataManager.java
│   ├── InternshipManager.java
│   ├── SystemManager.java
│   └── UserManager.java
├── entity/
│   ├── Application.java
│   ├── CareerCenterStaff.java
│   ├── CompanyRepresentative.java
│   ├── InternshipOpportunity.java
│   ├── Student.java
│   └── User.java
└── enums/
    ├── ApplicationStatus.java
    ├── InternshipLevel.java
    ├── InternshipStatus.java
    └── Major.java
```
