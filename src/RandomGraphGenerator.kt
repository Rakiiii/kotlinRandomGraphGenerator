package MainKt

import kotlin.random.Random
import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class RandomGraphGenerator(var amount : Int)
{
    fun generateRandomGraph(file : String)
    {
        val res : MutableList<MutableList<Int>> = MutableList(amount,{ m -> MutableList(0,{s->0}) })
        for( i in  0 until amount)
        {
            val amountOfEdges = Random.nextInt(1,amount/2)
            val subList : MutableList<Int> = MutableList(amountOfEdges,{s->-1})
            for(j in 0 until amountOfEdges)
            {
                var rnd = Random.nextInt(0,amount)
                while( subList.contains(rnd) || rnd == i)
                {
                    rnd = Random.nextInt(0,amount)
                }
                subList[j] = rnd
                if(!res[rnd].contains(i)) res[rnd].add(i)
            }
            for(j in subList)
            {
                if(!res[i].contains(j))res[i].add(j)
            }
        }

        val writter = File(file).bufferedWriter()
        for(i in res)
        {
            i.sort()
        }
        for(i in res)
        {
            for(j in i)
            {
                writter.write(j.toString())
                writter.write(" ")
            }
            writter.newLine()
        }
        writter.close()
    }

    fun generateRandomGraphWithSettedAmountOfVertexFast(file: String, amountOfEdges : Int)
    {
        val edgesDistribution: IntArray = IntArray(amount, { s -> 0 })

        println("generate distribution "+amountOfEdges.toString())
        for (i in 1 until amount)
        {
            edgesDistribution[i] = Random.nextInt(1, i + 1)
        }
        var actualSum : Long = edgesDistribution.sumLong()

        println("fix distrubuiton sum "+actualSum.toString())

        //println(( ceil( actualSum.toDouble() / amountOfEdges.toDouble() ) ).toInt().toString())
        //println(edgesDistribution.filter { s -> (s > ( ceil( actualSum.toDouble() / amountOfEdges.toDouble() ) ).toInt())}.size)


        for (i in 0 until edgesDistribution.size)
        {
            //if( edgesDistribution[i] > ( ceil( actualSum.toDouble() / amountOfEdges.toDouble() ) ).toInt() )
            //{
                edgesDistribution[i] =
                        (floor((edgesDistribution[i].toDouble() * (amountOfEdges.toDouble() / actualSum.toDouble())))).toInt()
            //}
        }

        actualSum = edgesDistribution.sumLong()

        println("fixed size "+actualSum.toString()+" should be smaller than start size")
        val incrised: HashSet<Int> = hashSetOf()


        var distributionDiff = amountOfEdges.toLong() - actualSum
        val zeroEdgesVertexAmount  = edgesDistribution.filter { i -> (i == 0) }.size


        if(zeroEdgesVertexAmount > distributionDiff)
        {
            println("decrese max vertexes "+(zeroEdgesVertexAmount-distributionDiff).toString())
            if(amount < 10000)
            {
                for (i in 0 until zeroEdgesVertexAmount - distributionDiff)
                {
                    var max = 0
                    for (j in 0 until edgesDistribution.size)
                    {
                        if (edgesDistribution[max] < edgesDistribution[j]) max = j
                    }
                    edgesDistribution[max]--;
                }
            }
            else
            {
                val copy = edgesDistribution.clone()
                copy.sortDescending()
                var flag = true
                var itterator = 0
                var counter = 0
                while(flag)
                {
                    when
                    {
                        counter >= zeroEdgesVertexAmount ->
                        {
                            flag = false
                        }
                        copy[itterator] > copy[itterator+1] && counter<zeroEdgesVertexAmount && copy[itterator]>1 ->
                        {
                            copy[itterator]--
                            counter++
                        }
                        copy[itterator] == 1 && copy[itterator] <= copy[itterator + 1] ->
                        {
                            itterator = 0
                        }
                        else -> itterator++
                    }
                }

            }
            println("remove 0 edges vertex "+zeroEdgesVertexAmount.toString())
            for(i in 0 until edgesDistribution.size)
            {
                if(edgesDistribution[i] == 0)edgesDistribution[i]++
            }

            distributionDiff = 0
        }
        else
        {
            println("remove 0 edges vertex "+zeroEdgesVertexAmount.toString())
            distributionDiff -= zeroEdgesVertexAmount
            for(i in 0 until edgesDistribution.size)
            {
                if(edgesDistribution[i] == 0)edgesDistribution[i]++
            }
        }

        edgesDistribution.forEach { s -> if(s <= 0)println(s) }
        println("incrise edges distribution "+distributionDiff.toString())
        for (i in 0 until distributionDiff)
        {
            var rndIndex = Random.nextInt(0, amount)
            while (!incrised.add(rndIndex))
            {
                rndIndex = Random.nextInt(0, amount)
            }
            edgesDistribution[rndIndex]++
        }

        println("filling first half of matrix")
        val graphMatrix: Array<BooleanArray> = Array(amount, { s -> BooleanArray(amount, { j -> false }) })
        for (i in 1 until amount)
        {
            for (j in 0 until edgesDistribution[i])
            {
                var rndIndex = Random.nextInt(0, i)
                while (graphMatrix[i][rndIndex])
                {
                    rndIndex = Random.nextInt(0, i)
                }
                graphMatrix[i][rndIndex] = true
            }
        }
//debug
        //debugOutFunc(graphMatrix , file+"_testBeforFilling")
//debug

        println("copping matrix ")
        for (i in amount - 1 downTo  0)
        {
            for (j in 0 until i)
            {
                graphMatrix[j][i] = graphMatrix[i][j]
            }
        }

//debug
       // debugOutFunc(graphMatrix , file+"_testAfterFilling")
//debug

        println("write result file")
        val writter = File(file).bufferedWriter()

        writter.write(amount.toString() + " " + edgesDistribution.sum().toString() )
        writter.newLine()

        for (i in graphMatrix)
        {
            for (j in 0 until amount)
            {
                if(i[j])
                {
                    writter.write(j.toString()+" ")
                }
            }
            writter.newLine()
        }

        writter.close()
    }


    fun generateRandomGraphWithSettedAmountOfVertexEfficent(file: String, amountOfEdges : Int)
    {
        val edgesDistribution: IntArray = IntArray(amount, { s -> 0 })

        println("generate distribution "+amountOfEdges.toString())
        for (i in 1 until amount)
        {
            edgesDistribution[i] = Random.nextInt(1, i + 1)
        }
        var actualSum : Long = edgesDistribution.sumLong()

        println("fix distrubuiton sum "+actualSum.toString())

        //println(( ceil( actualSum.toDouble() / amountOfEdges.toDouble() ) ).toInt().toString())
        //println(edgesDistribution.filter { s -> (s > ( ceil( actualSum.toDouble() / amountOfEdges.toDouble() ) ).toInt())}.size)


        for (i in 0 until edgesDistribution.size)
        {
            //if( edgesDistribution[i] > ( ceil( actualSum.toDouble() / amountOfEdges.toDouble() ) ).toInt() )
            //{
            edgesDistribution[i] =
                    (floor((edgesDistribution[i].toDouble() * (amountOfEdges.toDouble() / actualSum.toDouble())))).toInt()
            //}
        }

        actualSum = edgesDistribution.sumLong()

        println("fixed size "+actualSum.toString()+" should be smaller than start size")
        val incrised: HashSet<Int> = hashSetOf()


        var distributionDiff = amountOfEdges.toLong() - actualSum
        val zeroEdgesVertexAmount  = edgesDistribution.filter { i -> (i == 0) }.size


        if(zeroEdgesVertexAmount > distributionDiff)
        {
            println("decrese max vertexes "+(zeroEdgesVertexAmount-distributionDiff).toString())
            if(amount < 10000)
            {
                for (i in 0 until zeroEdgesVertexAmount - distributionDiff)
                {
                    var max = 0
                    for (j in 0 until edgesDistribution.size)
                    {
                        if (edgesDistribution[max] < edgesDistribution[j]) max = j
                    }
                    edgesDistribution[max]--;
                }
            }
            else
            {
                val copy: IntArray = edgesDistribution.clone()
                copy.sortDescending()
                var flag = true
                var itterator = 0
                var counter = 0
                while(flag)
                {
                    when
                    {
                        counter >= zeroEdgesVertexAmount ->
                        {
                            flag = false
                        }
                        copy[itterator] > copy[itterator+1] && counter<zeroEdgesVertexAmount && copy[itterator]>1 ->
                        {
                            copy[itterator]--
                            counter++
                        }
                        copy[itterator] == 1 && copy[itterator] <= copy[itterator + 1] ->
                        {
                            itterator = 0
                        }
                        else -> itterator++
                    }
                }

            }
            println("remove 0 edges vertex "+zeroEdgesVertexAmount.toString())
            for(i in 0 until edgesDistribution.size)
            {
                if(edgesDistribution[i] == 0)edgesDistribution[i]++
            }

            distributionDiff = 0
        }
        else
        {
            println("remove 0 edges vertex "+zeroEdgesVertexAmount.toString())
            distributionDiff -= zeroEdgesVertexAmount
            for(i in 0 until edgesDistribution.size)
            {
                if(edgesDistribution[i] == 0)edgesDistribution[i]++
            }
        }

        edgesDistribution.forEach { s -> if(s <= 0)println(s) }
        println("incrise edges distribution "+distributionDiff.toString())
        for (i in 0 until distributionDiff)
        {
            var rndIndex = Random.nextInt(0, amount)
            while (!incrised.add(rndIndex))
            {
                rndIndex = Random.nextInt(0, amount)
            }
            edgesDistribution[rndIndex]++
        }


        println("fill struct of matrix")
        var graph : Array<HashSet<Int>> = Array(amount , { i -> HashSet<Int>() } )
        //for(i in 1 until amount)
        //{
            for (i in 1 until amount)
            {
                println(i.toString())
                for (j in 0 until edgesDistribution[i])
                {
                    var rndIndex = Random.nextInt(0, i)
                    while (!graph[i].add(rndIndex))
                    {
                        rndIndex = Random.nextInt(0, i)
                    }
                    graph[rndIndex].add(i)
                }
            }
        //}

        println("write result file")
        val writter = File(file).bufferedWriter()

        writter.write(amount.toString() + " " + edgesDistribution.sum().toString() )
        writter.newLine()

        for (i in graph)
        {
            for (j in i)
            {
                    writter.write(j.toString()+" ")
            }
            writter.newLine()
        }

        writter.close()


    }

    private fun debugOutFunc(graphMatrix: Array<BooleanArray> , file : String)
    {
        val w = File(file+"_test").bufferedWriter()
        for(i in 0 until amount)
        {
            for(j in 0 until amount)
            {
                if(graphMatrix[i][j]) w.write("1 ")
                else w.write("0 ")
            }
            w.newLine()
        }
        w.close()
    }

    private fun IntArray.sumLong():Long
    {
        var result : Long = 0
        this.forEach {  i -> result += i.toLong() }
        return result
    }

}