# RHO Challenge

This is a detailed explanation of the implementation and installation process performed during the development of this project.

- Developed in a VS Code environment with the Spring Boot Extension package and Java Extension package. 
    - Spring Package ID: *vmware.vscode-boot-dev-pack*; 
    - Java Package ID: *vscjava.vscode-java-pack*.
- Maven version 3.9.4 was used to create and run the project.
- Java 20 was used to develop the application.

> [!NOTE]
> This project was developed using Windows 11.

## Installing Java

Go to https://www.oracle.com/pt/java/technologies/downloads/#java20 and download the JDK.

Open terminal window and check Java version using:

```shell script
java -version
```

You should see an output similar to this:

```
java version "20.0.2" 2023-07-18
Java(TM) SE Runtime Environment (build 20.0.2+9-78)
Java HotSpot(TM) 64-Bit Server VM (build 20.0.2+9-78, mixed mode, sharing)
```

> [!IMPORTANT]
> If the command is not recognized, check the setup of the environment variables following this guide: https://www.ibm.com/docs/en/b2b-integrator/5.2?topic=installation-setting-java-variables-in-windows.

## Installing Maven

Download Maven v3.9.4 from the website: https://maven.apache.org/download.cgi.

Next step is to setup the environment variables. It can be done following this guide: https://phoenixnap.com/kb/install-maven-windows.

Open a terminal window and check Maven version using:

```shell script
mvn -version
```

You should see an output similar to this:

```
Apache Maven 3.9.4 (dfbb324ad4a7c8fb0bf182e6d91b0ae20e3d2dd9)
Maven home: C:\Program Files (x86)\Maven\apache-maven-3.9.4
Java version: 20.0.2, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-20
Default locale: en_GB, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

> [!IMPORTANT]
> If the command is not recognized, check the setup of the environment variables again.

## Running Project

To test the project, first clone this repository.

Then open a terminal window in the path: ```RHO_Challenge/challenge```.

Within this directory run:

```shell script
mvn spring-boot:run
```

The application will start and the REST API will be hosted on ```localhost:8080```.

## Exchange Rate APIs used

In order to get a bigger sample of Exchange Rates, 3 API were used.

1. *host* - https://exchangerate.host. 
2. *ayr* - https://www.exchangerate-api.com *Account required for API key*.
3. *currency* - https://currencyapi.com *Account required for API key*.

The main API used by default is ```1.``` since is has the least restraints on the number of monthly API calls.

When requested by the user, the REST API created can request information from the other APIs.

## Challenge Description

API Operations Requested:
- Get all exchange rates from Currency A;
- Get exchange rate from Currency A to Currency B;
- Get value conversion from Currency A to Currency B;
- Get value conversion from Currency A to a list of supplied currencies.

## API Get Calls

Replace the ```{}``` in the URLs with the parameter wanted. Example: ```{currency}``` becomes ```USD```

**Get all exchange rates for Currency A:**

```
localhost:8080/rates/all/{currency}
```

The ```currency``` parameter represent the currency code used to determine the other exchange rates.

Alternatively you can specify the API used with:

```
localhost:8080/rates/all/{api}/{currency}
```

It can be ```host```, ```ayr``` or ```currency```.

**Get exchange rate from Currency A to Currency B:**

```
localhost:8080/rates/{currency}&{targets}
```

The ```currency``` parameter represents the currency code used to determine the other exchange rates. The ```target``` parameter represents the target currencies codes to get the exchange rate. The targets must be comma separated. Example: ```USD,EUR,GBP```.

In the case of getting exchange from Currency A to B, the ```target``` parameter specifies only one currency.

Alternatively you can specify the API used with:

```
localhost:8080/rates/{api}/{currency}&{targets}
```

It can be ```host```, ```ayr``` or ```currency```.

**Get value conversion from Currency A to Currency B:**

```
localhost:8080/convert/{from}&{to}&{amount}
```

The ```from``` parameter represent the currency code to convert from. The ```to``` parameter represent the currency code to convert to. The ```amount``` parameter represent the amount of the currency to convert.

Alternatively you can specify the API used with:

```
localhost:8080/convert/{api}/{from}&{to}&{amount}
```

It can be ```host```, ```ayr``` or ```currency```.

**Get value conversion from Currency A to a list of supplied currencies:**

```
localhost:8080/convert/multi/{from}&{targets}&{amount}
```

The ```from``` parameter represent the currency code to convert from. The ```targets``` parameter represents the target currencies codes to convert to. The targets must be comma separated. Example: ```USD,EUR,GBP```.

```
localhost:8080/convert/multi/{api}/{from}&{targets}&{amount}
```

It can be ```host```, ```ayr``` or ```currency```.

## JSON Objects Structure

This is the structure of the returned JSON Objects:

```json
{
    "SUCCESS": ,
    "REQUEST_TIME": {
        "Hour": ,
        "Minute": ,
        "Second": ,
    },
    "CALL_EXECUTED": , // Because of the Reduce Call Mechanism
    "CURRENCY_FROM": ,
    "CURRENCY_TO": , 
    "ORIGINAL_AMOUNT": , // Exclusive to conversion method returns
    "RESULT": , // Exclusive to conversion method returns
    "RATES": ,
    "RESULT_DATE": ,
    "API": 
}
```

## SwaggerHub Documentation



## Extra Features

These were the extra features developed for the challenge.

### Reduce API Calls Mechanism

To 

Not implemented in GET all rates 

### Unit Testing

To ensure the validity and strength of the code written, several unit tests were created.

The tests written test several features of the application including all its API calls and its reduce call mechanism

These are the methods written:

- ```testGetAllRatesHost()```
- ```testGetAllRatesAyr()```
- ```testGetAllRatesCurrency()```
- ```testSpecificRatesHost()```
- ```testSpecificRatesAyr()```
- ```testSpecificRatesCurrency()```
- ```testConvertHost()```
- ```testConvertAyr()```
- ```testConvertCurrency()```
- ```testConvertMultiHost()```
- ```testConvertMultiAyr()```
- ```testConvertMultiCurrency()```
- ```testReduceCallMechanismTrue()```
- ```testReduceCallMechanismAfterSave()```
- ```testReduceCallMechanismFalse()```

### Interface


