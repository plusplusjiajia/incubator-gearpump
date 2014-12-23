/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gearpump.streaming.examples.wordcount

import org.apache.gearpump.cluster.main.{ArgumentsParser, CLIOption, ParseResult, Starter}
import org.apache.gearpump.partitioner.HashPartitioner
import org.apache.gearpump.streaming.{AppMaster, AppDescription, TaskDescription}
import org.apache.gearpump.util.Graph._
import org.apache.gearpump.util.{Configs, Graph}
import org.apache.gearpump.util.Configs._
import org.slf4j.{Logger, LoggerFactory}

class WordCount extends Starter with ArgumentsParser {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[WordCount])

  override val options: Array[(String, CLIOption[Any])] = Array(
    "master" -> CLIOption[String]("<host1:port1,host2:port2,host3:port3>", required = true),
    "split" -> CLIOption[Int]("<how many split tasks>", required = false, defaultValue = Some(4)),
    "sum" -> CLIOption[Int]("<how many sum tasks>", required = false, defaultValue = Some(4)),
    "runseconds"-> CLIOption[Int]("<how long to run this example>", required = false, defaultValue = Some(60))
  )

  override def application(config: ParseResult) : AppDescription = {
    val splitNum = config.getInt("split")
    val sumNum = config.getInt("sum")
    val appConfig = Configs(Configs.SYSTEM_DEFAULT_CONFIG)
    val partitioner = new HashPartitioner()
    val split = TaskDescription(classOf[Split].getCanonicalName, splitNum)
    val sum = TaskDescription(classOf[Sum].getCanonicalName, sumNum)
    val app = AppDescription("wordCount", classOf[AppMaster].getCanonicalName, appConfig, Graph(split ~ partitioner ~> sum))
    app
  }
}
