# Vehicle Credit Simulator

A simple Spring Boot application for simulate vehicle loan installments with REST API endpoints and Interactive console mode.

## Features

- Calculate monthly installments for motorcycles or cars
- Support for new and used vehicles
- Support external API for existing loan calculation
- REST API and console mode
- Docker containerization support

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional)

### Quick Start

#### Option 1: Using Docker

```bash
# Build and run with Docker Compose
docker-compose up --build

# Or build Docker image manually
docker build -t credit-simulator .
docker run -p 8080:8080 credit-simulator
```

#### Option 2: Using Scripts

**Windows:**
```bash
# Run the application
credit_simulator.bat

# With file input
credit_simulator.bat file_inputs.txt
```

**Linux/Mac:**
```bash
# Run the application
./credit_simulator.sh

# With file input
./credit_simulator.sh file_inputs.txt
```

#### Option 3: Manual Run

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
java -jar target/credit-simulator-0.0.1-SNAPSHOT.jar

# Or using Maven
mvn spring-boot:run
```

## API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### Main Endpoint

**POST** `/api/v1/loan/calculate`

Calculate vehicle loan installments.

**Request Body:**
```json
{
  "vehicleType" : "Mobil",
  "vehicleCondition" : "Baru",
  "vehicleYear" : "2025",
  "totalLoanAmount" : "100000000",
  "loanTenure" : "3",
  "downPayment" : "25000000",
  "isActive" : "N"
}
```

**Response:**
```json
{
  "data" : {
    "installmentMonthlyAverage": "2441233.33",
    "yearlyInformations": [
      {
        "year": 1,
        "interestRate": 8.0,
        "principalAmount": "75000000.00",
        "totalLoanAmount": "81000000.00",
        "installmentMonthly": "2250000.00",
        "installmentYearly": "27000000.00"
      },
      {
        "year": 2,
        "interestRate": 8.1,
        "principalAmount": "54000000.00",
        "totalLoanAmount": "58374000.00",
        "installmentMonthly": "2432000.00",
        "installmentYearly": "29184000.00"
      },
      {
        "year": 3,
        "interestRate": 8.6,
        "principalAmount": "29190000.00",
        "totalLoanAmount": "31700340.00",
        "installmentMonthly": "2641700.00",
        "installmentYearly": "31700400.00"
      }
    ]
  },
  "errors" : ""
}
```

## File Input Format

Create a text file with the following format (one value per line):

```
Mobil
Bekas
2025
100000000
3
25000000
```

## Interest Rate Spesification

| Vehicle Type | Base Interest Rate |
|-----------------|--------------------|
| Motor           | 9% annual          |
| Mobil           | 8% annual       |


## Down Payment Spesification
| Vehicle Condition | Down Payment |
|-------------------|--------------|
| Used              | 35% annual   |
| New               | 8% annual    |

## Bussines Rule
- Vehicle with “NEW” Condition cannot input year less than currentYear - 1 (Numeric Only)
- Tenor Cannot more than 6 years.
- Interest Year On Year increase 0,5% every 2 year.
- Interest Year On Year increase 0,1% every 1 year.
- Total Down Payment New (Mobil or Motor) >= 35% Total Loan Amount
- Total Down Payment Used (Mobil or Motor) >= 25% Jumlah Pinjama

## Console Commands

In interactive mode, available commands:
- `1` or `calculate` - Calculate new loan
- `2` or `load` - Load from third party
- `3` or `save` - Save calculation
- `4` or `list` - List saved calculations
- `show` - Display commands
- `exit` - Exit application

## Configuration

Key configuration in `application.properties`:

```properties
# Server
server.port=8080

# External API
third-party.url=https://run.mocky.io/v3/9108b1da-beec-409e-ae14-e8091955666c

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

## Development

### Running Tests

```bash
mvn test
```

### Building

```bash
mvn clean package
```

---