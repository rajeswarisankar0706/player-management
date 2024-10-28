# ComeOn Player Service Assignment

In this assignment, we look forward to test your skills in software development. Please, be pragmatic and avoid over engineering your solution.

## Problem Statement:

We want you to implement a player service that allows player registration and does some operations on player.

* Design a REST API with the following:
    * It should be possible to register a new player by feeding it with the following:
        * Player details such as email, password, name, surname, date of birth, address
    * It should be possible to login the player when provided correct email & password.
        * A session structure should be created and stored.
    * It should be possible to logout the player when provided session identifier. 
    * It should be possible to set time limit for a player.
       * The time limit will be a daily limit on player's active session duration (between login & logout).    
       * Login should not be possible when player reached their time limit.
       * It should not be possible to set limit for an inactive player.

## Deliverables:
Your submission should contain a Java service based Maven project solution.

In this case, we recommend you to make use of spring boot as a starting point for your application.

In addition, in order for us to test your solution; make use of an embedded database like H2 or Sqlite, using a local file database scheme with your working solution 
(Optionally you can use other database solutions as well).

It is a nice-to-have to make your project dockerized.

We look forward to your solution and appreciate your time applying with us!

