// -----------------------------------------------------------------
// Parallel ROBDD: Application Main
// -----------------------------------------------------------------

import BDDStructure._
import ChordDHT.Chord
import org.apache.log4j.{Level, Logger}
object Main extends App {

  // Method profiler (timer)
  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }

  // Disable logging messages
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  // Create Distributed Hash Table singleton
  var DHT = new Chord(12, 4)
  // Transient delay to wait for Chord stabilization (5 seconds)
  Thread.sleep(4000)

  //var parser = new ExpParse("../input/test.txt")
  val exp1 = "(x1 && x3) || (!x1 && !x3)"
  val exp2 = "(x0 && x2) || (!x0 && !x2)"

  // Create ordered variable list
  var vars: Array[String] = Array("x0", "x1", "x2", "x3", "x4", "x5", "x6")

  // Build BDDs on DHT
  var bdd1 = new BDDDistributed.BDD(exp1, vars, DHT)
  time{bdd1.build()}
  bdd1.printTable()

  var bdd2 = new BDDDistributed.BDD(exp2, vars, DHT)
  time{bdd2.build()}
  bdd2.printTable()


  var bdd3 = new BDDDistributed.BDD("None", vars, DHT)
  time{bdd3.ite(bdd1, bdd1, AND)}
  bdd3.printTable()

  println(DHT.msgCount)

  // Test against Sequential (single-machine) BDDs
  var bdd4 = new BDDSingle.BDD(exp1, vars)
  time{bdd4.build()}
  bdd4.printTable()

  var bdd5 = new BDDSingle.BDD(exp2, vars)
  time{bdd5.build()}
  bdd5.printTable()

  var bdd6 = new BDDSingle.BDD("None", vars)
  time{bdd6.ite(bdd4, bdd5, AND)}
  bdd6.printTable()


}