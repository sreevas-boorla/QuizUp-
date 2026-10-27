# QuizUp — Android Exam Preparation Application

An Android application designed for students to prepare and test for their exams through interactive quizzes, integrated with Firebase and featuring dedicated Teacher/Recruiter and Student portals.

Developed in **Java** with **Material Design Components** and **Firebase Authentication & Realtime Database**.

---

## 🚀 Key Features

### 👨‍🏫 Teacher / Recruiter Portal
* **Dashboard & App-Bar**: Clean navigation with quick actions to create new quizzes or review past quizzes.
* **Create Quiz**: Set quiz titles, descriptions, and duration limits (seconds/minutes).
* **Manage Questions**: 
  * Add questions with 4 distinct options (A, B, C, D) and select the correct answer.
  * Delete questions with instant RecyclerView updates and confirmation dialogs.
* **Quiz History & Student Scores**:
  * View list of quizzes created.
  * Inspect student submission history, participant names, scores, and timestamps.

### 🎓 Student Portal
* **Available Quizzes**: Browse all active examination quizzes.
* **Real-Time Search**: Search and filter available quizzes dynamically by title or topic.
* **Quiz Rules & Duration Modal**: Pre-exam dialog outlining instructions, question counts, and duration before starting.
* **Timed Exam Engine**:
  * Real-time countdown timer (`seconds remaining: XX`).
  * Auto-advance to the next question upon option selection.
  * Auto-submission if the countdown timer expires.
* **Result & Evaluation Dialog**: Instant post-exam scorecard displaying total questions, score, percentage, and performance rating.
* **Progress History**: Track performance metrics across all attended exams.

---

## 🛠️ Architecture & Tech Stack

* **Language**: Java 17
* **Build System**: Gradle 8.7, Android Gradle Plugin 8.4.0
* **Target SDK**: Android 14 (API 34)
* **Backend**: Firebase Authentication (Email/Password), Firebase Realtime Database
* **UI Components**: Android Material Components (`com.google.android.material`), RecyclerView, CardView, Toolbar (`Theme.MyFirstApp.NoActionBar`)
* **Color Palette**: Primary Green `#1EB113`, Dark `#168E0E`, Surface Light `#F8FAFC`

---

## 📂 Project Structure

```
QuizUp-/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/quizapp/
│   │   │   │   ├── MainActivity.java           # Launcher Activity
│   │   │   │   ├── Login.java                  # Firebase Authentication Login
│   │   │   │   ├── Register.java               # Student / Teacher Registration
│   │   │   │   ├── TeacherHomeActivity.java    # Teacher Dashboard
│   │   │   │   ├── CreateQuizActivity.java     # Quiz Creation Form
│   │   │   │   ├── ManageQuizActivity.java     # Add / Delete Questions
│   │   │   │   ├── TeacherHistoryActivity.java # Quizzes & Student Scores
│   │   │   │   ├── StudentHomeActivity.java    # Student Available Quizzes
│   │   │   │   ├── StudentExamActivity.java    # Timed Exam with Countdown
│   │   │   │   ├── StudentHistoryActivity.java # Student Progress History
│   │   │   │   ├── QuestionAdapter.java        # RecyclerView Adapter for Questions
│   │   │   │   ├── AvailableQuizAdapter.java   # Adapter for Quizzes with Search
│   │   │   │   ├── QuizHistoryAdapter.java     # Teacher Quiz History Adapter
│   │   │   │   ├── StudentScoreAdapter.java    # Student Evaluation Scores Adapter
│   │   │   │   ├── StudentProgressAdapter.java # Student Performance History Adapter
│   │   │   │   └── models/ (User, Quiz, Question, QuizResult)
│   │   │   ├── res/layout/                     # Activity & Dialog XML Layouts
│   │   │   └── AndroidManifest.xml             # Permissions & Activity Declarations
│   │   └── build.gradle                        # App-level dependencies & Firebase BoM
│   └── google-services.json
├── gradle/
├── build.gradle                                # Project-level build script
├── settings.gradle
└── README.md
```

---

## ⚙️ Setup & Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/sreevas-boorla/QuizUp-.git
   ```

2. **Open in Android Studio**:
   * Open Android Studio -> **File** -> **Open** -> Select `QuizUp-`.
   * Allow Gradle to sync dependencies automatically.

3. **Firebase Configuration**:
   * Add your `google-services.json` from the Firebase Console to the `/app` directory.
   * Enable **Email/Password** Authentication and **Realtime Database** in your Firebase project.

4. **Run the Application**:
   * Select a connected Android device or Virtual Device (AVD API 24+).
   * Click **Run** (`Shift + F10`).
