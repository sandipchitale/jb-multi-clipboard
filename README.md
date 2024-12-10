# Paste From System Clipboard History

- Use new Cycle Paste from Clipboard History action repeatedly within one second to paste next item in the clipboard history maintained by JetBrains implementation. Of course this makes sense only if you bind the action to some keyboard shortcut.

  **NOTE:** This was implemented in response to the resolution of [IJPL-172881: Implement cycling through clipboard history by repeatedly invoking a CyclePasteAction within short timeframe .e.g within one sec](https://youtrack.jetbrains.com/issue/IJPL-172881) where it was suggested to implement the functionality in a plugin.

- Use new **Cycle Paste from System Clipboard History** action repeatedly within one second to paste next item in the system clipboard history. Of course this makes sense only if you bind the action to some `keyboard shortcut`.
- Paste from System Clipboard Action
- System Clipboard History tool window to manage the history.
  - Allow deletion by row

**IMPORTANT NOTE: Listens to the changes in system clipboard.**

Cleanup.