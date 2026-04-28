# UPH Dungeon

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
