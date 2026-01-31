# 🏥 GlobeMed Hospital Management System

**GlobeMed** is a healthcare management system architected using **Object-Oriented Design Patterns (OODP)** to ensure high maintainability, scalability, security, and flexibility. Every core hospital process—ranging from patient records to complex billing—is mapped to a specific design pattern to address real-world software engineering challenges.



## 🏗️ System Architecture & Design Patterns

The project leverages six distinct design patterns to create a robust and clean architecture.

### 1. Patient Record Management (**Bridge Pattern**)


**Implementation**: Separates the **PatientManager** (abstraction) from the **PatientRepository** (implementation).



**Why**: This ensures secure, role-based access for nurses, doctors, and admins while keeping the system flexible as data storage needs evolve.



**Benefit**: Promotes a clean architecture that scales well with large datasets.



### 2. Appointment Scheduling (**Mediator Pattern**)


**Implementation**: A central **AppointmentMediator** coordinates communication between patients, doctors, and the backend.



**Why**: It provides centralized control to prevent scheduling conflicts like double-booking.



**Benefit**: Creates loose coupling between the GUI and the database, making the system easier to maintain and extend with features like auto-reminders.



### 3. Billing & Insurance Claims (**Decorator Pattern**)


**Implementation**: A base bill is extended dynamically at runtime using decorators for insurance, taxes, and discounts.



**Why**: Each billing rule is handled by its own decorator, allowing multiple rules to be applied simultaneously without changing core logic.



**Benefit**: Ensures consistent billing across direct payments and insurance claims while remaining highly scalable.



### 4. Medical Staff Roles (**Composite Pattern**)


**Implementation**: Manages staff in a hierarchy where groups (e.g., "All Nurses") and individuals are treated uniformly.



**Benefit**: Provides a flexible structure for managing varied hospital roles and permissions.



### 5. Medical Report Generation (**Visitor Pattern**)


**Implementation**: Separates the report generation logic (**ReportGeneratorVisitor**) from the data objects like **Patient** or **MedicalHistoryReport**.



**Why**: Allows the addition of new report types (financial, diagnostic) without modifying existing data classes.



**Benefit**: Enhances reusability across different user dashboards.





## 🔒 Security & Access Control

The **Chain of Responsibility (CoR)** pattern is the backbone of GlobeMed's security layer.


**Multi-Step Authentication**: Validates credentials in a sequential chain: **Username → Password → Role Verification**.



**Role-Based Access**: Ensures users only reach their authorized dashboard (Admin vs. Staff).



**Extensibility**: The chain can be easily updated to include new security layers like OTP or biometric checks.

