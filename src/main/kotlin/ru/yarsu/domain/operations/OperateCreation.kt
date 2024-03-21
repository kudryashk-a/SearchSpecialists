package ru.yarsu.domain.operations

import ru.yarsu.files.*
import java.nio.file.Path

class OperateCreation(
    creation: Creation,
    settingsPath: Path,
) {
    val settings = Settings(settingsPath)

    val fetchUserOperate: FetchUserOperate = FetchUserOperation(
        creation.users,
    )

    val listUserOperate: ListUserOperate = ListUserOperation(
        creation.users,
    )

    val authenticateUser = AuthenticateUserLogin(
        creation.users,
        settings
    )

    val registrateUser = RegistrateUser(
        creation.users,
        settings
    )

    val fetchPermissionId = FetchPermissionId(creation.permissions)

    val fetchUserId = FetchUserId(creation.users)
}