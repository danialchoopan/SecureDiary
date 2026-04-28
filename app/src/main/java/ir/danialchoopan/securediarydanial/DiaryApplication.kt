package ir.danialchoopan.securediarydanial

import android.app.Application
import ir.danialchoopan.securediarydanial.di.AppContainer
import ir.danialchoopan.securediarydanial.di.AppContainerImpl

class DiaryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
