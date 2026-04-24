# Weather Dashboard

A Spring Boot weather application that fetches real-time weather data for any city worldwide. Search for current conditions, forecasts, astronomy data, timezone info, and local sports events. Built with Java 21, Thymeleaf, and the WeatherAPI.

## Features

**Weather Data:**
- Current weather with real-time temperature, humidity, wind speed, and conditions
- 3-day detailed weather forecasts with highs/lows
- Sunrise, sunset, moonrise, and moonset times
- Timezone information for any location
- Local sports events by location
- Wind and storm alerts based on current conditions

**User Features:**
- Search history with pagination (20 results per page)
- Statistics dashboard showing most searched cities and recent activity
- Auto-location detection based on your IP address
- Smart caching system to reduce API calls
- Persistent H2 database to store all search history

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 4.0.5 |
| Language | Java 21 |
| Web | Spring Web MVC |
| View Engine | Thymeleaf 3 |
| Database | H2 (Embedded) |
| ORM | Spring Data JPA / Hibernate |
| API Client | RestTemplate |
| Caching | Spring Cache |
| Build Tool | Maven 3.6+ |
| Utilities | Lombok, Jakarta Validation |
| Logging | SLF4J with Logback |

## Setup & Installation

### Prerequisites

Before you begin, make sure you have the following installed:

- **Java 21 or higher** - Check with: `java -version`
- **Maven 3.6+** - Check with: `mvn -version`
- **Git** - For cloning the repository
- A text editor or IDE (JetBrains IntelliJ, VS Code, etc.)

### Step-by-Step Installation

**1. Clone the Repository**

```bash
git clone https://github.com/yourusername/weather-dashboard.git
cd weather-dashboard
```

**2. Create a Free WeatherAPI Account**

