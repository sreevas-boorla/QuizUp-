# Student's Portal — Implementation & Verification Report

This document details the complete design, code architecture, and live verification of **Milestone 5: Student's Portal** for the Exam Quiz Application.

---

## 1. Milestone Requirements & Verification Summary

| Requirement | Implementation Details | Status |
| :--- | :--- | :---: |
| **Custom App-Bar Component** | Green Material Toolbar (`#1EB113`) implemented in Home, Exam, and History activities with logout / back navigation. | **Verified** |
| **Student Home Activity** | Dashboard with welcome greeting, "Progress History" option card, and "Available Quizzes" section. | **Verified** |
| **Real-Time Quiz Search** | `TextInputLayout` with search icon filtering available quizzes by title and topic as the student types. | **Verified** |
| **Quiz Information & Rules Modal** | Dialog displaying Quiz Name, Subject, Question Count, Duration, Exam Rules, and a "START QUIZ" button. | **Verified** |
| **Back-Counting CountDownTimer** | Active `CountDownTimer` displaying `seconds remaining: MM:SS`, updating every second during the test. | **Verified** |
| **Auto-Advance on Option Selection** | Tapping any of options A–D immediately evaluates the answer, increments score, and transitions to the next question. | **Verified** |
| **Timeout Auto-Submission** | When countdown timer reaches 0, the test closes automatically and evaluates answered questions. | **Verified** |
| **Exam Scorecard & Result Dialog** | Displays score (e.g. `5 / 5`), percentage (`100% • PASSED`), saves record to Firebase, and links to Progress History. | **Verified** |
| **Progress History Activity** | Performance Overview card (Tests Taken, Passed, Avg Accuracy) and `RecyclerView` listing attended quizzes and marks. | **Verified** |

---

## 2. Architecture & Exam Workflow

```mermaid
flowchart TD
    SH["StudentHomeActivity<br/>(App-Bar & Search)"] -->|"Type in Search Box"| SF["Real-Time Filter<br/>(AvailableQuizAdapter)"]
    SH -->|"View Progress History"| PH["StudentHistoryActivity<br/>(Overall Stats & Attended Tests)"]
    
    SH -->|"Tap Available Quiz"| QI["Quiz Instructions Dialog<br/>(Rules, Duration, Format)"]
    QI -->|"Click START QUIZ"| EX["StudentExamActivity<br/>(Custom App-Bar)"]
    
    EX -->|"Initialize Timer"| CT["CountDownTimer<br/>(seconds remaining: MM:SS)"]
    EX -->|"Tap Option A/B/C/D"| AA["Evaluate Answer &<br/>Auto-Advance Next Question"]
    
    AA -->|"All Questions Answered"| RD["Exam Result Dialog<br/>(Score, Percentage, Grade)"]
    CT -->|"Timer Countdown Finishes"| RD
    
    RD -->|"Sync Record"| FB[("Firebase Database<br/>/Results/")]
    RD -->|"View Progress History"| PH
```

---

## 3. Key Components & Source Files

### Activities & Controllers
- [`StudentHomeActivity.java`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/java/com/example/quizapp/StudentHomeActivity.java): Student dashboard with custom App-Bar, welcome banner, "Progress History" card, live search box, and available quizzes list.
- [`StudentExamActivity.java`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/java/com/example/quizapp/StudentExamActivity.java): Timed exam session activity implementing `CountDownTimer`, progress indicator, question card, option selectors with auto-advance, and timeout auto-submission.
- [`StudentHistoryActivity.java`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/java/com/example/quizapp/StudentHistoryActivity.java): Progress history screen with overall statistics (Tests Taken, Passed, Avg Accuracy) and `RecyclerView` of attended quizzes.

### Adapters & Layouts
- [`AvailableQuizAdapter.java`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/java/com/example/quizapp/AvailableQuizAdapter.java) & [`item_available_quiz.xml`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/res/layout/item_available_quiz.xml): Binds available quizzes, displays metadata badges, and filters items in real time based on search queries.
- [`StudentProgressAdapter.java`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/java/com/example/quizapp/StudentProgressAdapter.java) & [`item_student_progress.xml`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/res/layout/item_student_progress.xml): Formats attended test results, displaying date, score, percentage, and dynamic grade badges (`PASSED` / `NEEDS RETEST`).
- [`activity_student_home.xml`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/res/layout/activity_student_home.xml): Complete layout for student home with App-Bar, search bar, and empty search placeholder.
- [`dialog_quiz_instructions.xml`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/res/layout/dialog_quiz_instructions.xml): Modal dialog detailing quiz rules, duration, and question format before starting.
- [`activity_student_exam.xml`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/res/layout/activity_student_exam.xml): Exam layout featuring countdown timer header, completion bar, question statement card, and option buttons.
- [`dialog_exam_result.xml`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/res/layout/dialog_exam_result.xml): Comprehensive post-exam scorecard dialog.
- [`activity_student_history.xml`](file:///C:/Users/Sreevas/.gemini/antigravity/scratch/ExamQuizApp/app/src/main/res/layout/activity_student_history.xml): Progress history layout with performance metrics header.

---

## 4. Live Verification on Android Virtual Device

All student portal features were deployed to `emulator-5554` (`Medium_Phone_API_37.0`) and captured live:

````carousel
![Student Home Dashboard](emulator_student_home.png)
<!-- slide -->
![Real-Time Quiz Search](emulator_student_search.png)
<!-- slide -->
![Quiz Rules & Instructions Modal](emulator_quiz_instructions.png)
<!-- slide -->
![Timed Exam Session with CountDownTimer](emulator_student_exam.png)
<!-- slide -->
![Exam Scorecard & Completion Modal](emulator_exam_result.png)
<!-- slide -->
![Student Progress History & Stats](emulator_student_history.png)
````

---

## 5. How to Run & Verify

1. **Launch Student's Portal**:
   - Open from the emulator app launcher or via ADB:
   ```powershell
   & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n com.example.quizapp/.StudentHomeActivity
   ```
2. **Test Search**:
   - Tap the search input and type `Android` or `Java` — the quiz cards filter instantly and the availability badge reflects the matching count.
3. **Take a Timed Quiz**:
   - Tap **"TAKE QUIZ"** on any available test. Review the duration and instructions dialog, then tap **"START QUIZ"**.
   - Observe the back-counting timer at the top (`seconds remaining: MM:SS`).
   - Tap any option (A, B, C, or D) — the quiz automatically advances to the next question.
   - Upon answering the final question (or when the timer runs out), the scorecard modal automatically appears with your score, percentage, and grade.
4. **Inspect Progress History**:
   - Tap **"VIEW PROGRESS HISTORY"** from the result dialog or the home screen to see overall test statistics and attended quizzes.
