# MyChatAppPOE — PROG5121 (Programming 1A)

## Current submission: Part 1 only
 This repository holds the full three-part project as it's built up over the module, but only Part 1 (Registration and Login) is complete and due for this submission. Please assess only:

-src/main/java/poe/Login.java
-src/test/java/poe/LoginTest.java

The file also includes Message.java, MessageTest.java, and QuickChatApp.java that exist in this repository but are still to be finished for Part 2 and Part 3.

### Project Description
MyChatAppPOE is a console-based chat application built in three stages for the PROG5121 Portfolio of Evidence:

Part 1 — Registration and Login: a user creates a single account with a username, password, and South African cell phone number, all validated against specific rules, then logs back into that account.
Part 2 — Sending Messages (in progress): once logged in, the user can send, store, or disregard chat messages, each with a generated message ID and hash.
Part 3 — Store Data and Display Report (in progress): stored messages persist to a JSON file and can be searched, deleted, and reported on.

The intent behind Part 1 was to practise core object-oriented constructs like classes, methods, and decision structures by building a small, fully validated authentication feature where every input is checked against a specific rule before the system accepts it, and the system always responds with a clear success or failure message.

#### Part 1 — Registration and Login
Registration (registerUser) validates three things, in order, and returns the specific message for whichever rule fails first:

Username (checkUserName) — must contain an underscore and be no more than five characters long.
Password (checkPasswordComplexity) — must be at least eight characters and contain a capital letter, a number, and a special character. Implemented with a single regular expression.
Cell phone number (checkCellPhoneNumber) — must be in South Africa's international format, starting with +27. The regex approach was researched from an external source — see the citation in the code comment directly above checkCellPhoneNumber in Login.java.

Each rule lives in its own method that returns true/false, rather than one large method doing everything. This was a deliberate design choice: it keeps each unit test focused on exactly one rule, matching how the assignment's test-data table is laid out (one test per condition).

Login (loginUser / returnLoginStatus) compares the entered username and password against the account created during registration, and returns a welcome message on success or an error message on failure, via a decision structure as the rubric requires.

##### Reference

South African cell phone number format researched from: "How to Call South Africa: Phone Number Format, Area Codes & Validation Guide" — https://sent.dm/en/resources/phone-number-standards/za (accessed 17 September 2026).
