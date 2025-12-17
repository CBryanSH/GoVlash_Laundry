GoVlash Laundry Management System

GoVlash is a JavaFX-based Laundry Management System designed to manage laundry services, transactions, staff assignments, and notifications using the MVC (Model–View–Controller) architectural pattern.
The system supports multiple user roles with clearly separated responsibilities to ensure maintainability, scalability, and clean code structure.

📌 Features Overview
🔐 Authentication

User registration and login

Role-based access control

👥 User Roles

Customer

Admin

Receptionist

Laundry Staff

Each role has access only to features relevant to their responsibilities.

🧱 Architecture

This project follows the MVC Pattern:

Model Layer

Handles business logic and database operations.

UserModel – User authentication and role management

ServiceModel – Laundry service catalog

TransactionModel – Transaction lifecycle management

NotificationModel – User notifications

View Layer

Handles UI rendering using JavaFX.

LoginView, RegisterView – Authentication interfaces

CustomerView – Customer dashboard and transactions

AdminView – Service and employee management

ReceptionistView – Transaction assignment interface

StaffView – Laundry staff task dashboard

Controller Layer

Acts as the intermediary between View and Model.

LoginController – Login validation

RegisterController – User registration validation

CustomerController – Customer transactions & notifications

AdminController – Services, employees, and monitoring

ReceptionistController – Staff assignment

StaffController – Task completion

🗄️ Database

Database: MySQL

Connection Handling: Singleton pattern (Connect class)

Access Method: JDBC (PreparedStatement where applicable)

Key Tables

Users

Services

Transactions

Notifications

👤 User Roles & Features
Customer

Create new laundry transactions

View transaction history

Read and manage notifications

Admin

Manage laundry services

Manage employee accounts

Monitor all transactions

Receptionist

View pending transactions

Assign transactions to laundry staff

Laundry Staff

View assigned tasks

Mark transactions as completed

📘 Usage Guide
For Customers

Register an account

Log in

Create a laundry transaction

Track transaction status and notifications

For Admin

Log in as admin

Manage services

Add or manage employees

Monitor transactions

For Receptionist

Log in

View unassigned transactions

Assign transactions to laundry staff

For Laundry Staff

Log in

View assigned jobs

Mark jobs as completed

⚙️ Assumptions

Users provide valid date formats (YYYY-MM-DD)

Email domains are validated based on role

Passwords are stored as plain text (for academic/demo purposes only)

Each transaction follows a linear lifecycle (Pending → Finished)

One staff member handles one transaction at a time

🛠️ Technologies Used

Java

JavaFX

MySQL

JDBC

MVC Design Pattern

🚀 How to Run

Clone the repository

Import the project into an IDE (Eclipse / IntelliJ)

Configure MySQL database and credentials

Run Main.java
Upcoming Features
- Firing Employee
- Notifications Modifications
