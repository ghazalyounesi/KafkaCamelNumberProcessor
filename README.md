# KafkaCamelNumberProcessor

A lightweight Apache Camel-based microservice that produces and consumes random numbers using Apache Kafka.  
The project demonstrates how to integrate Kafka messaging with Camel routes, process and accumulate numbers,  
and report the sum periodically. It is fully Dockerized for easy deployment and testing.

---

## Features

- Produces random numbers within a configurable range and publishes them to a Kafka topic.  
- Consumes numbers from Kafka, validates, and accumulates them using a dedicated service.  
- Periodically logs the sum of numbers consumed in the last interval (e.g., every 60 seconds).  
- Supports manual triggering of the producer via a direct route.  
- Graceful handling of invalid input with custom exceptions.  
- Fully Dockerized environment with Kafka, Zookeeper, and the application containers orchestrated via Docker Compose.

---

## Technologies Used

- Java 11  
- Apache Camel  
- Apache Kafka & Zookeeper (Confluent Platform)  
- Spring Boot  
- Lombok  
- Docker & Docker Compose  
- Maven  

---

## Getting Started

### Prerequisites

- Docker & Docker Compose installed on your machine  
- Maven (for building the project)  

### Build the Project

```bash
mvn clean package
```

### Run with Docker Compose

The project includes a `docker-compose.yml` file that launches:

- Zookeeper
    
- Kafka broker
    
- The KafkaCamelNumberProcessor application
    

Simply run:

bash

CopyEdit

`docker-compose up --build`

The application will start and connect to Kafka automatically.

---

## Configuration

You can configure Kafka connection details and application behavior via environment variables or `application.properties`.

For example, Kafka bootstrap servers and topic name are configurable.

---

## Usage

- The producer route generates a random number every 10 seconds and publishes it to Kafka.
    
- You can also trigger the producer manually using the `direct:start` route.
    
- The consumer route listens to Kafka, extracts numbers, accumulates them, and logs the sum every minute.
    

---

## Project Structure

- `routes/` - Camel routes for producing and consuming messages.
    
- `producer/` - Producer logic with configurable range.
    
- `service/` - Accumulator service for number aggregation.
    
- `util/` - Utility classes like `NumberExtractor`.
    
- `exception/` - Custom exceptions for input validation.
    
- `config/` - Configuration classes.

  ---

## Author

Ghazal Younesi
[ghazal1384926054@gmail.com]
