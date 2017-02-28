# Swedbank To Google Spreadsheet
Export your Swedbank account and transaction information to Google spreadsheet. Example:

![Example spreadsheet](https://raw.githubusercontent.com/SoShibby/swedbank-google-spreadsheet/master/pictures/example-output.png)

## Requirements
Requires that you have an account at [Swedbank](https://www.swedbank.se) and that you can login using [mobile bank id](https://www.swedbank.se/privat/digitala-tjanster/mobilt-bankid/index.htm).

## How to run the application
There's two ways you can run the application.
- Open the project in intellij and right-click on the file "Main" (located at src/main/java/com.github.soshibby) and choose "Run" and follow the instructions in the console window.

or 

- Open the project in intellij and run the gradle task "fatJar". Then navigate to the folder "swedbank-google-spreadsheet\build\libs"
and run "java -jar swedbank-google-spreadsheet-all-1.0-SNAPSHOT.jar" in a cmd window and follow the instructions.

## Instructions
When you run the application you are presented with two questions. Here's a clarification regarding these.

- Personal number should be in the the following format YYYYMMDDXXXX.
- Spreadsheet id is the id of the spreadsheet that you want to export to. You can get this id by opening the spreadsheet and take the id from the url. Example if the url is https://docs.google.com/spreadsheets/d/14lNJUMqcirFka0U7zXTRURWjRwF9h_ltB1-s3mrPRMA/edit then the id would be 14lNJUMqcirFka0U7zXTRURWjRwF9h_ltB1-s3mrPRMA.
