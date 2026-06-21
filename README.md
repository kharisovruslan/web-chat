# Web Chat

A responsive and secure web application for real-time messaging, file sharing, and user management within local networks.

---

## 🚀 Features

* **Real-Time Updates**: Automatic message polling via JavaScript (every 15 seconds) ensures chat updates without full page reloads.
* **User Management & Roles**: The first registered user automatically grants Administrator privileges. Administrators can manage user profiles (names, passwords, tokens) and view system statistics.
* **Pagination & History**: Chat history is cleanly organized into pages (5 messages per page) with a built-in search functionality.
* **File Exchange**: Integrated file sharing capabilities. Uploaded files are securely stored with unique UUIDs. The default file size limit is **8MB** (configurable in `application.properties`).
* **Presence Tracking**: Visual status indicators show whether a user is online (green frame) or offline (red frame). Hovering over a username displays their last visited timestamp.
* **Flexible Authentication**: Secure login via traditional credentials or personal authentication tokens (ideal for rapid login via bookmarks).
* **Offline Messaging**: Send messages to offline users; they will be instantly available upon their next login.
* **External Integration API**: Features a lightweight API for observing new messages, built specifically to power the external ***web-chat-notification*** desktop client.
* **Production Readiness**: Pre-configured with Spring Boot Actuator for application monitoring and metrics out of the box.
* **Localization**: Full support for multi-language interfaces and localization.

---

## 📸 Screenshots

<p align="center">
  <img src="./src/test/resources/images/Frame.png" alt="Main Chat Window" width="45%"/>
  <img src="./src/test/resources/images/Frame1.png" alt="Chat Workspace" width="45%"/>
</p>
<p align="center">
  <img src="./src/test/resources/images/login.png" alt="Login Page" width="45%"/>
  <img src="./src/test/resources/images/regsiteration.png" alt="Registration Form" width="45%"/>
</p>
<p align="center">
  <img src="./src/test/resources/images/Profile.png" alt="User Profile" width="45%"/>
  <img src="./src/test/resources/images/Search.png" alt="Message Search" width="45%"/>
</p>
<p align="center">
  <img src="./src/test/resources/images/Statistics.png" alt="Admin Dashboard Statistics" width="60%"/>
</p>

---

## 🛠️ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend Framework** | Spring Boot (MVC, Security, Data JPA, Actuator) |
| **Language & Core** | Java 11 / Java 17 (Optionals, LocalDateTime, Lombok) |
| **Database & Migration** | HSQLDB (Embedded default), PostgreSQL support, Flyway |
| **Frontend Templates** | Thymeleaf, HTML5 (with data validation) |
| **UI Framework** | Bootstrap |
| **Client Scripting** | JavaScript, jQuery |

---

## ⚠️ Important Deployment Notes

By default, this application binds to all network interfaces and is optimized for local area networks (LAN).

To secure your production data:
1. Enable **Flyway** migrations.
2. Switch from the embedded HSQLDB to a dedicated production database like **PostgreSQL**.
3. Create an external `/config` directory relative to your application root and place your production `application.properties` file inside it to override default settings safely.

---

## 🏁 Quick Start

### Prerequisites
Ensure you have the following tools installed locally:
* [JDK 11 or later (JDK 17 recommended)](https://adoptium.net/)
* [Apache Maven](https://maven.apache.org/)
* [Git](https://git-scm.com/)

### 1. Clone & Build
Execute the following commands in your terminal to build the executable JAR file:

```bash
git clone https://github.com/kharisovruslan/web-chat
cd web-chat
mvn clean package
cd target
```

### 2. Run the Application
Start the application using the generated JAR file. On launch, the server will automatically generate a `/database` folder for HSQLDB and an `/uploadfiles` directory for chat attachments.

```bash
java -jar web-chat-2.0.0-SNAPSHOT.jar
```

### 3. Access the Application
Open your web browser and navigate to: **[http://localhost:8080/](http://localhost:8080/)**

* **First User**: Register your account immediately to claim the **Administrator** role.
* **Subsequent Users**: Will receive standard **User** permissions upon registration.
* **Monitoring**: Access application metrics and health checks at **[http://localhost:8080/actuator](http://localhost:8080/actuator)**.
