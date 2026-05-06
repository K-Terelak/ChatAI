# ChatAI

<!-- Plugin description -->
An IntelliJ Platform plugin with a chat interface and tools for code review.
<!-- Plugin description end -->
This project is based on Gradle and the IntelliJ Platform Plugin SDK. See `README_template.md` for configuration details.


## Features

- In-IDE chat with AI
- Quick summaries and suggestions for code review
- Configurable settings for providers and preferences (currently only GithubModels)

## Architecture

The code follows a simple interface/implementation pattern to keep UI and infrastructure decoupled and testable.
Key pairs:

- `ChatRepositoryApi` (contract) → `ChatRepository` (implementation)
- `CodeReviewRepositoryApi` (contract) → `CodeReviewRepository` (implementation)
- `SettingsService` (contract) → `SettingsServiceImpl` (implementation)
- `GithubModelsService` (contract) → `GithubModelsServiceImpl` (implementation)
- `GitDiffService` (contract) → `GitDiffServiceImpl` (implementation)


## Project structure

```
.
├── build.gradle.kts
├── gradle.properties
├── src
│   ├── main
│   │   ├── kotlin
│   │   │   └── org/jetbrains/plugins/template
│   │   │       ├── model/                 Data models (chat messages, issues, settings)
│   │   │       ├── repository/            Data access + service layer (chat, review, git, providers)
│   │   │       ├── settings/              Settings UI and configurable wiring
│   │   │       ├── toolWindow/            Tool window factory wiring
│   │   │       └── ui/                    Compose UI and ViewModels (chat + review)
│   │   └── resources
│   │       ├── META-INF/plugin.xml        Plugin registration (tool window + services)
│   │       ├── icons/                     Tool window icons
│   │       └── messages/                  Localized strings
│   └── test
│       └── kotlin                         
└── docs
    └── screenshots
```

## Implementation/decisions

- **Single provider (GitHub Models)**: the plugin currently targets one provider. The `GithubModelsService` abstraction keeps the door open for adding more providers later.
- **Single model**: the `LLMModel` enum currently contains only `GPT_4_1`.
- **Service registration in `plugin.xml`**: application-level services (`SettingsServiceImpl`, `GithubModelsServiceImpl`) are declared in `plugin.xml` to leverage IntelliJ Platform lifecycle and DI.
- **ViewModel + repository split**: UI stays Compose-focused, while repositories encapsulate IO, API calls, and parsing logic.
- **Code review diff collection**: `GitDiffServiceImpl` saves open documents and runs `git diff --no-color` in the project root. The raw diff is passed to `CodeReviewRepository`, which builds the review prompt and parses JSON back into `CodeReviewIssue`.
- **LLM requests via LangChain4j**: `GithubModelsServiceImpl` uses the LangChain4j GitHub Models client to avoid hand-rolled HTTP, centralize model configuration, and keep the provider integration minimal.

## Screenshots

### Chat

<img src="docs/screenshots/chat.png" width="400"/>

### Code review

<img src="docs/screenshots/code-review.png" width="400"/>

### Settings

<img src="docs/screenshots/settings.png" width="500"/>
