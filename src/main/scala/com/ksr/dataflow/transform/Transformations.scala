package com.ksr.dataflow.transform

import com.ksr.dataflow.Job
import com.ksr.dataflow.configuration.transform.Configuration
import com.ksr.dataflow.utils.FileUtils
import org.apache.log4j.LogManager

object Transformations {
  type transformationsCallback = (String) => Unit
  private var beforeRun: Option[transformationsCallback] = None
  private var afterRun: Option[transformationsCallback] = None

  def setBeforeRunCallback(callback: transformationsCallback) {
    beforeRun = Some(callback)
  }

  def setAfterRunCallback(callback: transformationsCallback) {
    afterRun = Some(callback)
  }
}

class Transformations(transformations: String, write: Boolean = true) {
  val log = LogManager.getLogger(this.getClass)

  val transformationSet: Seq[Transformation] = parseMetrics(transformations)

  def parseMetrics(transforms: String): Seq[Transformation] = {
    log.info(s"Starting to parse metricSet")

    FileUtils.isLocalDirectory(transforms) match {
      case true => {
        val metricsToCalculate = FileUtils.getListOfLocalFiles(transforms)
        metricsToCalculate.filter(Configuration.isValidFile(_)).map(f => Configuration.parse(f.getPath))
      }
      case false => Seq(Configuration.parse(transforms))
    }
  }
  def run(job: Job) {
    Transformations.beforeRun match {
      case Some(callback) => callback(transformations)
      case None =>
    }

    transformationSet.foreach(transformation => {
      val startTime = System.nanoTime()

      transformation.calculate(job)
      if (write) {
        transformation.write(job)
      }

      val endTime = System.nanoTime()
      val elapsedTimeInNS = (endTime - startTime)
    })

    Transformations.afterRun match {
      case Some(callback) => callback(transformations)
      case None =>
    }
  }
}
