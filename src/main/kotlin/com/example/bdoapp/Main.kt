package com.example.bdoapp

import com.example.bdoapp.Util.NavigationManager
import com.example.bdoapp.Util.NotificationService
import javafx.application.Application
import javafx.stage.Stage


class Main : Application() {
    override fun start(primaryStage: Stage) {
        val navigation = NavigationManager(primaryStage)
        NotificationService.start()
        primaryStage.title = "BDO Helper App"
        navigation.showMainMenu()
        primaryStage.show()
    }
}
