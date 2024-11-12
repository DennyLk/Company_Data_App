# Company Data App

A Java-based application structured in the MVC (Model-View-Controller) architecture. This project includes routing to handle HTTP requests, accessible via Postman or other API testing tools, and supports all CRUD (Create, Read, Update, Delete) operations for managing various pieces of information.

## Project Overview

The **Company Data App** is designed to manage and manipulate company-related data. With an MVC structure, it separates the application's core components—data, UI, and control logic—making it modular, maintainable, and scalable.

### Key Features

- **MVC Architecture**: Ensures a clean separation of concerns, with dedicated classes for Models, Views, and Controllers.
- **Routing**: Allows different endpoints to be accessed based on the request type (e.g., `GET`, `POST`, `PUT`, `DELETE`).
- **CRUD Operations**: Full CRUD functionality for managing various company data, accessible through HTTP endpoints.
- **Postman Compatibility**: All endpoints are tested and accessible via Postman.

## Project Structure

The project follows an MVC structure:

- **Model**: Contains data representation and business logic.
- **View**: Responsible for data presentation (in this case, likely returning JSON responses).
- **Controller**: Manages the flow between the model and the view, handling incoming HTTP requests.

### File Organization

