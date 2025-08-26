package pt.isel.ls.project

import pt.isel.ls.project.api.winterboot.annotations.injection.Fruit
import pt.isel.ls.project.api.winterboot.annotations.injection.Seed
import pt.isel.ls.project.services.SessionsServices
import pt.isel.ls.project.storage.database.JDBCDatabase
import pt.isel.ls.project.storage.memory.SessionsDataMem

@Fruit
class SessionsServer {
    @Seed
    val data = JDBCDatabase()
    @Seed
    val sessionsService = SessionsServices(data)
}