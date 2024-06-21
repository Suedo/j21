# Design patterns in daily life

## Singleton Pattern

- **Use Case:** Configuration Management
- **Description:** Singleton can be used to ensure that the configuration settings of an application are consistent and
  easily accessible throughout the application.

```kotlin
@Component
object AppConfig {
    val dbUrl: String = "jdbc:mysql://localhost:3306/mydb"
    val dbUser: String = "user"
    val dbPassword: String = "password"
}
```

## Factory Pattern

- **Use Case**: Service Creation
- **Description**: The factory pattern can be used to create different service implementations based on certain
  conditions or configurations.

```kotlin
sealed class NotificationService {
    class EmailService :

        NotificationService()

    class SmsService :

        NotificationService()

    companion object {
        fun create(type: String): NotificationService {
            return when (type) {
                "email" -> EmailService()
                "sms" -> SmsService()
                else -> throw IllegalArgumentException("Unknown service type")
            }
        }
    }
}
```

## Builder Pattern

- **Use Case**: Constructing Complex Objects
- **Description**: The builder pattern can be used for constructing complex objects such as creating requests with
  multiple
  optional parameters.

```kotlin
class User(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null
)

fun main() {
    val user = User(id = 1, name = "John Doe", email = "john.doe@example.com", address = "123 Main St")
}
```

## Strategy Pattern

- **Use Case**: Payment Processing
- **Description**: The strategy pattern can be used to encapsulate different payment methods such as credit card,
  PayPal, or
  bank transfer.

```kotlin
typealias PaymentStrategy = (Double) -> String

val creditCardPayment: PaymentStrategy = { amount -> "Paid $amount using Credit Card" }
val paypalPayment: PaymentStrategy = { amount -> "Paid $amount using PayPal" }

fun main() {
    val paymentMethod = creditCardPayment
    println(paymentMethod(100.0))
}
```

## Observer Pattern

- **Use Case**: Event Handling
- **Description**: The observer pattern can be used to handle events such as user registration, where multiple actions
  like
  sending a confirmation email and logging the event need to happen.

```kotlin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

class UserEventObservable {
    private val state = MutableStateFlow<String?>(null)
    val flow = state

    fun notify(event: String) {
        state.value = event
    }
}

fun main() =

    runBlocking {
        val userEvents = UserEventObservable()

        userEvents.flow.collect { event ->
            if (event != null) println("Event received: $event")
        }

        userEvents.notify("User Registered")
    }
```

## Decorator Pattern

- **Use Case**: Extending Functionality of Services
- **Description**: The decorator pattern can be used to add additional functionalities to services, such as adding
  logging or
  transaction management.

```kotlin
interface UserService {
    fun save(user: String): String
}

class SimpleUserService :

    UserService {
    override fun save(user: String) = "User $user saved"
}

class LoggingUserService(private val userService: UserService) :

    UserService {
    override fun save(user: String): String {
        println("Logging: Saving user $user")
        return userService.save(user)
    }
}

fun main() {
    val userService: UserService = LoggingUserService(SimpleUserService())
    println(userService.save("John Doe"))
}
```

## Execute Around Pattern

- **Use Case**: Resource Management
- **Description**: The execute around pattern can be used to manage resources such as opening and closing database
  connections.

```kotlin
import java.sql.Connection
import java.sql.DriverManager

fun <R> withDatabaseConnection(url: String, user: String, password: String, block: (Connection) -> R):

        R {
    DriverManager.getConnection(url, user, password).use { connection ->
        return block(connection)
    }
}

fun main() {
    val result = withDatabaseConnection("jdbc:mysql://localhost:3306/mydb", "user", "password") { connection ->
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM users")
        if (resultSet.next()) resultSet.getString("name")
        else "No user found"
    }
    println(result)
}
```
