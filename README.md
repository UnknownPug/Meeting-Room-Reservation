# This program was created as a semester project of the EAR

## (CTU - SIT winter semester 2022)

### Author: Dmitry Rastvorov

## Contents

### [Documentation](#doc)

### [Project goal](#projGoal)

### [Description of functionality](#descfunc)

<a name="doc"><h2>Documentation</h2></a>

📝 The final documentation in [Czech 🇨🇿](https://github.com/UnknownPug/Meeting-Room-Reservation/blob/main/docs/CP2-cz.pdf).

📝 The final document in [English 🇬🇧](https://github.com/UnknownPug/Meeting-Room-Reservation/blob/main/docs/CP2-en.pdf).

<a name="projGoal"><h2>Project goal</h2></a>

🔘 The goal of the project was to learn how Java Spring Boot works, what it is used for and what features it has.

🔘 Meeting Room Reservation System - A system that supports meeting room management and reservation.

<a name="descfunc"><h2>Description of functionality</h2></a>

🔘 List of main functions of the application:
        
  - The management system consists of a user and administrative part:
  
        ■ User 
        ■ Administrator 
  
  - To whom the resulting system is addressed:

        ● This system is intended for a group of people who schedule meetings in conference rooms.

🔘 Types of users
  
  ❖ The system is designed for 2 types of users: user and administrator.

        ➢ User - is a user who interacts with the conference room booking application.
        ➢ Administrator - is the user who manages the conference room reservations.
  
🔘 What functions it performs 

  ❖ The system includes the following features for users:

        ➢ User - has the possibility to book a room, cancel a reservation, see the status of the room and the time when it will be booked.
        ➢ Administrator - has the ability to add a room, remove a room, change room properties and has the same options as the User.

🔘 System limitations

  ❖ The system has limitations for users.
    ➢ The user cannot:
          
        ■ Book 1 room 2 times (as well as the administrator)
        ■ Create and delete a room.
        ■ Edit room properties.
        ■ View users who have booked rooms other than themselves

  ❖ We also have restrictions for the app:

        ■ We have real-time restrictions. That is, we can't make a reservation for next year or next month, but we can only make a reservation at this time in this month.
        ■ We cannot enter incorrect dates. We will receive an error.

🔘 Databases
	
  ❖ The project uses a PostgreSQL database.

🔘 API

  ❖ The project uses the REST API using the Postman application. 

🔘 This app allows you to make a room reservation where the user can create a reservation, view its hourly price and find out information about it. 

🔘 On the other hand, admin has much more options than user. In addition to what the user can do, the admin has full freedom to create rooms, reservations, users and other admins and can also change them and delete them.

🔘 Other:

  ❖ Docker Compose was alsp used in the project. information about it can be found in the [documentation](#doc).

## Thank you for your attention!✌🏻
