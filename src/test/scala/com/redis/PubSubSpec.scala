package com.redis

import org.scalatest.Spec
import org.scalatest.BeforeAndAfterEach
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith


@RunWith(classOf[JUnitRunner])
class PubSubSpec extends Spec 
                 with ShouldMatchers
                 with BeforeAndAfterEach
                 with BeforeAndAfterAll {

  val r = new RedisClient("localhost", 6379)
  val t = new RedisClient("localhost", 6379)

  override def beforeEach = {
  }

  override def afterEach = {
    // r.flushdb
  }

  override def afterAll = {
    r.disconnect
    t.disconnect
  }

  /**
   * How to play with this test case for pubsub ?
   * It invokes a subscribe on channels "a" and "b" and goes into a blocking loop. From some other client
   * like redis-cli, invoke the following messages:
   * <i>redis-cli publish a "+c":</i> This will add channel c to the subscription list
   * <i>redis-cli publish a "-c":</i> This will unsubscribe from channel c
   * <i>redis-cli publish b "hello!":</i> This will publish hello to the channel
   * <i>redis-cli publish b "exit":</i> This will do an unsubscribe all
   */
  describe("pubsub") {
    it("should do a pubsub protocol") {
      r.subscribe("a", "b") { pubsub =>
        pubsub match {
          case S(channel, no) => println("subscribed to " + channel + " and count = " + no)
          case U(channel, no) => println("unsubscribed from " + channel + " and count = " + no)
          case M(channel, msg) => 
            msg match {
              case "exit" => 
                println("unsubscribe all ..")
                r.unsubscribe

              case x if x startsWith "+" => 
                val s: Seq[Char] = x
                s match {
                  case Seq('+', rest @ _*) => r.subscribe(rest.toString){ m => }
                }

              case x if x startsWith "-" => 
                val s: Seq[Char] = x
                s match {
                  case Seq('-', rest @ _*) => r.unsubscribe(rest.toString)
                }

              case x => 
                println("received message on channel " + channel + " as : " + x)
            }
        }
      }
    }
  }
}
