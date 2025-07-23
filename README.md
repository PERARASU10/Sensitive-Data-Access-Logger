# Sensitive Data Access Logger

A full-stack web application designed to securely manage, monitor, and audit access to sensitive documents. This system provides role-based access control, detailed logging of all file interactions, and real-time alerts for suspicious activities, helping organizations prevent data leaks and maintain compliance.

---

## 🌐 Core Features

-   **Secure Authentication:** JWT-based login and registration with password encryption (BCrypt).
-   **Role-Based Access Control (RBAC):** Differentiated permissions for **Admin** and **User** roles.
-   **Complete Admin Dashboard:**
    -   **User Management:** View, delete, and promote users to admin status.
    -   **File Management:** Upload new documents and manage permissions for all files.
    -   **Permission Control:** Grant or revoke `VIEW`, `DOWNLOAD`, or `BOTH` permissions on a per-user, per-file basis.
    -   **Comprehensive Auditing:** View a master list of all permissions, detailed access logs (with `APPROVED`/`DENIED` status), and security alerts.
-   **Secure File Handling:**
    -   Admins can upload files.
    -   Users can only view or download files for which they have explicit permission.
    -   File viewing is supported directly in the browser for common types (PDF, images).
-   **Real-time Security Alerts:** Flags suspicious activities like mass downloads and notifies administrators.

---

## ⚙️ Tech Stack

| Category      | Technology / Library                               |
| :------------ | :------------------------------------------------- |
| **Backend** | Spring Boot, Spring Security, Spring Data JPA      |
| **Frontend** | React.js, React Router, Axios                      |
| **UI Library**| Material-UI (MUI)                                  |
| **Database** | MySQL                                              |
| **Auth** | JSON Web Tokens (JWT)                              |
| **Build Tools**| Maven (Backend), npm (Frontend)                    |

---

## 🚀 Getting Started

Follow these instructions to get a local copy of the project up and running.

### Prerequisites

You will need the following software installed on your machine:
-   **Java JDK** (Version 17 or higher)
-   **Apache Maven**
-   **Node.js and npm**
-   **MySQL Server** (or another compatible SQL database)

### 1. Database Setup

1.  Connect to your MySQL instance.
2.  Create a new database for the project.
    ```sql
    CREATE DATABASE sensitive_data_logger;
    ```

### 2. Backend Setup

1.  **Navigate to the `backend` directory:**
    ```bash
    cd backend
    ```
2.  **Configure the Application:**
    -   Open `src/main/resources/application.properties`.
    -   Update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties to match your MySQL database configuration.
    -   Update the `jwt.secret` with a long, random, and secret string.
3.  **Build and Run the Application:**
    ```bash
    mvn spring-boot:run
    ```
    The backend server will start on `http://localhost:8080`.

### 3. Frontend Setup

1.  **Navigate to the `frontend` directory in a new terminal:**
    ```bash
    cd frontend
    ```
2.  **Install Dependencies:**
    ```bash
    npm install
    ```
3.  **Configure Environment:**
    -   Ensure there is a `.env` file in the `frontend` directory.
    -   It should contain the following line, pointing to your backend server:
        ```
        REACT_APP_API_BASE_URL=http://localhost:8080
        ```
4.  **Start the Frontend Application:**
    ```bash
    npm start
    ```
    The frontend development server will start and open in your browser at `http://localhost:3000`.

---

## Usage

### Creating the First Admin User

Since the registration form only creates `USER` roles, you must create the first `ADMIN` user directly in the database.

1.  **Generate a BCrypt Hash:** Go to a trusted online tool like [bcrypt-generator.com](https://www.bcrypt-generator.com/) and hash a secure password.
2.  **Run SQL Insert:** Connect to your `sensitive_data_logger` database and run the following SQL command, replacing the placeholder values with your desired username and the hash you just generated.

    ```sql
    INSERT INTO users (username, password, role, created_at) 
    VALUES ('your_admin_username', 'the_bcrypt_hash_you_copied', 'ADMIN', NOW());
    ```

You can now log in to the application with your new admin credentials.

