# MTS Spring Boot Web Project Template

A comprehensive web project template based on Spring Boot 3.5.0-M2 with MyBatis 3 support for both Oracle and MariaDB databases.

## Overview

This project combines three major components into a unified web application framework:
1. Electronic Payment System
2. Project Management
3. Multi-Bulletin Board System

The project aims to provide a robust foundation for web development by implementing commonly used functionalities in advance, making it suitable for rapid application development.

## Core Features

### 1. Electronic Payment System
- **Schedule Management**
  - Monthly calendar view with event planning
  - Meeting and schedule tracking
  - Reminder notifications
- **Document Workflow**
  - Document drafting and creation
  - Multi-level approval process
  - Electronic signature integration
  - Document status tracking and history
  - Template management
- **Email Integration**
  - Inbox management (new, received, sent)
  - Email notifications and alerts
  - Attachment handling and storage
  - Email template support
- **Notice Board**
  - Announcements and notifications
  - Priority-based display
  - Category management
  - Rich media support

### 2. Project Management
- **Task Management**
  - Task creation and assignment
  - Progress tracking and status updates
  - Individual and team task views
  - Deadline management
  - Task dependencies
- **Resource Planning**
  - User assignment and role management
  - Schedule management
  - Workload distribution
  - Resource allocation tracking
  - Team capacity planning

### 3. Multi-Bulletin Board & User Management
- **Advanced Bulletin Features**
  - Multiple board types and categories
  - Infinite comment threading
  - Like/React functionality
  - Rich text content support
  - File attachment handling
- **User Management**
  - Role-based access control (User/Admin)
  - Department-based organization
  - User selection with popup interface
  - Profile management
  - Permission system

### 4. System Integration & Tools
- **Search System**
  - Full-text search capability
  - Document indexing
  - Search result highlighting
  - Advanced search filters
- **Map Integration**
  - Address conversion
  - Location mapping
  - Geocoding services
- **Document Processing**
  - PDF generation and handling
  - QR code generation
  - Document templates
- **System Features**
  - Multi-language support
  - Excel export/import (jXLS)
  - Responsive design
  - Error handling and monitoring
  - Comprehensive logging
  - Chart integration and visualization

### 5. Administration & Monitoring
- **System Administration**
  - Server resource monitoring
  - Hardware/Software management
  - Service status tracking
  - Configuration management
- **Security Management**
  - Access control
  - Authentication management
  - Security logging
  - Session management
- **Development Tools**
  - Database tools
  - File search utilities
  - Code dependency analysis
  - Log viewers

## Technical Stack

### Backend
- **Java 23**
- **Spring Boot 3.5.0-M2**
  - Spring Security 6.5.0-M2
  - Spring Integration
  - Spring Data JPA
- **MyBatis 3**
- **Databases**
  - Oracle 11g
  - MariaDB
- **Additional Services**
  - Elasticsearch 8.15.1
  - Redis
  - James Mail Server

### Frontend
- **Core Libraries**
  - jQuery 2.2.3
  - Bootstrap (SB-Admin 2)
- **UI Components**
  - CKEditor 4.5.10
  - FullCalendar v5
  - Morris Charts v0.5.0
  - DatePicker
  - DynaTree 1.2.4
  - jQuery EasyUI 1.4.3

### Development Tools
- **IDE**: IntelliJ IDEA (recommended)
- **Build Tool**: Gradle
- **Code Quality**
  - Checkstyle
  - Spotless
  - Spring Java Format
- **Documentation**: SpringDoc OpenAPI

### Configuration Files
- **Core Configuration**
  - application.properties/yml
  - DevkbilApplication.java
- **Security**
  - WebMvcConfig.java (CORS configuration)
- **Customization**
  - mts-banner.txt (Custom banner)

## Installation and Setup

### Prerequisites
1. **Development Tools**
   ```bash
   # Install Docker and Colima (for macOS)
   brew install colima docker qemu

   # Start Colima
   colima start --memory 4 --arch x86_64

   # Configure Docker context
   docker context use colima
   ```

2. **Required Software**
   ```bash
   # Install image processing tools
   brew install imagemagick tesseract tesseract-lang exiftool ffmpeg leptonica
   ```

### Database Setup

#### Oracle Database
```bash
# Pull and run Oracle container
docker pull linuxint/oraclexe11g
docker run --name oracle11g -d -p 1521:1521 linuxint/oracle11g

# Execute setup scripts
# 1. user_database_oracle.sql
# 2. tables_oracle.sql
# 3. tableData_oracle.sql
```

#### MariaDB
```bash
# Execute setup scripts
# 1. user_database_mariadb.sql
# 2. tables_mariadb.sql
# 3. tableData_mariadb.sql
```

### Service Configuration

