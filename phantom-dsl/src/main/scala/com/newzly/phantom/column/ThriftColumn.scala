package com.newzly.phantom.column

import java.nio.charset.Charset
import org.apache.thrift.protocol.{TCompactProtocol, TBinaryProtocol}
import org.apache.thrift.transport.TMemoryInputTransport
import com.datastax.driver.core.Row
import com.newzly.phantom.CassandraTable
import com.twitter.scrooge.{ ThriftStruct, ThriftStructCodec3 }
import com.twitter.util.Try

abstract class ThriftColumn[Owner <: CassandraTable[Owner, Record], Record, ValueType <: ThriftStruct](table: CassandraTable[Owner, Record]) extends Column[Owner, Record, ValueType](table) {

  def toCType(v: ValueType): AnyRef = {
    table.logger.info(s"Serialized to Thrift Binary: ${v.toString}")
    v.toString
  }

  def encode(v: ValueType): Array[Byte]
  def decode(data: Array[Byte]): ValueType

  val cassandraType = "text"

  def optional(r: Row): Option[ValueType] = {
    Try {
      val data = r.getString(name)
      Some(decode(data.getBytes(Charset.forName("UTF-8"))))
    } getOrElse None
  }
}
