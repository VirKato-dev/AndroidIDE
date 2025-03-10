package com.itsaky.androidide.lsp.java.actions.common

import com.google.googlejavaformat.java.FormatterException
import com.google.googlejavaformat.java.RemoveUnusedImports
import com.itsaky.androidide.actions.ActionData
import com.itsaky.androidide.actions.hasRequiredData
import com.itsaky.androidide.actions.markInvisible
import com.itsaky.androidide.actions.requireEditor
import com.itsaky.androidide.resources.R.string
import com.itsaky.androidide.lsp.java.actions.BaseJavaCodeAction
import com.itsaky.androidide.utils.ILogger
import io.github.rosemoe.sora.widget.CodeEditor

class RemoveUnusedImportsAction : BaseJavaCodeAction() {

  private val log = ILogger.newInstance(javaClass.simpleName)
  override val id: String = "lsp_java_remove_unused_imports"
  override var label: String = ""
  override val titleTextRes: Int = string.action_remove_unused_imports

  override fun prepare(data: ActionData) {
    super.prepare(data)
    if (!visible) {
      return
    }

    if (!data.hasRequiredData(CodeEditor::class.java)) {
      markInvisible()
      return
    }

    visible = true
    enabled = true
  }

  override suspend fun execAction(data: ActionData): Any {
    val watch = com.itsaky.androidide.utils.StopWatch("Remove unused imports")
    return try {
      val editor = data.requireEditor()
      val content = editor.text
      val output = RemoveUnusedImports.removeUnusedImports(content.toString())
      watch.log()
      output
    } catch (e: FormatterException) {
      log.error("Failed to remove unused imports", e)
      false
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result is String && result.isNotEmpty()) {
      val editor = data.requireEditor()
      editor.setText(result)
    }
  }
}
