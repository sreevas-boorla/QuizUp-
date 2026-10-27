# QuizUp — Android Exam Preparation & Quiz Application

An Android application designed for students to prepare and test for their exams through interactive quizzes, integrated with Firebase and featuring dedicated Teacher/Recruiter and Student portals.

Developed in **Java** with **Material Design Components**, **Firebase Authentication & Realtime Database**, and accompanied by a full **Live Interactive Web Simulator**.

---

## 🌐 Live Web Preview & GitHub Pages Deployment

The repository includes the complete website and interactive simulator running on localhost port 8080:
* **Web Simulator Source**: Located in [`web/`](./web/) and [`docs/`](./docs/)
* **Direct ZIP Download**: [`localhost-8080-website.zip`](./localhost-8080-website.zip) (Contains standalone HTML, CSS, assets, and simulator)
* **GitHub Pages**: Ready for deployment via GitHub Settings -> Pages -> Source: `/docs` folder -> `https://sreevas-boorla.github.io/QuizUp-/`

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
* **Available Quizzes**: Browse all active examination quizzes from Firebase Realtime Database.
* **Real-Time Search**: Search and filter available quizzes dynamically by title or topic.
* **Quiz Rules & Duration Modal**: Pre-exam dialog outlining instructions, question counts, and duration before starting.
* **Timed Exam Engine**:
  * Real-time countdown timer (`seconds remaining: XX`).
  * Auto-advance to the next question upon option selection.
  * Auto-submission if the countdown timer expires.
  * Race-condition guard: Session locking prevents duplicate starts or accidental exam restarts.
* **Result & Evaluation Dialog**: Instant post-exam scorecard displaying total questions, score, percentage, and performance rating.
* **Verified Firebase Persistence**: Results saving attaches explicit completion and success listeners verifying database confirmation before reporting success.
* **Progress History**: Track performance metrics across all attended exams.

---

## 🔒 Security & Database Integrity

* **Firebase Security Rules (`database.rules.json`)**:
  * Role-based access control (RBAC): Teachers have exclusive write access to `/quizzes` and `/quizzes/{quizId}/questions`.
  * Strict user profiles: Users can only modify their own `/users/{uid}` node. Role cannot be elevated from student to teacher client-side.
  * Tamper-proof submissions: Students can only write their own score record to `/results/{resultId}` where `newData.child('studentId').val() === auth.uid`, and cannot alter historical scores.
* **Strict Role Verification**:
  * Authentication in `Login.java` strictly queries the Firebase Realtime Database user node.
  * Client-side UI selections (such as radio buttons) are never trusted on database read failure or cancelled requests.

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
│   │   │   │   ├── Login.java                  # Firebase Authentication Login (Strict RBAC)
│   │   │   │   ├── Register.java               # Student / Teacher Registration
│   │   │   │   ├── TeacherHomeActivity.java    # Teacher Dashboard
│   │   │   │   ├── CreateQuizActivity.java     # Quiz Creation Form
│   │   │   │   ├── ManageQuizActivity.java     # Add / Delete Questions
│   │   │   │   ├── TeacherHistoryActivity.java # Quizzes & Student Scores
│   │   │   │   ├── StudentHomeActivity.java    # Student Available Quizzes
│   │   │   │   ├── StudentExamActivity.java    # Timed Exam with Countdown & Save Verification
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
├── database.rules.json                         # Firebase Realtime Database Security Rules
├── firebase.json                               # Firebase configuration
├── docs/                                       # Website & GitHub Pages Deployment Root
│   ├── index.html                              # Interactive Web Simulator
│   ├── preview.html                            # Verification & Screencaps Gallery
│   └── *.png                                   # High-resolution Emulator Screencaps
├── web/                                        # Web server source folder (port 8080)
├── localhost-8080-website.zip                  # Standalone ZIP Archive of port 8080 website
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
   * Deploy the security rules:
     ```bash
     firebase deploy --only database
     ```

4. **Run the Application**:
   * Select a connected Android device or Virtual Device (AVD API 24+).
   * Click **Run** (`Shift + F10`).
