# PsiKick

![Build](https://github.com/sfdez0/PsiKick/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)


<!-- Plugin description -->
PsiKick uses Gemini AI models to find and highlight code smells in Kotlin.

Features:
- Detects the following code smells:
  - !! (Not-null assertion)
  - Deep nesting
  - Empty catch blocks
  - Lack of immutability
- Configurable API Key in Tools > PsiKick.
- Configurable model in Tools > PsiKick:
  - Gemini 3.1 Flash-Lite (Preview)
  - Gemini 3 Flash (Preview)
  - Gemini 2.5 Flash-Lite
  - Gemini 2.5 Flash
<!-- Plugin description end -->

## Configuration

Get your API key from [Google AI Studio](https://aistudio.google.com/api-keys) and choose your preferred model.

<img width="472" height="160" alt="image" src="https://github.com/user-attachments/assets/46c582d2-d6de-430e-9184-d8cd0ce6a4bf" />


## Usage

- Editor Popup menu (right-click inside the editor).

<img width="424" height="121" alt="Captura de pantalla 2026-05-03 210349" src="https://github.com/user-attachments/assets/0f1ff0a9-49f2-401c-9a34-921add8b6098" /><br>

- Tools menu.

<img width="345" height="477" alt="Captura de pantalla 2026-05-03 210358" src="https://github.com/user-attachments/assets/3cd3f9c7-fe96-4182-8000-7073ac75cf1e" /><br>

- Analysis in progress.

<img width="291" height="33" alt="Captura de pantalla 2026-05-03 210411" src="https://github.com/user-attachments/assets/9a50191c-b906-4a0f-81da-df1bb95c4708" /><br>


## Examples

- !! (Not-null assertion).

<img width="754" height="134" alt="Captura de pantalla 2026-05-03 210832" src="https://github.com/user-attachments/assets/6a0defd1-8eec-4fd7-a25b-c62988bfed4f" /><br>

- Deep nesting.

<img width="712" height="166" alt="Captura de pantalla 2026-05-03 210903" src="https://github.com/user-attachments/assets/cad48ff1-4084-4f3b-bf18-14c2930dc97c" /><br>


## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "PsiKick"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/sfdez0/PsiKick/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---


## Author

- [@sfdez0](https://github.com/sfdez0)

Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation


