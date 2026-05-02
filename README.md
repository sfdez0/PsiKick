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
  - Lack of immutability.
- Configurable API Key in Tools > PsiKick.
- Configurable model in Tools > PsiKick:
  - Gemini 3.1 Flash-Lite (Preview)
  - Gemini 3 Flash (Preview)
  - Gemini 2.5 Flash-Lite
  - Gemini 2.5 Flash
<!-- Plugin description end -->

## Examples

- !! (Not-null assertion)
  <img width="702" height="64" alt="Captura de pantalla 2026-05-02 134156" src="https://github.com/user-attachments/assets/68817121-37bd-4a5c-a00d-4225211d59ff" /><br>

- Lack of immutability
  <img width="684" height="107" alt="Captura de pantalla 2026-05-02 133948" src="https://github.com/user-attachments/assets/07350e20-b39a-43df-9948-4aa23cd7b0d4" /><br>

- Deep nesting
  <img width="516" height="259" alt="Captura de pantalla 2026-05-02 134015" src="https://github.com/user-attachments/assets/d3e07158-47c4-45f7-b859-f6844ab9feea" /><br>

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


