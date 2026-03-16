package com.lifelog.web

sealed class Screen {
    data object Calendar  : Screen()
    data class  Diary(val date: String) : Screen()
    data object Projects  : Screen()
    data object Goals     : Screen()
    data object Activity  : Screen()
    data object Records   : Screen()
    data object Admin     : Screen()
}
