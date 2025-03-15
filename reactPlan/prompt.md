# Cursor AI Prompt for Project Migration

## Project Overview
I need to migrate a Spring MVC + JSP application to a React + Spring Boot REST API architecture. The migration involves:

1. Converting the backend from Spring MVC to Spring Boot REST API
2. Converting the frontend from JSP to React with TypeScript
3. Migrating the database from Oracle to PostgreSQL
4. Separating the monolithic architecture into frontend and backend

## Reference Documents
I have three reference documents that outline the migration plan:
1. `outline.md` - High-Level Migration Plan
2. `backend.md` - Detailed Backend Architecture Design
3. `backend_file.md` - Files to be Modified (Backend)
4. `front.md` - Detailed Frontend Architecture Design
5. `front_file.md` - Files to be Modified (Frontend)

## Current Project Structure
The current project is a Spring MVC application with JSP views. The main packages are:
- `/src/main/java/com/devkbil/mtssbj` - Main application code
  - `/admin` - Admin functionality
  - `/board` - Board functionality
  - `/common` - Common utilities
  - `/config` - Configuration
  - `/crud` - CRUD operations
  - `/develop` - Test code
  - `/error` - Error handling
  - `/etc` - Other functions
  - `/mail` - Mail functionality
  - `/main` - Main application
  - `/member` - Member management
  - `/monitor` - Monitoring
  - `/project` - Project management
  - `/schedule` - Schedule management
  - `/search` - Search functionality
  - `/sign` - Authentication
- `/src/main/webapp` - JSP views
- `/src/main/resources` - Configuration and static resources

## Required Changes

### Backend Changes
1. Convert Spring MVC controllers to REST controllers that return JSON instead of JSP views
2. Implement JWT-based authentication
3. Reorganize the package structure according to `backend.md`
4. Optimize MyBatis queries for PostgreSQL
5. Implement proper error handling and response formats for REST APIs

### Frontend Changes
1. Create a new React application with TypeScript
2. Implement the component structure as outlined in `front.md`
3. Use Zustand for state management
4. Use React Query and Axios for API communication
5. Implement responsive design with styled-components or tailwindcss

### Database Changes
1. Migrate from Oracle to PostgreSQL
2. Optimize table structures and indexes
3. Update database queries in MyBatis mappers

## Implementation Strategy
1. Start with setting up the basic project structure for both frontend and backend
2. Implement common components and utilities first
3. Migrate core functionality one module at a time
4. Implement comprehensive testing for each migrated module
5. Set up CI/CD pipeline for automated testing and deployment

## Specific Tasks

### Backend Tasks
1. Create a new Spring Boot project with the structure defined in `backend.md`
2. Implement JWT authentication
3. Convert existing controllers to REST controllers
4. Update service layer to work with REST controllers
5. Optimize database access with MyBatis for PostgreSQL
6. Implement proper error handling and response formats
7. Let's record changes made to each file in backend_file.md

### Frontend Tasks
1. Set up a new React project with TypeScript
2. Implement the component structure as defined in `front.md`
3. Create reusable UI components
4. Implement state management with Zustand
5. Set up API communication with React Query and Axios
6. Implement responsive design
7. Let's record changes made to each file in front_list.md

## Timeline
Follow the timeline outlined in `outline.md`:
1. Phase 1: Foundation (4 weeks)
2. Phase 2: Core Features (8 weeks)
3. Phase 3: Additional Features (4 weeks)
4. Phase 4: Stabilization (2 weeks)

## Quality Assurance
1. Implement unit tests for both frontend and backend
2. Set up end-to-end testing
3. Perform performance testing
4. Ensure security best practices are followed
5. Validate accessibility compliance

Please help me implement this migration plan step by step, starting with setting up the basic project structure for both frontend and backend.
