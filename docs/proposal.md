# Project Title: SlowPort

## Introduction

SlowPort is a all-in-one timetable and scheduling suite for universities and
other academic institutes.

It aims to solve the problem of filtering out timetable on a per student basis,
per every student's own needs. Since the entire university's timetable will be
too complex, and filtering manually is both time taking and error prone.

SlowPort will be a client side application that connects to the university's
server, and through a cloud API, fetch the latest schedule. It will utilize
a local Database (SQLite) to cache the latest schedule, so it can function
offline as well.

## Technologies

* Java
* Java Swing
* SQLite
* Slowtable parser (behind cloud API)
* [org.json](https://github.com/stleary/JSON-java) JSON parser

## Requirements:

* 1 Shall fetch latest schedules from university's cloud API
* 2 Shall allow user to create a course-selection criteria
	* 2.1 Shall allow AND/OR constructs in course-selection criteria, for example
		ElectiveA OR ElectiveB AND CoreA AND CoreB ..
* 3 Shall allow user to filter complete schedule via course-selection criteria
* 4 Shall implement a schedule-scoring algorithm, that shall assign a score to
	each generated schedule, based on user criteria
	* 4.1 User criteria for schedule score shall allow user to assign weights to:
		gaps between classes, consistency of time between days, number of days
* 5 Shall allow user to select the schedule with the best score
* 6 Shall provide a intuitive Graphical User Interface
* 7 Shall run on at least Linux and Windows
* 8 Shall alert user when a new schedule has been released, that affects them

## GitHub Repo:

[https://github.com/Nafees10/slowport](https://github.com/Nafees10/slowport)
