# Daisuke Clinic [![Version](https://img.shields.io/badge/version-1.0.0-green.svg)]()
> A Java-based console app simulating simple clinic management system.

![Screenshot.](/screenshots/1.png/)
## Table of Contents
* [Features](#features)
* [Installation](#installation)
* [Usage](#usage)
* [Program Structure](#program-structure)
* [Demo Video](#demo-video)
* [Credits](#credits)

## Features

### 🔐 Login System
Our system provides a role-based login mechanism for three types of users:
- `Patient` can log in to book appointments, view their medical record, and manage their profile.
- `Doctor`can log in to view and process their appointments, access patient information, and update medical records.
- `Admin` can log in to manage doctors, patients, specialties, and view all appointments.

> [!NOTE]  
> While patients can sign up themselves, only admins can create accounts for doctors and other admins, for safety reasons.

### 📋 Medical Record
This feature allows doctors to document and manage patient consultation details. Each record includes the patient's complaints, diagnosis, treatment, prescription, and additional notes so as to ensure accurate and organized trackings of patient health history across multiple visits.

As this feature contains sensitive and classified medical data, the table below specifies the access permissions for each user.  

| User Type  | View | Update | Note |
| ------------- |:-------------:| :---: | --- |
| `Patient`      |  ✅   |   🚫   |  Can view their own medical records for personal reference.
| `Doctor`      |   🔄   |       🔄      | Can manage medical records **only for patients they are currently treating via an active appointment**.
| `Admin`      |   🚫   | 🚫| Cannot access medical records to protect sensitive medical information.

### 📅 Appointment Scheduling
Patients can book appointments by selecting a doctor, specialty, and available time slot based on the doctor's working schedule.

Each appointment is linked to a patient, a doctor, and a scheduled time. Our system ensures that time slots do not overlap and only shows available options.

> [!NOTE]  
> Patients may book only one appointment per specialty, and each specialty can have its own maximum number of appointment slots (set by admins).

### 👨🏻‍⚕️ Doctor Scheduling
Each doctor has a predefined weekly work schedule (days and time ranges).This schedule determines their availability for appointments.

Admins can set doctor schedules, ensuring patients only book during valid time slots. The appointment system uses this schedule to validate and prevent double-booking.
### 🗂️ Clinic Data Management

Admins can manage core clinic data, including:
* **Patient data**
* **Doctor profiles** (name, specialty, schedule)
* **Specialties** (or departments)
* **Appointment monitoring**

## Installation

To run this console app, make sure you have **Java Runtime Environment (JRE)** installed since it's the minimum requirement to execute the `.jar` file.

Steps:
1. Download the ZIP file from this repository.
2. Extract the ZIP to your preferred folder.
3. Open **Command Prompt (CMD)** or terminal.
4. Navigate to the extracted folder.
5. Run the application using the following command:
```
java -jar "Daisuke Clinic.jar"
```

## Usage

### Login Menu
#### Log In as User
![Screenshot.](/screenshots/login/1.png/)

This is the program's starting point.
The user can choose which role to log in as (`1` for Patient, `2` for Doctor, and `3` for Admin).

![Screenshot.](/screenshots/login/2.png/)

The example above is when the user choose to log in as patient.

#### Register as Patient
![Screenshot.](/screenshots/login/3.png/)

New patient users can register an account by choosing option `4`.

### Patient Menu
![This is an alt text.](/image/sample.webp "This is a sample image.")

This is the patient's main menu. Below are the features they can access.

#### View Available Specialties
![This is an alt text.](/image/sample.webp "This is a sample image.")

It shows the clinic's available specialties that are on service. The menu above can be accessed by choosing the option `3` in the main menu. 
#### View Available Doctors
![This is an alt text.](/image/sample.webp "This is a sample image.")

Patient users can view available doctors in the clinic by choosing the option `4` (by Specialty) or `5` (by Name), or `5` The menu above shows the example of showing available doctors in the specified specialty ID.

> [!NOTE]  
 This feature should be accessed by the patient before scheduling an appointment, to get the doctor ID the patient wants to choose.
#### Book an Appointment
![This is an alt text.](/image/sample.webp "This is a sample image.")

The `Appointments` menu above can be accessed by choosing the option `1` in the main menu.

The patient can finally book an appointment after choosing their preferred doctor. To book an appointment select the option `1`

![This is an alt text.](/image/sample.webp "This is a sample image.")

The patient should schedule their appointment within the doctor's available hours; otherwise, the system will reject the booking.
#### View Appointments
#### View Medical Record
#### Manage Profile

### Doctor Menu
#### Process Appointments
#### View Current Patients
#### Update Patient's Medical Record
#### Manage Profile

### Admin Menu
#### Specialty Management
#### Doctor Management 
#### Patient Management
#### Admin Management
#### View Appointments

## Program Structure

## Demo Video
The demonstration of the app link:
https://youtu.be/MqnR4Qr4BXo

## Credits

