
# TIGO Inverter Data Retrieval Module
The TIGO Inverter Data Retrieval Module is responsible for fetching and processing data from TIGO inverters. It provides two main functionalities: fetching current data and fetching historic minute data. The module is designed to work asynchronously to handle multiple TIGO inverters simultaneously.

## Author

- Shaikh M. Shariq - Senior Software Engineer


## Table of Contents
**Introduction**
* API Endpoints: getMinuteData
* Helper Methods
* Data Processing 

## Introduction
The TIGO Inverter Data Retrieval Module is part of a larger project that collects and processes data from TIGO inverters for monitoring and analytics purposes. It utilizes the TIGO API to fetch both current and historic minute data for a given time range.

## API Endpoints
_https://ei.tigoenergy.com/p/{{S_AUTK}}/system/charts/e-chart-handler?sysid={{SYSID}}&view=gen&sysid={{SYSID}}&type=line&agg={{AGG}}&startDate={{startDate}}&endDate={{endDate}}&reclaimed=true&ids={{DEVNO}}_

This endpoint is used for retrieving data from TIGO inverters for half-hour schedule. It provides the option to fetch either current data or historic minute data within a specified time range.

**Endpoint:** _/getMinuteData_

**Method:** GET

**Parameters:**
* startTime (optional): The start time for fetching historic minute data (format: "yyyy-MM-ddTHH:mm:ss").
* endTime (optional): The end time for fetching historic minute data (format: "yyyy-MM-ddTHH:mm:ss").
* Response:
The response is an HTTP response containing data fetched from TIGO inverters in JSON format.

## Helper Methods
The following are some helper methods used in the data retrieval module:

* **getHistoricMinuteData**(startTime, endTime, deviceUrl, sysId, subscription, deviceNo, sAuth): Fetches historic minute data for the specified time range from a TIGO inverter.
* **getCurrentData**(subscription, sysId, deviceUrl, deviceNo, sAuth): Fetches current data from a TIGO inverter.
* **tigoInitializer**(timeList, deviceUrl, sysId, deviceUrlQ1, subscription, minuteResponse, deviceNo, sAuth): Initializes the data retrieval process for each minute in the specified time range.
* **jSonDecoding**(targetTime, deviceUrlQ1, sysId, subscription, subscriptionLastRecord, minuteResponse, hourResponse, localDateTime): Decodes and processes the JSON response from the TIGO API.

## Data Processing
The data processing in the TIGO Inverter Data Retrieval Module involves several steps:

* **Fetching Data:** The module fetches either current data or historic minute data from TIGO inverters based on the provided parameters.
* **Data Mapping:** The fetched data is mapped and processed to extract relevant information such as current values, yields, and log details.
* **Data Saving:** The processed data is saved to a database for further analysis and monitoring.
* **Error Handling:** In case of any exceptions or errors during data retrieval or processing, appropriate error responses are generated.

Please note that this documentation provides an overview of the TIGO Inverter Data Retrieval Module and its functionalities. Detailed implementation and usage may require reviewing the code and related components.