# Smart Campus API - Coursework 2026

## Project Overview
This is a RESTful Client-Server system developed to manage the infrastructure of a "Smart Campus." The system facilitates the management of campus rooms and their associated environmental sensors (such as CO2 and Temperature) through a versioned API.

## Technology Stack
To ensure full compliance with the coursework specifications, the following stack was used:
* **Framework:** JAX-RS (Jersey Implementation)
* **Language:** Java 8
* **Server:** GlassFish 4.1.1 / Payara
* **Data Storage:** In-memory (ArrayList & HashMap). No SQL or external databases were used.
* **Architecture:** RESTful Web Services

---

## Conceptual Report & Design Decisions

### JAX-RS Resource Lifecycle
The JAX-RS resource classes in this project are request-scoped by default. This means the runtime environment instantiates a new object for every incoming HTTP request and disposes of it after the response is sent. To maintain data persistence across these independent lifecycles, a centralized `DataStore` class was implemented using static members. This ensures that room and sensor data remains consistent while the server is active.

### Hypermedia and HATEOAS
Hypermedia support was integrated into the discovery endpoint to provide a self-descriptive API. By including navigational links in the JSON responses, the system reduces the coupling between the server and the client. Developers can discover available resource collections (like /rooms and /sensors) dynamically, making the API more resilient to future changes in the URL structure.

### Idempotency of DELETE Operations
The implementation of the DELETE method for room management is idempotent. While the first call returns a success status (204 No Content) and subsequent calls return a 404 Not Found (since the resource is already gone), the side effect on the server is the same: the resource remains deleted. This adheres to RESTful standards where multiple identical requests result in the same server state.

### Security and Error Handling
To enhance the security of the API, custom exception mappers were implemented to intercept server errors. This prevents the API from leaking internal Java stack traces to the client. Restricting this information is critical to prevent attackers from gaining insights into the server’s file structure, library versions, or internal logic.

---

## Build and Launch Instructions
1. **Prerequisites:** Ensure **JDK 8 or 11** is installed and configured in your IDE.
2. **Compile:** Right-click the project in NetBeans and select **Clean and Build**.
3. **Deployment:** Click the **Run** button to deploy the WAR file to the GlassFish server.
4. **API Access:** The service entry point is located at: `http://localhost:8080/SmartCampusAPI/api/v1/`

---

## Sample Testing Commands (curl)

**1. API Discovery**
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/

curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms

curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id": "R105", "name": "Computer Lab", "capacity": 40}'

curl -X PUT http://localhost:8080/SmartCampusAPI/api/v1/sensors/S101 \
-H "Content-Type: application/json" \
-d '{"status": "ACTIVE", "currentValue": 24.0}'

curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/R105

Part 1: Service Architecture & Setup 
1. Answer for question 01:  
By default, JAX-RS creates a new instance of a resource class for each incoming HTTP request. 
This is known as request-scoped behavior. Because of this, instance variables are not shared 
between requests and cannot be used to store data permanently. 
This directly affects how data is managed. If data were stored in instance variables inside 
resource classes, it would be lost after each request is completed. To avoid this, all shared data in 
this project is stored in a static DataStore class using ConcurrentHashMap. Since 
ConcurrentHashMap is thread-safe, it allows multiple clients to access and modify data at the 
same time without causing race conditions. In contrast, using a regular HashMap could lead to 
data loss or ConcurrentModificationException when accessed concurrently.

2. Answer for question 02: 
HATEOAS means including navigable links in API responses so that clients can discover what 
actions are available without relying on fixed documentation. For example, when a list of rooms 
is returned, the response might include a _links section with URLs like /api/v1/rooms/{id} and 
/api/v1/sensors. 
This approach is useful for client developers because they don’t have to hard-code URLs or 
constantly refer back to documentation. Instead, the API becomes self-describing. A client can 
start at /api/v1 and follow the links provided to move through the system. This reduces the 
dependency between the client and server, which makes it easier to update or expand the API 
later on. 

Part 2: Room Management 
3.Answer for question 03: 
Returning only IDs in a room list is more efficient in terms of bandwidth, especially for large 
collections. However, it forces the client to make additional requests to get the full details for 
each room, leading to the N+1 problem. On the other hand, returning full objects increases the 
size of the response, but allows the client to display all the necessary information with a single 
request. 
A balanced approach is to return full objects when dealing with moderately sized collections, and 
apply pagination when the dataset becomes very large 
. 

