# Daisuke Clinic [![Version](https://img.shields.io/badge/version-1.0.0-green.svg)]()
> A Java-based console app simulating simple clinic management system.

![Screenshot.](/screenshots/1.png/)
## Table of Contents
* [Features](#features)
* [Installation](#installation)
* [Usage](#usage)
    * [Login Menu](#login-menu)
    * [Patient Menu](#patient-menu)
    * [Doctor Menu](#doctor-menu)
    * [Admin Menu](#admin-menu)
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
![Screenshot.](/screenshots/login/1.png/)

The app's starting point. The user can choose which role to log in as (`1` for patients, `2` for doctors, and `3` for admins).
#### Log In as a User
![Screenshot.](/screenshots/login/2.png/)

The image above shows a example of when the user choose to log in as a patient.

#### Register as a Patient
![Screenshot.](/screenshots/login/3.png/)

New patient users can register an account by choosing option `4`.

### Patient Menu
![Screenshot.](/screenshots/patient/1.png/)

This is the patient's main menu. Below are the features they can access.

#### View Available Specialties
![Screenshot.](/screenshots/patient/3.png/)

Shows the clinic's available specialties that are on service. The menu above can be accessed by choosing option `3` in the main menu. 
#### View Available Doctors
![Screenshot.](/screenshots/patient/4.png/)

Patient users can view available doctors in the clinic by choosing option `4` (by Specialty) or `5` (by Name), or `5` The menu above shows an example of showing available doctors in the specified specialty.

> [!NOTE]  
 This feature should be accessed by the patient before scheduling an appointment, to get the doctor ID the patient wants to choose.
#### Book an Appointment
![Screenshot.](/screenshots/patient/2.png/)

The `Appointments` menu above can be accessed by choosing option `1` in the main menu.

The patient can finally book an appointment after choosing their preferred doctor. To book an appointment select option `1`.

![Screenshot.](/screenshots/patient/5.png/)

The example above shows a success appointment booking. The patient should schedule their appointment within the doctor's available hours; otherwise, the system will reject the booking.

#### View Appointments
![Screenshot.](/screenshots/patient/6.png/)

The patient can view their upcoming appointments by choosing option `2` or `3` in the `Appointments` menu. The example above shows option `3` menu (`View My Upcoming Appointments`).

#### Cancel an Appointment

![Screenshot.](/screenshots/patient/7.png/)

The menu above can be accessed by choosing option `6` in the `Appointments` menu. Patient can cancel their appointment through the menu if they are uncertain about it.

#### View Medical Record
![Screenshot.](/screenshots/patient/11.png/)

After a while, the doctor should process the appointment and may update the patient's medical record. 
The example above shows the current patient's medical record that can be accessed by choosing option `2` (`My Medical Record`) in the main menu.
#### Manage Profile
![Screenshot.](/screenshots/patient/9.png/)

Patients can update their profile details, including name, age, address, phone number, and password. The menu above can be accessed by choosing option `6` in the main menu.

### Doctor Menu
![Screenshot.](/screenshots/doctor/1.png/)

The doctor's main menu. Below are the features they can access.
#### View & Process Appointments
![Screenshot.](/screenshots/doctor/2.png/)

The image above is the `Appointments` menu for doctors. They can view the upcoming appointments by choosing option `2` (by their specialty) or `3` (their own).

![Screenshot.](/screenshots/doctor/3.png/)

The image above shows an example of viewing current upcoming appointments in their specialty.
To process an appointment, doctors can choose option `1` (`Process Next Appointment`) in the `Appointments` menu.

![Screenshot.](/screenshots/doctor/4.png/)

Once the appointment is processed, the doctor can decide whether to update the patient's medical record.

![Screenshot.](/screenshots/doctor/5.png/)

The example above shows how the doctor updates the patient's medical record, which requires inputting the complaints, diagnosis, treatment, and so on.

#### View Current Patients
![Screenshot.](/screenshots/doctor/7.png/)

Right after having an appointment with a patient, that patient will appear in the doctor's list of current patients, whose medical records the doctor can then manage. The menu above can be accessed by choosing option `2` (`My Patients`) in the main menu.

To view the current patients, doctors can choose option `2` (`View My Current Patients`).

![Screenshot.](/screenshots/doctor/8.png/)

The example above shows the current patients that the doctor are handling.

#### Update Patient's Medical Record
![Screenshot.](/screenshots/doctor/10.png/)

Within the `My Patients` menu, the doctor can check or update a patient’s medical record by selecting option `1`. They may **only access the medical records of patients they are currently handling**.

#### View Doctors
![Screenshot.](/screenshots/doctor/11.png/)

Doctors can also view the other doctors inside the `View Doctors` menu (option `3` in the main menu). The example above shows a list of doctors in the same specialty.

#### Manage Profile
![Screenshot.](/screenshots/doctor/12.png/)

Just like the patients, doctors can also manage their profile by choosing option `4` (`Edit Profile`) in the main menu.

### Admin Menu
![Screenshot.](/screenshots/admin/1.png/)

The main menu for admins. Provides access to clinic data management.
#### Specialty Management
![Screenshot.](/screenshots/admin/2.png/)

In this menu, specialties can be managed, including the maximum number of appointments in the specialty. The options inside this menu are self-explanatory; they let admins manage specialty data by adding, viewing, or removing entries.

![Screenshot.](/screenshots/admin/4.PNG/)

The image above (option `1`) shows an example of adding a new specialty.

![Screenshot.](/screenshots/admin/5.png/)

The image above (option `4`) shows an example of displaying a list of all specialties.
#### Doctor Management
![Screenshot.](/screenshots/admin/6.png/)

Doctor data management menu. Note that, unlike patients, **only admins can register doctors.** The options of this menu are self-explanatory.

![Screenshot.](/screenshots/admin/8.png/)

The image above (option `1`) shows an example of adding a new doctor along with their schedule.

![Screenshot.](/screenshots/admin/7.png/)

The image above (option `6`) shows an example of displaying a list of all doctors.
#### Patient Management
![Screenshot.](/screenshots/admin/10.png/)

This menu handles patient data. Admins can also manually add new patient records when users require help with data entry. The options of this menu are self-explanatory.

![Screenshot.](/screenshots/admin/12.png/)

The image above (option `1`) shows an example of adding a new patient.

![Screenshot.](/screenshots/admin/11.png/)

The image above (option `4`) shows an example of displaying a list of all patients.

#### Admin Management
![Screenshot.](/screenshots/admin/14.png/)

The admin management. Admins can also manage their own user types, but **this should be done with caution** to prevent misuse of the system. The options of this menu are self-explanatory.

![Screenshot.](/screenshots/admin/16.png/)

The image above (option `1`) shows an example of adding a new admin.

![Screenshot.](/screenshots/admin/15.png/)

The image above (option `4`) shows an example of displaying a list of all admins.

#### View Appointments

![Screenshot.](/screenshots/admin/18.png/)

In this version, admins are limited to monitoring appointments made in the clinic. Appointment management for admins will be available in the future.

The image above (option `6` in the main menu) shows an example of displaying all upcoming appointments in the specified specialty.

## Program Structure

## Demo Video
The demonstration of the app link:
https://youtu.be/MqnR4Qr4BXo

## Credits