# GameOfLife

A simple concurrent implementation of Conway's Game of Life, written in Scala and using a toroidal grid. The cells are initally randomly generated. You can easily set the size of the grid and the number of threads operating in parallel by editing the values in GameOfLife.scala. The concurrency is based on barrier synchronisation. In order to compile and run, use the following commands:
```
scalac src/GameOfLife.scala src/Display.scala
scala Life
```

<p align="center">
<img src ="https://github.com/PyvesB/game-of-life/blob/master/screenshot.png?raw=true" />
</p>
