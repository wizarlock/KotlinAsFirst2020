@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import lesson1.task1.sqr
import kotlin.TODO
import kotlin.math.*

// Урок 8: простые классы
// Максимальное количество баллов = 40 (без очень трудных задач = 11)

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая (2 балла)
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double = max(center.distance(other.center) - radius - other.radius, 0.0)

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = center.distance(p) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Средняя (3 балла)
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {

    // Если в множестве менее двух точек

    if (points.size < 2) throw IllegalArgumentException()
    var first = points[0]
    var second = points[1]
    var maxDistance = first.distance(second)

    //Бегаем по всем точкам, ищем наибольшее расстояние

    for (i in points.indices)
        for (j in i + 1 until points.size) {
            val distanceNow = points[i].distance(points[j])
            if (distanceNow > maxDistance) {
                maxDistance = distanceNow
                first = points[i]
                second = points[j]
            }
        }
    return Segment(first, second)
}

/**
 * Простая (2 балла)
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val p1 = (diameter.begin.x + diameter.end.x) / 2
    val p2 = (diameter.begin.y + diameter.end.y) / 2
    return Circle(Point(p1, p2), diameter.begin.distance(diameter.end) / 2)
}

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        require(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя (3 балла)
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point = TODO()

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

/**
 * Средняя (3 балла)
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = lineByPoints(s.begin, s.end)

/**
 * Средняя (3 балла)
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {

    return if (atan2(b.y - a.y, b.x - a.x) > 0) Line(a, atan2(b.y - a.y, b.x - a.x) % PI)
    else Line(a, (PI + (atan2(b.y - a.y, b.x - a.x))) % PI)
}

/**
 * Сложная (5 баллов)
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line =
    Line(Point((a.x + b.x) / 2, (a.y + b.y) / 2), ((lineByPoints(a, b).angle) + PI / 2) % PI)

/**
 * Средняя (3 балла)
 *
 * Задан список из n окружностей на плоскости.
 * Найти пару наименее удалённых из них; расстояние между окружностями
 * рассчитывать так, как указано в Circle.distance.
 *
 * При наличии нескольких наименее удалённых пар,
 * вернуть первую из них по порядку в списке circles.
 *
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> = TODO()

/**
 * Сложная (5 баллов)
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {

    // середина 1 стороны
    val p1 = (a.x + b.x) / 2
    val p2 = (a.y + b.y) / 2

    // середина 2 стороны
    val p3 = (b.x + c.x) / 2
    val p4 = (b.y + c.y) / 2

    // уравнения 2 прямых
    val a1 = b.x - a.x
    val b1 = b.y - a.y
    val c1 = p1 * a1 + p2 * b1
    val a2 = c.x - b.x
    val b2 = c.y - b.y
    val c2 = p3 * a2 + p4 * b2

    //точка их пересечения
    val x0 = (c1 * b2 - c2 * b1) / (a1 * b2 - a2 * b1)
    val y0 = (a1 * c2 - a2 * c1) / (a1 * b2 - a2 * b1)

    //радиус
    val radius = sqrt(sqr(a.x - x0) + sqr(a.y - y0))
    return Circle(Point(x0, y0), radius)
}

/**
 * Очень сложная (10 баллов)
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle {
    var flag = true
    if (points.isEmpty()) throw IllegalArgumentException()
    if (points.size == 1) return Circle(points[0], 0.0)
    if (points.size == 2) return circleByDiameter(Segment(points[0], points[1]))

    //Проверяем, существует ли окружность, которая проходит через 2 самые удаленные точки и содержит все точки множества
    val circle = circleByDiameter(diameter(*points))
    for (point in points) if (!circle.contains(point)) flag = false
    if (flag) return circle

    //А теперь находим окружность с минимальным радиусом, которая содержит все точки и проходит через 3 точки
    var minCircle = Circle(points[0], Double.MAX_VALUE)

    // Здесь происходит очень странное действие, сначала я написал так:
    // for (i in points.indices) {
    //         for (k in i + 1 until points.size) {
    //             for (j in k + 1 until points.size) {
    // По моему мнению, это должно работать как швейцарские часы. НООО, оно не работает...
    // Потыкав часик, обнаружил интересную вещь: если все циклы взять в in points.indices, то все работает
    // Это делает программу более не эффективной, но зато она работает, не очень понимаю, почему она не работала при моем условии
    // Но:

    for (i in points.indices) {
        for (k in points.indices) {
            for (j in points.indices) {
                flag = true
                val newCircle = circleByThreePoints(points[i], points[k], points[j])
                for (point in points) if (!newCircle.contains(point)) flag = false
                if (!flag) continue
                if (newCircle.radius < minCircle.radius) minCircle = newCircle
            }
        }
    }
    return minCircle
}