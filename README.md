# 📱 Habitizer

A customizable daily routine tracker Android app that helps users manage their time more effectively by creating, editing, and timing daily routines and tasks.

## Overview

**Habitizer** is an intuitive app that empowers users to:
- Create morning, evening, or event-specific routines.
- Track time per task and view total routine duration.
- Edit, reorder, or delete tasks and routines.
- Pause and resume routines seamlessly.
- Persist user data across sessions.

This project was developed as part of a UCSD CSE 110 software engineering course, using Agile methodology and BDD-style planning.

---

## 🛠️ Features

### ✅ Routine Management
- View default and custom routines.
- Add, rename, delete, and reorder tasks.
- Create new routines or remove existing ones.

### ⏱ Task Timing
- Live task timer and routine timer.
- Time displayed in minutes (rounded) or seconds (if < 1 min).
- Automatically ends routine when all tasks are complete.

### 📊 Persistence & Continuity
- Data persists even after closing the app.
- Pause/resume functionality retains session state.

---

## 📸 UI Preview

_(Insert screenshots here for launch screen, routine edit screen, and task timer screen)_

---

## 🧪 Testing

Each major feature is supported by:
- ✅ Unit Tests (data models, time logic)
- ✅ UI Tests (task completion, routine selection)
- ✅ Scenario-Based System Tests
---

## 🔄 Agile Development

- ✅ **Planning Poker** for estimating task effort.
- ✅ **GitHub Projects Board** for tracking iterations.
- ✅ Iteration-based development with increasing velocity.
- ✅ Resolved common team risks (e.g., unfamiliarity with GitHub, Android Studio issues).

---

## 📁 Project Structure

```
Habitizer/
│
├── app/                   # Android app source code
│   ├── activities/        # UI logic and views
│   ├── model/             # Routine and Task classes
│   └── storage/           # Persistence layer
├── tests/                 # Unit and UI tests
└── docs/                  # Planning PDFs, postmortems, scenarios
```

---

## 📈 Future Improvements

- Routine analytics (time trends, missed tasks)
- Notifications or alarms for tasks
- Cloud sync and multi-device support

---

MILESTONE 1:
In our first iteration, we finished about half of our planned work at about a total of 14 hours and a velocity of 0.3. This left us with 38 hours left to complete in iteration 2. With 48 working hours in each iteration, our velocity for iteration 2 was set to 0.8 to finish the milestone and all of its tasks. All of the unfinished tasks and User Story 2 were pushed to iteration 2 as well.

MILESTONE 2:
In our first iteration, we finished half of our planned work at about a total of 16 hours and a velocity of 0.32. This left us with 34 hours left of tasks to complete in iteration 2. With 48 working hours projected in each iteration, our velocity for iteration 2 was set to 0.7 to finish the milestone and all of its tasks. All of the unfinished tasks(US3, US4) were pushed to iteration 2 as well. We were also able to pull shorter tasks from iteration 2 into iteration 1, while we waited for more high-priority pair-programming tasks to be completed. 
