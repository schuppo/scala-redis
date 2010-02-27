package com.redis

trait StringOperations { self: RedisClient =>
  import self._

  // SET KEY (key, value)
  // sets the key with the specified value.
  def set(key: String, value: String): Boolean = {
    send("SET", key, value)
    asBoolean
  }

  // GET (key)
  // gets the value for the specified key.
  def get(key: String): Option[String] = {
    send("GET", key)
    asString
  }
  
  // GETSET (key, value)
  // is an atomic set this value and return the old value command.
  def getSet(key: String, value: String): Option[String] = {
    send("GETSET", key, value)
    asString
  }
  
  // SETNX (key, value)
  // sets the value for the specified key, only if the key is not there.
  def setUnlessExists(key: String, value: String): Boolean = {
    send("SETNX", key, value)
    asBoolean
  }

  // INCR (key)
  // increments the specified key by 1
  def incr(key: String): Option[Int] = {
    send("INCR", key)
    asInt
  }

  // INCR (key, increment)
  // increments the specified key by increment
  def incrBy(key: String, increment: Int): Option[Int] = {
    send("INCRBY", key, String.valueOf(increment))
    asInt
  }

  // DECR (key)
  // decrements the specified key by 1
  def decr(key: String): Option[Int] = {
    send("DECR", key)
    asInt
  }

  // DECR (key, increment)
  // decrements the specified key by increment
  def decrBy(key: String, increment: Int): Option[Int] = {
    send("DECRBY", key, String.valueOf(increment))
    asInt
  }

  // MGET (key, key, key, ...)
  // get the values of all the specified keys.
  def mget(key: String, keys: String*) = {
    send("MGET", key, keys: _*)
    asList
  }

  // MSET (key1 value1 key2 value2 ..)
  // set the respective key value pairs. Overwrite value if key exists
  def mset(kvs: (String, String)*) = {
    msetImpl("MSET", kvs: _*)
  }

  // MSETNX (key1 value1 key2 value2 ..)
  // set the respective key value pairs. Noop if any key exists
  def msetUnlessExists(kvs: (String, String)*) = {
    msetImpl("MSETNX", kvs: _*)
  }

  private def msetImpl(command: String, kvs: (String, String)*) = {
    var l: List[String] = List()
    kvs.toList.foreach {case (k, v) => l = l ::: List(k, v)}
    send(command, l.head, l.tail: _*)
    asBoolean
  }
}
