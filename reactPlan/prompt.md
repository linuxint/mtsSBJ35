# Cursor AI Prompt for Project Migration

## Project Overview
I need to migrate a Spring MVC + JSP application to a React + Spring Boot REST API architecture. The migration involves:

1. Converting the backend from Spring MVC to Spring Boot REST API
2. Converting the frontend from JSP to React with TypeScript
3. Migrating the database from Oracle to PostgreSQL
4. Separating the monolithic architecture into frontend and backend

## Reference Documents
I have several reference documents that outline the migration plan in detail:
1. `outline.md` - High-Level Migration Plan (overall structure and timeline)
2. `backend.md` - Detailed Backend Architecture Design
3. `backend_file.md` - Files to be Modified (Backend)
4. `backend_config.md` - Configuration changes needed for Spring Boot
5. `backend_security.md` - Security configuration and JWT implementation
6. `backend_service.md` - Service layer changes and business logic updates
7. `backend_controller.md`, `backend_dto.md`, etc. - Detailed specifications for each backend component
8. `front.md` - Detailed Frontend Architecture Design 
9. `front_list.md` - List of JSP files to be converted to React components

## Documented Files
I've already documented many files that need to be migrated. These are categorized in `backend_file.md` into:
1. Files that require changes (with detailed migration plans)
2. Files that can be used without changes
3. Files that still need documentation

The documentation work completed so far includes:
- Configuration settings (Redis, Cache, Elasticsearch)
- Environment-specific configurations 
- Security extensions and authorization annotations
- Application event listeners
- Mail module extensions
- QR code functionality

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
2. Implement JWT-based authentication to replace session-based authentication as detailed in `backend_security.md`
3. Reorganize the package structure according to `backend.md`
4. Optimize MyBatis queries for PostgreSQL
5. Implement proper error handling and response formats for REST APIs
6. Apply all configuration changes documented in `backend_config.md`
7. Update security implementations as per `backend_security.md`
8. Revise service components following `backend_service.md`
9. Record all changes made to each file in `backend_file.md`

### Frontend Changes
1. Create a new React application with TypeScript
2. Implement the component structure as outlined in `front.md`
3. Use Zustand for state management (replacing server-side session state)
4. Use React Query and Axios for API communication
5. Implement responsive design with styled-components or tailwindcss
6. Record all changes made to each file in `front_list.md`

### Database Changes
1. Migrate from Oracle to PostgreSQL
2. Optimize table structures and indexes
3. Update database queries in MyBatis mappers

## Implementation Strategy
1. Start with setting up the basic project structure for both frontend and backend
2. Implement common components and utilities first
3. Migrate core functionality one module at a time, prioritizing the already documented components
4. Focus next on files listed in backend_file.md as "still requiring documentation"
5. Implement comprehensive testing for each migrated module
6. Set up CI/CD pipeline for automated testing and deployment

## Specific Tasks

### Backend Tasks
1. Create a new Spring Boot project with the structure defined in `backend.md`
2. Implement JWT authentication according to `backend_security.md`
3. Convert existing controllers to REST controllers
4. Update service layer to work with REST controllers following `backend_service.md` guidelines
5. Implement caching, Redis, and Elasticsearch configurations as documented in `backend_config.md`
6. Apply all documented application event listeners
7. Configure environment-specific settings 
8. Optimize database access with MyBatis for PostgreSQL
9. Implement proper error handling and response formats
10. Record all changes made to each file in `backend_file.md`

### Frontend Tasks
1. Set up a new React project with TypeScript
2. Implement the component structure as defined in `front.md`
3. Create reusable UI components as specified in the frontend architecture
4. Implement state management with Zustand
5. Set up API communication with React Query and Axios
6. Implement responsive design
7. Record all changes made to each file in `front_list.md`

## Timeline
Follow the timeline outlined in `outline.md`:
1. Phase 1: Foundation (4 weeks)
   - Development environment setup
   - Common component development
   - Database migration preparation
2. Phase 2: Core Features (8 weeks)
   - Authentication/Authorization
   - Board functionality
   - Schedule management
   - Mail system
3. Phase 3: Additional Features (4 weeks)
   - Admin features
   - Statistics/Reports
   - System monitoring
4. Phase 4: Stabilization (2 weeks)
   - Integration testing
   - Performance optimization
   - User manual creation

## Quality Assurance
1. Implement unit tests for both frontend and backend
2. Set up end-to-end testing
3. Perform performance testing
4. Ensure security best practices are followed
5. Validate accessibility compliance

## Risk Management
1. Technical Risks
   - Legacy code dependencies
   - Data migration challenges
   - Performance issues
2. Operational Risks
   - Service disruption minimization
   - Rollback plan
   - User training

## Progress Tracking
1. Use backend_file.md to track backend file migration progress
2. Create a similar structure for frontend migration tracking
3. Update documentation as work progresses
4. Document issues encountered and their solutions

Please help me implement this migration plan step by step, starting with the already documented components, and then moving to the components that still need documentation.