4. Answer for question 04: 
DELETE is idempotent in this implementation. The first request removes the room and returns a 
200 response. If the same request is sent again, the room no longer exists, so the server returns a 
404. In both situations, the final state of the system is the same—the room is not present. There 
are no additional side effects from repeating the request. Therefore, this behavior satisfies the 
REST requirement for idempotency, since performing the operation multiple times has the same 
result as performing it once. 

Part 3: Sensor Operations & Linking 
5. Answer for question 05: 
If a client sends a request with Content-Type: text/plain or application/xml to an endpoint that 
only accepts JSON (@Consumes(MediaType.APPLICATION_JSON)), JAX-RS will 
automatically return an HTTP 415 Unsupported Media Type error. This happens before the 
method is even executed. The framework checks whether the request’s Content-Type matches 
what the endpoint expects, and if it doesn’t, the request is rejected immediately. This helps 
prevent the method from receiving data it cannot properly process. 

6.Answer for question 06: 
Using @QueryParam (for example, GET /sensors?type=CO2) is better for filtering because it 
shows that type is just an optional filter on a collection, not a separate resource. In contrast, using 
a path like /sensors/type/CO2 suggests a different resource structure, which can be misleading. 
Query parameters are also more flexible since you can combine multiple filters easily (e.g., 
?type=CO2&status=ACTIVE) without changing the overall URL format. In REST, path 
segments are usually meant to identify specific resources, while query parameters are meant for 
filtering or modifying the results. 

Part 4: Deep Nesting with Sub – Resources 
7. Answer for question 07: 
The sub-resource locator pattern is used to pass the handling of a nested path to a separate class. 
In this project, SensorResource manages the /sensors endpoint and delegates 
/sensors/{id}/readings to SensorReadingResource. This helps keep each class focused on a single 
responsibility. 
In larger APIs with many nested resources, placing all endpoints in one class can lead to a 
difficult-to-manage “God class.” By separating them into different resource classes, the code 
becomes easier to test, maintain, and extend. It also allows multiple team members to work on 
different parts of the API at the same time without conflicts. 

Part 5: Advanced Error Handling & Exception Mapping 
8. Answer for question 08: 
HTTP 404 means the requested URI could not be found, while HTTP 422 means the request is 
syntactically correct but cannot be processed because of its content. For example, if a client 
sends a POST request to create a sensor with a roomId that doesn’t exist, the endpoint 
/api/v1/sensors is still valid and accessible. The issue is within the request body, where it refers 
to a resource that isn’t there. Returning a 404 in this case could make it seem like the endpoint 
itself is missing, which isn’t true. Using 422 is more accurate because it clearly indicates that the 
problem lies in the request data, making it easier for the client to understand and fix the error. 

9. Answer for question 09: 
Exposing raw Java stack traces to API users is a serious security risk. A stack trace can reveal a 
lot of sensitive internal details, such as: 
Internal package and class names, which can confirm the technology stack being used (like Java 
and specific frameworks) Library versions, making it easier for attackers to find known 
vulnerabilities (CVEs) File paths on the server, exposing the system’s directory structure The 
flow of business logic, showing which methods were executed and giving insight into the 
application’s design Database or service names, especially if the error comes from the data layer, 
where things like table names or connection details might appear 
In this project, a global ExceptionMapper is used to catch any unhandled exceptions. It logs the 
full stack trace on the server for developers to review, but only returns a simple 500 error 
message to the client, without exposing any of these internal details. 

10. Answer for question 10: 
Using a JAX-RS filter for logging is a good way to handle cross-cutting concerns. The 
alternative—adding Logger.info() calls in every resource method—has several drawbacks. It’s 
easy to forget to include logging in new endpoints, which creates gaps. If the log format needs to 
change, every method has to be updated. It also makes resource classes cluttered with code that 
isn’t part of the core business logic. With a filter, logging is handled in one place. It 
automatically intercepts every request and response, no matter which endpoint processes them. 
This means it only needs to be added once, is easier to maintain, and won’t be accidentally left 
out. 