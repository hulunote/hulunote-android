package com.hulunote.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hulunote.android.ui.database.DatabaseListScreen
import com.hulunote.android.ui.editor.OutlineEditorScreen
import com.hulunote.android.ui.login.LoginScreen
import com.hulunote.android.ui.note.NoteListScreen

object Routes {
    const val LOGIN = "login"
    const val DATABASES = "databases"
    const val NOTES = "notes/{databaseId}/{databaseName}"
    const val EDITOR = "editor/{noteId}/{noteTitle}"

    fun notes(databaseId: String, databaseName: String) =
        "notes/$databaseId/$databaseName"

    fun editor(noteId: String, noteTitle: String) =
        "editor/$noteId/$noteTitle"
}

@Composable
fun HulunoteNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DATABASES) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DATABASES) {
            DatabaseListScreen(
                onDatabaseClick = { dbId, dbName ->
                    navController.navigate(Routes.notes(dbId, dbName))
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DATABASES) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.NOTES,
            arguments = listOf(
                navArgument("databaseId") { type = NavType.StringType },
                navArgument("databaseName") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val databaseId = backStackEntry.arguments?.getString("databaseId") ?: return@composable
            val databaseName = backStackEntry.arguments?.getString("databaseName") ?: ""
            NoteListScreen(
                databaseId = databaseId,
                databaseName = databaseName,
                onNoteClick = { noteId, noteTitle ->
                    navController.navigate(Routes.editor(noteId, noteTitle))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            Routes.EDITOR,
            arguments = listOf(
                navArgument("noteId") { type = NavType.StringType },
                navArgument("noteTitle") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
            val noteTitle = backStackEntry.arguments?.getString("noteTitle") ?: ""
            OutlineEditorScreen(
                noteId = noteId,
                noteTitle = noteTitle,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
