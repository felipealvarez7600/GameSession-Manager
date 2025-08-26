package pt.isel.ls.project.api

import pt.isel.ls.project.api.winterboot.annotations.injection.Fruit
import pt.isel.ls.project.api.winterboot.annotations.injection.Seed
import pt.isel.ls.project.services.SessionsServices
import pt.isel.ls.project.storage.memory.SessionsDataMem

@Fruit
class SessionsServerTests {
    @Seed
    //val data = JDBCDatabase("jdbc:postgresql://localhost:5433/test?user=test&password=test")
    val data = SessionsDataMem()
    @Seed
    val sessionsService = SessionsServices(data)
}