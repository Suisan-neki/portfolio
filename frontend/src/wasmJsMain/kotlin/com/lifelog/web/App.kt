package com.lifelog.web

import androidx.compose.runtime.*
import com.lifelog.web.components.MainLayout
import com.lifelog.web.pages.*
import com.lifelog.web.theme.PortfolioTheme

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Calendar) }

    PortfolioTheme {
        MainLayout(
            current = currentScreen,
            onNavigate = { currentScreen = it },
        ) {
            when (val screen = currentScreen) {
                is Screen.Calendar -> CalendarPage(
                    onDiaryClick = { date -> currentScreen = Screen.Diary(date) }
                )
                is Screen.Diary -> DiaryPage(
                    date = screen.date,
                    onBack = { currentScreen = Screen.Calendar }
                )
                is Screen.Projects  -> ProjectsPage()
                is Screen.Goals     -> GoalsPage()
                is Screen.Activity  -> ActivityPage()
                is Screen.Records   -> RecordsPage()
                is Screen.Admin     -> AdminPage()
            }
        }
    }
}
