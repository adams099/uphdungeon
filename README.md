# UPH Dungeon

## Collaborators

- **Adam Musyafa Adipratama** [@adams099](https://github.com/adams099) (01085250004)
- **Amelia Azzahra** [@amliaazzhrr](https://github.com/amliaazzhrr) (01085250008)
- **Davin Savero** [@Davinsav](https://github.com/Davinsav) (01085250016)
- **Yohanes Bandung Bondowoso** [@ybbond](https://github.com/ybbond) (01085250015)

## Running the program

To ease the process of running the project, we use Gradle as build system.
We chose gradle because of its plug-and-play nature, keeping in mind the different operating systems and text editor used by the collaborators
([Gradle and Maven Comparison](https://gradle.org/maven-and-gradle/)).

You won't need to install anything to run the project. After you clone or download the repository, you can just run:

```bash
# If you are on UNIX environment
./gradlew run


# If you are on Windows
./gradlew.bat run
```

## Code Formatting

To ensure consistent code formatting across different text editors (Zed, VSCode, IntelliJ), this project uses the **Spotless** Gradle plugin with **Google Java Format**.

### Automatic Formatting (Recommended)

- **VSCode**: When you open this project, you will be prompted to install the "Spotless Gradle" extension. Once installed, the project is configured to format your code automatically on save.
- **IntelliJ IDEA**: When you open this project, you will be prompted to install the "Spotless Applier" plugin. 
    - After installing, go to `Settings` -> `Tools` -> `Spotless Applier`.
    - Enable "Optimize imports" and "Run on save".
- **Zed**: You can run the formatting manually using the Gradle task.

### Manual Formatting

If you prefer to run the formatter manually, use the following commands in your terminal:

```bash
# Check if there are formatting violations
./gradlew spotlessCheck

# Apply formatting fixes
./gradlew spotlessApply
```

### Configuration Details

The formatting is defined in `build.gradle.kts` and follows the Google Java Style (2-space indentation).
