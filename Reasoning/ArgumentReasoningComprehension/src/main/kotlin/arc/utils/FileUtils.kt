package arc.utils

import java.io.File

fun userHome(): String {
  return System.getProperty("user.home")
}

fun userHome(subPath: String): String {
  return File(System.getProperty("user.home"), subPath).absolutePath
}