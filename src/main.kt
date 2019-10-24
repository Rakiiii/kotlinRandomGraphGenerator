import MainKt.RandomGraphGenerator
import java.time.LocalTime
import kotlin.random.Random

var random = Random(LocalTime.now().nano)

fun main(args: Array<String>) {
  // val rnd = RandomGraphGenerator(5)
//   rnd.generateRandomGraphWithSettedAmountOfVertex("graph" , 7)
            when(args[0])
            {
                "-fv" ->{
                    val rnd = RandomGraphGenerator(args[1].toInt())
                    rnd.generateRandomGraphWithSettedAmountOfVertexEfficent(                                            args[2], args[3].toInt())
                }
                "-v" -> {
                    val rnd = RandomGraphGenerator(args[1].toInt())
                    rnd.generateRandomGraphWithSettedAmountOfVertexFast(args[2], args[3].toInt())
                }
                else -> {
                    val rnd = RandomGraphGenerator(args[0].toInt())
                    rnd.generateRandomGraph(args[1])
                }
            }

}