# My-Budget Backend API

## Overview
My-Budget is a personal finance management API that allows users to track expenses. Users can create, update, and delete purchases while gaining insights through visual reports showing spending patterns over customizable date ranges. The API features robust authentication and comprehensive test coverage.

## Key Features
- **Purchase Management**: Full CRUD operations for expense tracking
- **Data Visualization**: Two report formats for expense analysis
- **User Authentication**: JWT-based registration/login with token refresh
- **Pagination**: Configurable pagination for purchase listings
- **Comprehensive Testing**: Unit test coverage for controllers and services

## API Endpoints

### 🔐 Authentication Controller
| Endpoint       | Method | Description                          | Status Codes        |
|----------------|--------|--------------------------------------|---------------------|
| `/register`    | POST   | Register new user                    | 201, 422            |
| `/login`       | POST   | Authenticate user                    | 200, 422, 404       |
| `/refresh`     | POST   | Refresh expired JWT token            | 200, 401, 404       |

### 💰 Purchase Controller
| Endpoint              | Method | Description                          | Status Codes        |
|-----------------------|--------|--------------------------------------|---------------------|
| `/purchases`          | GET    | Get paginated purchases              | 200, 401, 422       |
| `/purchases/{id}`     | GET    | Get purchase by ID                   | 200, 401, 404       |
| `/purchases`          | POST   | Create new purchase                  | 201, 401, 422       |
| `/purchases/{id}`     | PUT    | Update existing purchase             | 201, 401, 404, 422  |
| `/purchases/{id}`     | DELETE | Delete purchase                      | 204, 401, 404       |

**Pagination Parameters:**
| Parameter | Default | Description |
|-----------|---------|-------------|
| `pageNo`  | `0`     | Page number |
| `pageSize`| `10`    | Items per page |
| `sortBy`  | `"id"`  | Field to sort by |
| `sortDir` | `"asc"` | Sort direction (`asc`/`desc`) |

### 📊 Report Controller
| Endpoint                  | Method | Description                          | Parameters                     |
|---------------------------|--------|--------------------------------------|--------------------------------|
| `/reports/table`          | GET    | Get table report data                | `startDate`, `endDate` (format: yyyy-M-d) |
| `/reports/chart`          | GET    | Get chart report data                | `startDate`, `endDate` (format: yyyy-M-d) |


## Technologies

- **Java 21**
  
- **Spring Boot 3.4.4**
  
- **Spring Security**

- **JWT Authentication**
  
- **Swagger/OpenAPI 3**

- **Websocket**

- **Lombok**

- **Mockito**

## Docker instruction: run docker-compose up.

## For detailed API documentation, visit http://localhost:8087/my_budget/swagger-ui/index.html after starting app or docker container.
