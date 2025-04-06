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

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/fcb1823d-fecd-4560-9091-8ee740e2590f" width="200px"/><br>
      <strong>🏠 Launch Page</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4c2caa1c-97ca-4c46-843d-f7dfa6dc86db" width="200px"/><br>
      <strong>✅ Routine Running</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/14d75dcd-5ac7-4632-b3ed-001ba64be137" width="200px"/><br>
      <strong>⚙️ Edit Tasks</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/7bdf0b15-cc60-4c35-9d88-236619e04e6a" width="200px"/><br>
      <strong>⏸️ Paused State</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d1d44ff1-f9e7-4697-a2fe-538a2e71570e" width="200px"/><br>
      <strong>✅ Completed Routine </strong>
    </td>
  </tr>
</table>

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/24cadb81-8a3a-4b02-ba64-9670115e0389" width="200px"/><br>
      <strong>Rename Routine</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/931311aa-c0c5-4e36-aa53-cfd8aaa877fa" width="200px"/><br>
      <strong>Add New Task</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/b9272283-63cb-4609-9ba8-d0a49b814ec0" width="200px"/><br>
      <strong>Delete Task</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e6598803-3652-4f91-ba65-c3d7c0f5c760" width="200px"/><br>
      <strong>Delete Routine</strong>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/6aa85c05-7a12-42b1-856e-281b63366011" width="200px"/><br>
      <strong>Set Estimated Time</strong>
    </td>
  </tr>
</table>

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
