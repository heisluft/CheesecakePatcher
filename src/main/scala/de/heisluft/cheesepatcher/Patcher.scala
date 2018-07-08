package de.heisluft.cheesepatcher

import java.nio.file.attribute.FileAttribute
import java.nio.file.{Files, Path}
import java.util.function.Consumer

object Patcher {
  def doRecursively(path: Path, consumer: Consumer[Path]) {
    if(Files.isDirectory(path)) Files.newDirectoryStream(path).forEach(p => doRecursively(p, consumer))
    consumer.accept(path)
  }

  def copyFile(from: Path, to: Path) {
    if(!Files.exists(to)) Files.createFile(to, FileAttribute[_])
    Files.write(to, Files.readAllBytes(from))
    Files.delete(from)
  }

  def copyDir(from: Path, to: Path) {}

  def main(args: Array[String]) {}
}