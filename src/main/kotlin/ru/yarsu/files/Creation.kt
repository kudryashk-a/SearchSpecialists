package ru.yarsu.files

import ru.yarsu.domain.operations.Permission
import ru.yarsu.domain.operations.Permissions
import ru.yarsu.domain.operations.User
import ru.yarsu.domain.operations.Users
import java.util.*

data class Creation(
    val users: Users,
    val permissions: Permissions,
)

fun initCreation(): Creation {
    val yuriy = User(
        ID = UUID.fromString("8333f4b1-322e-4baa-be42-05079d9fbb95"),
        roleID = UUID.fromString("6b3b4b90-db6d-11ec-9d64-0242ac120002"),
        name = "Семенов Юрий Георгиевич",
        login = "yurasemenov",
        password = "f00d21d13c67b82d689afacf604bd941fb9ae3170ebc92919570128991ab903dd8c9fef5f9c4a7ae11e03ad4e782bbdaa8c40db38615ccc3c4eb6f1e181441ba",
    )
    val leonid = User(
        ID = UUID.fromString("c9096a41-b3c5-485a-8756-fd5056a8a944"),
        roleID = UUID.fromString("08fc459c-db6d-11ec-9d64-0242ac120002"),
        name = "Иванов Леонид Ефимович",
        login = "ivanovleonid",
        password = "f00d21d13c67b82d689afacf604bd941fb9ae3170ebc92919570128991ab903dd8c9fef5f9c4a7ae11e03ad4e782bbdaa8c40db38615ccc3c4eb6f1e181441ba",
    )
    val users = Users(
        listOf(
            leonid,
            yuriy,
    ))
    val guest = Permission(
        ID = UUID.fromString("39173158-db6b-11ec-9d64-0242ac120002"),
        name = "Гость",
        showSpecialists = false,
        showUsers = false
    )
    val user = Permission(
        ID = UUID.fromString("08fc459c-db6d-11ec-9d64-0242ac120002"),
        name = "Пользователь",
        showSpecialists = true,
        showUsers = false
    )
    val admin = Permission(
        ID = UUID.fromString("6b3b4b90-db6d-11ec-9d64-0242ac120002"),
        name = "Администратор",
        showSpecialists = true,
        showUsers = true
    )

    val permissions = Permissions(
        listOf(
            guest,
            user,
            admin,
        )
    )
    return Creation(
        users,
        permissions
    )
}
