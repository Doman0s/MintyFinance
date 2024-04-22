# MintyFinance

A web-based personal finance management application developed using Java and Spring Boot.

## Login Credentials

### Administrator Account
- **Login:** admin@example.com
- **Password:** adminpass

### User Account
- **Login:** user@example.com
- **Password:** userpass

## How to Run the Project
To start using the Minty Finance application:
1. Download or clone the repository to your local machine.
2. Open the project in IntelliJ IDEA.
3. Run the project using Java 17 and Spring Boot 2.7.0. (Note: The project was developed using Java 17 and Spring Boot 2.7.0.)
4. After starting the application, access the user interface by entering `http://localhost:8080` in your web browser. This will take you to the homepage of the Minty Finance application.
5. You can also access the H2 Database console at `http://localhost:8080/h2-console` to manage the database directly through the web interface.

## Data Storage
- The application uses an H2 Database for development, which does not require additional installation. Tables within the database are automatically generated using Liquibase scripts.
- Data initialization is handled through SQL scripts that populate the tables with default data at startup. This setup ensures that upon each restart of the application, the data is reset to its initial state.

## Features
- **User-defined Expenses and Incomes**: Users can create, edit, and delete their own expenses and incomes, with options to filter, sort, and search by name.
- **Category Management**: Administrators can add, modify, or delete predefined categories. Users can define, edit, and delete their own categories and assign items to them.
- **Savings Goals**: Users can define, modify, and delete savings goals. Upon completion, funds are automatically deducted from the account.
- **Priority Assignments**: Assign priorities to expenses and incomes for better management and sorting capabilities.
- **Transaction History**: Automatic generation of transaction history with options for viewing, clearing, or deleting specific entries.
- **Monthly Summaries**: Receive concise monthly reports detailing total incomes, expenses, and individual transactions.
- **User Management**: Administrators can view, search, filter, and block or unblock users to manage access effectively.
- **Error Reporting**: Users can report application issues through a dedicated form, and administrators can review and delete these reports.
- **User Authentication**: Support for user registration and login, ensuring secure and personalized access to the application.
- **Account Management**: Users can edit their account details and delete their account if necessary, providing full control over personal information and membership.
- **Automated Transaction Processing**: The application automatically checks and executes pending transactions every minute, ensuring timely and efficient management of financial operations.

## Additional Information
- Make sure you have Java 17 installed to run the project. I am currently migrating the project to Spring Boot 3.x.x, so a project with a different version of Java may not work.