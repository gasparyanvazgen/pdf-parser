# PDF Parser

PDF Parser is a Spring Boot-based application designed to extract text and images from PDF attachments within Gmail messages. It relies on Google APIs to access Gmail and Firebase Cloud Storage to save the extracted images.

## Prerequisites

To begin, ensure you have the following prerequisites in place:

- Java 11 or higher
- Gmail API Credentials JSON (Refer to the [Gmail API Quickstart Guide](https://developers.google.com/gmail/api/quickstart/java) for acquiring these credentials)
- Firebase API Credentials JSON for Firebase Cloud Storage (You can create a service account and obtain the JSON credentials from the [Firebase Console](https://console.firebase.google.com))

## Setup

1. Start by cloning the repository to your local machine:

   ```bash
   git clone https://github.com/gasparyanvazgen/pdf-parser.git
   ```

2. Ensure you have the necessary credentials files available:

    - [gmail-credentials.json](src/main/resources/gmail-credentials.json) (Gmail API Credentials JSON)
    - [firebase-credentials.json](src/main/resources/firebase-credentials.json) (Firebase API Credentials JSON)

3. Adjust the [application.yml](src/main/resources/application.yml) file within the [src/main/resources](src/main/resources) directory to match your specific configuration:

   ```yml
   gmail:
     applicationName: pdf-parser
     tokensDirectoryPath: tokens
     credentialsFilePath: /gmail-credentials.json
     userId: example@gmail.com

   firebase:
     storage:
       googleApplicationCredentials: /firebase-credentials.json
       storageBucket: project_id.appspot.com
   ```

4. To launch the application, use Gradle:

   ```bash
   ./gradlew bootRun
   ```

The application is now accessible at http://localhost:8080.

## Usage

### Extracting Bank Statements

To extract bank statements from Gmail messages, initiate a GET request to the following endpoint:

```
GET http://localhost:8080/api/pdf-parser/parse-bank-statements
```

Parameters:
- `subject` (required): Specify the subject of the Gmail messages you wish to fetch (e.g., "Bank statement").
- `startDate` (optional): Set the start date for message filtering (in the format "yyyy-MM-dd").
- `endDate` (optional): Define the end date for message filtering (in the format "yyyy-MM-dd").

This endpoint will return a JSON response that includes the extracted text and image URLs from the PDF attachments within matching Gmail messages.

## Code Structure

The project is organized as follows:

- [src/main/java/io/github/gasparyanvazgen/pdfparser](src/main/java/io/github/gasparyanvazgen/pdfparser): Java source files.
    - [config](src/main/java/io/github/gasparyanvazgen/pdfparser/config): Configuration classes for Gmail and Firebase setup.
    - [controller](src/main/java/io/github/gasparyanvazgen/pdfparser/controller): Controller classes for handling HTTP requests.
    - [exceptions](src/main/java/io/github/gasparyanvazgen/pdfparser/exceptions): Custom exception classes for error handling.
    - [model](src/main/java/io/github/gasparyanvazgen/pdfparser/model): Data model classes.
    - [services](src/main/java/io/github/gasparyanvazgen/pdfparser/services): Service classes for Gmail, PDF parsing, and Firebase Storage.

- [src/main/resources](src/main/resources): Configuration and resource files.
    - [application.yml](src/main/resources/application.yml): Configuration file for Gmail and Firebase credentials.
    - [gmail-credentials.json](src/main/resources/gmail-credentials.json): Gmail API Credentials JSON.
    - [firebase-credentials.json](src/main/resources/firebase-credentials.json): Firebase API Credentials JSON.

- [src/test](src/test): Unit tests.

## License

This project is licensed under the MIT License. Refer to the [LICENSE](LICENSE) file for details.