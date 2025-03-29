val bookList: MutableList<Book> = mutableListOf(
    Book(1111, true, "BFF", 50, "FF2"),
    Book(1112, true, "BEE", 500, "FF3"),
    Book(1113, false, "BRR", 5, "FF4")
)
val newspaperList: MutableList<Newspaper> = mutableListOf(
    Newspaper(2111, true, "NFF", 50, Month.December),
    Newspaper(2112, true, "NEE", 51, Month.April),
    Newspaper(2113, false, "NRR", 52, Month.August)
)
val diskList: MutableList<Disk> = mutableListOf(
    Disk(3111, true, "DFF", DiskFormat.DVD),
    Disk(3112, true, "DEE", DiskFormat.CD),
    Disk(3113, false, "DRR", DiskFormat.DVD)
)

enum class Month(val rusName: String) {
    January("Январь"),
    February("Февраль"),
    March("Март"),
    April("Апрель"),
    May("Май"),
    June("Июнь"),
    July("Июль"),
    August("Август"),
    September("Сентябрь"),
    October("Октябрь"),
    November("Ноябрь"),
    December("Декабрь")
}

interface ReadableAtHome

interface ReadableAtLibrary

interface Digitalizable

sealed class DiskFormat {
    object CD : DiskFormat()
    object DVD : DiskFormat()
}

abstract class Shop<out T : LibraryItem> {
    protected var id = 1
    abstract fun sell(): T
}

class BookShop : Shop<Book>() {
    override fun sell() = Book(id++, true, "BFFM", 50, "FF2")
}

class NewspaperShop : Shop<Newspaper>() {
    override fun sell() = Newspaper(id++, true, "NFFM", 50, Month.December)
}

class DiskShop : Shop<Disk>() {
    override fun sell() = Disk(id++, true, "DFFM", DiskFormat.CD)
}

class Manager() {
    fun <T : LibraryItem> buy(shop: Shop<T>): T {
        return shop.sell()
    }
}

abstract class LibraryItem(open val id: Int, open var isEnable: Boolean, open val name: String) {
    fun shortInfo(): String {
        return "$name доступна: ${
            if (isEnable) {
                "Да"
            } else "Нет"
        }"
    }

    abstract val engName: String

    abstract fun fullInfo(): String
}

class Book(id: Int, isEnable: Boolean, name: String, val numberOfPages: Int, val author: String) :
    LibraryItem(id, isEnable, name), ReadableAtLibrary, ReadableAtHome, Digitalizable {
    override val engName = "book"
    override fun fullInfo(): String {
        return "книга: $name($numberOfPages стр.) автора: $author с id: $id доступна: ${
            if (isEnable) {
                "Да"
            } else "Нет"
        }"
    }
}

class Newspaper(
    id: Int, isEnable: Boolean, name: String, val releaseNum: Int, val releaseMonth: Month
) :
    LibraryItem(id, isEnable, name), ReadableAtLibrary, Digitalizable {
    override val engName = "newspaper"
    override fun fullInfo(): String {
        return "выпуск: ${releaseMonth.rusName} №$releaseNum газеты: $name с id: $id доступен: ${
            if (isEnable) {
                "Да"
            } else "Нет"
        }"
    }
}

class Disk(id: Int, isEnable: Boolean, name: String, val diskType: DiskFormat) :
    LibraryItem(id, isEnable, name), ReadableAtHome {
    override val engName = "disk"
    override fun fullInfo(): String {
        return "$diskType $name доступен: ${
            if (isEnable) {
                "Да"
            } else "Нет"
        }"
    }
}

class SupportFunctions() {
    lateinit var obj: LibraryItem
    fun initObj(temp: LibraryItem) {
        obj = temp
    }

    fun showShortInfo(listItems: List<LibraryItem>) {
        var count = 1
        for (i in listItems)
            println("${count++}. ${i.shortInfo()}")
    }

    fun takeHome() {
        if (obj !is ReadableAtHome) {
            println("Этот объект нельзя брать домой")
        } else if (!obj.isEnable) {
            println("В данный момент этот объект недоступен")
        } else {
            obj.isEnable = false
            println("${obj.engName} ${obj.id} взяли домой.")
        }
    }

    fun takeToLibrary() {
        if (obj !is ReadableAtLibrary) {
            println("Этот объект нельзя читать в читальном зале")
        } else if (!obj.isEnable) {
            println("В данный момент этот объект недоступен")
        } else {
            obj.isEnable = false
            println("${obj.engName} ${obj.id} взяли в читальный зал.")
        }
    }

    fun bringBack() {
        if (obj.isEnable) {
            println("Этот объект нельзя вернуть")
        } else {
            obj.isEnable = true
            println("${obj.engName} ${obj.id} вернули.")
        }
    }

