package ru.netology.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

/*
Вопросы: Cancellation

Вопрос №1
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

/*
fun main() = runBlocking {
    val job = CoroutineScope(EmptyCoroutineContext).launch {
        launch {
            delay(500)
            println("ok") // <--
        }
        launch {
            delay(500)
            println("ok")
        }
    }
    delay(100)
    job.cancelAndJoin()
}
*/

/*
Ответ:
Код в отмеченной (<--) строке не отработает, т.к. основная корутина, созданная runBlocking,
выполняет задержку на 100 мс, а затем отменяет выполнение конутины job,
что определяется вызовом метода job.cancelAndJoin(). Поскольку внутри корутины job выполняются
две корутины, в которых println выполняется с задержкой в 500 мс, println не успевает выполниться,
т.к. корутина job на этот момент оказывается в отмененном состоянии.
 */


/*
Вопрос №2
Отработает ли в этом коде строка <--. Поясните, почему да или нет.
*/
/*
fun main() = runBlocking {
    val job = CoroutineScope(EmptyCoroutineContext).launch {
        val child = launch {
            delay(500)
            println("ok") // <--
        }
        launch {
            delay(500)
            println("ok")
        }
        delay(100)
        child.cancel()
    }
    delay(100)
    job.join()
}
*/

/*
Ответ:
Код в отмеченной (<--) строке не отработает, т.к. корутина job, выполняет задержку на 100 мс,
а затем отменяет выполнение конутины child и println внутри неё не успевает выполниться из-за
задержки перед его выполнением 500 мс. При этом корутина, выполняемая одновременно с корутиной
child, продолжает выполняться и println внутри неё отрабатывает, т.к. основная корутина ожидает
завершения корутины job, что определяется вызовом метода job.join().
 */

/*
Вопросы: Exception Handling

Вопрос №1
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

/*
fun main() {
    with(CoroutineScope(EmptyCoroutineContext)) {
        try {
            launch {
                throw Exception("something bad happened")
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
}
*/

/*
Ответ:
Код в отмеченной (<--) строке не отработает, т.к. функция launch не пробрасывает исключения, которые
возникают внутри. Вместо этого, она распространяет их вверх по иерархии корутины, пока не достигнет
объекта области видимости (scope). Поэтому перехват исключения с помощью try в этом примере не срабатывает.
*/

/*
Вопрос №2
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

/*
fun main() {
    val job = CoroutineScope(EmptyCoroutineContext).launch {
        try {
            coroutineScope {
                throw Exception("something bad happened")
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
}
*/

/*
Ответ:
Код в отмеченной (<--) строке отработает, поскольку для создания корутины используется
helper coroutineScope, который перехватывает ошибки и представляет их в виде Exception.
*/

/*
Вопрос №3
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

/*
fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            supervisorScope {
                launch {
                    throw Exception("something bad happened")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
}
*/

/*
Ответ:
Код в отмеченной (<--) строке не отработает, поскольку helper supervisorScope не пробрасывает исключения
из дочерних корутин наверх.
*/

/*
Вопрос №4
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

/*
fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            coroutineScope {
                launch {
                    delay(500)
                    throw Exception("something bad happened") // <--
                }
                launch {
                    throw Exception("something bad happened")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Thread.sleep(1000)
}
*/

/*
Ответ:
Код в отмеченной (<--) строке не отработает, т.к. исключение во второй корутине
внутри coroutineScope выбрасывается раньше, чем в первой из-за задержки 500 мс в первой корутине,
а это в свою очередь заставляет coroutineScope отменить все дочерние корутины.
*/

/*
Вопрос №5
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

/*
fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        try {
            supervisorScope {
                launch {
                    delay(500)
                    throw Exception("something bad happened") // <--
                }
                launch {
                    throw Exception("something bad happened")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // <--
        }
    }
    Thread.sleep(1000)
}
*/

/*
Ответ:
Код в первой отмеченной (<--) строке отработает, т.к. supervisorScope не отменяет дочерние корутины в случае,
если какая-либо из них выбрасывает исключение.
Код во второй отмеченной (<--) строке не отработает, поскольку helper supervisorScope
не распространяет исключения из дочерних корутин, вместо этого пробрасывает (re-throw) исключения.
Эта особенность требует обработчика CoroutineExceptionHandler в верхнеуровневой корутине,
иначе supervisorScope все равно упадет, что и происходит в данном случае.
*/

/*
Вопрос №6
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

/*
fun main() {
    try {
        CoroutineScope(EmptyCoroutineContext).launch {
            CoroutineScope(EmptyCoroutineContext).launch {
                launch {
                    //println("первая корутина")
                    delay(1000)
                    println("ok1") // <--
                }
                launch {
                    //println("вторая корутина")
                    delay(500)
                    println("ok2")
                }
                throw Exception("something bad happened")
            }
        }
    } catch(e: java.lang.Exception) {
        println("Ошибка")
    }
    Thread.sleep(1000)
    //println("ok")
}
*/

/*
Ответ:
Код в отмеченной (<--) строке не отработает, т.к. исключение выбрасывается раньше, чем отработают дочерние корутины
из-за установленной в них задержки.
*/

/*
Вопрос №7
Отработает ли в этом коде строка <--? Поясните, почему да или нет.
*/

fun main() {
    CoroutineScope(EmptyCoroutineContext).launch() {
        CoroutineScope(EmptyCoroutineContext + SupervisorJob()).launch() {
            launch {
                delay(1000)
                println("ok") // <--
            }
            launch {
                delay(500)
                println("ok")
            }
            throw Exception("something bad happened")
        }
    }
    Thread.sleep(1000)
}

/*
Ответ:
Код в отмеченной (<--) строке не отработает, т.к. исключение выбрасывается раньше, чем отработают дочерние корутины
из-за установленной в них задержки.
*/