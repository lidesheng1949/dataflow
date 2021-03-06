package com.ksr.dataflow.input.readers.file

import com.ksr.dataflow.utils.FileUtils
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}

trait FileInputBase {
  def getFormat(format: Option[String], path: String): String = {

    format match {
      case Some(f) => f
      case None => {
        FileType.getFileType(path) match {
          case FileType.json | FileType.jsonl => "json"
          case FileType.csv                   => "csv"
          case _                              => "parquet"
        }
      }
    }
  }

  def getOptions(
      readFormat: String,
      options: Option[Map[String, String]]): Option[Map[String, String]] = {
    readFormat match {
      case "csv" => {
        Option(
          Map("quote" -> "\"",
              "escape" -> "\"",
              "quoteAll" -> "true",
              "header" -> "true") ++ options.getOrElse(Map()))
      }
      case _ => options
    }
  }

  def processDF(df: DataFrame, readFormat: String): DataFrame = {
    readFormat match {
      case "csv" => df.na.fill("")
      case _     => df
    }
  }

  def getSchemaStruct(schemaPath: Option[String],
                      sparkSession: SparkSession): Option[StructType] = {
    schemaPath match {
      case Some(path) => {
        Option(SchemaConverter.convert(FileUtils.readFileWithHadoop(path)))
      }
      case None => None
    }
  }

}
