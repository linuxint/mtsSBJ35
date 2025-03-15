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
9. `front_list.md` - List of JSP files to be converted to React components with migration strategy

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

## Frontend Migration Plan
Based on the `front_list.md` document, I've structured a comprehensive plan for converting JSP files to React components:

### JSP File Organization
I've categorized the 90+ JSP files into functional groups:
1. Layout and Common Components
2. Schedule Management
3. Board Management
4. Mail System
5. User Management
6. Popups and Utilities
7. Admin Functions
8. Search Functionality
9. Project Management
10. CRUD Examples
11. Main Screens
12. Error Pages
13. Approval System
14. Static Files and Templates

### React Component Structure
The migration will follow a component-based architecture with:
- Reusable components in `/components` folders by feature
- Page components in `/pages` to compose complete views
- Separation of concerns between UI components and logic
- Clear folder structure mirroring the existing functionality

### Migration Priorities
The frontend migration will follow this sequence:
1. **Phase 1 (2 weeks)**: Core infrastructure and common components
   - Layout components (Header, Footer, Navigation)
   - Common UI components (Pagination, Messages, Loading)
   - Authentication components
   - Error pages
   
2. **Phase 2 (4 weeks)**: Primary business modules
   - Board-related components
   - Schedule management components
   - User management components
   
3. **Phase 3 (4 weeks)**: Secondary business modules
   - Mail system components
   - Approval system components
   - Project management components
   
4. **Phase 4 (2 weeks)**: Admin and auxiliary functions
   - Admin feature components
   - Search functionality
   - Popups and utility components

### Technical Implementation Strategies
The migration includes detailed strategies for:
- State management with Zustand
- Form handling with React Hook Form and Zod
- Routing with React Router
- Styling with Styled-components or TailwindCSS
- API communication with Axios and React Query
- Component testing with Jest and Storybook

## Detailed Backend Migration Plans

### Common Module Changes
- Convert Java utility classes to TypeScript for frontend use
- Implement HTML escape utilities for XSS protection
- Enhance date utilities with modern Java 8 APIs
- Update file handling utilities with better security and error handling
- Reorganize string utilities with proper validation
- Convert tree-building functionality to TypeScript
- Improve JSON handling with better error management
- Implement JWT utilities with modern security best practices
- Add comprehensive masking utilities for PII data

### Configuration Changes
- Update application entry point with new annotations (@EnableAsync, @EnableCaching)
- Replace WebMvcConfigurerAdapter with WebMvcConfigurer
- Implement JWT-based security configuration
- Enhance MyBatis configuration with improved type handling and caching
- Add JPA configuration with auditing support
- Update CORS configuration for REST API access
- Replace Swagger with SpringDoc OpenAPI
- Implement rate limiting configurations
- Consolidate application event listeners
- Add Redis and Cache configurations
- Implement Elasticsearch integration
- Add environment-specific configuration profiles
- Implement Thymeleaf configuration for email templates
- Add XSS prevention configuration
- Implement Git integration configuration

### Security Implementation
- Convert from session-based to JWT-based authentication
- Update Spring Security configuration for stateless architecture
- Add JWT token generation, validation, and refresh mechanisms
- Implement request filters for JWT validation
- Update CORS configuration
- Enhance authorization with role-based access control
- Improve password encryption with BCrypt
- Add session expiration handling
- Implement custom authentication success handler
- Define role and permission entities
- Add custom security annotations (@AdminAuthorize, @UserAuthorize, etc.)
- Implement role-based URL mapping loader

### Controller Transformations
- Convert all MVC controllers to REST controllers
- Replace JSP view returns with JSON responses
- Add OpenAPI documentation annotations
- Implement standardized response formats
- Update file upload/download endpoints
- Add proper validation handling
- Implement RESTful URL structures
- Add pagination support for list operations
- Update security annotations
- Implement proper HTTP status codes for responses
- Add versioning support where needed

### DTO Implementation
- Create separate Request/Response DTOs
- Add Bean Validation annotations
- Implement Builder pattern for all DTOs
- Add OpenAPI documentation annotations
- Remove circular references for JSON serialization
- Create search request objects with proper pagination support
- Add custom serialization/deserialization where needed

### Entity Changes
- Update VOs to support both MyBatis and JPA
- Add proper annotations
- Implement equals/hashCode/toString methods
- Add Serializable interface
- Implement Builder pattern
- Fix date handling with Java 8 Time API
- Add proper relationships between entities
- Implement audit fields (created/updated timestamps)

### Exception Handling
- Create standardized ErrorResponse structure
- Implement global exception handling
- Add business-specific exceptions
- Add validation exception handling
- Implement security exception handling
- Create custom JWT authentication exceptions
- Add proper logging for all exceptions
- Return appropriate HTTP status codes

### Repository Transformation
- Convert from JPA to MyBatis where needed
- Update mapper annotations
- Implement dynamic queries
- Add pagination support
- Update result mappings for PostgreSQL compatibility
- Implement proper transaction management
- Add batch operation support
- Optimize query performance

### Service Layer Changes
- Add DTO-Entity conversion logic
- Implement business validation
- Move business logic from controllers to services
- Add transaction management
- Implement caching where appropriate
- Add event publishing for cross-cutting concerns
- Implement asynchronous processing where needed
- Add proper error handling

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
2. Implement the component structure as outlined in `front.md` and `front_list.md`
3. Follow the component migration strategy in `front_list.md` with priority on core components
4. Use Zustand for state management (replacing server-side session state)
5. Use React Query and Axios for API communication
6. Implement responsive design with styled-components or tailwindcss
7. Follow the testing strategy outlined in `front_list.md`
8. Track progress using the component status tracker in `front_list.md`

### Database Changes
1. Migrate from Oracle to PostgreSQL
2. Optimize table structures and indexes
3. Update database queries in MyBatis mappers

## Implementation Strategy
1. Start with setting up the basic project structure for both frontend and backend
2. Implement common components and utilities first, following the priority order in `front_list.md`
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
2. Implement the component structure as defined in `front.md` and `front_list.md`
3. Start with layout and common components (Phase 1 in `front_list.md`)
4. Proceed with board, schedule, and user management components (Phase 2)
5. Continue with mail, approval, and project components (Phase 3)
6. Finish with admin features, search, and utilities (Phase 4)
7. Implement state management with Zustand
8. Set up API communication with React Query and Axios
9. Implement responsive design
10. Add comprehensive testing as outlined in `front_list.md`
11. Track progress using the component status tracker

## Timeline
Follow the timeline outlined in `outline.md`, with frontend work following the phased approach in `front_list.md`:
1. Phase 1: Foundation (4 weeks)
   - Development environment setup
   - Common component development (Phase 1 from `front_list.md`)
   - Database migration preparation
2. Phase 2: Core Features (8 weeks)
   - Authentication/Authorization
   - Board functionality (Phase 2 from `front_list.md`)
   - Schedule management
   - Mail system (start of Phase 3 from `front_list.md`)
3. Phase 3: Additional Features (4 weeks)
   - Admin features (Phase 4 from `front_list.md`)
   - Statistics/Reports
   - System monitoring
4. Phase 4: Stabilization (2 weeks)
   - Integration testing
   - Performance optimization
   - User manual creation

## Quality Assurance
1. Implement unit tests for both frontend and backend as outlined in `front_list.md`
2. Set up end-to-end testing with Cypress or Playwright
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
2. Use the component completion checklist in front_list.md for frontend tracking
3. Update documentation as work progresses
4. Document issues encountered and their solutions

Please help me implement this migration plan step by step, starting with the already documented components, 
and then moving to the components that still need documentation.