- Go to [WeatherAPI.com](https://www.weatherapi.com/)
- Sign up for a free account (you'll get 1 million free API calls per month)
- Navigate to your dashboard and copy your API key

**3. Configure Your API Key**

Open `src/main/resources/application.yaml` and add your API key:

```yaml
weather:
  api:
    key: YOUR_API_KEY_HERE          # Replace with your actual API key
    base-url: https://api.weatherapi.com/v1
```

**4. Build the Project**

```bash
mvn clean install
```

This downloads all dependencies and compiles the code. It may take a few minutes on first run.

**5. Run the Application**

**Option A: Using Maven (recommended for development)**
```bash
mvn spring-boot:run
```

**Option B: Run the JAR file directly**

First build the JAR:
```bash
mvn package
```

Then run it:
```bash
java -jar target/weather-dashboard-0.0.1-SNAPSHOT.jar
```

**6. Access the Application**

- **Main Application:** Open your browser and go to `http://localhost:8080`
- **H2 Database Console:** Go to `http://localhost:8080/h2-console`
  - Username: `weather-user`
  - Password: `password`

You should see the Weather Dashboard homepage with a search box. Try searching for a city!

## Usage Guide

### Available Pages

| Route | Purpose | Example URL |
|-------|---------|-------------|
| `/` | Home page with city search | `http://localhost:8080/` |
| `/weather` | Current weather for a city | `/weather?city=London` |
| `/forecast` | 3-day weather forecast | `/forecast?city=New York` |
| `/astronomy` | Sunrise/sunset/moon data | `/astronomy?city=Tokyo` |
| `/timezone` | Timezone information | `/timezone?city=Sydney` |
| `/sports` | Local sports events | `/sports?city=Paris` |
| `/history` | Your search history | `/history?page=0` |
| `/stats` | Analytics dashboard | `/stats` |
| `/detect-location` | Auto-detect your location | `/detect-location` |

### How to Use

1. **Search for a City**
   - Go to the home page (`/`)
   - Enter a city name in the search box
   - Click search or press Enter
   - You'll see the current weather for that city

2. **View Different Data Types**
   - Each main page (forecast, astronomy, timezone, sports) requires a city parameter
   - Example: Click "View Forecast" from a weather page, or manually go to `/forecast?city=Tokyo`

3. **Check Your Search History**
   - Navigate to `/history` to see all your past searches
   - Results are paginated with 20 items per page
   - Use the page parameter: `/history?page=0` (page 0 is the first page)

4. **View Statistics**
   - Go to `/stats` to see:
     - Most searched cities (all time)
     - Recent searches (last 24 hours)
     - Total number of searches

5. **Auto-Detect Location**
   - Click the "Auto-Detect Location" button
   - The app uses your IP address to determine your city
   - Automatically shows weather for your location

## Database

### H2 Embedded Database

The application uses **H2**, a lightweight embedded SQL database that requires no separate installation. The database file is stored locally on your machine.

**Database Details:**
- **Type:** H2 Embedded
- **Location:** Your home directory as `~/weatherdb`
- **Auto-creation:** Database is automatically created when you first run the app
- **Port:** Runs embedded within the application (no separate server)

### Accessing the H2 Console

You can view and manage the database through the web interface:

1. While the app is running, go to: `http://localhost:8080/h2-console`
2. Login with these credentials:
   - **JDBC URL:** `jdbc:h2:file:~/weatherdb`
   - **Username:** `weather-user`
   - **Password:** `password`
3. Click "Connect" to access the database

### Database Schema

The app creates two main tables:

**weather_history**
- Stores every weather search you perform
- Columns: city name, temperature, humidity, wind speed, description, search timestamp, alerts
- Used for: History page, statistics, caching

**forecast_history**
- Stores 3-day forecast data for each city
- Columns: city name, forecast date, max temp, min temp, weather condition
- Used for: Forecast display and historical trend analysis

You can query these tables directly in the H2 console using SQL.

### Resetting the Database

If you want to clear all history and start fresh:

1. Stop the application
2. Delete the file: `~/weatherdb.h2.db`
3. Restart the application (it will create a fresh new database)

## Caching

The application uses Spring Cache to reduce API calls and improve performance.

### How Caching Works

**Current Weather Caching (10 minutes):**
- When you search for a city's weather, the result is cached in memory for 10 minutes
- If you search for the same city again within 10 minutes, the cached result is returned instantly
- No new API call is made, saving your API quota and providing faster responses

**City Search Autocomplete (also cached):**
- City search suggestions are cached
- Repeated searches for the same prefix use cached results

### Benefits

- **Faster Response Times:** Cached results are instant (no API lag)
- **Reduced API Usage:** Fewer API calls means staying well under rate limits
- **Lower Latency:** No network round-trip for cached queries
- **Free Tier Friendly:** The free WeatherAPI tier gives 1M calls/month - caching helps stay within this

### Example

```
Search 1: London at 2:00 PM → API call made, result cached
Search 2: London at 2:05 PM → Cached result returned instantly (no API call)
Search 3: London at 2:15 PM → Cached result still valid (no API call)
Search 4: London at 2:12 PM (next day) → Cache expired, new API call made
```

## Code Structure & Architecture

### Project Layout

```
weather-dashboard/
├── src/main/java/com/weatherapp/weatherdashboard/
│   ├── WeatherDashboardApplication.java         ← Application entry point
│   ├── controller/
│   │   ├── WeatherController.java               ← Handles web page requests
│   │   └── WeatherRestController.java           ← REST API endpoints
│   ├── service/
│   │   └── WeatherService.java                  ← Business logic & API calls
│   ├── repository/                              ← Database queries (JPA)
│   │   ├── WeatherRepository.java
│   │   └── ForecastRepository.java
│   ├── entity/                                  ← Database models (JPA)
│   │   ├── WeatherHistory.java
│   │   └── ForecastHistory.java
│   ├── dto/                                     ← Data Transfer Objects
│   │   ├── WeatherDTO.java
│   │   ├── ForecastDTO.java
│   │   ├── AstronomyDTO.java
│   │   └── ... (other response DTOs)
│   └── exception/                               ← Custom exceptions
│       └── CityNotFoundException.java
├── src/main/resources/
│   ├── application.yaml                         ← Configuration & API key
│   ├── templates/                               ← HTML pages (Thymeleaf)
│   │   ├── index.html
│   │   ├── weather.html
│   │   ├── forecast.html
│   │   ├── history.html
│   │   ├── stats.html
│   │   └── ... (other pages)
│   └── static/css/                              ← Styling
│       └── style.css
├── pom.xml                                      ← Maven dependencies
└── README.md                                    ← This file
```

### How It Works

1. **User Request** → Browser sends request to `/weather?city=London`
2. **Controller** → `WeatherController` receives the request
3. **Service** → `WeatherService` checks cache first, then calls WeatherAPI if needed
4. **Database** → Result is saved to `weather_history` table
5. **Template** → Data passed to `weather.html` Thymeleaf template
6. **Response** → HTML page with weather data is rendered and sent to browser

### Key Components Explained

**Controllers:**
- Routes incoming HTTP requests to appropriate handlers
- Passes data from the service layer to templates
- Returns rendered HTML pages or JSON responses

**Service Layer (WeatherService):**
- Contains all business logic
- Makes REST calls to WeatherAPI
- Implements caching logic
- Saves results to database
- Handles error cases

**Repositories (JPA):**
- Provides database query methods
- Handles CRUD operations (Create, Read, Update, Delete)
- Custom queries for statistics and pagination

**DTOs (Data Transfer Objects):**
- Map the JSON responses from WeatherAPI to Java objects
- Deserialized automatically by RestTemplate
- Keeps API response structure clean and usable

**Templates (Thymeleaf):**
- HTML pages that display the data
- Dynamic content injection using Thymeleaf expressions
- Integrated with Bootstrap for styling

## Configuration

### application.yaml Settings

The main configuration file is at `src/main/resources/application.yaml`. Here are the key settings:

```yaml
server:
  port: 8080                                          # Application port (change if 8080 is busy)

spring:
  application:
    name: weather-dashboard
    
  datasource:
    url: jdbc:h2:file:~/weatherdb                    # Database location
    driverClassName: org.h2.Driver
    username: weather-user
    password: password
    
  h2:
    console:
      enabled: true                                   # Enable H2 web console
      
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update                               # Options: create/create-drop/update/none
    show-sql: true                                   # Print SQL queries to console
    
weather:
  api:
    key: YOUR_API_KEY_HERE                           # Your WeatherAPI.com API key (REQUIRED)
    base-url: https://api.weatherapi.com/v1          # WeatherAPI base URL
```

### Configuration Options Explained

**Server Port:**
- Default: `8080`
- Change this if port 8080 is already in use on your machine
- Example: `port: 9090` will run the app at `http://localhost:9090`

**Database DDL-Auto Options:**
- `create` - Drops existing tables and creates new ones (fresh start)
- `create-drop` - Creates tables on startup, drops on shutdown
- `update` - Modifies existing tables to match entities (safe for development)
- `none` - No automatic schema management (for production)

**Show SQL:**
- `true` - Prints all SQL queries to console (useful for debugging)
- `false` - Hides SQL output (cleaner console)

**API Key:**
- **Required:** Must be set for the app to work
- Get it from your WeatherAPI.com dashboard
- Free tier: 1,000,000 calls per month

### Custom Configuration

To use a different port, database location, or other settings, simply edit the YAML file and restart the application.

## Troubleshooting

### Application Won't Start

**Problem:** Getting "Address already in use" error
- **Cause:** Port 8080 is already being used
- **Solution:** 
  - Either close the other application using port 8080
  - Or change the port in `application.yaml`: `port: 9090`
  - Restart the app

**Problem:** "Java command not found"
- **Cause:** Java is not installed or not in your PATH
- **Solution:** 
  - Install Java 21 from [java.com](https://www.java.com/)
  - Check installation: `java -version` in terminal
  - Restart your terminal after installing

**Problem:** Maven errors during build
- **Cause:** Maven not installed or older version
- **Solution:**
  - Install Maven from [maven.apache.org](https://maven.apache.org/)
  - Check: `mvn -version`
  - Make sure it shows Java 21+ in the output

**Problem:** "Cannot fetch weather" or blank page
- **Cause:** API key is missing or invalid
- **Solution:** 
  - Check your API key is correctly added to `application.yaml`
  - Go to WeatherAPI.com dashboard and verify the key
  - Try a different key if it's not working

### City Not Found Error

**Problem:** "City not found" when searching a valid city
- **Cause:** City name too vague or API key issue
- **Solution:** 
  - Try being more specific: "London, UK" instead of "London"
  - Try "New York, US" instead of "New York"
  - Check your API key in `application.yaml` is valid
  - Verify WeatherAPI.com shows your account is active

### Database Issues

**Problem:** Getting database connection errors
- **Cause:** Database file is corrupted or in wrong location
- **Solution:**
  1. Stop the application
  2. Delete the database file: `~/weatherdb.h2.db`
  3. Restart the application (it will create a fresh database)

**Problem:** Can't access H2 console
- **Cause:** Console not enabled or wrong URL
- **Solution:**
  - Check `application.yaml` has `h2.console.enabled: true`
  - Try: `http://localhost:8080/h2-console`
  - Make sure the application is running

### API Rate Limiting

**Problem:** Getting rate limit errors
- **Cause:** Too many API calls made in short time
- **Details:** Free tier allows 1,000,000 calls per month
- **Solution:** 
  - Wait a few minutes before retrying
  - Application caching helps - it won't call API twice in 10 minutes for same city
  - Consider upgrading your WeatherAPI plan if you need more

### Port Already in Use

**Problem:** "Address already in use"
- **Solution:**
  ```bash
  # On Windows PowerShell, find what's using port 8080:
  netstat -ano | findstr :8080
  ```
  Then either kill that process or change your port in `application.yaml`

## Additional Information

### Key Features Explained

**Smart Caching:**
- Current weather results cached for 10 minutes per city
- Significantly reduces API calls and improves response time
- Automatically expires after 10 minutes

**Search History:**
- Every weather search is saved to database
- Viewable with pagination (20 results per page)
- Useful for tracking what you've looked up

**Statistics Dashboard:**
- Shows most searched cities (all time)
- Recent search activity (last 24 hours)
- Total search count
- Updates automatically as you search

**Auto-Location Detection:**
- Uses your IP address to detect approximate location
- Redirects to weather page for your detected city
- Good for quick weather check of your area

**Alerts System:**
- Wind speed alerts (triggers if wind > 50 kph)
- Storm warnings (if "storm" mentioned in conditions)
- Displayed on weather page when conditions trigger them

### Performance Notes

- **First Load:** May take a few seconds for Maven to download dependencies
- **Subsequent Loads:** Much faster due to caching
- **Database:** H2 is embedded, so no network overhead
- **Memory Usage:** Minimal - suitable for personal use

### API Limits (WeatherAPI.com Free Tier)

- **Calls per month:** 1,000,000 (very generous)
- **Call rate:** No hard limit per second
- **Data retention:** Current weather only
- **Forecast:** Up to 10 days available

### Browser Compatibility

- Chrome, Firefox, Safari - all modern versions supported
- IE11 not tested or supported
- Responsive design works on mobile browsers

### Project Templates

The HTML templates are located in `src/main/resources/templates/` and use:
- Thymeleaf templating engine for dynamic content
- Bootstrap CSS framework for responsive layout
- Basic custom styling in `static/css/style.css`

You can edit the templates to change the look and feel of the application.

### Logging

The application logs important events to console:
- `INFO` - Major operations (searches, saves)
- `DEBUG` - Detailed information (cache hits, API calls)
- `ERROR` - Problems and failures

To change log levels, edit `application.yaml`:
```yaml
logging:
  level:
    root: INFO
    com.weatherapp: DEBUG
```

### Building for Production

To create an optimized production JAR:

```bash
mvn clean package -DskipTests
```

This creates an executable JAR at `target/weather-dashboard-0.0.1-SNAPSHOT.jar`

Then deploy and run on your server:
```bash
java -jar weather-dashboard-0.0.1-SNAPSHOT.jar
```

