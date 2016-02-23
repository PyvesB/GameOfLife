import java.util.concurrent.CyclicBarrier

/**
 * A simple concurrent implementation of Conway's Game of Life, written in Scala and using a toroidal grid.
 *
 * @param gridWidth defines the size of the grid displayed.
 * @param nbThreads defines the number of threads running in parallel.
 */
class GameOfLife(gridWidth: Int, nbThreads: Int) {

  val grid = Array.ofDim[Boolean](gridWidth, gridWidth) // Grid of Game of Life.
  val barrier = new CyclicBarrier(nbThreads + 1) // Used to synchronise threads.
  val display = new Display(gridWidth, grid) // Used to display the grid.

  def main(args: Array[String]) {
    // Initialise grid. Put in your own initial parameters here.
    for (l <- 9 to 11) grid(14)(l) = true
    for (l <- 10 to 23) grid(14)(l) = true
    for (l <- 9 to 11) grid(18)(l) = true
    for (l <- 10 to 23) grid(18)(l) = true

    // Launch parallel workers to process grid.
    for (p <- 0 to nbThreads - 1)
      new Thread(new Runnable {
        def run() {
          computeRegion((gridWidth / nbThreads * p).asInstanceOf[Int], (gridWidth / nbThreads * (p + 1) - 1).asInstanceOf[Int])
        }
      }).start()

    // Launch graphical display thread.
    new Thread(new Runnable {
      def run() {
        while (true) {
          Thread.sleep(100) // Display frequency.
          display.draw // Refresh display.
          barrier.await()
          barrier.await()
        }
      }
    }).start()
  }

  /** Updates one region of the grid for each thread. */
  private def computeRegion(startIndex: Int, endIndex: Int) = {
    // Create region for thread to work on.
    val region = Array.ofDim[Boolean](endIndex - startIndex + 1, gridWidth)
    for (k <- 0 to endIndex - startIndex)
      region(k) = grid(startIndex + k).clone() // Clone initial arrays from grid.

    var localLiveNeighbours = 0 // Live neighbours of a cell.

    while (true) {
      // Scan through thread's region.
      for (i <- startIndex to endIndex) {
        for (j <- 0 to gridWidth - 1) {
          localLiveNeighbours = countLiveNeighbours(i, j)
          if (localLiveNeighbours == 3 || localLiveNeighbours == 2 && grid(i)(j) == true)
            region(i - startIndex)(j) = true
          else
            // Overcrowding or undercrowding.
            region(i - startIndex)(j) = false
        }
      }

      // Synchronise all threads.
      barrier.await()

      // Update grid with each thread's own region copy.
      // No interference as each thread operates on a different part of the grid.
      for (k <- startIndex to endIndex)
        for (l <- 0 to gridWidth - 1)
          grid(k)(l) = region(k - startIndex)(l)

      // Synchronise all threads.
      barrier.await()
    }
  }

  /** Number of live neighbours */
  private def countLiveNeighbours(i: Int, j: Int): Int = {
    var liveNeighbours = 0

    val iMinusOne = if (i == 0) gridWidth - 1 else i - 1
    val jMinusOne = if (j == 0) gridWidth - 1 else j - 1
    val iPlusOne = if (i == gridWidth - 1) 0 else i + 1
    val jPlusOne = if (j == gridWidth - 1) 0 else j + 1

    // Check all positions around current cell, using toroidal grid.
    if (grid(iMinusOne)(jMinusOne)) liveNeighbours += 1
    if (grid(iMinusOne)(j)) liveNeighbours += 1
    if (grid(iMinusOne)(jPlusOne)) liveNeighbours += 1
    if (grid(i)(jMinusOne)) liveNeighbours += 1
    if (grid(i)(jPlusOne)) liveNeighbours += 1
    if (grid(iPlusOne)(jMinusOne)) liveNeighbours += 1
    if (grid(iPlusOne)(j)) liveNeighbours += 1
    if (grid(iPlusOne)(jPlusOne)) liveNeighbours += 1

    return liveNeighbours
  }

}

// Modify size of grid or number of parallel threads here. Grid width must me a multiple of the number of threads.
object Test extends GameOfLife(32, 4) 