    var newDiskId = 1
    fun <T : DiskFormat> doDigitalization(format: T) {
        if (obj is Digitalizable) {
            diskList.add(Disk(newDiskId++, true, obj.name, format))
            println("Успешно создан новый диск")
        } else println("Мы не умеем оцифровывать данный тип объектов")
    }
}

class TaskMenuLoops() {
    lateinit var listType: List<LibraryItem>
    lateinit var shopType: Shop<LibraryItem>

    val support = SupportFunctions()
    val bookShop = BookShop()
    val newspaperShop = NewspaperShop()
    val diskShop = DiskShop()
    val manager = Manager()

    fun chooseTypeLoop() {
        println(
            "Выберите тип:\n" +
                    "1.книги\n" +
                    "2.газеты\n" +
                    "3.диски"
        )
        loop1@ while (true) {
            when (readlnOrNull()?.toIntOrNull()) {
                1 -> {
                    listType = bookList
                    shopType = bookShop
                    break@loop1
                }

                2 -> {
                    listType = newspaperList
                    shopType = newspaperShop
                    break@loop1
                }

                3 -> {
                    listType = diskList
                    shopType = diskShop
                    break@loop1
                }

                else -> println("неверный выбор, попробуйте еще раз")
            }
        }
        actionWithTypeLoop()
    }

    fun actionWithTypeLoop() {
        println(
            "Что нужно сделать?\n" +
                    "0.Вернуться к выбору типа\n" +
                    "1.Купить новый экземпляр\n" +
                    "2.Посмотреть список имеющихся"
        )
        loop4@ while (true) {
            when (readlnOrNull()?.toIntOrNull()) {
                0 -> {
                    chooseTypeLoop()
                    break@loop4
                }

                1 -> {
                    val obj = manager.buy(shopType)
                    when (obj) {
                        is Book -> bookList.add(obj)
                        is Newspaper -> newspaperList.add(obj)
                        is Disk -> diskList.add(obj)
                    }
                    println("Покупка совершена")
                    chooseTypeLoop()
                    break@loop4
                }

                2 -> {
                    support.showShortInfo(listType)
                    chooseObjectLoop()
                    break@loop4
                }

                else -> println("неверный выбор, попробуйте еще раз")
            }
        }
    }

    fun chooseObjectLoop() {
        println(
            "Введите 0, чтобы вернуться к выбору типа.\n" +
                    "Или введите номер интересующего вас объекта."
        )
        loop2@ while (true) {
            val inp: Int? = readlnOrNull()?.toIntOrNull()
            try {
                require(inp != null)
            } catch (_: IllegalArgumentException) {
                println("неверный выбор, попробуйте еще раз")
                continue@loop2
            }
            if (inp == 0) {
                chooseTypeLoop()
                break@loop2
            }
            if (inp in 1..listType.size) {
                actionWithItemLoop(inp - 1) //так как в списке нумерация с 0, а не с 1
                break@loop2
            } else
                println("неверный выбор, попробуйте еще раз")
        }
    }

    fun actionWithItemLoop(number: Int) {
        support.initObj(listType[number])
        println(
            "0.Вернуться к выбору типа\n" +
                    "1.Взять домой\n" +
                    "2.Читать в читальном зале\n" +
                    "3.Показать подробную информацию\n" +
                    "4.Вернуть\n" +
                    "5.Оцифровать"
        )
        loop3@ while (true) {
            when (readlnOrNull()?.toIntOrNull()) {
                0 -> {
                    chooseTypeLoop(); break@loop3
                }

                1 -> support.takeHome()
                2 -> support.takeToLibrary()
                3 -> println(listType[number].fullInfo())
                4 -> support.bringBack()
                5 -> {
                    println("Введите интересующий вас формат: ")
                    when (readlnOrNull()) {
                        "CD" -> support.doDigitalization(DiskFormat.CD)
                        else -> println("Мы пока умеем оцифровывать только в формат CD")
                    }
                }

                else -> println("неверный выбор, попробуйте еще раз")
            }
        }
    }
}

inline fun <reified T> getIndicatedType(list: List<Any>): List<T> {
    val resultList = mutableListOf<T>()
    for (i in list)
        if (i is T)
            resultList.add(i)
    return resultList
}

fun main() {
    val test = getIndicatedType<Int>(listOf(4, "", true, 5, 1.1, "LIST"))
    for (i in test)
        println(i)

    println("\nHello, user")
    val taskMenu = TaskMenuLoops()
    runCatching {
        taskMenu.chooseTypeLoop()
    }.onFailure {
        println("Что-то пошло не так. Возвращение к выбору типа")
        taskMenu.chooseTypeLoop()
    }
}