#### Elasticsearch
```bash
# Pull and run Elasticsearch
docker pull elasticsearch:8.15.1
docker run -p 9200:9200 -p 9300:9300 --name es8 \
    -e "discovery.type=single-node" \
    -e "xpack.ml.enabled=false" \
    elasticsearch:8.15.1

# Configure Elasticsearch
docker cp elasticsearch:/usr/share/elasticsearch/config/elasticsearch.yml ./
# Edit elasticsearch.yml: set xpack.security.enabled: false
docker cp elasticsearch.yml elasticsearch:/usr/share/elasticsearch/config/

# Install Nori analyzer
docker exec -it elasticsearch elasticsearch-plugin install analysis-nori

# Copy dictionaries
docker cp stopwords.txt elasticsearch:/usr/share/elasticsearch/config/
docker cp synonyms.txt elasticsearch:/usr/share/elasticsearch/config/
docker cp userdict.txt elasticsearch:/usr/share/elasticsearch/config/

# Create index
curl -XPUT localhost:9200/mts -d @index_board.json \
    -H "Content-Type: application/json"

# Set up user passwords
docker exec -it elasticsearch /bin/bash
./bin/elasticsearch-setup-passwords interactive
# Set passwords for the following users:
# - elastic
# - apm_system
# - kibana_system
# - logstash_system
# - beats_system
# - remote_monitoring_user
# Default password recommendation: manager

# Example password setup process:
# Initiating the setup of passwords for reserved users elastic,apm_system,kibana,
# kibana_system,logstash_system,beats_system,remote_monitoring_user.
# You will be prompted to enter passwords as the process progresses.
# Please confirm that you would like to continue [y/N]y
#
# Enter password for [elastic]: manager
# Reenter password for [elastic]: manager
# Enter password for [apm_system]: manager
# ... (repeat for all users)
```

#### Mail Server (James)
```bash
docker pull apache/james:demo-latest
docker run -p "465:465" -p "993:993" --name james apache/james:demo-latest
# IMAP: 993, SMTP: 465
# Default user: user01@james.local/1234
```

#### Redis
```bash
docker pull redis
docker run --name redis -d -p 6379:6379 redis
```

### Network Configuration
```bash
# Create Docker network
docker network create mts-network

# Connect services
docker network connect mts-network elasticsearch
docker network connect mts-network oracle11g
docker network connect mts-network james
docker network connect mts-network redis

# Verify connectivity
docker network inspect mts-network
```

### Application Setup
1. Configure `application.properties` with appropriate connection settings
2. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```
3. Access the application at http://localhost:9090

### Default Users
- Administrator: admin/1234
- Test Users: user1/1234, user2/1234

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/          # Java source files
│   │   ├── resources/     # Application resources
│   │   └── webapp/        # Web application files
│   └── test/
│       └── java/          # Test files
├── database/              # Database scripts
├── docker/               # Docker configurations
├── elasticsearch/        # Elasticsearch configs
├── gradle/              # Gradle wrapper files
└── docs/                # Documentation
```

## Development

### Code Style
This project follows the Spring Java Format conventions. To ensure consistent code formatting:

1. Install the Spring Java Format plugin for your IDE
2. Format code before committing:
   ```bash
   ./gradlew format
   ```
3. Verify formatting:
   ```bash
   ./gradlew checkFormat
   ```

For more information, visit [Spring Java Format](https://github.com/spring-io/spring-javaformat).

### Building
```bash
# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Build without tests
./gradlew build -x test
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.devkbil.mtssbj.YourTestClass"
```

## Contributing

1. Fork the repository
2. Create your feature branch:
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. Commit your changes:
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. Push to the branch:
   ```bash
   git push origin feature/amazing-feature
   ```
5. Open a Pull Request

### Contribution Guidelines
- Follow the existing code style
- Add tests for new features
- Update documentation as needed
- Keep commits atomic and well-described
- Reference issues in commit messages

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
  - Verify database credentials in application.properties
  - Ensure database container is running:
    ```bash
    docker ps | grep oracle11g
    ```
  - Check database logs:
    ```bash
    docker logs oracle11g
    ```

2. **Elasticsearch Issues**
  - Verify Elasticsearch is running:
    ```bash
    curl localhost:9200/_cluster/health
    ```
  - Check Elasticsearch logs:
    ```bash
    docker logs elasticsearch
    ```
  - Ensure proper configuration in elasticsearch.yml

3. **Mail Server Issues**
  - Verify James server is running:
    ```bash
    docker ps | grep james
    ```
  - Test SMTP connection:
    ```bash
    telnet localhost 465
    ```
  - Check mail server logs:
    ```bash
    docker logs james
    ```

4. **Build Issues**
  - Clear Gradle cache:
    ```bash
    ./gradlew clean --refresh-dependencies
    ```
  - Verify Java version:
    ```bash
    java -version
    ```

For more help, please check our [issue tracker](https://github.com/your-repo/issues).

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
